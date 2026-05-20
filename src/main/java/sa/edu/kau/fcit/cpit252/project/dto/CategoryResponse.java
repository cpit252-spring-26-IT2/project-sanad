package sa.edu.kau.fcit.cpit252.project.dto;

import java.util.ArrayList;
import java.util.List;

public class CategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private Long parentId;
    private List<CategoryResponse> children = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public List<CategoryResponse> getChildren() { return children; }
    public void setChildren(List<CategoryResponse> children) { this.children = children; }
}
