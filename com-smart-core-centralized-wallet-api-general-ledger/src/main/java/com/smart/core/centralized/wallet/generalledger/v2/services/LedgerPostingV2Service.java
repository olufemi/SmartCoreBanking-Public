/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.services;

/**
 *
 * @author SmartCore Contributors
 */



import com.smart.core.centralized.wallet.generalledger.domains.GlobalLimitConfig;
import com.smart.core.centralized.wallet.generalledger.domains.Onboarded;
import com.smart.core.centralized.wallet.generalledger.domains.UserLimitConfig;
import com.smart.core.centralized.wallet.generalledger.repository.GlobalLimitConfigRepo;
import com.smart.core.centralized.wallet.generalledger.repository.OnboardedRepo;
import com.smart.core.centralized.wallet.generalledger.repository.UserLimitConfigRepo;
import com.smart.core.centralized.wallet.generalledger.utils.GlobalMethods;
import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerBatchV2;
import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerEntryV2;
import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerIdempotencyV2;
import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerOutboxEventV2;
import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerWalletBalanceV2;
import com.smart.core.centralized.wallet.generalledger.v2.enumm.LegTypeV2;
import com.smart.core.centralized.wallet.generalledger.v2.enumm.PostingModeV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLedgerPostRequestV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLedgerPostResponseV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLegV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.LegResultV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.SingleLedgerPostRequestV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.SingleLedgerPostResponseV2;
import com.smart.core.centralized.wallet.generalledger.v2.repository.LedgerBatchV2Repo;
import com.smart.core.centralized.wallet.generalledger.v2.repository.LedgerEntryV2Repo;
import com.smart.core.centralized.wallet.generalledger.v2.repository.LedgerIdempotencyV2Repo;
import com.smart.core.centralized.wallet.generalledger.v2.repository.LedgerOutboxEventV2Repo;
import com.smart.core.centralized.wallet.generalledger.v2.repository.LedgerWalletBalanceV2Repo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Slf4j
@Service
public class LedgerPostingV2Service {

    private final LedgerBatchV2Repo batchRepo;
    private final LedgerWalletBalanceV2Repo walletRepo;
    private final LedgerEntryV2Repo entryRepo;
    private final LedgerIdempotencyV2Repo idempotencyRepo;
    private final LedgerOutboxEventV2Repo outboxRepo;
    private final LedgerSecurityAuditV2Service securityAuditService;
    private final OnboardedRepo onboardedRepo;
    private final UserLimitConfigRepo userLimitConfigRepo;
    private final GlobalLimitConfigRepo globalLimitConfigRepo;

    public LedgerPostingV2Service(LedgerBatchV2Repo batchRepo,
                                 LedgerWalletBalanceV2Repo walletRepo,
                                 LedgerEntryV2Repo entryRepo,
                                 LedgerIdempotencyV2Repo idempotencyRepo,
                                 LedgerOutboxEventV2Repo outboxRepo,
                                 LedgerSecurityAuditV2Service securityAuditService,
                                 OnboardedRepo onboardedRepo,
                                 UserLimitConfigRepo userLimitConfigRepo,
                                 GlobalLimitConfigRepo globalLimitConfigRepo) {
        this.batchRepo = batchRepo;
        this.walletRepo = walletRepo;
        this.entryRepo = entryRepo;
        this.idempotencyRepo = idempotencyRepo;
        this.outboxRepo = outboxRepo;
        this.securityAuditService = securityAuditService;
        this.onboardedRepo = onboardedRepo;
        this.userLimitConfigRepo = userLimitConfigRepo;
        this.globalLimitConfigRepo = globalLimitConfigRepo;
    }

    // -------------------------
    // Public APIs (single-leg)
    // -------------------------

    @Transactional
    public SingleLedgerPostResponseV2 credit(SingleLedgerPostRequestV2 rq, String authProductCodeFromJwt) {
        BatchLedgerPostRequestV2 brq = new BatchLedgerPostRequestV2();
        brq.setBatchRef(rq.getRequestRef());
        brq.setProductCode(rq.getProductCode());
        brq.setProductName(rq.getProductName());
        brq.setNarration(rq.getNarration());
        brq.setPostingMode(PostingModeV2.ONE_SIDED);

        BatchLegV2 leg = new BatchLegV2();
        leg.setLegRef("LEG-1");
        leg.setAccountNumber(rq.getAccountNumber());
        leg.setProductCode(rq.getProductCode());
        leg.setProductName(rq.getProductName());
        leg.setLegType(LegTypeV2.CREDIT);
        leg.setTransType(rq.getTransType());
        leg.setAmount(nz(rq.getAmount()));
        leg.setFees(nz(rq.getFees()));
        leg.setFinalCharges(nz(rq.getAmount())); // optional for credit
        leg.setNarration(rq.getNarration());
        leg.setDescription(rq.getDescription());
        leg.setRequestRef(rq.getRequestRef());

        brq.setLegs(Collections.singletonList(leg));

        BatchLedgerPostResponseV2 bres = batchPost(brq, authProductCodeFromJwt);

        SingleLedgerPostResponseV2 res = new SingleLedgerPostResponseV2();
        res.setStatusCode(bres.getStatusCode());
        res.setDescription(bres.getDescription());
        res.setData(bres.getData());
        return res;
    }

    @Transactional
    public SingleLedgerPostResponseV2 debit(SingleLedgerPostRequestV2 rq, String authProductCodeFromJwt) {
        BatchLedgerPostRequestV2 brq = new BatchLedgerPostRequestV2();
        brq.setBatchRef(rq.getRequestRef());
        brq.setProductCode(rq.getProductCode());
        brq.setProductName(rq.getProductName());
        brq.setNarration(rq.getNarration());
        brq.setPostingMode(PostingModeV2.ONE_SIDED);

        BatchLegV2 leg = new BatchLegV2();
        leg.setLegRef("LEG-1");
        leg.setAccountNumber(rq.getAccountNumber());
        leg.setProductCode(rq.getProductCode());
        leg.setProductName(rq.getProductName());
        leg.setLegType(LegTypeV2.DEBIT);
        leg.setTransType(rq.getTransType());
        leg.setAmount(nz(rq.getAmount()));
        leg.setFees(nz(rq.getFees()));
        leg.setFinalCharges(nz(rq.getFinalCharges())); // required for debit
        leg.setNarration(rq.getNarration());
        leg.setDescription(rq.getDescription());
        leg.setRequestRef(rq.getRequestRef());

        brq.setLegs(Collections.singletonList(leg));

        BatchLedgerPostResponseV2 bres = batchPost(brq, authProductCodeFromJwt);

        SingleLedgerPostResponseV2 res = new SingleLedgerPostResponseV2();
        res.setStatusCode(bres.getStatusCode());
        res.setDescription(bres.getDescription());
        res.setData(bres.getData());
        return res;
    }

    // -------------------------
    // Core API (batch)
    // -------------------------

    @Transactional
    public BatchLedgerPostResponseV2 batchPost(BatchLedgerPostRequestV2 rq, String authProductCodeFromJwt) {

        BatchLedgerPostResponseV2 resp = new BatchLedgerPostResponseV2();
        if (rq != null) {
            resp.setBatchRef(rq.getBatchRef());
        }

        // 0) basic checks
        if (rq == null || isBlank(rq.getBatchRef()) || isBlank(rq.getProductCode()) || rq.getLegs() == null || rq.getLegs().isEmpty()) {
            return fail(resp, 400, "Invalid request: batchRef/productCode/legs required");
        }

        PostingModeV2 mode = (rq.getPostingMode() == null) ? PostingModeV2.ONE_SIDED : rq.getPostingMode();
        String requestHash = requestHash(rq, mode);
        String idempotencyId = LedgerIdempotencyV2.id(rq.getProductCode(), rq.getBatchRef());

        // 1) tenant auth check (productCode)
        if (!rq.getProductCode().equals(authProductCodeFromJwt)) {
            recordSecurityEvent(rq, requestHash, "PRODUCT_CODE_MISMATCH", "HIGH", "JWT product code does not match request productCode.");
            return fail(resp, 400, "Invalid product code!");
        }

        // 2) Idempotency: same key + same payload returns the original result; same key + different payload is suspicious.
        Optional<LedgerIdempotencyV2> existingIdempotency = idempotencyRepo.findById(idempotencyId);
        if (existingIdempotency.isPresent()) {
            LedgerIdempotencyV2 idem = existingIdempotency.get();
            if (!requestHash.equals(idem.getRequestHash())) {
                recordSecurityEvent(rq, requestHash, "IDEMPOTENCY_PAYLOAD_MISMATCH", "HIGH", "Duplicate idempotency key reused with different payload.");
                return fail(resp, 409, "Duplicate idempotency key reused with different payload.");
            }
            if ("POSTED".equalsIgnoreCase(idem.getStatus())) {
                return postedResponseFromEntries(resp, rq.getBatchRef(), "Batch already posted (idempotent).");
            }
            if ("PENDING".equalsIgnoreCase(idem.getStatus())) {
                return fail(resp, 409, "Batch is already being processed.");
            }
            if ("FAILED".equalsIgnoreCase(idem.getStatus())) {
                return fail(resp, 409, firstNonBlank(idem.getErrorMessage(), "Previous request with this idempotency key failed."));
            }
        }

        Optional<LedgerBatchV2> existing = batchRepo.findById(rq.getBatchRef());
        if (existing.isPresent()) {
            LedgerBatchV2 b = existing.get();
            if (!isBlank(b.getRequestHash()) && !requestHash.equals(b.getRequestHash())) {
                recordSecurityEvent(rq, requestHash, "BATCH_REF_PAYLOAD_MISMATCH", "HIGH", "Batch reference already exists with a different payload.");
                return fail(resp, 409, "Batch reference already exists with a different payload.");
            }
            if ("POSTED".equalsIgnoreCase(b.getStatus())) {
                return postedResponseFromEntries(resp, rq.getBatchRef(), "Batch already posted (idempotent).");
            }
            if ("PENDING".equalsIgnoreCase(b.getStatus())) {
                return fail(resp, 409, "Batch is already being processed.");
            }
            if ("FAILED".equalsIgnoreCase(b.getStatus())) {
                return fail(resp, 409, firstNonBlank(b.getErrorMessage(), "Previous batch with this reference failed."));
            }
        }

        // Create batch header
        LedgerBatchV2 batch = LedgerBatchV2.pending(rq.getBatchRef(), rq.getProductCode());
        batch.setRequestHash(requestHash);
        batch.setPostingMode(mode.name());
        batch.setTotalLegs(rq.getLegs().size());
        batchRepo.save(batch);
        LedgerIdempotencyV2 idempotency = LedgerIdempotencyV2.pending(rq.getProductCode(), rq.getBatchRef(), rq.getBatchRef(), requestHash);
        idempotencyRepo.save(idempotency);

        try {
            // 3) Validate legs and ensure same productCode
            Set<String> legRefs = new HashSet<>();
            Set<String> requestRefs = new HashSet<>();
            for (BatchLegV2 leg : rq.getLegs()) {
                String err = validateLegBasics(leg, rq.getProductCode(), mode);
                if (err != null) {
                    batch.setStatus("FAILED");
                    batch.setErrorMessage(err);
                    batchRepo.save(batch);
                    failIdempotency(idempotency, err);
                    return fail(resp, 400, err);
                }
                if (!isBlank(leg.getLegRef()) && !legRefs.add(leg.getLegRef())) {
                    String duplicate = "Duplicate legRef in batch: " + leg.getLegRef();
                    recordSecurityEvent(rq, requestHash, "DUPLICATE_LEG_REF", "MEDIUM", duplicate);
                    batch.setStatus("FAILED");
                    batch.setErrorMessage(duplicate);
                    batchRepo.save(batch);
                    failIdempotency(idempotency, duplicate);
                    return fail(resp, 400, duplicate);
                }
                if (!isBlank(leg.getRequestRef()) && !requestRefs.add(leg.getRequestRef())) {
                    String duplicate = "Duplicate requestRef in batch: " + leg.getRequestRef();
                    recordSecurityEvent(rq, requestHash, "DUPLICATE_REQUEST_REF", "MEDIUM", duplicate);
                    batch.setStatus("FAILED");
                    batch.setErrorMessage(duplicate);
                    batchRepo.save(batch);
                    failIdempotency(idempotency, duplicate);
                    return fail(resp, 400, duplicate);
                }
            }

            // 4) BALANCED mode: enforce net movement sums to zero
            if (mode == PostingModeV2.BALANCED) {
                BigDecimal net = BigDecimal.ZERO;
                for (BatchLegV2 leg : rq.getLegs()) {
                    net = net.add(netMovement(leg));
                }
                if (net.compareTo(BigDecimal.ZERO) != 0) {
                    String err = "Batch not balanced. Net movement must be 0, got: " + net.toPlainString();
                    batch.setStatus("FAILED");
                    batch.setErrorMessage(err);
                    batchRepo.save(batch);
                    failIdempotency(idempotency, err);
                    return fail(resp, 400, err);
                }
            }

            // 5) Lock all wallets touched (distinct keys, sorted to avoid deadlocks)
            List<String> keys = distinctWalletKeysSorted(rq.getLegs(), rq.getProductCode());

            Map<String, LedgerWalletBalanceV2> lockedWallets = new HashMap<>();
            for (String key : keys) {
                LedgerWalletBalanceV2 w = walletRepo.lockByWalletKey(key).orElseGet(() -> {
                    // Create wallet balance row on first ever transaction (or you may require onboarding existence)
                    LedgerWalletBalanceV2 nw = new LedgerWalletBalanceV2();
                    nw.setAccountNumberProductCode(key);
                    //key = accountNumber + productCode; we need parse accountNumber:
                    // Since concatenation has no delimiter, prefer sending explicit accountNumber+productCode.
                    // Here we will re-derive from leg map later. For now, set them in apply step.
                    return nw;
                });
                lockedWallets.put(key, w);
            }

            // 6) Build start balances + simulation
            Map<String, BigDecimal> workingBal = new HashMap<>();
            for (String key : keys) {
                LedgerWalletBalanceV2 w = lockedWallets.get(key);
                BigDecimal b = (w.getBalance() == null) ? BigDecimal.ZERO : w.getBalance();
                workingBal.put(key, b);
            }

            // simulate + per-leg checks that depend on balance
            for (BatchLegV2 leg : rq.getLegs()) {
                String key = LedgerWalletBalanceV2.walletKey(leg.getAccountNumber(), rq.getProductCode());
                BigDecimal before = workingBal.getOrDefault(key, BigDecimal.ZERO);
                BigDecimal delta = netMovement(leg);
                BigDecimal after = before.add(delta);

                String policyErr = validateLegPolicyUnderLock(leg, before, after, rq.getProductCode());
                if (policyErr != null) {
                    batch.setStatus("FAILED");
                    batch.setErrorMessage(policyErr);
                    batchRepo.save(batch);
                    failIdempotency(idempotency, policyErr);
                    return fail(resp, 400, policyErr);
                }

                workingBal.put(key, after);
            }

            // 7) Persist all legs: update wallet + insert entry
            List<LegResultV2> legResults = new ArrayList<>();
            Map<String, Object> data = new HashMap<>();
            Map<String, String> previousHashByWallet = loadPreviousHashes(keys);

            for (BatchLegV2 leg : rq.getLegs()) {
                String key = LedgerWalletBalanceV2.walletKey(leg.getAccountNumber(), rq.getProductCode());

                LedgerWalletBalanceV2 wallet = lockedWallets.get(key);
                if (wallet.getAccountNumberProductCode() == null) {
                    wallet.setAccountNumberProductCode(key);
                }
                wallet.setAccountNumber(leg.getAccountNumber());
                wallet.setProductCode(rq.getProductCode());
                wallet.setProductName(firstNonBlank(leg.getProductName(), rq.getProductName()));

                BigDecimal before = nz(wallet.getBalance());
                BigDecimal delta = netMovement(leg);
                BigDecimal after = before.add(delta);

                // Update wallet totals
                if (leg.getLegType() == LegTypeV2.CREDIT) {
                    wallet.setTotalCredit(nz(wallet.getTotalCredit()).add(nz(leg.getAmount())));
                } else {
                    wallet.setTotalDebit(nz(wallet.getTotalDebit()).add(nz(leg.getFinalCharges())));
                }
                wallet.setTotalCharges(nz(wallet.getTotalCharges()).add(nz(leg.getFees())));
                wallet.setBalance(after);

                walletRepo.save(wallet);

                // Insert entry
                LedgerEntryV2 entry = new LedgerEntryV2();
                entry.setId(String.valueOf(GlobalMethods.generateTransactionId()));
                entry.setBatchRef(rq.getBatchRef());
                entry.setLegRef(firstNonBlank(leg.getLegRef(), leg.getRequestRef()));
                entry.setTransactionId(firstNonBlank(leg.getRequestRef(), String.valueOf(GlobalMethods.generateTransactionId())));
                entry.setAccountNumber(leg.getAccountNumber());
                entry.setAccountNumberProductCode(key);
                entry.setProductCode(rq.getProductCode());
                entry.setProductName(wallet.getProductName());
                entry.setLegType(leg.getLegType().name());
                entry.setTransType(leg.getTransType());
                entry.setAmount(nz(leg.getAmount()));
                entry.setFees(nz(leg.getFees()));
                entry.setFinalCharges(finalCharges(leg));
                entry.setBalanceBefore(before);
                entry.setBalanceAfter(after);
                entry.setNarration(firstNonBlank(leg.getNarration(), rq.getNarration()));
                entry.setDescription(leg.getDescription());
                entry.setRequestRef(leg.getRequestRef());
                entry.setRequestHash(requestHash);
                entry.setReversalOfEntryId(leg.getReversalOfEntryId());
                entry.setReversalReason(leg.getReversalReason());
                entry.setStatusCode(200);
                String previousHash = previousHashByWallet.get(key);
                entry.setPreviousEntryHash(previousHash);
                entry.setHashPayload(ledgerHashPayload(entry, previousHash));
                entry.setEntryHash(sha256(entry.getHashPayload()));
                previousHashByWallet.put(key, entry.getEntryHash());

                entryRepo.save(entry);

                // Leg result
                LegResultV2 lr = new LegResultV2();
                lr.setLegRef(leg.getLegRef());
                lr.setStatusCode(200);
                lr.setDescription("Posted");
                Map<String, Object> lrData = new HashMap<>();
                lrData.put("accountBalance", after.toPlainString());
                lrData.put("accountNumberProductCode", key);
                lr.setData(lrData);
                legResults.add(lr);
                data = lrData;
            }

            saveBatchPostedOutboxEvent(rq, requestHash, legResults);

            // 8) Mark batch posted
            batch.setStatus("POSTED");
            batch.setPostedAt(java.time.LocalDateTime.now());
            batchRepo.save(batch);
            idempotency.setStatus("POSTED");
            idempotencyRepo.save(idempotency);

            resp.setStatusCode(200);
            resp.setDescription("Batch posted successfully.");
            resp.setLegResults(legResults);
            resp.setData(data);
            return resp;

        } catch (Exception e) {
            log.error("Batch posting failed", e);
            recordSecurityEvent(rq, requestHash, "LEDGER_POSTING_SYSTEM_ERROR", "CRITICAL", "System error during ledger posting: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            batch.setStatus("FAILED");
            batch.setErrorMessage("System error: " + e.getMessage());
            batchRepo.save(batch);
            return fail(resp, 500, "An error occurred, please try again");
        }
    }

    // -------------------------
    // Helpers
    // -------------------------

    private String validateLegBasics(BatchLegV2 leg, String batchProductCode, PostingModeV2 mode) {
        if (leg == null) return "Invalid leg";
        if (leg.getLegType() == null) return "legType is required";
        if (isBlank(leg.getAccountNumber())) return "accountNumber is required";
        if (isBlank(leg.getRequestRef())) return "requestRef is required";
        if (isBlank(leg.getTransType())) return "transType is required";
        if (leg.getAmount() == null || leg.getAmount().compareTo(BigDecimal.ZERO) <= 0) return "amount must be > 0";
        if (leg.getFees() == null || leg.getFees().compareTo(BigDecimal.ZERO) < 0) return "fees must be >= 0";

        // enforce same productCode tenant
        if (isBlank(leg.getProductCode()) || !batchProductCode.equals(leg.getProductCode())) {
            return "All legs must have the same productCode as the batch";
        }

        // debit requires finalCharges = amount + fees
        if (leg.getLegType() == LegTypeV2.DEBIT) {
            if (!"Withdrawal".equals(leg.getTransType())) return "Invalid transaction type!";
            if (leg.getFinalCharges() == null) return "finalCharges required for DEBIT";
            BigDecimal expected = nz(leg.getAmount()).add(nz(leg.getFees()));
            if (expected.compareTo(nz(leg.getFinalCharges())) != 0) {
                return "finalCharges must equal amount + fees for DEBIT";
            }
        } else if (!"Deposit".equals(leg.getTransType())) {
            return "Invalid transaction type!";
        }

        // ONE_SIDED allows any mix, BALANCED allows mix but must net to zero (checked later)
        return null;
    }

    private String validateLegPolicyUnderLock(BatchLegV2 leg, BigDecimal before, BigDecimal after, String productCode) {
        List<Onboarded> onboarded = onboardedRepo.findByWalletNoProductCode(leg.getAccountNumber(), productCode);
        if (onboarded == null || onboarded.isEmpty()) {
            return "Wallet Number does not exist";
        }

        List<UserLimitConfig> userLimits = userLimitConfigRepo.findByPhoneNumberProductCode(onboarded.get(0).getPhoneNumbProductCode());
        if (userLimits == null || userLimits.isEmpty()) {
            return "User limit config not found for wallet.";
        }

        List<GlobalLimitConfig> globalLimits = globalLimitConfigRepo.findByLimitCategory(userLimits.get(0).getTierCategory());
        if (globalLimits == null || globalLimits.isEmpty()) {
            return "Global limit config not found for tier.";
        }

        GlobalLimitConfig limit = globalLimits.get(0);
        if (leg.getLegType() == LegTypeV2.DEBIT) {
            BigDecimal totalTransAmt = nz(leg.getFinalCharges());
            BigDecimal minimumBalance = bd(limit.getMinimumBalance());

            if (after.compareTo(minimumBalance) < 0) {
                return "Sorry, minimum account balance must be: N" + minimumBalance;
            }
            if (bd(limit.getWithdrawalSingleTransaction()).compareTo(totalTransAmt) < 0) {
                return "Sorry, your single maximum withdrwal is: N" + limit.getWithdrawalSingleTransaction();
            }
            if (bd(limit.getWithdrawal()).compareTo(totalTransAmt) < 0) {
                return "Sorry, your maximum withdrwal is: N" + limit.getWithdrawal();
            }
            return null;
        }

        BigDecimal amount = nz(leg.getAmount());
        BigDecimal maxSingleDeposit = bd(limit.getWalletSingleDeposit());
        if (amount.compareTo(maxSingleDeposit) > 0) {
            return "Sorry, your single maximum deposit is: N" + maxSingleDeposit;
        }

        BigDecimal maxBalance = bd(limit.getMaximumBalance());
        if (after.compareTo(maxBalance) > 0) {
            return "Sorry, your maximum account balance is: N" + maxBalance;
        }

        return null;
    }

    // net movement on BALANCE
    // CREDIT increases balance by (amount - fees)
    // DEBIT decreases balance by (amount + fees) aka finalCharges
    private BigDecimal netMovement(BatchLegV2 leg) {
        if (leg.getLegType() == LegTypeV2.CREDIT) {
            return nz(leg.getAmount()).subtract(nz(leg.getFees()));
        }
        // debit
        return nz(leg.getFinalCharges()).negate();
    }

    private BigDecimal finalCharges(BatchLegV2 leg) {
        if (leg.getLegType() == LegTypeV2.DEBIT) {
            return nz(leg.getFinalCharges());
        }
        // for credit we store amount (or amount-fees). keep amount for clarity.
        return nz(leg.getAmount());
    }

    private List<String> distinctWalletKeysSorted(List<BatchLegV2> legs, String productCode) {
        Set<String> set = new HashSet<>();
        for (BatchLegV2 leg : legs) {
            set.add(LedgerWalletBalanceV2.walletKey(leg.getAccountNumber(), productCode));
        }
        List<String> list = new ArrayList<>(set);
        Collections.sort(list);
        return list;
    }

    private BatchLedgerPostResponseV2 fail(BatchLedgerPostResponseV2 resp, int code, String msg) {
        resp.setStatusCode(code);
        resp.setDescription(msg);
        return resp;
    }

    private void failIdempotency(LedgerIdempotencyV2 idempotency, String err) {
        idempotency.setStatus("FAILED");
        idempotency.setErrorMessage(err);
        idempotencyRepo.save(idempotency);
    }

    private BatchLedgerPostResponseV2 postedResponseFromEntries(BatchLedgerPostResponseV2 resp, String batchRef, String description) {
        List<LedgerEntryV2> entries = entryRepo.findByBatchRef(batchRef);
        List<LegResultV2> legResults = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();

        for (LedgerEntryV2 entry : entries) {
            LegResultV2 lr = new LegResultV2();
            lr.setLegRef(firstNonBlank(entry.getLegRef(), entry.getRequestRef()));
            lr.setStatusCode(200);
            lr.setDescription("Posted");
            Map<String, Object> lrData = new HashMap<>();
            lrData.put("accountBalance", entry.getBalanceAfter().toPlainString());
            lrData.put("accountNumberProductCode", entry.getAccountNumberProductCode());
            lr.setData(lrData);
            legResults.add(lr);
            data = lrData;
        }

        resp.setStatusCode(200);
        resp.setDescription(description);
        resp.setLegResults(legResults);
        resp.setData(data);
        return resp;
    }

    private String requestHash(BatchLedgerPostRequestV2 rq, PostingModeV2 mode) {
        StringBuilder sb = new StringBuilder();
        sb.append(nv(rq.getBatchRef())).append('|')
                .append(nv(rq.getProductCode())).append('|')
                .append(mode.name()).append('|')
                .append(nv(rq.getNarration()));

        for (BatchLegV2 leg : rq.getLegs()) {
            sb.append("||")
                    .append(nv(leg.getLegRef())).append('|')
                    .append(nv(leg.getRequestRef())).append('|')
                    .append(nv(leg.getAccountNumber())).append('|')
                    .append(nv(leg.getProductCode())).append('|')
                    .append(leg.getLegType() == null ? "" : leg.getLegType().name()).append('|')
                    .append(nv(leg.getTransType())).append('|')
                    .append(decimalString(leg.getAmount())).append('|')
                    .append(decimalString(leg.getFees())).append('|')
                    .append(decimalString(leg.getFinalCharges()));
        }
        return sha256(sb.toString());
    }

    private Map<String, String> loadPreviousHashes(List<String> walletKeys) {
        Map<String, String> previousHashByWallet = new HashMap<>();
        for (String walletKey : walletKeys) {
            Optional<LedgerEntryV2> previous = entryRepo.findFirstByAccountNumberProductCodeAndStatusCodeOrderByCreatedAtDesc(walletKey, 200);
            previousHashByWallet.put(walletKey, previous.map(LedgerEntryV2::getEntryHash).orElse("GENESIS"));
        }
        return previousHashByWallet;
    }

    private void saveBatchPostedOutboxEvent(BatchLedgerPostRequestV2 rq, String requestHash, List<LegResultV2> legResults) {
        if (outboxRepo.existsByProductCodeAndEventTypeAndAggregateRef(rq.getProductCode(), "LEDGER_BATCH_POSTED", rq.getBatchRef())) {
            return;
        }

        LedgerOutboxEventV2 event = new LedgerOutboxEventV2();
        event.setId(String.valueOf(GlobalMethods.generateTransactionId()));
        event.setProductCode(rq.getProductCode());
        event.setEventType("LEDGER_BATCH_POSTED");
        event.setAggregateRef(rq.getBatchRef());
        event.setRequestHash(requestHash);
        event.setPayload(batchPostedPayload(rq, requestHash, legResults));
        event.setStatus("PENDING");
        event.setRetryCount(0);
        outboxRepo.save(event);
    }

    private String batchPostedPayload(BatchLedgerPostRequestV2 rq, String requestHash, List<LegResultV2> legResults) {
        StringBuilder sb = new StringBuilder();
        sb.append('{')
                .append("\"eventType\":\"LEDGER_BATCH_POSTED\",")
                .append("\"productCode\":\"").append(json(nv(rq.getProductCode()))).append("\",")
                .append("\"batchRef\":\"").append(json(nv(rq.getBatchRef()))).append("\",")
                .append("\"requestHash\":\"").append(json(nv(requestHash))).append("\",")
                .append("\"postingMode\":\"").append(rq.getPostingMode() == null ? "" : rq.getPostingMode().name()).append("\",")
                .append("\"totalLegs\":").append(rq.getLegs() == null ? 0 : rq.getLegs().size()).append(',')
                .append("\"legs\":[");

        for (int i = 0; i < legResults.size(); i++) {
            LegResultV2 result = legResults.get(i);
            if (i > 0) {
                sb.append(',');
            }
            sb.append('{')
                    .append("\"legRef\":\"").append(json(nv(result.getLegRef()))).append("\",")
                    .append("\"statusCode\":").append(result.getStatusCode()).append(',')
                    .append("\"description\":\"").append(json(nv(result.getDescription()))).append("\"")
                    .append('}');
        }

        sb.append("]}");
        return sb.toString();
    }

    private void recordSecurityEvent(BatchLedgerPostRequestV2 rq,
                                     String requestHash,
                                     String eventType,
                                     String severity,
                                     String reason) {
        securityAuditService.record(
                rq == null ? null : rq.getProductCode(),
                eventType,
                severity,
                rq == null ? null : rq.getBatchRef(),
                requestHash,
                reason,
                securityPayload(rq, eventType, severity, reason));
    }

    private String securityPayload(BatchLedgerPostRequestV2 rq, String eventType, String severity, String reason) {
        StringBuilder sb = new StringBuilder();
        sb.append('{')
                .append("\"eventType\":\"").append(json(nv(eventType))).append("\",")
                .append("\"severity\":\"").append(json(nv(severity))).append("\",")
                .append("\"reason\":\"").append(json(nv(reason))).append("\",")
                .append("\"productCode\":\"").append(json(rq == null ? "" : nv(rq.getProductCode()))).append("\",")
                .append("\"batchRef\":\"").append(json(rq == null ? "" : nv(rq.getBatchRef()))).append("\",")
                .append("\"totalLegs\":").append(rq == null || rq.getLegs() == null ? 0 : rq.getLegs().size())
                .append('}');
        return sb.toString();
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

    private String json(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private BigDecimal bd(String s) {
        if (s == null || s.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(s.trim());
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private BigDecimal nz(BigDecimal b) {
        return (b == null) ? BigDecimal.ZERO : b;
    }

    private String firstNonBlank(String a, String b) {
        if (!isBlank(a)) return a;
        return b;
    }
}
