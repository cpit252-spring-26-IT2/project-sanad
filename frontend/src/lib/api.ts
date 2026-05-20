import { useMutation, useQuery } from "@tanstack/react-query";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "";

export type UserRole = "CUSTOMER" | "SHOP_OWNER";

export interface AuthUser {
  id: number;
  name: string;
  email: string;
  role: UserRole;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  user: AuthUser;
}

export interface CategoryNode {
  id: number;
  name: string;
  slug: string;
  parentId: number | null;
  children: CategoryNode[];
}

export interface ProductListItem {
  id: number;
  name: string;
  variant?: string | null;
  description?: string | null;
  categoryName: string;
  categorySlug: string;
  imageUrl?: string | null;
  bestOfferShop: string;
  price: number;
  available: boolean;
  stockQuantity: number;
  rating: number;
  reviewCount: number;
}

export interface PagedResponse<T> {
  items: T[];
  page: number;
  limit: number;
  total: number;
}

export interface ProductDetail {
  id: number;
  name: string;
  variant?: string | null;
  description?: string | null;
  categoryName: string;
  categorySlug: string;
  imageUrl?: string | null;
  bestPrice: number;
  available: boolean;
  stockQuantity: number;
  rating: number;
  reviewCount: number;
}

export interface ProductOffer {
  id: number;
  productId: number;
  productName: string;
  variant?: string | null;
  shopId: number;
  shopName: string;
  price: number;
  available: boolean;
  stockQuantity: number;
}

export type ReviewTargetType = "PRODUCT" | "SHOP";

export interface ReviewSummary {
  targetType: ReviewTargetType;
  targetId: number;
  averageRating: number;
  totalReviews: number;
  visualRating: string;
}

export interface ProductFilters {
  search?: string;
  category?: string;
  minPrice?: number;
  maxPrice?: number;
  availableOnly?: boolean;
  minRating?: number;
  sort?: "price_asc" | "price_desc" | "name_asc" | "newest";
  page?: number;
  limit?: number;
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(init?.headers ?? {})
    }
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || `Request failed with ${response.status}`);
  }

  return (await response.json()) as T;
}

export const api = {
  login: (payload: { email: string; password: string }) =>
    request<AuthResponse>("/api/auth/login", {
      method: "POST",
      body: JSON.stringify(payload)
    }),

  register: (payload: {
    name: string;
    email: string;
    password: string;
    role: UserRole;
    shopName?: string;
    shopCategory?: string;
  }) =>
    request<AuthResponse>("/api/auth/register", {
      method: "POST",
      body: JSON.stringify(payload)
    }),

  me: (token: string) =>
    request<AuthUser>("/api/auth/me", {
      headers: { Authorization: `Bearer ${token}` }
    }),

  categories: () => request<CategoryNode[]>("/api/categories"),

  products: (filters: ProductFilters) => {
    const params = new URLSearchParams();
    Object.entries(filters).forEach(([key, value]) => {
      if (value === undefined || value === null || value === "") return;
      params.set(key, String(value));
    });
    return request<PagedResponse<ProductListItem>>(`/api/products?${params.toString()}`);
  },

  product: (id: number) => request<ProductDetail>(`/api/products/${id}`),

  productOffers: (id: number, sort: "price_asc" | "price_desc" = "price_asc") =>
    request<ProductOffer[]>(`/api/products/${id}/offers?sort=${sort}`),

  compare: (productId: number, sort: "price_asc" | "price_desc" = "price_asc") =>
    request<ProductOffer[]>(`/api/compare?productId=${productId}&sort=${sort}`),

  createReview: (
    token: string,
    payload: { targetType: ReviewTargetType; targetId: number; rating: number }
  ) =>
    request<ReviewSummary>("/api/reviews", {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
      body: JSON.stringify(payload)
    }),

  reviewSummary: (targetType: ReviewTargetType, targetId: number) =>
    request<ReviewSummary>(`/api/reviews/summary?targetType=${targetType}&targetId=${targetId}`)
};

export function useCategories() {
  return useQuery({
    queryKey: ["categories"],
    queryFn: api.categories
  });
}

export function useProducts(filters: ProductFilters) {
  return useQuery({
    queryKey: ["products", filters],
    queryFn: () => api.products(filters)
  });
}

export function useProduct(id: number) {
  return useQuery({
    queryKey: ["product", id],
    queryFn: () => api.product(id),
    enabled: id > 0
  });
}

export function useProductOffers(id: number, sort: "price_asc" | "price_desc") {
  return useQuery({
    queryKey: ["product-offers", id, sort],
    queryFn: () => api.productOffers(id, sort),
    enabled: id > 0
  });
}

export function useReviewSummary(targetType: ReviewTargetType, targetId: number) {
  return useQuery({
    queryKey: ["review-summary", targetType, targetId],
    queryFn: () => api.reviewSummary(targetType, targetId),
    enabled: targetId > 0
  });
}

export function useCreateReview() {
  return useMutation({
    mutationFn: ({
      token,
      targetType,
      targetId,
      rating
    }: {
      token: string;
      targetType: ReviewTargetType;
      targetId: number;
      rating: number;
    }) => api.createReview(token, { targetType, targetId, rating })
  });
}

export function flattenCategories(categories: CategoryNode[]): CategoryNode[] {
  const all: CategoryNode[] = [];
  const visit = (node: CategoryNode) => {
    all.push(node);
    node.children.forEach(visit);
  };
  categories.forEach(visit);
  return all;
}
