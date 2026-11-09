package etf.nwt.product.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockCheckResponseDTO {
    private Long variantId;
    private boolean inStock;
    private Integer availableQuantity;
    private BigDecimal price;
}
