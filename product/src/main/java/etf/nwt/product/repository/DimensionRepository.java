package etf.nwt.product.repository;

import etf.nwt.product.model.Dimension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DimensionRepository extends JpaRepository<Dimension, Long> {

    Optional<Dimension> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    @Query("SELECT d FROM Dimension d WHERE " +
            "d.width BETWEEN :minWidth AND :maxWidth " +
            "AND d.height BETWEEN :minHeight AND :maxHeight " +
            "AND d.depth BETWEEN :minDepth AND :maxDepth")
    List<Dimension> findBySizeRange(
            @Param("minWidth") Double minWidth,
            @Param("maxWidth") Double maxWidth,
            @Param("minHeight") Double minHeight,
            @Param("maxHeight") Double maxHeight,
            @Param("minDepth") Double minDepth,
            @Param("maxDepth") Double maxDepth);

    @Query("SELECT d FROM Dimension d WHERE d.weightCapacity >= :minWeightCapacity")
    List<Dimension> findByMinWeightCapacity(@Param("minWeightCapacity") Double minWeightCapacity);


    @Query("SELECT d FROM Dimension d WHERE " +
            "LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Dimension> search(@Param("keyword") String keyword);


    @Query("SELECT MIN(d.width), MAX(d.width), AVG(d.width), " +
            "MIN(d.height), MAX(d.height), AVG(d.height), " +
            "MIN(d.depth), MAX(d.depth), AVG(d.depth) " +
            "FROM Dimension d")
    Object[] getSizeStatistics();

}