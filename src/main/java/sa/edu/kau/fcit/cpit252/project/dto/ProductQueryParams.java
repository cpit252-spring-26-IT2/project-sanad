package sa.edu.kau.fcit.cpit252.project.dto;

public class ProductQueryParams {
    private String search;
    private String category;
    private Double minPrice;
    private Double maxPrice;
    private Boolean availableOnly;
    private Double minRating;
    private String sort;
    private int page;
    private int limit;

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getMinPrice() { return minPrice; }
    public void setMinPrice(Double minPrice) { this.minPrice = minPrice; }
    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }
    public Boolean getAvailableOnly() { return availableOnly; }
    public void setAvailableOnly(Boolean availableOnly) { this.availableOnly = availableOnly; }
    public Double getMinRating() { return minRating; }
    public void setMinRating(Double minRating) { this.minRating = minRating; }
    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
}
