package sa.edu.kau.fcit.cpit252.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sa.edu.kau.fcit.cpit252.project.entity.ShopEntity;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<ShopEntity, Long> {
    Optional<ShopEntity> findByOwnerId(Long ownerId);
}
