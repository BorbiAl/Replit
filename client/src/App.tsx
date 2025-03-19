import { QueryClientProvider } from "@tanstack/react-query";
import { Switch, Route } from "wouter";

import HomePage from "@/pages/home-page";
import NotFound from "@/pages/not-found";
import AuthPage from "@/pages/auth-page";
import TestsPage from "@/pages/tests-page";
import StudyPlanPage from "@/pages/study-plan-page";
import ProgressPage from "@/pages/progress-page";

// New engaging UI pages
import LandingPage from "@/pages/engagement-ui/Landing";
import EngagingTestsPage from "@/pages/engagement-ui/TestsPage";
import LeaderboardPage from "@/pages/engagement-ui/LeaderboardPage";
import ProfilePage from "@/pages/engagement-ui/ProfilePage";

import { queryClient } from "@/lib/queryClient";
import { AuthProvider } from "@/hooks/use-auth";
import { Toaster } from "@/components/ui/toaster";
import { ProtectedRoute } from "./lib/protected-route";

function Router() {
  return (
    <Switch>
      {/* Original routes */}
      <Route path="/old-home" component={HomePage} />
      <Route path="/auth" component={AuthPage} />
      <Route path="/old-tests" component={TestsPage} />
      <Route path="/old-study-plan" component={StudyPlanPage} />
      <Route path="/old-progress" component={ProgressPage} />
      
      {/* New engaging UI routes */}
      <ProtectedRoute path="/" component={LandingPage} />
      <ProtectedRoute path="/home" component={LandingPage} />
      <ProtectedRoute path="/tests" component={EngagingTestsPage} />
      <ProtectedRoute path="/leaderboard" component={LeaderboardPage} />
      <ProtectedRoute path="/profile" component={ProfilePage} />
      
      <Route component={NotFound} />
    </Switch>
  );
}

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <div className="min-h-screen">
          <Router />
        </div>
        <Toaster />
      </AuthProvider>
    </QueryClientProvider>
  );
}
