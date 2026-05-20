package sa.edu.kau.fcit.cpit252.project.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import sa.edu.kau.fcit.cpit252.project.entity.ProductEntity;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    @Override
    @EntityGraph(attributePaths = {"category"})
    Optional<ProductEntity> findById(Long id);
}
