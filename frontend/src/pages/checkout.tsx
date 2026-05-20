import { useLocation } from "wouter";
import { CreditCard } from "lucide-react";
import { Button } from "@/components/ui/button";

export default function CheckoutPage() {
  const [, setLocation] = useLocation();

  return (
    <div className="min-h-screen bg-background flex items-center justify-center px-4">
      <div className="max-w-md text-center border border-border rounded bg-card p-8">
        <CreditCard className="h-10 w-10 text-primary mx-auto mb-3" />
        <h1 className="text-xl font-bold mb-2">Checkout Coming Soon</h1>
        <p className="text-sm text-muted-foreground mb-4">
          Secure checkout and order processing are outside this integration scope.
        </p>
        <Button onClick={() => setLocation("/shop")}>Continue Browsing</Button>
      </div>
    </div>
  );
}
