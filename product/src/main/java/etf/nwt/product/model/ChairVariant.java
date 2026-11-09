package etf.nwt.product.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "chair_variants")
       // uniqueConstraints = {@UniqueConstraint(columnNames = {"chair_id", "material_id", "color_option_id", "dimension_id"})})
public class ChairVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chair_id", nullable = false)
    private Chair chair;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "color_option_id", nullable = false)
    private ColorOption colorOption;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dimension_id", nullable = false)
    private Dimension dimension;

    @Getter
    @NotNull
    @DecimalMin("0.0")
    @Column(name = "final_price")
    private BigDecimal finalPrice;

    @NotNull
    @Min(0)
    private Integer stockQuantity;

    @Column(name = "image_url")
    private String imageUrl;

}
