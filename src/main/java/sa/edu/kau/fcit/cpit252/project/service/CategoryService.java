package sa.edu.kau.fcit.cpit252.project.service;

import org.springframework.stereotype.Service;
import sa.edu.kau.fcit.cpit252.project.composite.categories.ToolCategory;
import sa.edu.kau.fcit.cpit252.project.dto.CategoryResponse;
import sa.edu.kau.fcit.cpit252.project.entity.CategoryEntity;
import sa.edu.kau.fcit.cpit252.project.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getCategoryTree() {
        List<CategoryEntity> categories = categoryRepository.findAll().stream()
                .sorted(Comparator.comparing(CategoryEntity::getId))
                .toList();

        Map<Long, Node> nodes = new LinkedHashMap<>();
        for (CategoryEntity category : categories) {
            nodes.put(category.getId(), new Node(category));
        }

        List<Node> roots = new ArrayList<>();
        for (CategoryEntity category : categories) {
            Node current = nodes.get(category.getId());
            if (category.getParent() == null) {
                roots.add(current);
                continue;
            }

            Node parent = nodes.get(category.getParent().getId());
            if (parent != null) {
                parent.children.add(current);
                parent.compositeNode.addComponent(current.compositeNode);
            } else {
                roots.add(current);
            }
        }

        return roots.stream()
                .map(this::toResponse)
                .toList();
    }

    private CategoryResponse toResponse(Node node) {
        CategoryResponse response = new CategoryResponse();
        response.setId(node.entity.getId());
        response.setName(node.entity.getName());
        response.setSlug(node.entity.getSlug());
        response.setParentId(node.entity.getParent() == null ? null : node.entity.getParent().getId());
        response.setChildren(node.children.stream().map(this::toResponse).toList());
        return response;
    }

    private static final class Node {
        private final CategoryEntity entity;
        private final ToolCategory compositeNode;
        private final List<Node> children = new ArrayList<>();

        private Node(CategoryEntity entity) {
            this.entity = entity;
            this.compositeNode = new ToolCategory(entity.getName());
        }
    }
}
