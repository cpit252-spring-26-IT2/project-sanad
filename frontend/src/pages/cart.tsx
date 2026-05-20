import { useLocation } from "wouter";
import { ShoppingCart } from "lucide-react";
import { Button } from "@/components/ui/button";

export default function CartPage() {
  const [, setLocation] = useLocation();

  return (
    <div className="min-h-screen bg-background flex items-center justify-center px-4">
      <div className="max-w-md text-center border border-border rounded bg-card p-8">
        <ShoppingCart className="h-10 w-10 text-primary mx-auto mb-3" />
        <h1 className="text-xl font-bold mb-2">Cart Prototype</h1>
        <p className="text-sm text-muted-foreground mb-4">
          Cart and checkout are planned for a later milestone. Browse products and compare offers for now.
        </p>
        <Button onClick={() => setLocation("/shop")}>Back to Shop</Button>
      </div>
    </div>
  );
}
