package sa.edu.kau.fcit.cpit252.project.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sa.edu.kau.fcit.cpit252.project.dto.CategoryResponse;
import sa.edu.kau.fcit.cpit252.project.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> categories() {
        return categoryService.getCategoryTree();
    }
}
