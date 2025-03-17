import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import { CalendarIcon, PlusCircleIcon } from "lucide-react";
import { 
  Table, 
  TableBody, 
  TableCaption, 
  TableCell, 
  TableHead, 
  TableHeader, 
  TableRow 
} from "@/components/ui/table";
import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { format } from "date-fns";
import { cn } from "@/lib/utils";

export default function StudyPlanPage() {
  const [date, setDate] = useState<Date | undefined>(new Date());
  
  const { data: tests = [] } = useQuery({
    queryKey: ["/api/tests"],
  });
  
  // Filter tests by selected date
  const testsOnSelectedDate = tests.filter(test => {
    if (!test.exam_date || !date) return false;
    const testDate = new Date(test.exam_date);
    return (
      testDate.getDate() === date.getDate() &&
      testDate.getMonth() === date.getMonth() &&
      testDate.getFullYear() === date.getFullYear()
    );
  });
  
  // Group tests by exam date for the calendar view
  const testDates = tests.reduce<Record<string, number>>((acc, test) => {
    if (!test.exam_date) return acc;
    
    const dateKey = format(new Date(test.exam_date), "yyyy-MM-dd");
    acc[dateKey] = (acc[dateKey] || 0) + 1;
    
    return acc;
  }, {});
  
  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-neutral-900">Study Plan</h1>
      
      <div className="grid md:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <CardTitle>Study Calendar</CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            <Calendar
              mode="single"
              selected={date}
              onSelect={setDate}
              className="rounded-md border"
              components={{
                DayContent: (props) => {
                  const dateKey = format(props.date, "yyyy-MM-dd");
                  const hasTests = testDates[dateKey] > 0;
                  
                  return (
                    <div className="relative flex h-8 w-8 items-center justify-center">
                      {props.children}
                      {hasTests && (
                        <div className="absolute bottom-1 right-0 h-1.5 w-1.5 rounded-full bg-primary" />
                      )}
                    </div>
                  );
                }
              }}
            />
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle>
              {date ? (
                <div className="flex items-center">
                  <CalendarIcon className="mr-2 h-4 w-4" />
                  {format(date, "MMMM d, yyyy")}
                </div>
              ) : (
                "Selected Date"
              )}
            </CardTitle>
            <Popover>
              <PopoverTrigger asChild>
                <Button variant="outline" size="sm">
                  <PlusCircleIcon className="h-4 w-4 mr-2" />
                  Add Test
                </Button>
              </PopoverTrigger>
              <PopoverContent className="w-80">
                <div className="text-sm">
                  To add a test for this date, go to the Dashboard and create a new test.
                </div>
              </PopoverContent>
            </Popover>
          </CardHeader>
          <CardContent>
            {testsOnSelectedDate.length > 0 ? (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Subject</TableHead>
                    <TableHead>Title</TableHead>
                    <TableHead>Status</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {testsOnSelectedDate.map(test => (
                    <TableRow key={test.id}>
                      <TableCell>{test.subject_id}</TableCell>
                      <TableCell>{test.title}</TableCell>
                      <TableCell>
                        <span className={cn(
                          "px-2 py-1 text-xs font-semibold rounded",
                          test.is_completed 
                            ? "bg-green-100 text-green-800" 
                            : "bg-blue-100 text-blue-800"
                        )}>
                          {test.is_completed ? "Completed" : "Scheduled"}
                        </span>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            ) : (
              <div className="text-center py-6 text-neutral-500">
                <p>No tests scheduled for this date</p>
                <Button variant="link" className="mt-2">
                  Create a test
                </Button>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
      
      <Card>
        <CardHeader>
          <CardTitle>Monthly Overview</CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableCaption>Your test schedule for the current month</TableCaption>
            <TableHeader>
              <TableRow>
                <TableHead>Date</TableHead>
                <TableHead>Subject</TableHead>
                <TableHead>Title</TableHead>
                <TableHead>Topics</TableHead>
                <TableHead>Status</TableHead>
                <TableHead className="text-right">Action</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {tests.slice(0, 5).map(test => (
                <TableRow key={test.id}>
                  <TableCell>{test.exam_date ? format(new Date(test.exam_date), "MMM d, yyyy") : "-"}</TableCell>
                  <TableCell>{test.subject_id}</TableCell>
                  <TableCell className="font-medium">{test.title}</TableCell>
                  <TableCell>Pages {test.pages_from}-{test.pages_to}</TableCell>
                  <TableCell>
                    <span className={cn(
                      "px-2 py-1 text-xs font-semibold rounded",
                      test.is_completed 
                        ? "bg-green-100 text-green-800" 
                        : "bg-blue-100 text-blue-800"
                    )}>
                      {test.is_completed ? "Completed" : "Scheduled"}
                    </span>
                  </TableCell>
                  <TableCell className="text-right">
                    <Button variant="ghost" size="sm">
                      View
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
              
              {tests.length === 0 && (
                <TableRow>
                  <TableCell colSpan={6} className="text-center py-6 text-neutral-500">
                    No tests scheduled yet. Create your first test to get started.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
}
