package etf.nwt.product.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
//@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "categories")
public class Category {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @Getter
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("category")
    private List<Chair> chairs;


    public Category(Long id, String name, String description, List<Chair> chairs){
        this.id = id;
        this.name = name;
        this.description = description;
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
        chair.setCategory(this);
    }

    public void removeChair(Chair chair) {
        chairs.remove(chair);
        chair.setCategory(null);
    }
}