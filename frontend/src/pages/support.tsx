import { Mail } from "lucide-react";

export default function SupportPage() {
  return (
    <div className="min-h-screen bg-background flex items-center justify-center px-4">
      <div className="max-w-md text-center border border-border rounded bg-card p-8">
        <Mail className="h-10 w-10 text-primary mx-auto mb-3" />
        <h1 className="text-xl font-bold mb-2">Support Prototype</h1>
        <p className="text-sm text-muted-foreground mb-2">
          Messaging and support workflows are pending and not connected to backend APIs yet.
        </p>
        <p className="text-sm text-muted-foreground">Contact: support@sanad.sa</p>
      </div>
    </div>
  );
}
