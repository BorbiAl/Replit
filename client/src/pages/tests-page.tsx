import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { 
  CheckCircle, 
  Clock, 
  Filter,
  Search 
} from "lucide-react";
import TestPreview from "@/components/test/test-preview";
import { Input } from "@/components/ui/input";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuCheckboxItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

export default function TestsPage() {
  const [testPreviewId, setTestPreviewId] = useState<number | null>(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [filterCompleted, setFilterCompleted] = useState<boolean | null>(null);
  
  const { data: tests = [] } = useQuery({
    queryKey: ["/api/tests"],
  });
  
  // Filter tests based on search and completion status
  const filteredTests = tests.filter(test => {
    const matchesSearch = test.title.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesCompleted = filterCompleted === null || test.is_completed === filterCompleted;
    return matchesSearch && matchesCompleted;
  });
  
  const completedTests = filteredTests.filter(test => test.is_completed);
  const pendingTests = filteredTests.filter(test => !test.is_completed);
  
  const selectedTest = testPreviewId ? tests.find(t => t.id === testPreviewId) : null;
  
  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <h1 className="text-2xl font-bold text-neutral-900">Your Tests</h1>
        
        <div className="flex items-center gap-2">
          <div className="relative flex-1">
            <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-neutral-500" />
            <Input
              type="search"
              placeholder="Search tests..."
              className="pl-8 w-full"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
          
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" size="icon">
                <Filter className="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuCheckboxItem
                checked={filterCompleted === true}
                onCheckedChange={() => setFilterCompleted(filterCompleted === true ? null : true)}
              >
                Completed
              </DropdownMenuCheckboxItem>
              <DropdownMenuCheckboxItem
                checked={filterCompleted === false}
                onCheckedChange={() => setFilterCompleted(filterCompleted === false ? null : false)}
              >
                Pending
              </DropdownMenuCheckboxItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>

      {selectedTest ? (
        <div className="space-y-4">
          <Button 
            variant="ghost" 
            onClick={() => setTestPreviewId(null)}
            className="mb-2"
          >
            &larr; Back to tests
          </Button>
          <TestPreview test={selectedTest} />
        </div>
      ) : (
        <Tabs defaultValue="all">
          <TabsList>
            <TabsTrigger value="all">All Tests</TabsTrigger>
            <TabsTrigger value="pending">
              Pending ({pendingTests.length})
            </TabsTrigger>
            <TabsTrigger value="completed">
              Completed ({completedTests.length})
            </TabsTrigger>
          </TabsList>
          
          <TabsContent value="all" className="mt-6">
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
              {filteredTests.length > 0 ? (
                filteredTests.map(test => (
                  <Card 
                    key={test.id} 
                    className={`border-l-4 ${test.is_completed ? 'border-l-green-500' : 'border-l-blue-500'}`}
                  >
                    <CardHeader className="pb-2">
                      <div className="flex justify-between items-start">
                        <CardTitle className="text-base">{test.title}</CardTitle>
                        <span 
                          className={`
                            text-xs font-semibold px-2 py-1 rounded
                            ${test.is_completed 
                              ? 'bg-green-100 text-green-800' 
                              : 'bg-blue-100 text-blue-800'
                            }
                          `}
                        >
                          {test.is_completed ? 'Completed' : 'Pending'}
                        </span>
                      </div>
                      <CardDescription>
                        Pages {test.pages_from}-{test.pages_to} • {test.question_count} questions
                      </CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="flex justify-between items-center mt-2">
                        <Button 
                          className="px-4 py-2 bg-primary text-white text-sm font-bold rounded-full hover:bg-primary/90"
                          onClick={() => setTestPreviewId(test.id)}
                        >
                          {test.is_completed ? 'Review Test' : 'Start Test'}
                        </Button>
                        <div className="flex items-center text-xs text-neutral-500">
                          {test.is_completed ? (
                            <CheckCircle className="h-4 w-4 text-green-500 mr-1" />
                          ) : (
                            <Clock className="h-4 w-4 text-blue-500 mr-1" />
                          )}
                          {test.score !== null && test.is_completed ? `Score: ${test.score}%` : ''}
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))
              ) : (
                <div className="col-span-full text-center py-12">
                  <p className="text-neutral-600 mb-4">No tests match your filters</p>
                  <Button onClick={() => {
                    setSearchQuery("");
                    setFilterCompleted(null);
                  }}>
                    Clear Filters
                  </Button>
                </div>
              )}
            </div>
          </TabsContent>
          
          <TabsContent value="pending" className="mt-6">
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
              {pendingTests.length > 0 ? (
                pendingTests.map(test => (
                  <Card 
                    key={test.id} 
                    className="border-l-4 border-l-blue-500"
                  >
                    <CardHeader className="pb-2">
                      <div className="flex justify-between items-start">
                        <CardTitle className="text-base">{test.title}</CardTitle>
                        <span className="bg-blue-100 text-blue-800 text-xs font-semibold px-2 py-1 rounded">
                          Pending
                        </span>
                      </div>
                      <CardDescription>
                        Pages {test.pages_from}-{test.pages_to} • {test.question_count} questions
                      </CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="flex justify-between items-center mt-2">
                        <Button 
                          className="px-4 py-2 bg-primary text-white text-sm font-bold rounded-full hover:bg-primary/90"
                          onClick={() => setTestPreviewId(test.id)}
                        >
                          Start Test
                        </Button>
                        <div className="flex items-center text-xs text-neutral-500">
                          <Clock className="h-4 w-4 text-blue-500 mr-1" />
                          {test.exam_date ? new Date(test.exam_date).toLocaleDateString() : 'Not scheduled'}
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))
              ) : (
                <div className="col-span-full text-center py-12">
                  <p className="text-neutral-600">No pending tests found</p>
                </div>
              )}
            </div>
          </TabsContent>
          
          <TabsContent value="completed" className="mt-6">
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
              {completedTests.length > 0 ? (
                completedTests.map(test => (
                  <Card 
                    key={test.id} 
                    className="border-l-4 border-l-green-500"
                  >
                    <CardHeader className="pb-2">
                      <div className="flex justify-between items-start">
                        <CardTitle className="text-base">{test.title}</CardTitle>
                        <span className="bg-green-100 text-green-800 text-xs font-semibold px-2 py-1 rounded">
                          Completed
                        </span>
                      </div>
                      <CardDescription>
                        Pages {test.pages_from}-{test.pages_to} • {test.question_count} questions
                      </CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="flex justify-between items-center mt-2">
                        <Button 
                          className="px-4 py-2 bg-primary text-white text-sm font-bold rounded-full hover:bg-primary/90"
                          onClick={() => setTestPreviewId(test.id)}
                        >
                          Review Test
                        </Button>
                        <div className="flex items-center text-xs text-neutral-700 font-semibold">
                          <CheckCircle className="h-4 w-4 text-green-500 mr-1" />
                          Score: {test.score !== null ? `${test.score}%` : 'N/A'}
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))
              ) : (
                <div className="col-span-full text-center py-12">
                  <p className="text-neutral-600">No completed tests yet</p>
                </div>
              )}
            </div>
          </TabsContent>
        </Tabs>
      )}
    </div>
  );
}
