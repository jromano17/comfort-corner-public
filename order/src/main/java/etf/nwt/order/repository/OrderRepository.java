package etf.nwt.order.repository;

import etf.nwt.order.models.Order;
import etf.nwt.order.models.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    Page<Order> findByCurrentStatus(OrderStatus status, Pageable pageable);
    Page<Order> findByUserId(Long userId, Pageable pageable);

    @Query("""
        SELECT o FROM Order o 
        WHERE o.totalPrice >= :minAmount 
        AND o.currentStatus = 'PENDING'
        AND o.createdAt >= :sinceDate
        ORDER BY o.totalPrice DESC, o.createdAt ASC
        """)
    List<Order> findHighValuePendingOrders(
            @Param("minAmount") Double minAmount,
            @Param("sinceDate") LocalDateTime sinceDate);
}
