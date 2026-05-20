package sa.edu.kau.fcit.cpit252.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sa.edu.kau.fcit.cpit252.project.dto.CategoryResponse;
import sa.edu.kau.fcit.cpit252.project.entity.CategoryEntity;
import sa.edu.kau.fcit.cpit252.project.repository.CategoryRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceUnitTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryService service;

    @BeforeEach
    void setUp() {
        service = new CategoryService(categoryRepository);
    }

    @Test
    void buildsTreeAndHandlesMissingParent() {
        CategoryEntity root = category(1L, "Building Materials", "building-materials", null);
        CategoryEntity child = category(2L, "Plumbing", "plumbing", root);
        CategoryEntity orphanParent = category(999L, "Ghost", "ghost", null);
        CategoryEntity orphan = category(3L, "Orphan", "orphan", orphanParent);

        when(categoryRepository.findAll()).thenReturn(List.of(orphan, child, root));

        List<CategoryResponse> tree = service.getCategoryTree();
        assertThat(tree).hasSize(2);

        CategoryResponse first = tree.get(0);
        assertThat(first.getName()).isEqualTo("Building Materials");
        assertThat(first.getChildren()).hasSize(1);
        assertThat(first.getChildren().get(0).getSlug()).isEqualTo("plumbing");

        CategoryResponse second = tree.get(1);
        assertThat(second.getName()).isEqualTo("Orphan");
        assertThat(second.getParentId()).isEqualTo(999L);
    }

    private static CategoryEntity category(Long id, String name, String slug, CategoryEntity parent) {
        CategoryEntity category = new CategoryEntity();
        category.setId(id);
        category.setName(name);
        category.setSlug(slug);
        category.setParent(parent);
        return category;
    }
}
