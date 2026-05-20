import { useLocation } from "wouter";
import { useState, useEffect } from "react";
import { Search, Star } from "lucide-react";
import { flattenCategories, useCategories, useProducts } from "@/lib/api";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";

function StarRating({ rating }: { rating: number }) {
  return (
    <div className="flex items-center gap-0.5">
      {[1, 2, 3, 4, 5].map((s) => (
        <Star key={s} className={`h-3 w-3 ${s <= Math.round(rating) ? "text-primary fill-primary" : "text-muted"}`} />
      ))}
    </div>
  );
}

export default function ShopPage() {
  const [, setLocation] = useLocation();

  const urlParams = new URLSearchParams(window.location.search);
  const [search, setSearch] = useState(urlParams.get("search") ?? "");
  const [debouncedSearch, setDebouncedSearch] = useState(search);
  const [category, setCategory] = useState<string | null>(urlParams.get("category"));
  const [minPrice, setMinPrice] = useState<number | null>(null);
  const [maxPrice, setMaxPrice] = useState<number | null>(null);
  const [sort, setSort] = useState<"price_asc" | "price_desc" | "name_asc" | "newest">("newest");
  const [page, setPage] = useState(1);

  useEffect(() => {
    const t = setTimeout(() => setDebouncedSearch(search), 300);
    return () => clearTimeout(t);
  }, [search]);

  const { data: categories } = useCategories();
  const allCategories = categories ? flattenCategories(categories) : [];

  const { data: productData, isLoading } = useProducts({
    search: debouncedSearch || undefined,
    category: category ?? undefined,
    minPrice: minPrice ?? undefined,
    maxPrice: maxPrice ?? undefined,
    sort,
    page,
    limit: 12
  });

  const totalPages = productData ? Math.ceil(productData.total / 12) : 0;

  return (
    <div className="min-h-screen bg-background">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
        <div className="flex flex-col sm:flex-row gap-3 mb-6">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input
              type="search"
              placeholder="Search products..."
              value={search}
              onChange={(e) => {
                setSearch(e.target.value);
                setPage(1);
              }}
              className="pl-9"
              data-testid="input-search"
            />
          </div>
          <div className="flex gap-2">
            <select
              value={sort}
              onChange={(e) => setSort(e.target.value as typeof sort)}
              className="px-3 py-2 text-sm border border-border rounded bg-background text-foreground"
              data-testid="select-sort"
            >
              <option value="newest">Newest</option>
              <option value="price_asc">Price: Low to High</option>
              <option value="price_desc">Price: High to Low</option>
              <option value="name_asc">Name A-Z</option>
            </select>
          </div>
        </div>

        <div className="flex gap-6">
          <aside className="w-full sm:w-[30%] shrink-0 space-y-5">
            <div className="border border-border rounded bg-card p-4">
              <h3 className="font-semibold text-sm mb-3 text-foreground">Categories</h3>
              <div className="space-y-1">
                <button
                  onClick={() => {
                    setCategory(null);
                    setPage(1);
                  }}
                  className={`w-full text-left px-2 py-1.5 text-sm rounded transition-colors ${
                    category === null ? "bg-primary text-primary-foreground font-medium" : "hover:bg-accent text-foreground"
                  }`}
                  data-testid="filter-category-all"
                >
                  All Products
                </button>
                {allCategories.map((cat) => (
                  <button
                    key={cat.id}
                    onClick={() => {
                      setCategory(cat.slug);
                      setPage(1);
                    }}
                    className={`w-full text-left px-2 py-1.5 text-sm rounded transition-colors ${
                      category === cat.slug ? "bg-primary text-primary-foreground font-medium" : "hover:bg-accent text-foreground"
                    }`}
                    data-testid={`filter-category-${cat.id}`}
                  >
                    {cat.name}
                  </button>
                ))}
              </div>
            </div>

            <div className="border border-border rounded bg-card p-4">
              <h3 className="font-semibold text-sm mb-3 text-foreground">Price Range (SAR)</h3>
              <div className="flex gap-2 items-center">
                <Input
                  type="number"
                  placeholder="Min"
                  value={minPrice ?? ""}
                  onChange={(e) => {
                    setMinPrice(e.target.value ? Number(e.target.value) : null);
                    setPage(1);
                  }}
                  className="text-sm h-8"
                  data-testid="input-price-min"
                />
                <span className="text-muted-foreground text-sm">-</span>
                <Input
                  type="number"
                  placeholder="Max"
                  value={maxPrice ?? ""}
                  onChange={(e) => {
                    setMaxPrice(e.target.value ? Number(e.target.value) : null);
                    setPage(1);
                  }}
                  className="text-sm h-8"
                  data-testid="input-price-max"
                />
              </div>
              {(minPrice || maxPrice || category) && (
                <Button
                  variant="ghost"
                  size="sm"
                  className="mt-2 h-7 text-xs"
                  onClick={() => {
                    setCategory(null);
                    setMinPrice(null);
                    setMaxPrice(null);
                    setSearch("");
                    setPage(1);
                  }}
                  data-testid="button-clear-filters"
                >
                  Clear all filters
                </Button>
              )}
            </div>
          </aside>

          <main className="flex-1">
            <div className="flex items-center justify-between mb-4">
              <p className="text-sm text-muted-foreground">{productData ? `${productData.total} products` : "Loading..."}</p>
            </div>

            {isLoading ? (
              <div className="grid grid-cols-2 lg:grid-cols-3 gap-4">
                {Array.from({ length: 9 }).map((_, i) => (
                  <Skeleton key={i} className="h-64 rounded" />
                ))}
              </div>
            ) : productData?.items.length === 0 ? (
              <div className="text-center py-20">
                <p className="text-muted-foreground text-sm">No products found. Try adjusting your filters.</p>
              </div>
            ) : (
              <>
                <div className="grid grid-cols-2 lg:grid-cols-3 gap-4">
                  {productData?.items.map((product) => (
                    <div
                      key={product.id}
                      className="border border-border rounded bg-card hover:border-primary hover:shadow-md transition-all cursor-pointer group flex flex-col"
                      onClick={() => setLocation(`/product/${product.id}`)}
                      data-testid={`card-product-${product.id}`}
                    >
                      <div className="aspect-square bg-muted flex items-center justify-center rounded-t overflow-hidden">
                        {product.imageUrl ? (
                          <img src={product.imageUrl} alt={product.name} className="w-full h-full object-cover" />
                        ) : (
                          <span className="text-4xl">🔧</span>
                        )}
                      </div>
                      <div className="p-3 flex flex-col flex-1">
                        <p className="text-xs text-muted-foreground mb-0.5">{product.categoryName}</p>
                        <h3 className="text-sm font-semibold text-foreground line-clamp-2 group-hover:text-primary flex-1">{product.name}</h3>
                        <p className="text-xs text-muted-foreground mt-1">Shop: {product.bestOfferShop}</p>
                        <div className="flex items-center gap-1 mt-1">
                          <StarRating rating={product.rating} />
                          <span className="text-xs text-muted-foreground">({product.reviewCount})</span>
                        </div>
                        <div className="flex items-center justify-between mt-2">
                          <p className="text-primary font-bold" data-testid={`text-price-${product.id}`}>
                            SAR {product.price.toFixed(2)}
                          </p>
                          <Button size="sm" className="h-7 text-xs px-2" data-testid={`button-view-${product.id}`}>
                            View
                          </Button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>

                {totalPages > 1 && (
                  <div className="flex items-center justify-center gap-2 mt-8">
                    <Button
                      variant="outline"
                      size="sm"
                      disabled={page === 1}
                      onClick={() => setPage(page - 1)}
                      data-testid="button-prev-page"
                    >
                      Previous
                    </Button>
                    <span className="text-sm text-muted-foreground">
                      Page {page} of {totalPages}
                    </span>
                    <Button
                      variant="outline"
                      size="sm"
                      disabled={page === totalPages}
                      onClick={() => setPage(page + 1)}
                      data-testid="button-next-page"
                    >
                      Next
                    </Button>
                  </div>
                )}
              </>
            )}
          </main>
        </div>
      </div>
    </div>
  );
}
