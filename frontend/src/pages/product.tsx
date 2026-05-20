import { useRoute, useLocation } from "wouter";
import { ArrowLeft, Star } from "lucide-react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { api, useCreateReview, useProduct, useProductOffers, useReviewSummary } from "@/lib/api";
import { useAuth } from "@/lib/auth-context";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { Badge } from "@/components/ui/badge";
import { useToast } from "@/hooks/use-toast";

function StarRating({ rating }: { rating: number }) {
  return (
    <div className="flex items-center gap-0.5">
      {[1, 2, 3, 4, 5].map((s) => (
        <Star key={s} className={`h-4 w-4 ${s <= Math.round(rating) ? "text-primary fill-primary" : "text-muted-foreground"}`} />
      ))}
    </div>
  );
}

export default function ProductPage() {
  const [, params] = useRoute("/product/:id");
  const [, setLocation] = useLocation();
  const { isAuthenticated, token } = useAuth();
  const { toast } = useToast();
  const queryClient = useQueryClient();

  const [sort, setSort] = useState<"price_asc" | "price_desc">("price_asc");
  const [selectedRating, setSelectedRating] = useState(5);

  const productId = params?.id ? parseInt(params.id, 10) : 0;
  const { data: product, isLoading } = useProduct(productId);
  const { data: offers, isLoading: offersLoading } = useProductOffers(productId, sort);
  const { data: summary } = useReviewSummary("PRODUCT", productId);

  const submitReview = useCreateReview();

  const compareMutation = useMutation({
    mutationFn: (nextSort: "price_asc" | "price_desc") => api.compare(productId, nextSort),
    onSuccess: (data, nextSort) => {
      queryClient.setQueryData(["product-offers", productId, nextSort], data);
    }
  });

  const handleReview = () => {
    if (!isAuthenticated || !token) {
      toast({ title: "Please login first", description: "You need an account to rate products." });
      setLocation("/login");
      return;
    }

    submitReview.mutate(
      { token, targetType: "PRODUCT", targetId: productId, rating: selectedRating },
      {
        onSuccess: () => {
          toast({ title: "Thanks for your rating" });
          queryClient.invalidateQueries({ queryKey: ["review-summary", "PRODUCT", productId] });
          queryClient.invalidateQueries({ queryKey: ["products"] });
        },
        onError: () => {
          toast({ title: "Could not submit rating", variant: "destructive" });
        }
      }
    );
  };

  const handleSortChange = (nextSort: "price_asc" | "price_desc") => {
    setSort(nextSort);
    compareMutation.mutate(nextSort);
  };

  if (isLoading) {
    return (
      <div className="max-w-5xl mx-auto px-4 py-8">
        <div className="grid md:grid-cols-2 gap-8">
          <Skeleton className="aspect-square rounded" />
          <div className="space-y-4">
            <Skeleton className="h-8 w-3/4" />
            <Skeleton className="h-4 w-1/2" />
            <Skeleton className="h-6 w-1/4" />
            <Skeleton className="h-24 w-full" />
          </div>
        </div>
      </div>
    );
  }

  if (!product) {
    return (
      <div className="max-w-5xl mx-auto px-4 py-20 text-center">
        <p className="text-muted-foreground">Product not found.</p>
        <Button variant="ghost" onClick={() => setLocation("/shop")} className="mt-4">
          <ArrowLeft className="h-4 w-4 mr-1" /> Back to Shop
        </Button>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Button variant="ghost" size="sm" onClick={() => setLocation("/shop")} className="mb-6" data-testid="link-back-shop">
          <ArrowLeft className="h-4 w-4 mr-1" /> Back to Shop
        </Button>

        <div className="grid md:grid-cols-2 gap-10">
          <div className="aspect-square bg-muted rounded flex items-center justify-center border border-border overflow-hidden" data-testid="img-product">
            {product.imageUrl ? (
              <img src={product.imageUrl} alt={product.name} className="w-full h-full object-cover" />
            ) : (
              <span className="text-8xl">🔧</span>
            )}
          </div>

          <div className="space-y-5">
            <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
              <span>{product.categoryName}</span>
              <span>›</span>
              <span className="text-foreground font-medium">{product.variant || "Standard"}</span>
            </div>

            <h1 className="text-2xl font-bold text-foreground" data-testid="text-product-name">{product.name}</h1>

            <div className="flex flex-wrap gap-2">
              <Badge variant="secondary">{product.categoryName}</Badge>
              {product.variant && <Badge variant="outline">{product.variant}</Badge>}
              <Badge variant={product.available ? "outline" : "destructive"}>
                {product.available ? "Available" : "Unavailable"}
              </Badge>
            </div>

            <div className="flex items-center gap-2">
              <StarRating rating={summary?.averageRating ?? product.rating} />
              <span className="text-sm font-medium">{(summary?.averageRating ?? product.rating).toFixed(1)}</span>
              <span className="text-sm text-muted-foreground">
                ({summary?.totalReviews ?? product.reviewCount} reviews)
              </span>
            </div>

            <div className="text-3xl font-bold text-primary" data-testid="text-product-price">
              SAR {product.bestPrice.toFixed(2)}
            </div>

            {product.description && <p className="text-sm text-muted-foreground leading-relaxed">{product.description}</p>}

            <p className="text-sm text-muted-foreground">Stock: {product.stockQuantity}</p>
          </div>
        </div>

        <section className="mt-10 border border-border rounded bg-card p-5">
          <div className="flex items-center justify-between mb-4">
            <h2 className="font-bold text-foreground">Price Comparison</h2>
            <div className="flex gap-2">
              <Button
                variant={sort === "price_asc" ? "default" : "outline"}
                size="sm"
                onClick={() => handleSortChange("price_asc")}
              >
                Lowest First
              </Button>
              <Button
                variant={sort === "price_desc" ? "default" : "outline"}
                size="sm"
                onClick={() => handleSortChange("price_desc")}
              >
                Highest First
              </Button>
            </div>
          </div>

          {offersLoading ? (
            <div className="space-y-2">
              <Skeleton className="h-16 rounded" />
              <Skeleton className="h-16 rounded" />
            </div>
          ) : (
            <div className="space-y-2">
              {offers?.map((offer) => (
                <div key={offer.id} className="border border-border rounded p-3 flex items-center justify-between">
                  <div>
                    <p className="text-sm font-semibold">{offer.shopName}</p>
                    <p className="text-xs text-muted-foreground">Stock: {offer.stockQuantity}</p>
                  </div>
                  <div className="text-right">
                    <p className="font-bold text-primary">SAR {offer.price.toFixed(2)}</p>
                    <p className="text-xs text-muted-foreground">{offer.available ? "Available" : "Unavailable"}</p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>

        <section className="mt-6 border border-border rounded bg-card p-5">
          <h2 className="font-bold text-foreground mb-3">Rate this Product</h2>
          <div className="flex gap-1 mb-4">
            {[1, 2, 3, 4, 5].map((rating) => (
              <button key={rating} onClick={() => setSelectedRating(rating)}>
                <Star className={`h-6 w-6 ${rating <= selectedRating ? "text-primary fill-primary" : "text-muted-foreground"}`} />
              </button>
            ))}
          </div>
          <Button onClick={handleReview} disabled={submitReview.isPending}>
            {submitReview.isPending ? "Submitting..." : "Submit Rating"}
          </Button>
        </section>
      </div>
    </div>
  );
}
