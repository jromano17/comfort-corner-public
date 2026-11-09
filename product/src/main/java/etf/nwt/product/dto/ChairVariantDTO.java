package etf.nwt.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChairVariantDTO {
    private Long id;

    @NotNull(message = "Chair ID is required")
    private Long chairId;

    @NotNull(message = "Material is required")
    private MaterialDTO material;

    @NotNull(message = "Color option is required")
    private ColorOptionDTO colorOption;

    @NotNull(message = "Dimension is required")
    private DimensionDTO dimension;

    @NotNull(message = "Final price is required")
    @Positive(message = "Final price must be positive")
    private BigDecimal finalPrice;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    private String imageUrl;
    // For GET only
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties("variants")
    private ChairDTO chair;
}
