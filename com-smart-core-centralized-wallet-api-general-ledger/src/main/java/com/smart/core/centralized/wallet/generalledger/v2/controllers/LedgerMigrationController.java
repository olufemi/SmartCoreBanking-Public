/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.controllers;

/**
 *
 * @author SmartCore Contributors
 */
import com.smart.core.centralized.wallet.generalledger.v2.services.LedgerV1ToV2MigrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/migrate")
public class LedgerMigrationController {

    /*
    Create v2 tables (you already did)

Run:

POST /internal/migrate/v1-to-v2/wallet-balances

Then:

POST /internal/migrate/v1-to-v2/entries?pageSize=2000&skipIfTxnExists=true
     */
    private final LedgerV1ToV2MigrationService migration;

    public LedgerMigrationController(LedgerV1ToV2MigrationService migration) {
        this.migration = migration;
    }

    @PostMapping("/v1-to-v2/wallet-balances")
    public ResponseEntity<?> migrateWalletBalances() {
        return ResponseEntity.ok(migration.migrateWalletBalancesFromV1Cum());
    }

    @PostMapping("/v1-to-v2/entries")
    public ResponseEntity<?> migrateEntries(
            @RequestParam(name = "pageSize", defaultValue = "2000") int pageSize,
            @RequestParam(name = "skipIfTxnExists", defaultValue = "true") boolean skipIfTxnExists
    ) {
        return ResponseEntity.ok(migration.migrateLedgerEntriesFromV1(pageSize, skipIfTxnExists));
    }
}
