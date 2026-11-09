package etf.nwt.order.dto.orderStatusHistory;


import etf.nwt.order.models.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderStatusHistoryDTO {

    private Long id;
    @NotNull(message = "Order status is required")
    private OrderStatus orderStatus;
    @NotNull
    private LocalDateTime changedAt;
    @NotBlank(message = "Changed by is required")
    private String changedBy;
}