import { useState } from "react";
import { useAuth } from "@/hooks/use-auth";
import { useQuery } from "@tanstack/react-query";
import { Redirect, Link } from "wouter";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  CalendarIcon,
  CheckCircleIcon,
  FilterIcon,
  PlusIcon,
  SearchIcon,
  XCircleIcon
} from "lucide-react";

export default function TestsPage() {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState("all");
  const [searchTerm, setSearchTerm] = useState("");
  const [subjectFilter, setSubjectFilter] = useState("all");
  const [showFilters, setShowFilters] = useState(false);

  // Fetch tests
  const { data: tests = [] } = useQuery({
    queryKey: ["/api/tests"],
  });

  // If not logged in, redirect to auth page
  if (!user) {
    return <Redirect to="/auth" />;
  }

  // Filter tests based on active tab, search term, and subject filter
  const filteredTests = tests.filter((test: any) => {
    // Filter by tab
    if (activeTab === "upcoming" && test.completed) return false;
    if (activeTab === "completed" && !test.completed) return false;
    
    // Filter by search term
    if (
      searchTerm &&
      !test.title.toLowerCase().includes(searchTerm.toLowerCase())
    )
      return false;
    
    // Filter by subject
    if (subjectFilter !== "all" && test.subject_id.toString() !== subjectFilter)
      return false;
    
    return true;
  });

  return (
    <div className="min-h-screen bg-gradient-to-b from-blue-50 to-white pb-20">
      {/* Header */}
      <div className="sticky top-0 z-10 bg-white shadow-sm border-b">
        <div className="container mx-auto p-4">
          <div className="flex justify-between items-center">
            <h1 className="text-xl font-bold">My Tests</h1>
            <Link href="/create-test">
              <Button size="sm">
                <PlusIcon className="h-4 w-4 mr-1" /> New Test
              </Button>
            </Link>
          </div>
          
          <div className="mt-4">
            <div className="relative">
              <SearchIcon className="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
              <Input
                placeholder="Search tests..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-9 pr-9"
              />
              {searchTerm && (
                <button 
                  className="absolute right-3 top-2.5" 
                  onClick={() => setSearchTerm("")}
                >
                  <XCircleIcon className="h-4 w-4 text-gray-400" />
                </button>
              )}
            </div>
          </div>
          
          <div className="mt-4 flex justify-between items-center">
            <Tabs defaultValue="all" className="w-full" onValueChange={setActiveTab}>
              <TabsList className="grid w-full grid-cols-3">
                <TabsTrigger value="all">All</TabsTrigger>
                <TabsTrigger value="upcoming">Upcoming</TabsTrigger>
                <TabsTrigger value="completed">Completed</TabsTrigger>
              </TabsList>
            </Tabs>
            
            <Button 
              variant="outline" 
              size="icon" 
              className="ml-2"
              onClick={() => setShowFilters(!showFilters)}
            >
              <FilterIcon className="h-4 w-4" />
            </Button>
          </div>
          
          {/* Filters */}
          {showFilters && (
            <div className="mt-4 p-4 bg-gray-50 rounded-lg">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm font-medium">Subject</label>
                  <Select 
                    value={subjectFilter} 
                    onValueChange={setSubjectFilter}
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="All subjects" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">All subjects</SelectItem>
                      <SelectItem value="1">🧮 Mathematics</SelectItem>
                      <SelectItem value="2">🔬 Physics</SelectItem>
                      <SelectItem value="3">🧪 Chemistry</SelectItem>
                      <SelectItem value="4">🌿 Biology</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                
                <div>
                  <label className="text-sm font-medium">Sort by</label>
                  <Select defaultValue="date">
                    <SelectTrigger>
                      <SelectValue placeholder="Date" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="date">Date</SelectItem>
                      <SelectItem value="name">Name</SelectItem>
                      <SelectItem value="subject">Subject</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      <div className="container mx-auto px-4 py-6">
        {/* Tests List */}
        {filteredTests.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-12">
            <div className="bg-blue-100 p-6 rounded-full mb-4">
              <SearchIcon className="h-10 w-10 text-blue-500" />
            </div>
            <h3 className="text-lg font-semibold mb-1">No tests found</h3>
            <p className="text-gray-500 text-center mb-4">
              {searchTerm || subjectFilter !== "all"
                ? "Try adjusting your filters"
                : "Create your first test to get started"}
            </p>
            {!searchTerm && subjectFilter === "all" && (
              <Link href="/create-test">
                <Button>
                  <PlusIcon className="h-4 w-4 mr-1" /> Create Test
                </Button>
              </Link>
            )}
          </div>
        ) : (
          <div className="space-y-4">
            {filteredTests.map((test: any) => (
              <TestCard key={test.id} test={test} />
            ))}
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
            <div className="flex flex-col items-center py-1 px-3 text-blue-500">
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
  const daysLeft = Math.floor(
    (new Date(test.exam_date || Date.now() + 86400000*7).getTime() - Date.now()) / 
    (1000 * 60 * 60 * 24)
  );
  
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
            {test.completed ? (
              <div className="flex items-center text-sm text-green-500">
                <CheckCircleIcon className="h-3 w-3 mr-1" />
                <span>Completed • Score: {test.score || 85}%</span>
              </div>
            ) : (
              <div className="flex items-center text-sm text-gray-500">
                <CalendarIcon className="h-3 w-3 mr-1" />
                <span>{daysLeft > 0 ? `${daysLeft} days left` : "Due today"}</span>
              </div>
            )}
          </div>
          <Badge className="ml-auto" variant={test.completed ? "outline" : "secondary"}>
            {test.completed ? "✓ Completed" : `+${test.question_count * 5} XP`}
          </Badge>
        </div>
        
        <div className="flex justify-between mb-3 text-xs">
          <span className="bg-gray-100 px-2 py-1 rounded-full">
            📖 Pages {test.pages_from}-{test.pages_to}
          </span>
          <span className="bg-gray-100 px-2 py-1 rounded-full">
            ❓ {test.question_count} questions
          </span>
        </div>
        
        <Button 
          className="w-full" 
          size="sm"
          variant={test.completed ? "outline" : "default"}
        >
          {test.completed ? "Review Test" : "Practice Now"}
        </Button>
      </CardContent>
    </Card>
  );
}
