package etf.nwt.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class DimensionDTO {

    private Long id;

    @NotBlank(message = "Dimension name is required")
    @Size(max = 50, message = "Name cannot exceed 50 characters")
    private String name;

    @Positive(message = "Width must be positive")
    private Double width; // cm

    @Positive(message = "Height must be positive")
    private Double height; // cm

    @Positive(message = "Depth must be positive")
    private Double depth; // cm

    @Positive(message = "Weight capacity must be positive")
    private Double weightCapacity; // kg

}