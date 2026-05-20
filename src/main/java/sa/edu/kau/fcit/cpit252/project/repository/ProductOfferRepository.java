package sa.edu.kau.fcit.cpit252.project.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import sa.edu.kau.fcit.cpit252.project.entity.ProductOfferEntity;

import java.util.List;

public interface ProductOfferRepository extends JpaRepository<ProductOfferEntity, Long> {

    @EntityGraph(attributePaths = {"product", "product.category", "shop"})
    List<ProductOfferEntity> findAll();

    @EntityGraph(attributePaths = {"product", "product.category", "shop"})
    List<ProductOfferEntity> findByProductId(Long productId);
}
