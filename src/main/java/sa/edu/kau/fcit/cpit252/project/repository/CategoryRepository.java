package sa.edu.kau.fcit.cpit252.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sa.edu.kau.fcit.cpit252.project.entity.CategoryEntity;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findBySlugIgnoreCase(String slug);
}
