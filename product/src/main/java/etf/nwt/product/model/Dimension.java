package etf.nwt.product.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dimension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Dimension name is required")
    @Size(max = 50, message = "Name cannot exceed 50 characters")
    private String name;

    @Positive(message = "Width must be positive")
    private Double width;

    @Positive(message = "Height must be positive")
    private Double height; // in cm

    @Positive(message = "Depth must be positive")
    private Double depth; // in cm

    @Column(name = "weight_capacity")
    @Positive(message = "Weight capacity must be positive")
    private Double weightCapacity; // in kg

}