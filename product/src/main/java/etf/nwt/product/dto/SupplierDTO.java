package etf.nwt.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SupplierDTO {

    private Long id;

    @NotBlank(message = "Supplier name is required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Contact email is required")
    private String contactEmail;

    @NotBlank(message = "Country is required")
    private String country;

    // For POST/PUT
    private List<Long> chairIds = new ArrayList<>();

    // For GET
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties("supplier")
    private List<ChairDTO> chairs;
}
