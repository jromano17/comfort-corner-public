package etf.nwt.product.repository;

import etf.nwt.product.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    Optional<Category> findByName(String name);

    Optional<Category> findByNameIgnoreCase(String name);

    boolean existsByName(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.chairs WHERE c.id = :id")
    Optional<Category> findByIdWithChairs(@Param("id") Long id);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.chairs")
    List<Category> findAllWithChairs();

    @Query("SELECT c FROM Category c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Category> searchByNameOrDescription(@Param("keyword") String keyword);

    @Query("SELECT COUNT(ch) FROM Category c JOIN c.chairs ch WHERE c.id = :categoryId")
    Long countChairsByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT c FROM Category c WHERE SIZE(c.chairs) > :minChairs")
    List<Category> findCategoriesWithMinChairs(@Param("minChairs") int minChairs);

    @Query("SELECT c.name, COUNT(ch) as chairCount " +
            "FROM Category c LEFT JOIN c.chairs ch " +
            "GROUP BY c.id, c.name " +
            "ORDER BY chairCount DESC")
    List<Object[]> getCategoryStats();
}
