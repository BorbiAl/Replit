import { useState } from "react";
import { useAuth } from "@/hooks/use-auth";
import { Redirect, Link } from "wouter";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import { Progress } from "@/components/ui/progress";
import {
  MedalIcon,
  TrophyIcon,
  StarIcon,
  UserIcon,
  LogOutIcon,
  SettingsIcon,
  BarChart3Icon,
  TargetIcon,
  CalendarIcon,
  BookOpenIcon,
  BookIcon,
  FlameIcon
} from "lucide-react";

export default function ProfilePage() {
  const { user, logoutMutation } = useAuth();
  const [activeTab, setActiveTab] = useState("stats");

  // If not logged in, redirect to auth page
  if (!user) {
    return <Redirect to="/auth" />;
  }

  // User's achievements (mock data)
  const achievements = [
    {
      id: 1,
      title: "Early Bird",
      description: "Complete 5 tests before the due date",
      progress: 100,
      emoji: "🌅",
      completed: true
    },
    {
      id: 2,
      title: "Math Master",
      description: "Score 90%+ on 10 math tests",
      progress: 70,
      emoji: "🧮",
      completed: false
    },
    {
      id: 3,
      title: "Science Whiz",
      description: "Complete all science subjects",
      progress: 40,
      emoji: "🔬",
      completed: false
    },
    {
      id: 4,
      title: "7-Day Streak",
      description: "Study for 7 days in a row",
      progress: 100,
      emoji: "🔥",
      completed: true
    },
    {
      id: 5,
      title: "Test Creator",
      description: "Create your first test",
      progress: 100,
      emoji: "✏️",
      completed: true
    }
  ];

  // Activity history (mock data)
  const activityHistory = [
    {
      id: 1,
      action: "Completed test",
      subject: "Mathematics: Algebra Fundamentals",
      score: 92,
      xp: 75,
      date: "Today, 3:45 PM"
    },
    {
      id: 2,
      action: "Created test",
      subject: "Physics: Mechanics & Dynamics",
      xp: 30,
      date: "Yesterday, 2:20 PM"
    },
    {
      id: 3,
      action: "Completed test",
      subject: "Chemistry: Organic Chemistry",
      score: 85,
      xp: 65,
      date: "May 15, 10:15 AM"
    },
    {
      id: 4,
      action: "Reached level 7",
      xp: 100,
      date: "May 10, 7:30 PM"
    }
  ];

  // User stats
  const userStats = {
    totalXP: user.xp || 1245,
    testsCreated: 8,
    testsCompleted: 12,
    averageScore: 88,
    totalStudyTime: 24, // hours
    longestStreak: user.streak || 8,
  };

  // Calculate progress to next level
  const currentLevel = user.level || 8;
  const xpForNextLevel = currentLevel * 200;
  const currentLevelXP = (currentLevel - 1) * 200;
  const progressToNextLevel = ((userStats.totalXP - currentLevelXP) / (xpForNextLevel - currentLevelXP)) * 100;

  return (
    <div className="min-h-screen bg-gradient-to-b from-blue-50 to-white pb-20">
      {/* Header */}
      <div className="sticky top-0 z-10 bg-white shadow-sm border-b">
        <div className="container mx-auto p-4">
          <div className="flex justify-between items-center">
            <h1 className="text-xl font-bold">My Profile</h1>
            <div className="flex space-x-2">
              <Button size="icon" variant="ghost">
                <SettingsIcon className="h-5 w-5" />
              </Button>
              <Button 
                size="icon" 
                variant="ghost"
                onClick={() => logoutMutation.mutate()}
              >
                <LogOutIcon className="h-5 w-5" />
              </Button>
            </div>
          </div>
          
          <div className="mt-4">
            <Tabs defaultValue="stats" className="w-full" onValueChange={setActiveTab}>
              <TabsList className="grid w-full grid-cols-2">
                <TabsTrigger value="stats">Stats</TabsTrigger>
                <TabsTrigger value="achievements">Achievements</TabsTrigger>
              </TabsList>
            </Tabs>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-6">
        {/* User Profile Card */}
        <Card className="shadow-md overflow-hidden mb-6">
          <div className="bg-blue-500 pt-6 pb-10"></div>
          <div className="px-6 -mt-8">
            <div className="flex items-end mb-4">
              <div className="h-16 w-16 rounded-full bg-white p-1 mr-4">
                <div className="h-full w-full rounded-full bg-blue-100 flex items-center justify-center">
                  <span className="text-lg font-semibold text-blue-700">
                    {user.first_name?.[0] || user.username?.[0] || "U"}
                  </span>
                </div>
              </div>
              
              <div className="flex-1">
                <h2 className="text-lg font-bold">
                  {user.first_name ? `${user.first_name} ${user.last_name || ''}` : user.username}
                </h2>
                <p className="text-gray-600">@{user.username}</p>
              </div>
              
              <Badge>
                <TrophyIcon className="h-3 w-3 mr-1" />
                Level {currentLevel}
              </Badge>
            </div>
            
            <div className="mb-4">
              <div className="flex justify-between text-sm mb-1">
                <span>Level {currentLevel}</span>
                <span>Level {currentLevel + 1}</span>
              </div>
              <Progress value={progressToNextLevel} className="h-2" />
              <div className="text-xs text-center mt-1 text-gray-500">
                {Math.floor(xpForNextLevel - (userStats.totalXP - currentLevelXP))} XP to next level
              </div>
            </div>
            
            <div className="grid grid-cols-3 gap-2 mb-4">
              <div className="bg-blue-50 rounded-lg p-3 text-center">
                <div className="text-2xl font-bold text-blue-700">{userStats.totalXP}</div>
                <div className="text-xs text-gray-600">Total XP</div>
              </div>
              <div className="bg-orange-50 rounded-lg p-3 text-center">
                <div className="text-2xl font-bold text-orange-700">{userStats.longestStreak}</div>
                <div className="text-xs text-gray-600">Day Streak</div>
              </div>
              <div className="bg-green-50 rounded-lg p-3 text-center">
                <div className="text-2xl font-bold text-green-700">{userStats.testsCompleted}</div>
                <div className="text-xs text-gray-600">Tests Done</div>
              </div>
            </div>
          </div>
        </Card>

        {/* Tabs Content */}
        <TabsContent value="stats" className="mt-0">
          <div className="space-y-6">
            <Card className="shadow-sm">
              <CardContent className="p-4">
                <h3 className="text-base font-bold mb-3 flex items-center">
                  <BarChart3Icon className="h-4 w-4 mr-2" />
                  Your Stats
                </h3>
                
                <div className="grid grid-cols-2 gap-4">
                  <div className="flex items-center">
                    <div className="h-8 w-8 rounded-full bg-blue-100 flex items-center justify-center mr-3">
                      <BookOpenIcon className="h-4 w-4 text-blue-600" />
                    </div>
                    <div>
                      <div className="text-sm text-gray-500">Tests Created</div>
                      <div className="font-bold">{userStats.testsCreated}</div>
                    </div>
                  </div>
                  
                  <div className="flex items-center">
                    <div className="h-8 w-8 rounded-full bg-green-100 flex items-center justify-center mr-3">
                      <TargetIcon className="h-4 w-4 text-green-600" />
                    </div>
                    <div>
                      <div className="text-sm text-gray-500">Avg. Score</div>
                      <div className="font-bold">{userStats.averageScore}%</div>
                    </div>
                  </div>
                  
                  <div className="flex items-center">
                    <div className="h-8 w-8 rounded-full bg-purple-100 flex items-center justify-center mr-3">
                      <CalendarIcon className="h-4 w-4 text-purple-600" />
                    </div>
                    <div>
                      <div className="text-sm text-gray-500">Study Time</div>
                      <div className="font-bold">{userStats.totalStudyTime} hours</div>
                    </div>
                  </div>
                  
                  <div className="flex items-center">
                    <div className="h-8 w-8 rounded-full bg-orange-100 flex items-center justify-center mr-3">
                      <BookIcon className="h-4 w-4 text-orange-600" />
                    </div>
                    <div>
                      <div className="text-sm text-gray-500">Subjects</div>
                      <div className="font-bold">4</div>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
            
            <div>
              <h3 className="text-base font-bold mb-3 flex items-center">
                <CalendarIcon className="h-4 w-4 mr-2" />
                Activity History
              </h3>
              
              <div className="space-y-3">
                {activityHistory.map(activity => (
                  <Card key={activity.id} className="shadow-sm">
                    <CardContent className="p-4">
                      <div className="flex items-center">
                        <div className="h-10 w-10 rounded-lg bg-blue-100 flex items-center justify-center mr-3">
                          {activity.action.includes("Completed") && "✅"}
                          {activity.action.includes("Created") && "📝"}
                          {activity.action.includes("Reached") && "🏆"}
                        </div>
                        
                        <div className="flex-1">
                          <div className="flex justify-between">
                            <h4 className="font-semibold">{activity.action}</h4>
                            <span className="text-xs text-gray-500">{activity.date}</span>
                          </div>
                          
                          {activity.subject && (
                            <p className="text-sm text-gray-600">{activity.subject}</p>
                          )}
                          
                          <div className="flex mt-1">
                            {activity.score && (
                              <Badge variant="outline" className="mr-2">
                                Score: {activity.score}%
                              </Badge>
                            )}
                            
                            <Badge variant="secondary">
                              +{activity.xp} XP
                            </Badge>
                          </div>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            </div>
          </div>
        </TabsContent>
        
        <TabsContent value="achievements" className="mt-0">
          <div className="space-y-6">
            <div>
              <h3 className="text-base font-bold mb-3 flex items-center">
                <MedalIcon className="h-4 w-4 mr-2" />
                Your Achievements
              </h3>
              
              <div className="space-y-3">
                {achievements.map(achievement => (
                  <Card 
                    key={achievement.id} 
                    className={`shadow-sm ${achievement.completed ? 'border-green-200' : ''}`}
                  >
                    <CardContent className="p-4">
                      <div className="flex items-center">
                        <div className={`h-12 w-12 rounded-lg flex items-center justify-center mr-3
                          ${achievement.completed ? 'bg-green-100' : 'bg-blue-100'}`}>
                          <span className="text-2xl">{achievement.emoji}</span>
                        </div>
                        
                        <div className="flex-1">
                          <div className="flex justify-between items-center">
                            <h4 className="font-semibold">{achievement.title}</h4>
                            {achievement.completed && (
                              <Badge variant="success" className="bg-green-100 text-green-800">
                                Completed
                              </Badge>
                            )}
                          </div>
                          
                          <p className="text-sm text-gray-600">{achievement.description}</p>
                          
                          {!achievement.completed && (
                            <div className="mt-2">
                              <div className="flex justify-between text-xs mb-1">
                                <span>Progress</span>
                                <span>{achievement.progress}%</span>
                              </div>
                              <Progress value={achievement.progress} className="h-1.5" />
                            </div>
                          )}
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            </div>
          </div>
        </TabsContent>
      </div>

      {/* Bottom Navigation */}
      <div className="fixed bottom-0 left-0 right-0 bg-white border-t shadow-lg">
        <div className="flex justify-around py-2">
          <Link href="/home">
            <div className="flex flex-col items-center py-1 px-3 text-gray-500">
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
            <div className="flex flex-col items-center py-1 px-3 text-blue-500">
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
