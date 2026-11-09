package etf.nwt.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CategoryDTO {
    private Long id;

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be 2-100 characters")
    private String name;

    @NotBlank(message = "Category description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    // POST/PUT
    private List<Long> chairIds = new ArrayList<>();

    // GET
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    //private List<ChairDTO> chairs;
}
