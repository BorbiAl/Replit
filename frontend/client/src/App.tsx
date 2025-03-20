import { Switch, Route } from 'wouter';
import { QueryClientProvider } from '@tanstack/react-query';
import { queryClient } from './lib/queryClient';
import { AuthProvider } from './hooks/use-auth';
import { ProtectedRoute } from './lib/protected-route';
import { Toaster } from '@/components/ui/toaster';

// Pages
function HomePage() {
  return (
    <div className="container mx-auto p-4">
      <h1 className="text-3xl font-bold">StudyQuest</h1>
      <p>Welcome to your personalized study dashboard!</p>
    </div>
  );
}

function AuthPage() {
  return (
    <div className="container mx-auto p-4">
      <h1 className="text-3xl font-bold">Login or Register</h1>
      <p>Authentication forms will go here.</p>
    </div>
  );
}

function NotFound() {
  return (
    <div className="container mx-auto p-4 flex flex-col items-center justify-center min-h-screen">
      <h1 className="text-4xl font-bold">404</h1>
      <p className="text-lg">Page not found</p>
    </div>
  );
}

function Router() {
  return (
    <Switch>
      <ProtectedRoute path="/" component={HomePage} />
      <Route path="/auth" component={AuthPage} />
      <Route component={NotFound} />
    </Switch>
  );
}

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <Router />
        <Toaster />
      </AuthProvider>
    </QueryClientProvider>
  );
}