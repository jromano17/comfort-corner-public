package etf.nwt.product.repository;

import etf.nwt.product.model.ColorOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ColorOptionRepository extends JpaRepository<ColorOption,Long> {

    Optional<ColorOption> findByName(String name);

    Optional<ColorOption> findByHexCode(String hexCode);

    boolean existsByName(String name);

    boolean existsByHexCode(String hexCode);

    boolean existsByNameAndIdNot(String name, Long id);

    boolean existsByHexCodeAndIdNot(String hexCode, Long id);

    List<ColorOption> findByNameContainingIgnoreCase(String name);

    @Query("SELECT c FROM ColorOption c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.hexCode) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ColorOption> search(@Param("keyword") String keyword);

    long count();
}
