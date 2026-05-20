package sa.edu.kau.fcit.cpit252.project.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CategoryEntity parent;

    @Column(nullable = false, unique = true)
    private String slug;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public CategoryEntity getParent() { return parent; }
    public void setParent(CategoryEntity parent) { this.parent = parent; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
}
