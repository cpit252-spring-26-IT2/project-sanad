import { useLocation } from "wouter";
import { Package } from "lucide-react";
import { Button } from "@/components/ui/button";

export default function OrderDetailPage() {
  const [, setLocation] = useLocation();

  return (
    <div className="min-h-screen bg-background flex items-center justify-center px-4">
      <div className="max-w-md text-center border border-border rounded bg-card p-8">
        <Package className="h-10 w-10 text-primary mx-auto mb-3" />
        <h1 className="text-xl font-bold mb-2">Orders Prototype</h1>
        <p className="text-sm text-muted-foreground mb-4">
          Order tracking will be implemented in a future phase. This page is currently a placeholder.
        </p>
        <Button onClick={() => setLocation("/profile")}>Back to Profile</Button>
      </div>
    </div>
  );
}
