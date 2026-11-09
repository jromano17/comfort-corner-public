package etf.nwt.order.dto.orderItem;
import lombok.*;


@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockCheckResponseDTO {
    private Long variantId;
    private boolean inStock;
    private Integer availableQuantity;
    private Double price;
}
