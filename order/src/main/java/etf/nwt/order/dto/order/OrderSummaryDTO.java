package etf.nwt.order.dto.order;

import etf.nwt.order.models.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {
    private Long id;
    private Long userId;
    private String orderNumber;
    private OrderStatus currentStatus;
    private Double totalPrice;
    private LocalDateTime createdAt;
    private String customerName;
    private Boolean hasShipment;
    private LocalDateTime lastUpdated;
}
