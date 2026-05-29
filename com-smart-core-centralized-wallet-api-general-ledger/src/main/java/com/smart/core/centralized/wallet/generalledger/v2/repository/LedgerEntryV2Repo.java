package com.smart.core.centralized.wallet.generalledger.v2.repository;

import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerEntryV2;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LedgerEntryV2Repo extends JpaRepository<LedgerEntryV2, String> {

    List<LedgerEntryV2> findByBatchRef(String batchRef);

    List<LedgerEntryV2> findByAccountNumberProductCodeOrderByCreatedAtDesc(String accountNumberProductCode);

    List<LedgerEntryV2> findByAccountNumberProductCodeAndStatusCodeOrderByCreatedAtAsc(String accountNumberProductCode, Integer statusCode);

    Optional<LedgerEntryV2> findFirstByAccountNumberProductCodeAndStatusCodeOrderByCreatedAtDesc(String accountNumberProductCode, Integer statusCode);

    @Query(
            "SELECT "
            + "COALESCE(SUM(CASE WHEN e.legType = 'CREDIT' THEN e.amount ELSE 0 END), 0) AS creditAmount, "
            + "COALESCE(SUM(CASE WHEN e.legType = 'CREDIT' THEN 1 ELSE 0 END), 0) AS creditCount, "
            + "COALESCE(SUM(CASE WHEN e.legType = 'DEBIT' THEN e.finalCharges ELSE 0 END), 0) AS debitAmount, "
            + "COALESCE(SUM(CASE WHEN e.legType = 'DEBIT' THEN 1 ELSE 0 END), 0) AS debitCount "
            + "FROM LedgerEntryV2 e "
            + "WHERE e.accountNumber = :accountNumber "
            + "AND e.productCode = :productCode "
            + "AND e.createdAt BETWEEN :from AND :to"
    )
    LedgerSummaryProjection summarize(
            @Param("accountNumber") String accountNumber,
            @Param("productCode") String productCode,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    boolean existsByTransactionId(String transactionId);

    Optional<LedgerEntryV2> findFirstByProductCodeAndRequestRefOrderByCreatedAtDesc(String productCode, String requestRef);

    Optional<LedgerEntryV2> findFirstByProductCodeAndTransactionIdOrderByCreatedAtDesc(String productCode, String transactionId);

    boolean existsByProductCodeAndReversalOfEntryId(String productCode, String reversalOfEntryId);

    @Query(
            "SELECT COALESCE(SUM(CASE WHEN e.legType = 'CREDIT' THEN (e.amount - e.fees) ELSE -e.finalCharges END), 0) "
            + "FROM LedgerEntryV2 e "
            + "WHERE e.accountNumberProductCode = :walletKey "
            + "AND e.statusCode = 200"
    )
    BigDecimal calculatePostedBalance(@Param("walletKey") String walletKey);

    @Query(
            "SELECT DISTINCT e.accountNumberProductCode "
            + "FROM LedgerEntryV2 e "
            + "WHERE (:productCode IS NULL OR e.productCode = :productCode)"
    )
    List<String> findDistinctWalletKeys(@Param("productCode") String productCode);

    @Query("select count(e) from LedgerEntryV2 e")
    long countAll();

    @Query(
            "select e from LedgerEntryV2 e "
            + "where (:productCode is null or e.productCode = :productCode) "
            + "and (:statusCode is null or e.statusCode = :statusCode) "
            + "and (:legType is null or upper(e.legType) = :legType) "
            + "and (:from is null or e.createdAt >= :from) "
            + "and (:to is null or e.createdAt <= :to) "
            + "and (:search is null or upper(e.requestRef) like :search or upper(e.transactionId) like :search or upper(e.accountNumber) like :search or upper(e.batchRef) like :search) "
            + "order by e.createdAt desc"
    )
    List<LedgerEntryV2> searchForPortal(
            @Param("productCode") String productCode,
            @Param("statusCode") Integer statusCode,
            @Param("legType") String legType,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("search") String search,
            Pageable pageable
    );

    @Query(
            "select "
            + "coalesce(sum(case when e.legType = 'CREDIT' and e.statusCode = 200 then (e.amount - e.fees) else 0 end), 0), "
            + "coalesce(sum(case when e.legType = 'DEBIT' and e.statusCode = 200 then e.finalCharges else 0 end), 0), "
            + "count(e), "
            + "coalesce(sum(case when e.statusCode <> 200 then 1 else 0 end), 0), "
            + "coalesce(sum(case when e.reversalOfEntryId is not null then 1 else 0 end), 0) "
            + "from LedgerEntryV2 e "
            + "where (:productCode is null or e.productCode = :productCode) "
            + "and (:from is null or e.createdAt >= :from) "
            + "and (:to is null or e.createdAt <= :to)"
    )
    Object[] summarizeForPortal(
            @Param("productCode") String productCode,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query(
            "select e.productCode as productCode, count(e) as txnCount, "
            + "coalesce(sum(case when e.legType = 'CREDIT' and e.statusCode = 200 then (e.amount - e.fees) when e.legType = 'DEBIT' and e.statusCode = 200 then e.finalCharges else 0 end), 0) as totalValue "
            + "from LedgerEntryV2 e "
            + "where (:productCode is null or e.productCode = :productCode) "
            + "and (:from is null or e.createdAt >= :from) "
            + "and (:to is null or e.createdAt <= :to) "
            + "group by e.productCode "
            + "order by totalValue desc"
    )
    List<LedgerPortalProductProjection> summarizeTopProducts(
            @Param("productCode") String productCode,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );
}
