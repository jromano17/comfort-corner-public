package etf.nwt.auth_service.repository;

import etf.nwt.auth_service.models.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}