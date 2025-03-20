import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useQuery } from "@tanstack/react-query";
import { Link } from "wouter";
import { format, isToday, isTomorrow, addDays } from "date-fns";

export default function UpcomingTests() {
  const { data: upcomingTests = [] } = useQuery({
    queryKey: ["/api/tests/upcoming"],
  });
  
  const getDateLabel = (date: string) => {
    const testDate = new Date(date);
    
    if (isToday(testDate)) {
      return { text: 'Today', className: 'bg-danger/10 text-danger' };
    } else if (isTomorrow(testDate)) {
      return { text: 'Tomorrow', className: 'bg-warning/10 text-warning' };
    } else if (testDate <= addDays(new Date(), 3)) {
      return { text: 'Soon', className: 'bg-warning/10 text-warning' };
    } else {
      return { 
        text: format(testDate, 'MMM d'), 
        className: 'bg-neutral-200/50 text-neutral-700' 
      };
    }
  };
  
  return (
    <div className="mb-8">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-xl font-bold text-neutral-900">Upcoming Tests</h2>
        <Link href="/tests">
          <Button variant="link" className="text-primary text-sm font-semibold hover:underline p-0">
            View All
          </Button>
        </Link>
      </div>
      
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {upcomingTests.length > 0 ? (
          upcomingTests.slice(0, 3).map((test) => {
            const dateLabel = test.exam_date ? getDateLabel(test.exam_date) : { 
              text: 'Not scheduled', 
              className: 'bg-neutral-200/50 text-neutral-700' 
            };
            
            return (
              <Card 
                key={test.id}
                className={`bg-white shadow-md p-4 ${
                  dateLabel.text === 'Today' || dateLabel.text === 'Tomorrow'
                    ? 'border-l-4 border-warning'
                    : 'border-l-4 border-neutral-200'
                }`}
              >
                <CardContent className="p-0">
                  <div className="flex justify-between items-start">
                    <div>
                      <h3 className="font-bold text-neutral-900">{test.title}</h3>
                      <p className="text-sm text-neutral-700 mt-1">
                        Pages {test.pages_from}-{test.pages_to} • {test.question_count} questions
                      </p>
                    </div>
                    <span className={`${dateLabel.className} text-xs font-semibold px-2 py-1 rounded`}>
                      {dateLabel.text}
                    </span>
                  </div>
                  <div className="flex justify-between items-center mt-4">
                    <Link href={`/tests/${test.id}`}>
                      <Button className="px-4 py-2 bg-primary text-white text-sm font-bold rounded-full hover:bg-primary/90">
                        Start Test
                      </Button>
                    </Link>
                    <span className="text-xs text-neutral-700">
                      Created: {format(new Date(test.created_at), 'MMM d')}
                    </span>
                  </div>
                </CardContent>
              </Card>
            );
          })
        ) : (
          <div className="col-span-full text-center py-8 bg-white rounded-lg shadow-sm">
            <p className="text-neutral-700 mb-4">You don't have any upcoming tests</p>
            <Button>Create Your First Test</Button>
          </div>
        )}
      </div>
    </div>
  );
}
