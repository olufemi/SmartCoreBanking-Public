/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.controllers;

import com.smart.core.centralized.wallet.generalledger.v2.models.LedgerSummaryRequest;
import com.smart.core.centralized.wallet.generalledger.v2.models.LedgerSummaryResponse;
import com.smart.core.centralized.wallet.generalledger.v2.services.LedgerSummaryService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author SmartCore Contributors
 */
@RestController
@RequestMapping("/v2")
public class LedgerSummaryController {

    private final LedgerSummaryService ledgerSummaryService;

    public LedgerSummaryController(LedgerSummaryService ledgerSummaryService) {
        this.ledgerSummaryService = ledgerSummaryService;
    }

    @PostMapping(value = "/summary", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LedgerSummaryResponse summary(
            @RequestHeader("authorization") String auth,
            @RequestHeader("channel") String channel,
            @RequestBody LedgerSummaryRequest request
    ) {
        return ledgerSummaryService.getSummary(request, channel);
    }
}
