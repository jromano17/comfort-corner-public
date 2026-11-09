package etf.nwt.product.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
//@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "chairs")
public class Chair {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 1000)
    private String description;

    @Setter
    @NotNull
    @DecimalMin("0.0")
    @Column(name = "base_price")
    private BigDecimal basePrice;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "chair", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChairVariant> variants;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ElementCollection
    @CollectionTable(name = "chair_images", joinColumns = @JoinColumn(name = "chair_id"))
    @Column(name = "image_url")
    private List<String> galleryImageUrls = new ArrayList<>();

    public Chair(Long id, String name, String description, BigDecimal basePrice, Category category, List<ChairVariant> variants, Supplier supplier, List<String> galleryImageUrls) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.category = category;
        setVariants(variants);
        this.supplier = supplier;
        this.galleryImageUrls = galleryImageUrls;
    }
    public void setVariants(List<ChairVariant> newChairs) {
        if (this.variants == null) {
            this.variants = new ArrayList<>();
        }
        this.variants.clear();
        if (newChairs != null) {
            this.variants.addAll(newChairs);
        }
    }
    public void addVariant(ChairVariant variant) {
        variants.add(variant);
        variant.setChair(this);
    }
    public void removeVariant(ChairVariant variant) {
        variants.remove(variant);
        variant.setChair(null);
    }

}
