/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.services;

import com.smart.core.centralized.wallet.generalledger.v2.enumm.LedgerPeriod;
import com.smart.core.centralized.wallet.generalledger.v2.models.LedgerSummaryRequest;
import com.smart.core.centralized.wallet.generalledger.v2.models.LedgerSummaryResponse;
import com.smart.core.centralized.wallet.generalledger.v2.repository.LedgerEntryV2Repo;
import com.smart.core.centralized.wallet.generalledger.v2.repository.LedgerSummaryProjection;

import net.logstash.logback.encoder.org.apache.commons.lang3.tuple.Pair;


/**
 *
 * @author SmartCore Contributors
 */
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
@Service
public class LedgerSummaryService {

    private final LedgerPeriodResolver periodResolver;
    private final LedgerEntryV2Repo entryRepo;

    public LedgerSummaryService(LedgerPeriodResolver periodResolver, LedgerEntryV2Repo entryRepo) {
        this.periodResolver = periodResolver;
        this.entryRepo = entryRepo;
    }

    public LedgerSummaryResponse getSummary(LedgerSummaryRequest req, String channel) {

        LedgerSummaryResponse resp = new LedgerSummaryResponse();

        try {
            // Basic validation
            if (req == null) {
                return fail(resp, 400, "Request body is required");
            }
            if (isBlank(req.getAccountNumber())) {
                return fail(resp, 400, "accountNumber is required");
            }
            if (isBlank(req.getProductCode())) {
                return fail(resp, 400, "productCode is required");
            }

            List<LedgerSummaryRequest.LedgerPeriodQuery> periods = req.getPeriods();
            if (periods == null || periods.isEmpty()) {
                return fail(resp, 400, "periods is required");
            }

            // Limits (example: max 3 periods)
            if (periods.size() > 3) {
                return fail(resp, 400, "Too many periods requested. Max=3");
            }

            for (LedgerSummaryRequest.LedgerPeriodQuery p : periods) {
                if (p == null || isBlank(p.getCode())) {
                    return fail(resp, 400, "Each period item must have code");
                }

                // from/to rule
                if ((p.getFrom() == null) ^ (p.getTo() == null)) {
                    return fail(resp, 400, "from/to must both be provided or both omitted");
                }

                LocalDateTime start;
                LocalDateTime end;

                if (p.getFrom() != null) {
                    start = p.getFrom();
                    end = p.getTo();

                    if (start.isAfter(end)) {
                        return fail(resp, 400, "Invalid range: from > to for " + p.getCode());
                    }

                    long days = Duration.between(start, end).toDays() + 1;
                    if (days > 366) {
                        return fail(resp, 400, "Date range too large for " + p.getCode() + " (max 366 days)");
                    }
                } else {
                    // resolve default date range from code (DAILY/WEEKLY/MONTHLY/YEARLY)
                    LedgerPeriod periodEnum = LedgerPeriod.from(p.getCode());
                    Pair<LocalDateTime, LocalDateTime> range = periodResolver.resolve(periodEnum);
                    start = range.getLeft();
                    end = range.getRight();
                }

                // Query aggregation once per period
                LedgerSummaryProjection s = entryRepo.summarize(
                        req.getAccountNumber().trim(),
                        req.getProductCode().trim(),
                        start,
                        end
                );

                BigDecimal creditAmount = (s != null && s.getCreditAmount() != null) ? s.getCreditAmount() : BigDecimal.ZERO;
                long creditCountL = (s != null && s.getCreditCount() != null) ? s.getCreditCount() : 0L;

                BigDecimal debitAmount = (s != null && s.getDebitAmount() != null) ? s.getDebitAmount() : BigDecimal.ZERO;
                long debitCountL = (s != null && s.getDebitCount() != null) ? s.getDebitCount() : 0L;

                LedgerSummaryResponse.LedgerPeriodSummary ps = new LedgerSummaryResponse.LedgerPeriodSummary();
                ps.setCode(p.getCode().trim().toUpperCase());
                ps.setCreditAmount(creditAmount);
                ps.setCreditCount(safeInt(creditCountL));
                ps.setDebitAmount(debitAmount);
                ps.setDebitCount(safeInt(debitCountL));

                resp.getPeriods().add(ps);
            }

            resp.setStatusCode(200);
            resp.setDescription("SUCCESS");
            return resp;

        } catch (Exception e) {
            return fail(resp, 500, "FAILED: " + e.getMessage());
        }
    }

    private LedgerSummaryResponse fail(LedgerSummaryResponse resp, int code, String msg) {
        resp.setStatusCode(code);
        resp.setDescription(msg);
        return resp;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private Integer safeInt(long v) {
        if (v > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        if (v < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        return (int) v;
    }
}

