import { useLocation } from "wouter";
import { Search, ArrowRight, Star } from "lucide-react";
import { useState } from "react";
import { useCategories, useProducts } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Skeleton } from "@/components/ui/skeleton";

function StarRating({ rating }: { rating: number }) {
  return (
    <div className="flex items-center gap-0.5">
      {[1, 2, 3, 4, 5].map((s) => (
        <Star
          key={s}
          className={`h-3 w-3 ${s <= Math.round(rating) ? "text-primary fill-primary" : "text-muted-foreground"}`}
        />
      ))}
    </div>
  );
}

export default function HomePage() {
  const [, setLocation] = useLocation();
  const [search, setSearch] = useState("");
  const { data: productData, isLoading: featuredLoading } = useProducts({ limit: 8, sort: "price_asc" });
  const { data: categories } = useCategories();

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (search.trim()) {
      setLocation(`/shop?search=${encodeURIComponent(search.trim())}`);
    } else {
      setLocation("/shop");
    }
  };

  return (
    <div className="min-h-screen bg-background">
      <section className="bg-foreground text-background py-16 px-4">
        <div className="max-w-3xl mx-auto text-center">
          <h1 className="text-4xl md:text-5xl font-bold mb-3 tracking-tight">Sanad سند</h1>
          <p className="text-background/70 mb-8 text-lg">
            Compare building material prices across trusted suppliers in Saudi Arabia.
          </p>
          <form onSubmit={handleSearch} className="flex gap-2 max-w-xl mx-auto">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-foreground/40" />
              <Input
                type="search"
                placeholder="Search products..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="pl-9 bg-background text-foreground border-0 h-11"
                data-testid="input-search"
              />
            </div>
            <Button type="submit" size="lg" variant="default" data-testid="button-search">
              Search
            </Button>
          </form>
        </div>
      </section>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-12">
        <section>
          <h2 className="text-sm font-semibold text-muted-foreground uppercase tracking-wider mb-4">Browse by Category</h2>
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3">
            {categories?.map((cat) => (
              <button
                key={cat.id}
                onClick={() => setLocation(`/shop?category=${cat.slug}`)}
                className="flex flex-col items-start gap-2 p-4 border border-border rounded bg-card hover:border-primary hover:bg-accent/20 transition-colors"
                data-testid={`button-category-${cat.slug}`}
              >
                <span className="text-sm font-semibold text-foreground">{cat.name}</span>
                <span className="text-xs text-muted-foreground">Explore offers</span>
              </button>
            ))}
          </div>
        </section>

        <section>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-bold text-foreground">Featured Offers</h2>
            <Button variant="ghost" size="sm" onClick={() => setLocation("/shop")} data-testid="link-view-all">
              View all <ArrowRight className="ml-1 h-4 w-4" />
            </Button>
          </div>

          {featuredLoading ? (
            <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
              {Array.from({ length: 8 }).map((_, i) => (
                <Skeleton key={i} className="h-56 rounded" />
              ))}
            </div>
          ) : (
            <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
              {productData?.items.map((product) => (
                <div
                  key={product.id}
                  onClick={() => setLocation(`/product/${product.id}`)}
                  className="border border-border rounded bg-card hover:border-primary hover:shadow-md transition-all cursor-pointer group"
                  data-testid={`card-product-${product.id}`}
                >
                  <div className="aspect-square bg-muted flex items-center justify-center rounded-t overflow-hidden">
                    {product.imageUrl ? (
                      <img src={product.imageUrl} alt={product.name} className="w-full h-full object-cover" />
                    ) : (
                      <span className="text-4xl">🔧</span>
                    )}
                  </div>
                  <div className="p-3">
                    <p className="text-xs text-muted-foreground mb-1">{product.categoryName}</p>
                    <h3 className="text-sm font-semibold text-foreground line-clamp-2 group-hover:text-primary">{product.name}</h3>
                    <p className="text-xs text-muted-foreground mt-1">Best shop: {product.bestOfferShop}</p>
                    <div className="flex items-center gap-1 mt-1">
                      <StarRating rating={product.rating} />
                      <span className="text-xs text-muted-foreground">({product.reviewCount})</span>
                    </div>
                    <p className="text-primary font-bold mt-2" data-testid={`text-price-${product.id}`}>
                      SAR {product.price.toFixed(2)}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>
      </div>
    </div>
  );
}
