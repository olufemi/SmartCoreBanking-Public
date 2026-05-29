package com.smart.core.centralized.wallet.generalledger.v2.services;

import com.smart.core.centralized.wallet.generalledger.utils.DecodedJWTToken;
import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerEntryV2;
import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerWalletBalanceV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.LedgerReconciliationRequest;
import com.smart.core.centralized.wallet.generalledger.v2.models.LedgerReconciliationResponse;
import com.smart.core.centralized.wallet.generalledger.v2.repository.LedgerEntryV2Repo;
import com.smart.core.centralized.wallet.generalledger.v2.repository.LedgerWalletBalanceV2Repo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class LedgerReconciliationService {

    private final LedgerEntryV2Repo entryRepo;
    private final LedgerWalletBalanceV2Repo walletRepo;

    public LedgerReconciliationService(LedgerEntryV2Repo entryRepo, LedgerWalletBalanceV2Repo walletRepo) {
        this.entryRepo = entryRepo;
        this.walletRepo = walletRepo;
    }

    public LedgerReconciliationResponse reconcile(LedgerReconciliationRequest request, String auth) {
        LedgerReconciliationResponse response = new LedgerReconciliationResponse();

        try {
            if (request == null) {
                return fail(response, 400, "Request body is required");
            }

            DecodedJWTToken decoded = DecodedJWTToken.getDecoded(auth);
            String productCode = firstNonBlank(request.getProductCode(), decoded.productCode);
            if (!productCode.equals(decoded.productCode)) {
                return fail(response, 400, "Invalid product code!");
            }

            int maxResults = request.getMaxResults() <= 0 ? 500 : Math.min(request.getMaxResults(), 5000);
            List<String> walletKeys = resolveWalletKeys(request, productCode);

            for (String walletKey : walletKeys) {
                if (response.getResults().size() >= maxResults) {
                    break;
                }

                Optional<LedgerWalletBalanceV2> walletOpt = walletRepo.findByAccountNumberProductCode(walletKey);
                BigDecimal ledgerBalance = nz(entryRepo.calculatePostedBalance(walletKey));
                BigDecimal walletBalance = walletOpt.isPresent() ? nz(walletOpt.get().getBalance()) : BigDecimal.ZERO;
                BigDecimal difference = ledgerBalance.subtract(walletBalance);
                boolean matched = difference.compareTo(BigDecimal.ZERO) == 0;

                response.setCheckedCount(response.getCheckedCount() + 1);
                if (matched) {
                    response.setMatchedCount(response.getMatchedCount() + 1);
                } else {
                    response.setMismatchCount(response.getMismatchCount() + 1);
                }

                if (matched && !request.isIncludeMatched()) {
                    continue;
                }

                LedgerReconciliationResponse.WalletReconciliationResult result =
                        new LedgerReconciliationResponse.WalletReconciliationResult();
                result.setAccountNumberProductCode(walletKey);
                result.setLedgerBalance(ledgerBalance);
                result.setWalletBalance(walletBalance);
                result.setDifference(difference);
                result.setStatus(matched ? "MATCHED" : "MISMATCH");
                HashCheckResult hashCheck = verifyHashChain(walletKey);
                result.setHashStatus(hashCheck.status);
                result.setHashIssue(hashCheck.issue);
                if (walletOpt.isPresent()) {
                    result.setAccountNumber(walletOpt.get().getAccountNumber());
                    result.setProductCode(walletOpt.get().getProductCode());
                } else {
                    result.setProductCode(productCode);
                }
                response.getResults().add(result);
            }

            response.setStatusCode(response.getMismatchCount() == 0 ? 200 : 409);
            response.setDescription(response.getMismatchCount() == 0 ? "Reconciliation matched." : "Reconciliation mismatch detected.");
            return response;
        } catch (Exception ex) {
            return fail(response, 500, "An error occurred, please try again");
        }
    }

    private List<String> resolveWalletKeys(LedgerReconciliationRequest request, String productCode) {
        Set<String> walletKeys = new LinkedHashSet<String>();
        if (!isBlank(request.getAccountNumber())) {
            walletKeys.add(LedgerWalletBalanceV2.walletKey(request.getAccountNumber(), productCode));
            return new ArrayList<String>(walletKeys);
        }

        walletKeys.addAll(entryRepo.findDistinctWalletKeys(productCode));
        List<LedgerWalletBalanceV2> wallets = walletRepo.findByProductCode(productCode);
        for (LedgerWalletBalanceV2 wallet : wallets) {
            walletKeys.add(wallet.getAccountNumberProductCode());
        }
        return new ArrayList<String>(walletKeys);
    }

    private LedgerReconciliationResponse fail(LedgerReconciliationResponse response, int code, String description) {
        response.setStatusCode(code);
        response.setDescription(description);
        return response;
    }

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String firstNonBlank(String first, String second) {
        return isBlank(first) ? second : first;
    }

    private HashCheckResult verifyHashChain(String walletKey) {
        List<LedgerEntryV2> entries = entryRepo.findByAccountNumberProductCodeAndStatusCodeOrderByCreatedAtAsc(walletKey, 200);
        String previousHash = "GENESIS";
        for (LedgerEntryV2 entry : entries) {
            if (isBlank(entry.getEntryHash()) || isBlank(entry.getPreviousEntryHash()) || isBlank(entry.getHashPayload())) {
                return new HashCheckResult("NOT_AVAILABLE", "Ledger hash fields are missing for entry " + entry.getId());
            }
            if (!previousHash.equals(entry.getPreviousEntryHash())) {
                return new HashCheckResult("BROKEN", "Previous hash mismatch at entry " + entry.getId());
            }
            String expectedPayload = ledgerHashPayload(entry, previousHash);
            if (!expectedPayload.equals(entry.getHashPayload())) {
                return new HashCheckResult("BROKEN", "Hash payload mismatch at entry " + entry.getId());
            }
            String expectedHash = sha256(expectedPayload);
            if (!expectedHash.equals(entry.getEntryHash())) {
                return new HashCheckResult("BROKEN", "Entry hash mismatch at entry " + entry.getId());
            }
            previousHash = entry.getEntryHash();
        }
        return new HashCheckResult("VERIFIED", null);
    }

    private String ledgerHashPayload(LedgerEntryV2 entry, String previousHash) {
        StringBuilder sb = new StringBuilder();
        sb.append(nv(previousHash)).append('|')
                .append(nv(entry.getId())).append('|')
                .append(nv(entry.getBatchRef())).append('|')
                .append(nv(entry.getLegRef())).append('|')
                .append(nv(entry.getRequestRef())).append('|')
                .append(nv(entry.getTransactionId())).append('|')
                .append(nv(entry.getAccountNumberProductCode())).append('|')
                .append(nv(entry.getProductCode())).append('|')
                .append(nv(entry.getLegType())).append('|')
                .append(nv(entry.getTransType())).append('|')
                .append(decimalString(entry.getAmount())).append('|')
                .append(decimalString(entry.getFees())).append('|')
                .append(decimalString(entry.getFinalCharges())).append('|')
                .append(decimalString(entry.getBalanceBefore())).append('|')
                .append(decimalString(entry.getBalanceAfter())).append('|')
                .append(nv(entry.getRequestHash())).append('|')
                .append(nv(entry.getReversalOfEntryId()));
        return sb.toString();
    }

    private String sha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm not available", ex);
        }
    }

    private String decimalString(BigDecimal value) {
        return nz(value).stripTrailingZeros().toPlainString();
    }

    private String nv(String value) {
        return value == null ? "" : value.trim();
    }

    private static class HashCheckResult {
        private final String status;
        private final String issue;

        private HashCheckResult(String status, String issue) {
            this.status = status;
            this.issue = issue;
        }
    }
}
