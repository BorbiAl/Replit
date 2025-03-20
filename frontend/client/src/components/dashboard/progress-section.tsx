import { useQuery } from "@tanstack/react-query";
import { Link } from "wouter";
import { Button } from "@/components/ui/button";
import { CheckCheck, Clock, Sparkles, TrendingUp } from "lucide-react";

export default function ProgressSection() {
  const { data: tests = [] } = useQuery({
    queryKey: ["/api/tests"],
  });
  
  const { data: user } = useQuery({
    queryKey: ["/api/user"],
  });
  
  const completedTests = tests.filter(test => test.is_completed);
  const avgScore = completedTests.length > 0 
    ? Math.round(completedTests.reduce((acc, test) => acc + (test.score || 0), 0) / completedTests.length) 
    : 0;
  const studyHours = Math.round(completedTests.length * 0.5); // Estimate: 30 min per test
  
  // Count tests completed this week
  const oneWeekAgo = new Date();
  oneWeekAgo.setDate(oneWeekAgo.getDate() - 7);
  
  const thisWeekCompleted = completedTests.filter(test => 
    new Date(test.created_at) >= oneWeekAgo
  ).length;
  
  const progressStats = [
    {
      title: "Tests Completed",
      value: completedTests.length,
      change: `+${thisWeekCompleted} this week`,
      icon: <CheckCheck className="h-6 w-6" />,
      iconBg: "bg-primary/10 text-primary",
    },
    {
      title: "Study Hours",
      value: studyHours.toFixed(1),
      change: `+${thisWeekCompleted * 0.5} this week`,
      icon: <Clock className="h-6 w-6" />,
      iconBg: "bg-accent/10 text-accent",
    },
    {
      title: "Avg Score",
      value: `${avgScore}%`,
      change: completedTests.length > 0 ? "+5% improvement" : "No data yet",
      icon: <TrendingUp className="h-6 w-6" />,
      iconBg: "bg-green-100 text-secondary",
    },
    {
      title: "Total XP",
      value: user?.xp || 0,
      change: `Level ${user?.level || 1}`,
      icon: <Sparkles className="h-6 w-6" />,
      iconBg: "bg-primary/10 text-primary",
    },
  ];
  
  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-xl font-bold text-neutral-900">Your Progress</h2>
        <Link href="/progress">
          <Button variant="link" className="text-primary text-sm font-semibold hover:underline p-0">
            View Details
          </Button>
        </Link>
      </div>
      
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
        {progressStats.map((stat, index) => (
          <div key={index} className="bg-white rounded-lg shadow-md p-4">
            <div className="flex items-center mb-3">
              <div className={`w-10 h-10 rounded-full ${stat.iconBg} flex items-center justify-center mr-3`}>
                {stat.icon}
              </div>
              <h3 className="font-semibold text-neutral-900">{stat.title}</h3>
            </div>
            <div className="flex items-end justify-between">
              <span className="text-3xl font-bold text-neutral-900">{stat.value}</span>
              <span className="text-sm text-secondary font-semibold">{stat.change}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
