package etf.nwt.order.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackingNumber;

    @NotNull(message  = "Created  date is required")
    private LocalDateTime createDate;

    private LocalDateTime estimatedDeliveryDate;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
}