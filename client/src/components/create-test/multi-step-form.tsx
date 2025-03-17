import { useState } from "react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Calendar } from "@/components/ui/calendar";
import { format } from "date-fns";
import { CalendarIcon, Loader2 } from "lucide-react";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { cn } from "@/lib/utils";
import { InsertTest } from "@shared/schema";
import { useMutation, useQuery } from "@tanstack/react-query";
import { apiRequest, queryClient } from "@/lib/queryClient";
import { useToast } from "@/hooks/use-toast";

const steps = [
  { id: 1, name: "Grade & Subject" },
  { id: 2, name: "Textbook" },
  { id: 3, name: "Pages & Content" },
  { id: 4, name: "Scheduling" },
];

type FormData = {
  grade_id: number;
  subject_id: number;
  textbook_id: number;
  title: string;
  pages_from: number;
  pages_to: number;
  question_count: number;
  exam_date: Date | null;
  scheduled_reminders: boolean;
};

export default function MultiStepForm() {
  const [step, setStep] = useState(1);
  const { toast } = useToast();
  
  // Fetch data from API
  const { data: grades = [] } = useQuery({
    queryKey: ["/api/grades"],
  });
  
  const { data: subjects = [] } = useQuery({
    queryKey: ["/api/subjects"],
  });
  
  const [selectedGrade, setSelectedGrade] = useState<number | null>(null);
  const [selectedSubject, setSelectedSubject] = useState<number | null>(null);
  
  // Only fetch textbooks when both grade and subject are selected
  const { data: textbooks = [] } = useQuery({
    queryKey: ["/api/textbooks", { subject_id: selectedSubject, grade_id: selectedGrade }],
    enabled: !!selectedGrade && !!selectedSubject,
  });
  
  const form = useForm<FormData>({
    resolver: zodResolver(
      z.object({
        grade_id: z.number().positive(),
        subject_id: z.number().positive(),
        textbook_id: z.number().positive(),
        title: z.string().min(3, "Title must be at least 3 characters"),
        pages_from: z.number().positive("Must be a positive number"),
        pages_to: z.number().positive("Must be a positive number")
          .refine(val => val >= form.getValues().pages_from, {
            message: "End page must be greater than or equal to start page",
          }),
        question_count: z.number().int().min(1).max(50),
        exam_date: z.date().nullable(),
        scheduled_reminders: z.boolean().default(true),
      })
    ),
    defaultValues: {
      grade_id: 0,
      subject_id: 0,
      textbook_id: 0,
      title: "",
      pages_from: 1,
      pages_to: 10,
      question_count: 10,
      exam_date: null,
      scheduled_reminders: true,
    },
  });
  
  const createTestMutation = useMutation({
    mutationFn: async (data: FormData) => {
      const testData: Partial<InsertTest> = {
        ...data,
        title: data.title,
      };
      const res = await apiRequest("POST", "/api/tests", testData);
      return res.json();
    },
    onSuccess: () => {
      toast({
        title: "Test created",
        description: "Your test has been created successfully.",
      });
      // Reset form
      form.reset();
      setStep(1);
      // Invalidate test queries to refetch data
      queryClient.invalidateQueries({ queryKey: ["/api/tests"] });
      queryClient.invalidateQueries({ queryKey: ["/api/tests/upcoming"] });
    },
    onError: (error) => {
      toast({
        title: "Failed to create test",
        description: error.message,
        variant: "destructive",
      });
    }
  });
  
  const onSubmit = (data: FormData) => {
    if (step < steps.length) {
      setStep(step + 1);
    } else {
      createTestMutation.mutate(data);
    }
  };
  
  // Handle grade selection
  const handleGradeSelect = (id: number) => {
    form.setValue("grade_id", id);
    setSelectedGrade(id);
  };
  
  // Handle subject selection
  const handleSubjectSelect = (id: number) => {
    form.setValue("subject_id", id);
    setSelectedSubject(id);
  };
  
  // Handle textbook selection
  const handleTextbookSelect = (id: number, name: string) => {
    form.setValue("textbook_id", id);
    form.setValue("title", `${subjects.find(s => s.id === selectedSubject)?.name}: ${name}`);
  };
  
  // Get the selected textbook to display metadata
  const selectedTextbook = textbooks.find(t => t.id === form.getValues().textbook_id);
  
  return (
    <Card>
      <CardContent className="pt-6">
        {/* Step Progress Indicator */}
        <div className="flex mb-6">
          {steps.map((s, i) => (
            <div key={s.id} className="flex-1 relative">
              <div className="flex items-center">
                <div className={`w-8 h-8 ${step >= s.id ? 'bg-primary text-white' : 'bg-neutral-200 text-neutral-700'} rounded-full flex items-center justify-center font-bold text-sm z-10`}>
                  {s.id}
                </div>
                {i < steps.length - 1 && (
                  <div className={`flex-1 h-1 ${step > s.id ? 'bg-primary' : 'bg-neutral-200'}`}></div>
                )}
              </div>
              <span className={`absolute -left-1 mt-2 text-xs font-semibold ${step >= s.id ? 'text-primary' : 'text-neutral-700'}`}>
                {s.name}
              </span>
            </div>
          ))}
        </div>
        
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)}>
            {/* Step 1: Grade & Subject */}
            {step === 1 && (
              <div>
                <h3 className="text-lg font-bold text-neutral-900 mb-4">Select Your Grade and Subject</h3>
                
                {/* Grade Selection */}
                <div className="mb-6">
                  <FormField
                    control={form.control}
                    name="grade_id"
                    render={() => (
                      <FormItem>
                        <FormLabel className="block text-neutral-700 font-semibold mb-2">Grade Level</FormLabel>
                        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
                          {grades.map((grade) => (
                            <Button
                              key={grade.id}
                              type="button"
                              variant="outline"
                              className={cn(
                                "p-3 border-2 rounded-lg text-center",
                                form.getValues().grade_id === grade.id 
                                  ? "border-primary bg-primary/5 font-semibold text-primary" 
                                  : "hover:border-primary"
                              )}
                              onClick={() => handleGradeSelect(grade.id)}
                            >
                              {grade.name}
                            </Button>
                          ))}
                        </div>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </div>
                
                {/* Subject Selection */}
                <div className="mb-6">
                  <FormField
                    control={form.control}
                    name="subject_id"
                    render={() => (
                      <FormItem>
                        <FormLabel className="block text-neutral-700 font-semibold mb-2">Subject</FormLabel>
                        <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                          {subjects.map((subject) => (
                            <Button
                              key={subject.id}
                              type="button"
                              variant="outline"
                              className={cn(
                                "p-4 border-2 rounded-lg flex items-center",
                                form.getValues().subject_id === subject.id 
                                  ? "border-primary bg-primary/5 text-primary font-semibold" 
                                  : "hover:border-primary"
                              )}
                              onClick={() => handleSubjectSelect(subject.id)}
                            >
                              <span className={`w-8 h-8 rounded-full bg-${subject.color}/10 text-${subject.color} flex items-center justify-center mr-3`}>
                                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                                </svg>
                              </span>
                              <span>{subject.name}</span>
                            </Button>
                          ))}
                        </div>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </div>
              </div>
            )}
            
            {/* Step 2: Textbook */}
            {step === 2 && (
              <div>
                <h3 className="text-lg font-bold text-neutral-900 mb-4">Select Your Textbook</h3>
                
                <FormField
                  control={form.control}
                  name="textbook_id"
                  render={() => (
                    <FormItem>
                      <div className="grid gap-4">
                        {textbooks.length > 0 ? (
                          textbooks.map((textbook) => (
                            <Button
                              key={textbook.id}
                              type="button"
                              variant="outline"
                              className={cn(
                                "p-4 border-2 rounded-lg flex items-center justify-between w-full",
                                form.getValues().textbook_id === textbook.id 
                                  ? "border-primary bg-primary/5" 
                                  : "hover:border-primary"
                              )}
                              onClick={() => handleTextbookSelect(textbook.id, textbook.name)}
                            >
                              <div className="flex items-center">
                                <span className="w-10 h-10 rounded-lg bg-primary/10 text-primary flex items-center justify-center mr-3">
                                  <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                                  </svg>
                                </span>
                                <div>
                                  <h4 className="font-semibold text-neutral-900">{textbook.name}</h4>
                                  <p className="text-sm text-neutral-600">{textbook.total_pages} pages</p>
                                </div>
                              </div>
                              {form.getValues().textbook_id === textbook.id && (
                                <span className="text-primary">
                                  <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                                  </svg>
                                </span>
                              )}
                            </Button>
                          ))
                        ) : (
                          <div className="text-center py-8 bg-neutral-50 rounded-lg border-2 border-dashed border-neutral-200">
                            <p className="text-neutral-600 mb-2">No textbooks available for the selected grade and subject</p>
                            <Button type="button" variant="outline" onClick={() => setStep(1)}>
                              Go back and change your selection
                            </Button>
                          </div>
                        )}
                      </div>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>
            )}
            
            {/* Step 3: Pages & Content */}
            {step === 3 && (
              <div>
                <h3 className="text-lg font-bold text-neutral-900 mb-4">Select Pages & Question Count</h3>
                
                <div className="space-y-4">
                  <FormField
                    control={form.control}
                    name="title"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Test Title</FormLabel>
                        <FormControl>
                          <Input {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  
                  <div className="grid grid-cols-2 gap-4">
                    <FormField
                      control={form.control}
                      name="pages_from"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Starting Page</FormLabel>
                          <FormControl>
                            <Input 
                              type="number" 
                              min={1} 
                              max={selectedTextbook?.total_pages || 100}
                              {...field}
                              onChange={e => field.onChange(parseInt(e.target.value))}
                            />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                    
                    <FormField
                      control={form.control}
                      name="pages_to"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Ending Page</FormLabel>
                          <FormControl>
                            <Input 
                              type="number" 
                              min={form.getValues().pages_from} 
                              max={selectedTextbook?.total_pages || 100}
                              {...field}
                              onChange={e => field.onChange(parseInt(e.target.value))}
                            />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </div>
                  
                  <FormField
                    control={form.control}
                    name="question_count"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Number of Questions</FormLabel>
                        <FormControl>
                          <Input 
                            type="number" 
                            min={1} 
                            max={50}
                            {...field}
                            onChange={e => field.onChange(parseInt(e.target.value))}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  
                  {selectedTextbook && (
                    <div className="mt-4 p-4 bg-primary/5 border border-primary/20 rounded-lg">
                      <p className="text-sm text-neutral-700">
                        <strong>Selected Textbook:</strong> {selectedTextbook.name}
                      </p>
                      <p className="text-sm text-neutral-700">
                        <strong>Total Pages:</strong> {selectedTextbook.total_pages}
                      </p>
                    </div>
                  )}
                </div>
              </div>
            )}
            
            {/* Step 4: Scheduling */}
            {step === 4 && (
              <div>
                <h3 className="text-lg font-bold text-neutral-900 mb-4">Schedule Your Test</h3>
                
                <div className="space-y-4">
                  <FormField
                    control={form.control}
                    name="exam_date"
                    render={({ field }) => (
                      <FormItem className="flex flex-col">
                        <FormLabel>Exam Date</FormLabel>
                        <Popover>
                          <PopoverTrigger asChild>
                            <FormControl>
                              <Button
                                variant={"outline"}
                                className={cn(
                                  "w-full pl-3 text-left font-normal",
                                  !field.value && "text-muted-foreground"
                                )}
                              >
                                {field.value ? (
                                  format(field.value, "PPP")
                                ) : (
                                  <span>Pick a date</span>
                                )}
                                <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                              </Button>
                            </FormControl>
                          </PopoverTrigger>
                          <PopoverContent className="w-auto p-0" align="start">
                            <Calendar
                              mode="single"
                              selected={field.value || undefined}
                              onSelect={field.onChange}
                              disabled={(date) =>
                                date < new Date() || date > new Date(new Date().setFullYear(new Date().getFullYear() + 1))
                              }
                              initialFocus
                            />
                          </PopoverContent>
                        </Popover>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  
                  <FormField
                    control={form.control}
                    name="scheduled_reminders"
                    render={({ field }) => (
                      <FormItem className="flex flex-row items-start space-x-3 space-y-0 rounded-md border p-4">
                        <FormControl>
                          <input
                            type="checkbox"
                            checked={field.value}
                            onChange={field.onChange}
                            className="h-4 w-4 text-primary rounded focus:ring-primary"
                          />
                        </FormControl>
                        <div className="space-y-1 leading-none">
                          <FormLabel>Enable Daily Reminders</FormLabel>
                          <p className="text-sm text-neutral-600">
                            You will receive daily reminders to practice this test leading up to your exam date.
                          </p>
                        </div>
                      </FormItem>
                    )}
                  />
                  
                  <div className="p-4 bg-neutral-50 rounded-lg border border-neutral-200">
                    <h4 className="font-semibold mb-2">Test Summary</h4>
                    <ul className="text-sm text-neutral-700 space-y-1">
                      <li><strong>Title:</strong> {form.getValues().title}</li>
                      <li><strong>Pages:</strong> {form.getValues().pages_from} to {form.getValues().pages_to}</li>
                      <li><strong>Questions:</strong> {form.getValues().question_count}</li>
                      <li>
                        <strong>Exam Date:</strong> {form.getValues().exam_date 
                          ? format(form.getValues().exam_date, "PPP") 
                          : "Not scheduled"}
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
            )}
            
            <div className="flex justify-between mt-6">
              {step > 1 && (
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setStep(step - 1)}
                >
                  Back
                </Button>
              )}
              
              <Button
                type="submit"
                className={`${step === 1 ? 'ml-auto' : ''} px-6 py-2 bg-primary text-white font-bold rounded-full hover:bg-primary/90`}
                disabled={createTestMutation.isPending}
              >
                {createTestMutation.isPending ? (
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                ) : null}
                {step < steps.length ? "Continue" : "Create Test"}
              </Button>
            </div>
          </form>
        </Form>
      </CardContent>
    </Card>
  );
}
