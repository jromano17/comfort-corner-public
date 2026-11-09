package etf.nwt.product.repository;

import etf.nwt.product.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier,Long> {

    Optional<Supplier> findByName(String name);

    Optional<Supplier> findByNameIgnoreCase(String name);

    Optional<Supplier> findByContactEmail(String email);

    Optional<Supplier> findByContactEmailIgnoreCase(String email);

    List<Supplier> findByCountry(String country);

    List<Supplier> findByCountryIgnoreCase(String country);

    boolean existsByName(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByContactEmail(String email);

    boolean existsByContactEmailIgnoreCase(String email);

    boolean existsByContactEmailIgnoreCaseAndIdNot(String email, Long id);

    @Query("SELECT s FROM Supplier s LEFT JOIN FETCH s.chairs WHERE s.id = :id")
    Optional<Supplier> findByIdWithChairs(@Param("id") Long id);

    @Query("SELECT s FROM Supplier s LEFT JOIN FETCH s.chairs")
    List<Supplier> findAllWithChairs();

    @Query("SELECT s FROM Supplier s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.contactEmail) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.country) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Supplier> search(@Param("keyword") String keyword);

    @Query("SELECT s FROM Supplier s LEFT JOIN FETCH s.chairs WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.contactEmail) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.country) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Supplier> searchWithChairs(@Param("keyword") String keyword);

    @Query("SELECT COUNT(ch) FROM Supplier s JOIN s.chairs ch WHERE s.id = :supplierId")
    Long countChairsBySupplierId(@Param("supplierId") Long supplierId);

    @Query("SELECT s.country, COUNT(s) as supplierCount " +
            "FROM Supplier s " +
            "GROUP BY s.country " +
            "ORDER BY supplierCount DESC")
    List<Object[]> getSupplierCountByCountry();

    @Query("SELECT s FROM Supplier s " +
            "LEFT JOIN s.chairs ch " +
            "GROUP BY s.id, s.name " +
            "ORDER BY COUNT(ch) DESC " +
            "LIMIT :limit")
    List<Supplier> findTopSuppliersByChairCount(@Param("limit") int limit);

    @Query("SELECT s FROM Supplier s WHERE SIZE(s.chairs) = 0")
    List<Supplier> findSuppliersWithoutChairs();

    @Query("SELECT s.name, s.country, COUNT(ch) as chairCount, " +
            "COALESCE(AVG(ch.basePrice), 0) as avgChairPrice " +
            "FROM Supplier s LEFT JOIN s.chairs ch " +
            "GROUP BY s.id, s.name, s.country " +
            "ORDER BY chairCount DESC")
    List<Object[]> getSupplierPerformanceReport();
}
