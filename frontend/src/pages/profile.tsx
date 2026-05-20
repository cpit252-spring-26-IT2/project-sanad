import { useLocation } from "wouter";
import { useQuery } from "@tanstack/react-query";
import { User, Store } from "lucide-react";
import { api } from "@/lib/api";
import { useAuth } from "@/lib/auth-context";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { Badge } from "@/components/ui/badge";

export default function ProfilePage() {
  const [, setLocation] = useLocation();
  const { isAuthenticated, token, user } = useAuth();

  const { data: me, isLoading } = useQuery({
    queryKey: ["me", token],
    queryFn: () => api.me(token as string),
    enabled: !!token
  });

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <User className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
          <p className="text-muted-foreground mb-4">Please login to view your profile</p>
          <Button onClick={() => setLocation("/login")}>Login</Button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
        <h1 className="text-2xl font-bold text-foreground">My Profile</h1>

        <div className="border border-border rounded bg-card p-5">
          {isLoading ? (
            <div className="space-y-3">
              <Skeleton className="h-6 w-1/3" />
              <Skeleton className="h-4 w-1/2" />
            </div>
          ) : (
            <div>
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-center gap-3">
                  <div className="h-12 w-12 rounded bg-primary/10 flex items-center justify-center">
                    {me?.role === "SHOP_OWNER" ? (
                      <Store className="h-6 w-6 text-primary" />
                    ) : (
                      <User className="h-6 w-6 text-primary" />
                    )}
                  </div>
                  <div>
                    <p className="font-bold text-foreground">{me?.name ?? user?.name}</p>
                    <p className="text-sm text-muted-foreground">{me?.email ?? user?.email}</p>
                  </div>
                </div>
                <Badge variant="outline" className="capitalize text-xs">
                  {(me?.role ?? user?.role)?.toLowerCase().replace("_", " ")}
                </Badge>
              </div>
              <p className="text-sm text-muted-foreground">
                Profile editing and order history are prototype-only in this integration build.
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
