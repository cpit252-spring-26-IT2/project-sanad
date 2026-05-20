import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useLocation, Link } from "wouter";
import { Hammer, Shield, User } from "lucide-react";
import { useMutation } from "@tanstack/react-query";
import { api } from "@/lib/api";
import { useAuth } from "@/lib/auth-context";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useToast } from "@/hooks/use-toast";

const schema = z.object({
  email: z.string().email("Enter a valid email"),
  password: z.string().min(6, "Password must be at least 6 characters")
});

type FormData = z.infer<typeof schema>;

export default function LoginPage() {
  const [, setLocation] = useLocation();
  const { login } = useAuth();
  const { toast } = useToast();

  const loginMutation = useMutation({ mutationFn: api.login });

  const form = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { email: "", password: "" }
  });

  const onSubmit = (data: FormData) => {
    loginMutation.mutate(data, {
      onSuccess: (res) => {
        login(res.user, res.token);
        toast({ title: `Welcome back, ${res.user.name}`, description: `Logged in as ${res.user.role}` });
        setLocation("/");
      },
      onError: () => {
        toast({ title: "Login failed", description: "Invalid email or password", variant: "destructive" });
      }
    });
  };

  return (
    <div className="min-h-screen bg-background flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <div className="flex justify-center mb-4">
            <div className="h-14 w-14 rounded bg-primary flex items-center justify-center">
              <Hammer className="h-8 w-8 text-primary-foreground" />
            </div>
          </div>
          <h1 className="text-2xl font-bold text-foreground">Welcome to Sanad</h1>
          <p className="text-muted-foreground mt-1 text-sm">Sign in to your account</p>
        </div>

        <div className="border border-border rounded bg-card p-6 shadow-sm">
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <Label htmlFor="email">Email</Label>
              <Input id="email" type="email" placeholder="you@example.com" {...form.register("email")} className="mt-1" />
              {form.formState.errors.email && <p className="text-destructive text-xs mt-1">{form.formState.errors.email.message}</p>}
            </div>

            <div>
              <Label htmlFor="password">Password</Label>
              <Input id="password" type="password" placeholder="••••••••" {...form.register("password")} className="mt-1" />
              {form.formState.errors.password && <p className="text-destructive text-xs mt-1">{form.formState.errors.password.message}</p>}
            </div>

            <Button type="submit" className="w-full" disabled={loginMutation.isPending}>
              {loginMutation.isPending ? "Signing in..." : "Sign in"}
            </Button>
          </form>

          <p className="text-center text-sm text-muted-foreground mt-4">
            Don't have an account?{" "}
            <Link href="/register" className="text-primary font-medium hover:underline">Create one</Link>
          </p>
        </div>

        <div className="mt-6 border border-border rounded bg-muted/40 p-4 space-y-3">
          <p className="text-xs font-semibold text-muted-foreground uppercase tracking-wide">Demo Accounts</p>
          <div className="flex gap-3">
            <div className="flex-1 bg-card border border-border rounded p-3">
              <div className="flex items-center gap-1.5 mb-1">
                <User className="h-3 w-3 text-primary" />
                <span className="text-xs font-semibold">Customer</span>
              </div>
              <p className="text-xs text-muted-foreground">customer@sanad.sa</p>
              <p className="text-xs text-muted-foreground">customer123</p>
            </div>
            <div className="flex-1 bg-card border border-border rounded p-3">
              <div className="flex items-center gap-1.5 mb-1">
                <Shield className="h-3 w-3 text-primary" />
                <span className="text-xs font-semibold">Shop Owner</span>
              </div>
              <p className="text-xs text-muted-foreground">toney@sanad.sa</p>
              <p className="text-xs text-muted-foreground">shop123</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
