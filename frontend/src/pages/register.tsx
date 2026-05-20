import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useLocation, Link } from "wouter";
import { Hammer } from "lucide-react";
import { useMutation } from "@tanstack/react-query";
import { api } from "@/lib/api";
import { useAuth } from "@/lib/auth-context";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useToast } from "@/hooks/use-toast";

const schema = z
  .object({
    name: z.string().min(2, "Name must be at least 2 characters"),
    email: z.string().email("Enter a valid email"),
    password: z.string().min(6, "Password must be at least 6 characters"),
    confirmPassword: z.string(),
    role: z.enum(["CUSTOMER", "SHOP_OWNER"]),
    shopName: z.string().optional(),
    shopCategory: z.string().optional()
  })
  .refine((d) => d.password === d.confirmPassword, {
    message: "Passwords do not match",
    path: ["confirmPassword"]
  });

type FormData = z.infer<typeof schema>;

export default function RegisterPage() {
  const [, setLocation] = useLocation();
  const { login } = useAuth();
  const { toast } = useToast();
  const registerMutation = useMutation({ mutationFn: api.register });

  const form = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: {
      name: "",
      email: "",
      password: "",
      confirmPassword: "",
      role: "CUSTOMER",
      shopName: "",
      shopCategory: ""
    }
  });

  const role = form.watch("role");

  const onSubmit = (data: FormData) => {
    registerMutation.mutate(
      {
        name: data.name,
        email: data.email,
        password: data.password,
        role: data.role,
        shopName: data.role === "SHOP_OWNER" ? data.shopName : undefined,
        shopCategory: data.role === "SHOP_OWNER" ? data.shopCategory : undefined
      },
      {
        onSuccess: (res) => {
          login(res.user, res.token);
          toast({ title: "Account created", description: `Welcome to Sanad, ${res.user.name}` });
          setLocation("/");
        },
        onError: () => {
          toast({ title: "Registration failed", description: "Please verify your details.", variant: "destructive" });
        }
      }
    );
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
          <h1 className="text-2xl font-bold text-foreground">Create your account</h1>
          <p className="text-muted-foreground mt-1 text-sm">Join Sanad and start comparing offers</p>
        </div>

        <div className="border border-border rounded bg-card p-6 shadow-sm">
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <Label htmlFor="name">Full Name</Label>
              <Input id="name" placeholder="Your Name" {...form.register("name")} className="mt-1" />
              {form.formState.errors.name && <p className="text-destructive text-xs mt-1">{form.formState.errors.name.message}</p>}
            </div>

            <div>
              <Label htmlFor="email">Email</Label>
              <Input id="email" type="email" placeholder="you@example.com" {...form.register("email")} className="mt-1" />
              {form.formState.errors.email && <p className="text-destructive text-xs mt-1">{form.formState.errors.email.message}</p>}
            </div>

            <div>
              <Label htmlFor="role">Account Type</Label>
              <select id="role" {...form.register("role")} className="mt-1 w-full px-3 py-2 text-sm border border-input rounded bg-background text-foreground">
                <option value="CUSTOMER">Customer</option>
                <option value="SHOP_OWNER">Shop Owner</option>
              </select>
            </div>

            {role === "SHOP_OWNER" && (
              <>
                <div>
                  <Label htmlFor="shopName">Shop Name</Label>
                  <Input id="shopName" placeholder="Toney Flooring" {...form.register("shopName")} className="mt-1" />
                </div>
                <div>
                  <Label htmlFor="shopCategory">Shop Category</Label>
                  <Input id="shopCategory" placeholder="Flooring" {...form.register("shopCategory")} className="mt-1" />
                </div>
              </>
            )}

            <div>
              <Label htmlFor="password">Password</Label>
              <Input id="password" type="password" placeholder="••••••••" {...form.register("password")} className="mt-1" />
              {form.formState.errors.password && <p className="text-destructive text-xs mt-1">{form.formState.errors.password.message}</p>}
            </div>

            <div>
              <Label htmlFor="confirmPassword">Confirm Password</Label>
              <Input id="confirmPassword" type="password" placeholder="••••••••" {...form.register("confirmPassword")} className="mt-1" />
              {form.formState.errors.confirmPassword && (
                <p className="text-destructive text-xs mt-1">{form.formState.errors.confirmPassword.message}</p>
              )}
            </div>

            <Button type="submit" className="w-full" disabled={registerMutation.isPending}>
              {registerMutation.isPending ? "Creating account..." : "Create account"}
            </Button>
          </form>

          <p className="text-center text-sm text-muted-foreground mt-4">
            Already have an account? <Link href="/login" className="text-primary font-medium hover:underline">Sign in</Link>
          </p>
        </div>
      </div>
    </div>
  );
}
