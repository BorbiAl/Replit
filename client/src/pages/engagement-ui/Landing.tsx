import { useAuth } from "@/hooks/use-auth";
import { useQuery } from "@tanstack/react-query";
import { Redirect, Link } from "wouter";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import { 
  CalendarIcon, 
  TrophyIcon, 
  FlameIcon, 
  StarIcon, 
  BookOpenIcon,
  PlusIcon,
  ArrowRightIcon,
  UserIcon,
  ChartBarIcon,
  ListIcon
} from "lucide-react";
import { useEffect, useState } from "react";

export default function LandingPage() {
  const { user } = useAuth();
  const [xpGoal, setXpGoal] = useState(25);
  const [xpToday, setXpToday] = useState(10);
  const [scrollPosition, setScrollPosition] = useState(0);

  // Fetch upcoming tests
  const { data: upcomingTests = [] } = useQuery({
    queryKey: ["/api/tests/upcoming"],
  });

  // If not logged in, redirect to auth page
  if (!user) {
    return <Redirect to="/auth" />;
  }

  const firstName = user.firstName || "Scholar";
  const daysOfWeek = ["M", "T", "W", "T", "F", "S", "S"];
  const today = new Date().getDay() - 1; // 0-6, adjust to match daysOfWeek
  
  // Simulated streaks data 
  const dailyStatus = [
    { day: "M", status: "complete" },
    { day: "T", status: "complete" },
    { day: "W", status: today === 2 ? "today" : today > 2 ? "missed" : "future" },
    { day: "T", status: today === 3 ? "today" : today > 3 ? "missed" : "future" },
    { day: "F", status: today === 4 ? "today" : today > 4 ? "missed" : "future" },
    { day: "S", status: today === 5 ? "today" : today > 5 ? "missed" : "future" },
    { day: "S", status: today === 6 ? "today" : today > 6 ? "missed" : "future" },
  ];

  const getStatusIcon = (status: string) => {
    switch(status) {
      case "complete": return "✅";
      case "today": return "📌";
      case "missed": return "❌";
      default: return "⏳";
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-blue-50 to-white">
      {/* Top App Bar */}
      <div className="sticky top-0 z-10 bg-white shadow-sm border-b p-4">
        <div className="container mx-auto flex justify-between items-center">
          <div className="flex items-center space-x-2">
            <div className="h-10 w-10 bg-blue-100 rounded-full flex items-center justify-center">
              <UserIcon className="h-6 w-6 text-blue-500" />
            </div>
            <span className="font-bold">StudyQuest</span>
          </div>
          
          <div className="flex items-center space-x-6">
            <div className="flex items-center">
              <FlameIcon className="h-5 w-5 text-orange-500 mr-1" />
              <span className="font-bold">{user.streak || 5}</span>
            </div>
            
            <div className="flex items-center">
              <StarIcon className="h-5 w-5 text-yellow-500 mr-1" />
              <span className="font-bold">{user.xp || 245}</span>
            </div>
            
            <div className="flex items-center">
              <TrophyIcon className="h-5 w-5 text-blue-500 mr-1" />
              <span className="font-bold">{user.level || 8}</span>
            </div>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-6">
        {/* Welcome and Goal Card */}
        <Card className="mb-6 overflow-hidden border-none shadow-md">
          <div className="bg-blue-500 text-white p-6">
            <h2 className="text-xl font-bold">👋 Hey, {firstName}!</h2>
          </div>
          
          <CardContent className="p-6">
            <h3 className="font-medium text-gray-700">Daily Goal</h3>
            <div className="flex justify-between items-center mt-1 mb-2">
              <span className="text-sm">{xpToday} XP Today</span>
              <span className="text-sm font-bold">Goal: {xpGoal} XP</span>
            </div>
            
            <Progress value={(xpToday / xpGoal) * 100} className="h-2" />
            
            <div className="flex justify-between mt-6">
              {dailyStatus.map((day, i) => (
                <div key={i} className="flex flex-col items-center">
                  <div className={`h-10 w-10 rounded-full flex items-center justify-center
                    ${day.status === 'today' ? 'bg-blue-100 text-blue-600 font-bold' : 
                      day.status === 'complete' ? 'bg-green-100' : 
                      day.status === 'missed' ? 'bg-red-100' : 'bg-gray-100'}`}>
                    {getStatusIcon(day.status)}
                  </div>
                  <span className={`text-xs mt-1 ${day.status === 'today' ? 'font-bold text-blue-600' : ''}`}>
                    {day.day}
                  </span>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* Quick Actions */}
        <div className="grid grid-cols-2 gap-4 mb-6">
          <Link href="/create-test">
            <Button 
              variant="default" 
              className="w-full h-16 rounded-xl shadow-sm bg-blue-500 hover:bg-blue-600">
              <div className="flex flex-col items-center">
                <PlusIcon className="h-6 w-6 mb-1" />
                <span>New Test</span>
              </div>
            </Button>
          </Link>
          
          <Link href="/tests">
            <Button 
              variant="outline" 
              className="w-full h-16 rounded-xl shadow-sm border-blue-200 bg-blue-50 hover:bg-blue-100">
              <div className="flex flex-col items-center">
                <ListIcon className="h-6 w-6 mb-1" />
                <span>My Tests</span>
              </div>
            </Button>
          </Link>
          
          <Link href="/progress">
            <Button 
              variant="outline" 
              className="w-full h-16 rounded-xl shadow-sm border-blue-200 bg-blue-50 hover:bg-blue-100">
              <div className="flex flex-col items-center">
                <ChartBarIcon className="h-6 w-6 mb-1" />
                <span>Progress</span>
              </div>
            </Button>
          </Link>
          
          <Link href="/leaderboard">
            <Button 
              variant="outline" 
              className="w-full h-16 rounded-xl shadow-sm border-blue-200 bg-blue-50 hover:bg-blue-100">
              <div className="flex flex-col items-center">
                <TrophyIcon className="h-6 w-6 mb-1" />
                <span>Leaderboard</span>
              </div>
            </Button>
          </Link>
        </div>

        {/* Upcoming Tests Section */}
        <div className="mb-6">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-lg font-bold">Upcoming Tests</h2>
            <Link href="/tests">
              <span className="text-blue-500 text-sm flex items-center">
                See all <ArrowRightIcon className="h-3 w-3 ml-1" />
              </span>
            </Link>
          </div>
          
          {upcomingTests.length === 0 ? (
            <Card className="shadow-sm border-dashed border-blue-200 bg-blue-50">
              <CardContent className="flex flex-col items-center justify-center py-8">
                <BookOpenIcon className="h-12 w-12 text-blue-300 mb-3" />
                <p className="text-center text-gray-600 mb-4">No upcoming tests</p>
                <Link href="/create-test">
                  <Button size="sm">Create your first test</Button>
                </Link>
              </CardContent>
            </Card>
          ) : (
            <div className="space-y-4">
              {upcomingTests.map((test: any) => (
                <TestCard key={test.id} test={test} />
              ))}
            </div>
          )}
        </div>

        {/* Learning Paths */}
        <div className="mb-4">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-lg font-bold">Learning Paths</h2>
            <span className="text-blue-500 text-sm">View all</span>
          </div>
          
          <div className="space-y-3">
            <LearningPathCard 
              title="Algebra" 
              emoji="📊" 
              progress={100} 
              status="complete" />
            <LearningPathCard 
              title="Geometry" 
              emoji="📐" 
              progress={75} 
              status="in-progress" />
            <LearningPathCard 
              title="Trigonometry" 
              emoji="📏" 
              progress={0} 
              status="locked" />
          </div>
        </div>
      </div>

      {/* Bottom Navigation */}
      <div className="fixed bottom-0 left-0 right-0 bg-white border-t shadow-lg">
        <div className="flex justify-around py-2">
          <Link href="/home">
            <div className="flex flex-col items-center py-1 px-3 text-blue-500">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
              </svg>
              <span className="text-xs">Home</span>
            </div>
          </Link>
          
          <Link href="/tests">
            <div className="flex flex-col items-center py-1 px-3 text-gray-500">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" />
              </svg>
              <span className="text-xs">Tests</span>
            </div>
          </Link>
          
          <Link href="/leaderboard">
            <div className="flex flex-col items-center py-1 px-3 text-gray-500">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 8v8m-4-5v5m-4-2v2m-2 4h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              <span className="text-xs">Progress</span>
            </div>
          </Link>
          
          <Link href="/profile">
            <div className="flex flex-col items-center py-1 px-3 text-gray-500">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              <span className="text-xs">Profile</span>
            </div>
          </Link>
        </div>
      </div>
    </div>
  );
}

function TestCard({ test }: { test: any }) {
  const daysLeft = Math.floor((new Date(test.exam_date || Date.now() + 86400000*7).getTime() - Date.now()) / (1000 * 60 * 60 * 24));
  const subjectEmojis: {[key: number]: string} = {
    1: "🧮", // Math
    2: "🔬", // Physics
    3: "🧪", // Chemistry
    4: "🌿"  // Biology
  };
  const emoji = subjectEmojis[test.subject_id] || "📚";
  
  return (
    <Card className="shadow-sm hover:shadow-md transition-shadow">
      <CardContent className="p-4">
        <div className="flex items-center mb-3">
          <div className="h-10 w-10 bg-blue-100 rounded-lg flex items-center justify-center mr-3">
            <span className="text-xl">{emoji}</span>
          </div>
          <div>
            <h3 className="font-bold text-base line-clamp-1">{test.title}</h3>
            <div className="flex items-center text-sm text-gray-500">
              <CalendarIcon className="h-3 w-3 mr-1" />
              <span>{daysLeft} days left</span>
            </div>
          </div>
          <Badge className="ml-auto" variant="secondary">+{test.question_count * 5} XP</Badge>
        </div>
        
        <div className="flex justify-between mb-3 text-xs">
          <span className="bg-gray-100 px-2 py-1 rounded-full">📖 Pages {test.pages_from}-{test.pages_to}</span>
          <span className="bg-gray-100 px-2 py-1 rounded-full">❓ {test.question_count} questions</span>
        </div>
        
        <Button className="w-full" size="sm">Practice Now</Button>
      </CardContent>
    </Card>
  );
}

function LearningPathCard({ 
  title, 
  emoji, 
  progress, 
  status 
}: { 
  title: string; 
  emoji: string; 
  progress: number; 
  status: "complete" | "in-progress" | "locked" 
}) {
  return (
    <Card className={`shadow-sm ${status === 'locked' ? 'opacity-60' : ''}`}>
      <CardContent className="p-4">
        <div className="flex items-center">
          <div className={`h-10 w-10 rounded-lg flex items-center justify-center mr-3
            ${status === 'complete' ? 'bg-green-100' : 
              status === 'in-progress' ? 'bg-blue-100' : 'bg-gray-100'}`}>
            <span className="text-xl">{emoji}</span>
          </div>
          
          <div className="flex-1">
            <div className="flex justify-between items-center mb-1">
              <h3 className="font-bold text-sm">{title}</h3>
              
              {status === 'complete' && <span className="text-green-500 text-sm">✅</span>}
              {status === 'in-progress' && <Button size="sm" variant="ghost" className="h-7 px-2 py-0">Continue</Button>}
              {status === 'locked' && <span className="text-gray-400">🔒</span>}
            </div>
            
            {status !== 'locked' && (
              <>
                <Progress value={progress} className="h-1.5" />
                <div className="text-xs text-gray-500 mt-1">
                  {progress === 100 ? (
                    "Completed!"
                  ) : (
                    `${progress}% complete`
                  )}
                </div>
              </>
            )}
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
