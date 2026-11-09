package etf.nwt.order.repository;

import etf.nwt.order.models.IncomeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface IncomeRecordRepository  extends JpaRepository<IncomeRecord,Long> {
    @Query("""
        SELECT COALESCE(SUM(i.incomeAmount), 0) 
        FROM IncomeRecord i 
        WHERE i.recordedAt BETWEEN :startDate AND :endDate
        """)
    Double getTotalIncomeByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
