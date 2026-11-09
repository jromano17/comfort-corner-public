package etf.nwt.order.dto.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import etf.nwt.order.dto.orderItem.OrderItemDTO;
import etf.nwt.order.dto.orderStatusHistory.OrderStatusHistoryDTO;
import etf.nwt.order.dto.shipment.ShipmentDTO;
import etf.nwt.order.models.Address;
import etf.nwt.order.models.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {


    private Long id;

    //@NotNull(message = "User ID is required")
    private Long userId;

    //@NotNull(message = "Order number is required")
    //@NotBlank(message = "Order number can not be blank")
    private String orderNumber;

    //@NotNull(message = "Order status is required")
    private OrderStatus currentStatus;

    //@NotNull
    //@Positive(message = "Total price must be positive")
    private Double totalPrice;
    //@NotNull
    private LocalDateTime createdAt;

    @Valid
    @NotNull(message = "Shipping address is required")
    private Address billingAddress;
    @Valid
    @NotNull(message = "Shipping address is required")
    private Address shippingAddress;

    @Valid
    private List<OrderItemDTO> items;

    //@Valid
    private List<OrderStatusHistoryDTO> statusHistory;

    //@Valid
    private ShipmentDTO shipment;
}

