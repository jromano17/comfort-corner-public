package etf.nwt.order.repository;

import etf.nwt.order.models.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory,Long> {
}
