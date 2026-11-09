package etf.nwt.product.repository;

import etf.nwt.product.model.Chair;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChairRepository extends JpaRepository<Chair,Long> {
}
