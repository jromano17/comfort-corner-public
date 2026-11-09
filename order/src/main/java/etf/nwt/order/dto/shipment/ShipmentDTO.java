package etf.nwt.order.dto.shipment;

import etf.nwt.order.models.ShipmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShipmentDTO {

    private Long id;

    @NotNull
    private Long orderId;
    @NotNull(message = "Tracking number is required")
    private String trackingNumber;
    @NotNull(message = "Shipment status is required")
    private ShipmentStatus status;
    @NotNull(message  = "Estimated delivery date is required")
    private LocalDateTime estimatedDeliveryDate;
    //@NotNull(message  = "Created  date is required")
    private LocalDateTime createDate;

}
