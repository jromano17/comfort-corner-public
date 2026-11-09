package etf.nwt.product.repository;

import etf.nwt.product.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChairVariantRepository extends JpaRepository<ChairVariant,Long> {

    List<ChairVariant> findByChairId(Long chairId);

    List<ChairVariant> findByStockQuantityGreaterThan(Integer quantity);

    boolean existsByChairAndMaterialAndColorOptionAndDimension(
            Chair chair, Material material, ColorOption colorOption, Dimension dimension);

    boolean existsByChairAndMaterialAndColorOptionAndDimensionAndIdNot(
            Chair chair, Material material, ColorOption colorOption, Dimension dimension, Long id);

    @Query("SELECT v FROM ChairVariant v LEFT JOIN FETCH v.chair WHERE v.id = :id")
    Optional<ChairVariant> findByIdWithChair(@Param("id") Long id);

    @Query("SELECT v FROM ChairVariant v LEFT JOIN FETCH v.chair")
    List<ChairVariant> findAllWithChair();

    void deleteByChairId(Long chairId);

    @Query("SELECT SUM(v.stockQuantity) FROM ChairVariant v WHERE v.chair.id = :chairId")
    Integer getTotalStockByChairId(@Param("chairId") Long chairId);

    @Query("SELECT v FROM ChairVariant v WHERE v.chair.id = :chairId AND v.stockQuantity > 0")
    List<ChairVariant> findInStockByChairId(@Param("chairId") Long chairId);
}
