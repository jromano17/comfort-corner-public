package etf.nwt.product.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
//@AllArgsConstructor
@Builder
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 150)
    private String name;

    @Email
    @NotBlank
    @Size(max = 100)
    private String contactEmail;

    @NotBlank
    @Size(max = 100)
    private String country;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("supplier")
    private List<Chair> chairs;

    public Supplier(Long id, String name, String contactEmail, String country, List<Chair> chairs) {
        this.id = id;
        this.name = name;
        this.contactEmail = contactEmail;
        this.country = country;
        setChairs(chairs);
    }
    public void setChairs(List<Chair> newChairs) {
        if (this.chairs == null) {
            this.chairs = new ArrayList<>();
        }
        this.chairs.clear();
        if (newChairs != null) {
            this.chairs.addAll(newChairs);
        }
    }
    public void addChair(Chair chair) {
        chairs.add(chair);
        chair.setSupplier(this);
    }

    public void removeChair(Chair chair) {
        chairs.remove(chair);
        chair.setSupplier(null);
    }
}
