package etf.nwt.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChairDTO{
    private Long id;

    @Size(max = 50, message = "Chair name must not exceed 50 characters")
    @NotBlank(message = "Chair name is required")
    private String name;

    @Size(max = 200, message = "Chair description must not exceed 50 characters")
    @NotBlank(message = "Chair description is required")
    private String description;

    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be positive")
    private BigDecimal basePrice;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    private List<String> galleryImageUrls = new ArrayList<>();
    private List<String> allImages = new ArrayList<>();

    // For GET
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties("chairs")
    private CategoryDTO category;
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties("chairs")
    private SupplierDTO supplier;
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties("chairs")
    private List<ChairVariantDTO> variants;

}
