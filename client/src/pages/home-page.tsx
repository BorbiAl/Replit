import { useAuth } from "@/hooks/use-auth";
import Navbar from "@/components/layout/navbar";
import Footer from "@/components/layout/footer";
import WelcomeSection from "@/components/dashboard/welcome-section";
import UpcomingTests from "@/components/dashboard/upcoming-tests";
import MultiStepForm from "@/components/create-test/multi-step-form";
import ProgressSection from "@/components/dashboard/progress-section";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { useQuery } from "@tanstack/react-query";
import StudyPlanPage from "./study-plan-page";
import TestsPage from "./tests-page";
import ProgressPage from "./progress-page";

export default function HomePage() {
  const { user } = useAuth();
  
  const { data: upcomingTests = [] } = useQuery({
    queryKey: ["/api/tests/upcoming"],
  });
  
  return (
    <div className="flex flex-col min-h-screen bg-neutral-100 font-nunito">
      <Navbar />
      
      <main className="flex-grow container mx-auto px-4 py-6">
        <Tabs defaultValue="dashboard">
          <div className="border-b border-neutral-200 mb-6">
            <TabsList className="flex bg-transparent space-x-8">
              <TabsTrigger 
                value="dashboard" 
                className="data-[state=active]:border-b-2 data-[state=active]:border-primary data-[state=active]:text-primary data-[state=active]:font-semibold border-b-2 border-transparent px-1 py-4"
              >
                Dashboard
              </TabsTrigger>
              <TabsTrigger 
                value="study-plan" 
                className="data-[state=active]:border-b-2 data-[state=active]:border-primary data-[state=active]:text-primary data-[state=active]:font-semibold border-b-2 border-transparent px-1 py-4"
              >
                Study Plan
              </TabsTrigger>
              <TabsTrigger 
                value="tests" 
                className="data-[state=active]:border-b-2 data-[state=active]:border-primary data-[state=active]:text-primary data-[state=active]:font-semibold border-b-2 border-transparent px-1 py-4"
              >
                Tests
              </TabsTrigger>
              <TabsTrigger 
                value="progress" 
                className="data-[state=active]:border-b-2 data-[state=active]:border-primary data-[state=active]:text-primary data-[state=active]:font-semibold border-b-2 border-transparent px-1 py-4"
              >
                Progress
              </TabsTrigger>
            </TabsList>
          </div>
          
          <TabsContent value="dashboard">
            <div>
              <WelcomeSection 
                firstName={user?.firstName || ""} 
                testsCount={upcomingTests.length}
              />
              
              <UpcomingTests />
              
              <div className="mb-8">
                <h2 className="text-xl font-bold text-neutral-900 mb-4">Create a New Test</h2>
                <MultiStepForm />
              </div>
              
              <ProgressSection />
            </div>
          </TabsContent>
          
          <TabsContent value="study-plan">
            <StudyPlanPage />
          </TabsContent>
          
          <TabsContent value="tests">
            <TestsPage />
          </TabsContent>
          
          <TabsContent value="progress">
            <ProgressPage />
          </TabsContent>
        </Tabs>
      </main>
      
      <Footer />
    </div>
  );
}
