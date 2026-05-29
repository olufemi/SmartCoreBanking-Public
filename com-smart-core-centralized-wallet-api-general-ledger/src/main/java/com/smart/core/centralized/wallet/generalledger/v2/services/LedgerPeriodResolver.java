/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.services;

import com.smart.core.centralized.wallet.generalledger.v2.enumm.LedgerPeriod;
import java.time.LocalDate;
import java.time.LocalDateTime;
import net.logstash.logback.encoder.org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

/**
 *
 * @author SmartCore Contributors
 */
@Component
public class LedgerPeriodResolver {

    public Pair<LocalDateTime, LocalDateTime> resolve(LedgerPeriod p) {
        LocalDateTime now = LocalDateTime.now();
        switch (p) {
            case DAILY:
                return Pair.of(now.toLocalDate().atStartOfDay(), now);
            case WEEKLY:
                LocalDate startOfWeek = now.toLocalDate()
                        .with(java.time.temporal.WeekFields.ISO.getFirstDayOfWeek());
                return Pair.of(startOfWeek.atStartOfDay(), now);
            case MONTHLY:
                return Pair.of(now.withDayOfMonth(1).toLocalDate().atStartOfDay(), now);
            case YEARLY:
                return Pair.of(now.withDayOfYear(1).toLocalDate().atStartOfDay(), now);
            default:
                throw new IllegalArgumentException("Unsupported period");
        }
    }
}
