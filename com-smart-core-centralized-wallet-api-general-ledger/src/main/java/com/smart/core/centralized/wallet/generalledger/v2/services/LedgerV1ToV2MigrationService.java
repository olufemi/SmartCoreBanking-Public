/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.services;

/**
 *
 * @author SmartCore Contributors
 */
import com.smart.core.centralized.wallet.generalledger.domains.GenLedgAccount;
import com.smart.core.centralized.wallet.generalledger.domains.GenLedgAccountCum;
import com.smart.core.centralized.wallet.generalledger.repository.GenLedgAccountCumRepo;
import com.smart.core.centralized.wallet.generalledger.repository.GenLedgAccountRepo;
import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerEntryV2;
import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerWalletBalanceV2;
import com.smart.core.centralized.wallet.generalledger.v2.repository.LedgerEntryV2Repo;
import com.smart.core.centralized.wallet.generalledger.v2.repository.LedgerWalletBalanceV2Repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class LedgerV1ToV2MigrationService {

    private final GenLedgAccountCumRepo genLedgAccountCumV1Repo;
    private final GenLedgAccountRepo genLedgAccountV1Repo;

    private final LedgerWalletBalanceV2Repo walletV2Repo;
    private final LedgerEntryV2Repo entryV2Repo;

    @Autowired
    public LedgerV1ToV2MigrationService(GenLedgAccountCumRepo genLedgAccountCumV1Repo,
            GenLedgAccountRepo genLedgAccountV1Repo,
            LedgerWalletBalanceV2Repo walletV2Repo,
            LedgerEntryV2Repo entryV2Repo) {
        this.genLedgAccountCumV1Repo = genLedgAccountCumV1Repo;
        this.genLedgAccountV1Repo = genLedgAccountV1Repo;
        this.walletV2Repo = walletV2Repo;
        this.entryV2Repo = entryV2Repo;
    }

    /**
     * Step A: migrate wallet balances using WALLET_GenLedgAccountCum as truth.
     */
    @Transactional
    public MigrationResult migrateWalletBalancesFromV1Cum() {

        List<GenLedgAccountCum> rows = genLedgAccountCumV1Repo.findAll();
        int created = 0;
        int updated = 0;
        int skipped = 0;

        for (GenLedgAccountCum c : rows) {
            if (c == null) {
                continue;
            }

            String accountNumber = trim(c.getPhoneNumber());
            String phnProductCode = trim(c.getPhnProductCode());

            if (isBlank(accountNumber) || isBlank(phnProductCode)) {
                skipped++;
                continue;
            }

            String productCode = extractProductCode(phnProductCode, accountNumber);
            if (isBlank(productCode)) {
                skipped++;
                continue;
            }

            String key = LedgerWalletBalanceV2.walletKey(accountNumber, productCode);

            LedgerWalletBalanceV2 w = walletV2Repo.findById(key).orElse(null);
            if (w == null) {
                w = LedgerWalletBalanceV2.newWallet(accountNumber, productCode, null);
                created++;
            } else {
                updated++;
            }

            // SOURCE OF TRUTH: v1 cum (phnProductCode totals)
            w.setBalance(nz(c.getTotalBalancePhnProCode()));
            w.setBookBalance(nz(c.getTotalBookBalancePhnProCode()));
            w.setMerchantBookedBalance(nz(c.getTotalMerBookedBalPhnProCode()));

            w.setTotalCredit(nz(c.getTotalAmtCreditedPhnProCode()));
            w.setTotalDebit(nz(c.getTotalAmtDebitedPhnProCode()));
            w.setTotalCharges(nz(c.getTotalPayChargePhnProCode()));
            w.setTotalSwCharges(nz(c.getTotalSwChargesPhnProCode()));

            // keep modifiedAt updated automatically via @PreUpdate
            walletV2Repo.save(w);
        }

        return new MigrationResult("wallet-balances", created, updated, skipped);
    }

    /**
     * Step B: migrate ledger entries using WALLET_GenLedgAccount rows. Uses v1
     * running balance (balancePhnProCode) as balanceAfter.
     *
     * debit finalCharges = accountDebit + demoPayCharges (your confirmed
     * rule)
     */
    @Transactional
    public MigrationResult migrateLedgerEntriesFromV1(int pageSize, boolean skipIfTxnExists) {

        int created = 0;
        int updated = 0; // not used here, but kept for uniform result
        int skipped = 0;

        Pageable pageable = PageRequest.of(0, pageSize, Sort.by("Created").ascending().and(Sort.by("id").ascending()));

        while (true) {
            Page<GenLedgAccount> page = genLedgAccountV1Repo.findAllByOrderByCreatedAscIdAsc((Pageable) pageable);
            if (!page.hasContent()) {
                break;
            }

            List<LedgerEntryV2> batch = new ArrayList<>();
            for (GenLedgAccount v1 : page.getContent()) {
                if (v1 == null) {
                    continue;
                }

                String txnId = trim(v1.getTransactionId());
                if (skipIfTxnExists && !isBlank(txnId) && entryV2Repo.existsByTransactionId(txnId)) {
                    skipped++;
                    continue;
                }

                LedgerEntryV2 e = mapV1ToV2Entry(v1);
                batch.add(e);
                created++;
            }

            if (!batch.isEmpty()) {
                entryV2Repo.saveAll(batch);
            }

            if (!page.hasNext()) {
                break;
            }
            pageable = page.nextPageable();
        }

        return new MigrationResult("ledger-entries", created, updated, skipped);
    }

    private LedgerEntryV2 mapV1ToV2Entry(GenLedgAccount v1) {

        String accountNumber = trim(v1.getPhoneNumber());
        String productCode = trim(v1.getProductCode());
        String productName = trim(v1.getProductName());
        String key = LedgerWalletBalanceV2.walletKey(accountNumber, productCode);

        BigDecimal credit = nz(v1.getAccountCredit());
        BigDecimal debit = nz(v1.getAccountDebit());
        BigDecimal fee = nz(v1.getDemoPayCharges());

        boolean isCredit = credit.compareTo(BigDecimal.ZERO) > 0;
        boolean isDebit = !isCredit && debit.compareTo(BigDecimal.ZERO) > 0;

        String legType = isCredit ? "CREDIT" : (isDebit ? "DEBIT" : "UNKNOWN");

        BigDecimal amount = isCredit ? credit : debit;

        // confirmed by you for debit: finalCharges = accountDebit + demoPayCharges
        BigDecimal finalCharges;
        if (isDebit) {
            finalCharges = debit.add(fee);
        } else if (isCredit) {
            // safest default: do NOT subtract fee again; v1 running balance already reflects the truth
            finalCharges = amount;
        } else {
            finalCharges = amount;
        }

        BigDecimal after = nz(v1.getBalancePhnProCode()); // v1 running balance column (you confirmed you have it)
        BigDecimal before;

        if (isDebit) {
            // after = before - finalCharges  => before = after + finalCharges
            before = after.add(finalCharges);
        } else if (isCredit) {
            // after = before + amount => before = after - amount
            before = after.subtract(amount);
        } else {
            before = after;
        }

        LedgerEntryV2 e = new LedgerEntryV2();
        e.setId(trim(v1.getId())); // ok to reuse id; or generate new if you prefer
        e.setBatchRef(null);       // v1 was not batch
        e.setRequestRef(trim(v1.getTransactionId()));
        e.setTransactionId(trim(v1.getTransactionId()));

        e.setAccountNumber(accountNumber);
        e.setAccountNumberProductCode(key);
        e.setProductCode(productCode);
        e.setProductName(productName);

        e.setLegType(legType);
        e.setTransType(trim(v1.getTransType()));

        e.setAmount(nz(amount));
        e.setFees(fee);
        e.setFinalCharges(nz(finalCharges));

        e.setBalanceBefore(nz(before));
        e.setBalanceAfter(nz(after));

        e.setNarration(trim(v1.getNarration()));
        e.setDescription(trim(v1.getNarration()));

        e.setStatusCode(200);

        // createdAt is @PrePersist; if you want exact v1 Created, set it manually:
        LocalDateTime createdAt = v1.getCreated();
        if (createdAt != null) {
            e.setCreatedAt(createdAt);
        }

        return e;
    }

    /**
     * v1 phnProductCode = phoneNumber + productCode (no delimiter) so
     * productCode = substring after phoneNumber prefix
     */
    private String extractProductCode(String phnProductCode, String phoneNumber) {
        if (isBlank(phnProductCode) || isBlank(phoneNumber)) {
            return null;
        }
        if (!phnProductCode.startsWith(phoneNumber)) {
            return null;
        }
        String pc = phnProductCode.substring(phoneNumber.length());
        return trim(pc);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String trim(String s) {
        return (s == null) ? null : s.trim();
    }

    private BigDecimal nz(BigDecimal b) {
        return (b == null) ? BigDecimal.ZERO : b;
    }

    public static class MigrationResult {

        private String name;
        private int created;
        private int updated;
        private int skipped;

        public MigrationResult(String name, int created, int updated, int skipped) {
            this.name = name;
            this.created = created;
            this.updated = updated;
            this.skipped = skipped;
        }

        public String getName() {
            return name;
        }

        public int getCreated() {
            return created;
        }

        public int getUpdated() {
            return updated;
        }

        public int getSkipped() {
            return skipped;
        }
    }
}
