import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { useAuth } from "@/hooks/use-auth";
import { useQuery } from "@tanstack/react-query";
import {
  CheckCircle,
  Clock,
  Award,
  TrendingUp,
  BadgeCheck,
} from "lucide-react";
import {
  BarChart,
  Bar,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";

// Helper function to generate mock chart data
const generateChartData = (tests: any[]) => {
  const completedTests = tests.filter(test => test.is_completed);
  
  if (completedTests.length === 0) {
    return Array(7).fill(0).map((_, i) => ({
      name: `Day ${i+1}`,
      score: 0,
    }));
  }
  
  // Use actual test data if available
  return completedTests.slice(0, 7).map((test, index) => ({
    name: `Test ${index+1}`,
    score: test.score || 0,
  }));
};

export default function ProgressPage() {
  const { user } = useAuth();
  
  const { data: tests = [] } = useQuery({
    queryKey: ["/api/tests"],
  });
  
  const completedTests = tests.filter(test => test.is_completed);
  const completedCount = completedTests.length;
  const pendingCount = tests.length - completedCount;
  const totalHours = Math.round(completedCount * 0.5); // Estimated study hours
  
  // Calculate average score
  const averageScore = completedTests.length > 0
    ? Math.round(completedTests.reduce((acc, test) => acc + (test.score || 0), 0) / completedTests.length)
    : 0;
  
  const progressChartData = generateChartData(tests);
  
  // Subject performance data
  const subjectPerformance = [
    { name: "Physics", score: 78 },
    { name: "Math", score: 85 },
    { name: "Chemistry", score: 65 },
    { name: "Biology", score: 72 },
    { name: "Literature", score: 90 },
  ];
  
  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-neutral-900">Your Progress</h1>
      
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Tests Completed
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex items-center">
              <CheckCircle className="mr-2 h-4 w-4 text-green-500" />
              <span className="text-3xl font-bold">{completedCount}</span>
            </div>
            {completedCount > 0 && (
              <p className="text-xs text-muted-foreground">
                {Math.round((completedCount / tests.length) * 100)}% completion rate
              </p>
            )}
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Pending Tests
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex items-center">
              <Clock className="mr-2 h-4 w-4 text-amber-500" />
              <span className="text-3xl font-bold">{pendingCount}</span>
            </div>
            {pendingCount > 0 && (
              <p className="text-xs text-muted-foreground">
                Next test: {tests.find(t => !t.is_completed)?.title || 'None scheduled'}
              </p>
            )}
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Average Score
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex items-center">
              <Award className="mr-2 h-4 w-4 text-primary" />
              <span className="text-3xl font-bold">{averageScore}%</span>
            </div>
            {averageScore > 0 && (
              <p className="text-xs text-muted-foreground">
                {averageScore >= 70 ? 'Great work!' : 'Keep improving!'}
              </p>
            )}
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Study Hours
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex items-center">
              <TrendingUp className="mr-2 h-4 w-4 text-blue-500" />
              <span className="text-3xl font-bold">{totalHours}</span>
            </div>
            <p className="text-xs text-muted-foreground">
              Based on completed tests
            </p>
          </CardContent>
        </Card>
      </div>
      
      <div className="grid gap-6 grid-cols-1 lg:grid-cols-2">
        <Card className="col-span-1">
          <CardHeader>
            <CardTitle>Performance Trend</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="h-[300px]">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart
                  data={progressChartData}
                  margin={{
                    top: 5,
                    right: 30,
                    left: 20,
                    bottom: 5,
                  }}
                >
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis domain={[0, 100]} />
                  <Tooltip />
                  <Legend />
                  <Line
                    type="monotone"
                    dataKey="score"
                    stroke="#4255FF"
                    activeDot={{ r: 8 }}
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>
        
        <Card className="col-span-1">
          <CardHeader>
            <CardTitle>Subject Performance</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="h-[300px]">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart
                  width={500}
                  height={300}
                  data={subjectPerformance}
                  margin={{
                    top: 5,
                    right: 30,
                    left: 20,
                    bottom: 5,
                  }}
                >
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis domain={[0, 100]} />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="score" fill="#58CC02" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>
      </div>
      
      <Card>
        <CardHeader>
          <CardTitle>Achievements</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div className={`p-4 rounded-lg border ${user?.streak >= 3 ? 'bg-amber-50 border-amber-200' : 'bg-gray-50 border-gray-200'}`}>
              <div className="flex items-center mb-2">
                <div className={`w-8 h-8 rounded-full ${user?.streak >= 3 ? 'bg-amber-100 text-amber-500' : 'bg-gray-100 text-gray-400'} flex items-center justify-center mr-2`}>
                  <BadgeCheck className="h-5 w-5" />
                </div>
                <h3 className="font-semibold">3-Day Streak</h3>
              </div>
              <p className="text-xs text-neutral-600">
                {user?.streak >= 3 ? 'Achieved!' : `${user?.streak || 0}/3 days`}
              </p>
            </div>
            
            <div className={`p-4 rounded-lg border ${completedCount >= 5 ? 'bg-primary/5 border-primary/20' : 'bg-gray-50 border-gray-200'}`}>
              <div className="flex items-center mb-2">
                <div className={`w-8 h-8 rounded-full ${completedCount >= 5 ? 'bg-primary/10 text-primary' : 'bg-gray-100 text-gray-400'} flex items-center justify-center mr-2`}>
                  <CheckCircle className="h-5 w-5" />
                </div>
                <h3 className="font-semibold">5 Tests</h3>
              </div>
              <p className="text-xs text-neutral-600">
                {completedCount >= 5 ? 'Achieved!' : `${completedCount}/5 tests`}
              </p>
            </div>
            
            <div className={`p-4 rounded-lg border ${averageScore >= 80 ? 'bg-green-50 border-green-200' : 'bg-gray-50 border-gray-200'}`}>
              <div className="flex items-center mb-2">
                <div className={`w-8 h-8 rounded-full ${averageScore >= 80 ? 'bg-green-100 text-green-500' : 'bg-gray-100 text-gray-400'} flex items-center justify-center mr-2`}>
                  <Award className="h-5 w-5" />
                </div>
                <h3 className="font-semibold">80% Average</h3>
              </div>
              <p className="text-xs text-neutral-600">
                {averageScore >= 80 ? 'Achieved!' : `${averageScore}% current avg.`}
              </p>
            </div>
            
            <div className={`p-4 rounded-lg border ${totalHours >= 10 ? 'bg-blue-50 border-blue-200' : 'bg-gray-50 border-gray-200'}`}>
              <div className="flex items-center mb-2">
                <div className={`w-8 h-8 rounded-full ${totalHours >= 10 ? 'bg-blue-100 text-blue-500' : 'bg-gray-100 text-gray-400'} flex items-center justify-center mr-2`}>
                  <Clock className="h-5 w-5" />
                </div>
                <h3 className="font-semibold">10 Study Hours</h3>
              </div>
              <p className="text-xs text-neutral-600">
                {totalHours >= 10 ? 'Achieved!' : `${totalHours}/10 hours`}
              </p>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
