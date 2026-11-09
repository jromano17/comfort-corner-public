package etf.nwt.order.dto.orderItem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {

    private Long id;
    @NotNull
    private Long variantId;

    @NotNull
    @Positive(message = "Quantity of order items must be positive")
    private Integer quantity;

    @NotNull
    @Positive(message = "Price at purchase must be positive")
    private Double priceAtPurchase;

    private String name;


}