package etf.nwt.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class ColorOptionDTO {

    private Long id;

    @NotBlank(message = "Color name is required")
    @Size(min = 2, max = 50, message = "Color name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Hex code is required")
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Hex code must be in format #RRGGBB")
    private String hexCode;
}