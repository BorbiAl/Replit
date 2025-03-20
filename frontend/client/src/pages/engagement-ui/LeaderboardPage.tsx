import { useState } from "react";
import { useAuth } from "@/hooks/use-auth";
import { Redirect, Link } from "wouter";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import {
  MedalIcon,
  TrophyIcon,
  StarIcon,
  UserIcon,
  ChevronUpIcon,
  Users2Icon
} from "lucide-react";

export default function LeaderboardPage() {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState("weekly");

  // If not logged in, redirect to auth page
  if (!user) {
    return <Redirect to="/auth" />;
  }

  // Mock leaderboard data
  const leaderboard = [
    {
      id: 1,
      username: "math_wizard",
      first_name: "Sarah",
      last_name: "Johnson",
      xp: 2430,
      streak: 31,
      level: 15,
      position: 1,
      isCurrentUser: false,
    },
    {
      id: 2,
      username: "science_king",
      first_name: "Michael",
      last_name: "Brown",
      xp: 1980,
      streak: 24,
      level: 12,
      position: 2,
      isCurrentUser: false,
    },
    {
      id: 3,
      username: "physics_pro",
      first_name: "Jennifer",
      last_name: "Davis",
      xp: 1750,
      streak: 18,
      level: 10,
      position: 3,
      isCurrentUser: false,
    },
    {
      id: 0, // Current user
      username: user.username,
      first_name: user.first_name || "You",
      last_name: user.last_name || "",
      xp: user.xp || 1245,
      streak: user.streak || 8,
      level: user.level || 7,
      position: 4,
      isCurrentUser: true,
    },
    {
      id: 5,
      username: "chemistry_geek",
      first_name: "David",
      last_name: "Wilson",
      xp: 1050,
      streak: 12,
      level: 6,
      position: 5,
      isCurrentUser: false,
    },
  ];

  const userPositionChange = +2; // Simulated position change from last week

  // Class groups
  const classGroups = [
    { id: 1, name: "Science AP Class", members: 28, rank: 2, joinCode: "SCI345" },
    { id: 2, name: "Math Champions", members: 22, rank: 5, joinCode: "MATH123" },
  ]

  return (
    <div className="min-h-screen bg-gradient-to-b from-blue-50 to-white pb-20">
      {/* Header */}
      <div className="sticky top-0 z-10 bg-white shadow-sm border-b">
        <div className="container mx-auto p-4">
          <div className="flex justify-between items-center">
            <h1 className="text-xl font-bold">Leaderboard</h1>
            <Button size="sm" variant="outline">
              <Users2Icon className="h-4 w-4 mr-1" /> Join Class
            </Button>
          </div>
          
          <div className="mt-4">
            <Tabs defaultValue="weekly" className="w-full" onValueChange={setActiveTab}>
              <TabsList className="grid w-full grid-cols-3">
                <TabsTrigger value="weekly">Weekly</TabsTrigger>
                <TabsTrigger value="monthly">Monthly</TabsTrigger>
                <TabsTrigger value="classes">My Classes</TabsTrigger>
              </TabsList>
            </Tabs>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-6">
        {activeTab !== "classes" ? (
          <div className="space-y-4">
            <div className="mb-8">
              <h2 className="text-base font-semibold mb-3 text-gray-600">
                Top Students • {activeTab === "weekly" ? "This Week" : "This Month"}
              </h2>
              
              {/* Top 3 with medals */}
              <div className="flex justify-around mb-8 mt-4">
                {leaderboard.slice(0, 3).map((user, idx) => (
                  <div key={user.id} className="flex flex-col items-center">
                    <div className={`w-14 h-14 rounded-full border-2 flex items-center justify-center
                      ${idx === 0 ? 'bg-yellow-100 border-yellow-400' :
                        idx === 1 ? 'bg-gray-100 border-gray-400' :
                          'bg-orange-100 border-orange-400'}`}>
                      {user.first_name.charAt(0)}{user.last_name?.charAt(0)}
                    </div>
                    
                    <div className={`text-center mt-2 ${user.isCurrentUser ? 'font-bold text-blue-600' : ''}`}>
                      <div className="font-semibold text-sm line-clamp-1">{user.first_name}</div>
                      <div className="text-xs text-gray-600">
                        {idx === 0 ? '🥇 ' : idx === 1 ? '🥈 ' : '🥉 '}
                        {user.xp} XP
                      </div>
                    </div>
                  </div>
                ))}
              </div>
              
              {/* Full leaderboard */}
              {leaderboard.map((user) => (
                <LeaderboardCard key={user.id} user={user} activeTab={activeTab} />
              ))}
            </div>
          </div>
        ) : (
          <div className="space-y-4">
            <div className="mb-4">
              <h2 className="text-base font-semibold mb-3 text-gray-600">
                Your Class Groups
              </h2>
              
              {classGroups.length > 0 ? (
                <div className="space-y-4">
                  {classGroups.map(group => (
                    <Card key={group.id} className="shadow-sm">
                      <CardContent className="p-4">
                        <div className="flex items-center">
                          <div className="h-10 w-10 bg-blue-100 rounded-lg flex items-center justify-center mr-3">
                            <Users2Icon className="h-5 w-5 text-blue-500" />
                          </div>
                          
                          <div className="flex-1">
                            <h3 className="font-bold">{group.name}</h3>
                            <div className="flex text-xs text-gray-500">
                              <span className="mr-3">{group.members} members</span>
                              <span>Rank: #{group.rank}</span>
                            </div>
                          </div>
                          
                          <Button size="sm" variant="ghost">View</Button>
                        </div>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              ) : (
                <Card className="shadow-sm border-dashed border-blue-200 bg-blue-50">
                  <CardContent className="flex flex-col items-center justify-center py-8">
                    <Users2Icon className="h-12 w-12 text-blue-300 mb-3" />
                    <p className="text-center text-gray-600 mb-4">You haven't joined any classes yet</p>
                    <Button size="sm">Join a class</Button>
                  </CardContent>
                </Card>
              )}
              
              <div className="mt-8 mb-4">
                <h2 className="text-base font-semibold mb-3 text-gray-600">
                  Join a Class
                </h2>
                
                <Card className="shadow-sm">
                  <CardContent className="p-4">
                    <p className="mb-4 text-sm">
                      Enter a class code to join a new study group and compete together!
                    </p>
                    
                    <div className="flex space-x-2">
                      <input
                        type="text"
                        placeholder="Enter class code"
                        className="px-3 py-2 border rounded-md flex-1"
                      />
                      <Button>Join</Button>
                    </div>
                  </CardContent>
                </Card>
              </div>
            </div>
          </div>
        )}
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
            <div className="flex flex-col items-center py-1 px-3 text-blue-500">
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

function LeaderboardCard({ user, activeTab }: { user: any, activeTab: string }) {
  const positionChange = user.position === 4 ? 2 : Math.floor(Math.random() * 5) - 2; // Simulated change for demo
  
  return (
    <Card className={`shadow-sm ${user.isCurrentUser ? 'border-blue-300 bg-blue-50' : ''}`}>
      <CardContent className="p-4">
        <div className="flex items-center">
          <div className="w-9 mr-4 text-center">
            <span className={`font-bold text-lg ${
              user.position === 1 ? 'text-yellow-500' :
              user.position === 2 ? 'text-gray-500' :
              user.position === 3 ? 'text-orange-500' : ''
            }`}>
              {user.position}
            </span>
          </div>
          
          <div className="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center mr-3">
            <span className="font-medium">
              {user.first_name.charAt(0)}{user.last_name?.charAt(0)}
            </span>
          </div>
          
          <div className="flex-1">
            <h3 className={`font-bold ${user.isCurrentUser ? 'text-blue-600' : ''}`}>
              {user.first_name} {user.isCurrentUser ? '(You)' : ''}
            </h3>
            
            <div className="flex items-center text-sm text-gray-500">
              <StarIcon className="h-4 w-4 text-yellow-500 mr-1" />
              <span>{user.xp} XP</span>
              
              <span className="mx-2">•</span>
              
              <span className="flex items-center">
                <TrophyIcon className="h-3 w-3 mr-1" />
                Level {user.level}
              </span>
            </div>
          </div>
          
          <div className="flex flex-col items-end">
            {positionChange !== 0 && (
              <span className={`text-xs flex items-center ${
                positionChange > 0 ? 'text-green-500' : 'text-red-500'
              }`}>
                {positionChange > 0 ? (
                  <>
                    <ChevronUpIcon className="h-3 w-3" />
                    {positionChange}
                  </>
                ) : (
                  <>
                    <span className="rotate-180 inline-block">
                      <ChevronUpIcon className="h-3 w-3" />
                    </span>
                    {Math.abs(positionChange)}
                  </>
                )}
              </span>
            )}
            
            <Badge variant="outline" className="mt-1">
              <span className="text-red-500 mr-1">🔥</span> {user.streak} days
            </Badge>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
