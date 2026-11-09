package etf.nwt.order.repository;

import etf.nwt.order.models.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment,Long> {

    @Query("""
        SELECT s FROM Shipment s 
        WHERE s.estimatedDeliveryDate < :currentDate 
        AND s.status NOT IN ('DELIVERED', 'CANCELLED')
        ORDER BY s.estimatedDeliveryDate ASC
        """)
    List<Shipment> findDelayedShipments(@Param("currentDate") LocalDateTime currentDate);

}
