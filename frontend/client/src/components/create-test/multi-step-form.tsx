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
        <h2 className="text-2xl font-bold text-neutral-900 mb-6">Create a New Test</h2>
        
        {/* Step Progress Indicator */}
        <div className="flex items-center justify-center mb-8">
          {steps.map((s, i) => (
            <div key={s.id} className="flex-1 relative text-center">
              <div className="flex items-center">
                <div className={`w-12 h-12 ${step >= s.id ? 'bg-primary text-white' : 'bg-neutral-200 text-neutral-600'} rounded-full flex items-center justify-center font-bold text-lg z-10 mx-auto`}>
                  {s.id}
                </div>
                {i < steps.length - 1 && (
                  <div className={`flex-1 h-1 ${step > s.id ? 'bg-primary' : 'bg-neutral-200'}`}></div>
                )}
              </div>
              <span className={`mt-2 text-sm font-semibold block ${step === s.id ? 'text-primary' : 'text-neutral-600'}`}>
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
                <h3 className="text-xl font-bold text-neutral-900 mb-6 text-center">Select Your Grade and Subject</h3>
                
                {/* Grade Selection */}
                <div className="mb-8">
                  <FormField
                    control={form.control}
                    name="grade_id"
                    render={() => (
                      <FormItem>
                        <FormLabel className="block text-neutral-700 font-semibold mb-3">Grade Level</FormLabel>
                        <div className="grid grid-cols-2 gap-4">
                          {grades.map((grade) => (
                            <Button
                              key={grade.id}
                              type="button"
                              variant="outline"
                              className={cn(
                                "p-4 rounded-lg text-center h-14 text-lg",
                                form.getValues().grade_id === grade.id 
                                  ? "bg-blue-200 border-blue-300 text-blue-800 font-semibold" 
                                  : "bg-blue-100 border-blue-200 hover:bg-blue-200"
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
                <div className="mb-8">
                  <FormField
                    control={form.control}
                    name="subject_id"
                    render={() => (
                      <FormItem>
                        <FormLabel className="block text-neutral-700 font-semibold mb-3">Subject</FormLabel>
                        <div className="grid grid-cols-2 gap-4">
                          {subjects.map((subject) => (
                            <Button
                              key={subject.id}
                              type="button"
                              variant="outline"
                              className={cn(
                                "p-4 rounded-lg flex items-center justify-center h-14",
                                form.getValues().subject_id === subject.id 
                                  ? "bg-blue-200 border-blue-300 text-blue-800 font-semibold" 
                                  : "bg-blue-100 border-blue-200 hover:bg-blue-200"
                              )}
                              onClick={() => handleSubjectSelect(subject.id)}
                            >
                              <span className="flex items-center justify-center">
                                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                                </svg>
                                <span>{subject.name}</span>
                              </span>
                            </Button>
                          ))}
                        </div>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </div>
                
                {/* Continue Button */}
                <div className="flex justify-end">
                  <Button 
                    type="button" 
                    onClick={form.handleSubmit(() => setStep(2))}
                    className="bg-primary text-white rounded-full px-8 py-2 text-lg"
                    disabled={!selectedGrade || !selectedSubject}
                  >
                    Continue
                  </Button>
                </div>
              </div>
            )}
            
            {/* Step 2: Textbook */}
            {step === 2 && (
              <div>
                <h3 className="text-xl font-bold text-neutral-900 mb-6 text-center">Select Your Textbook</h3>
                
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
                                "p-4 border rounded-lg flex items-center justify-between w-full",
                                form.getValues().textbook_id === textbook.id 
                                  ? "bg-blue-200 border-blue-300 text-blue-800" 
                                  : "bg-blue-100 border-blue-200 hover:bg-blue-200"
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
                
                <div className="flex justify-between mt-8">
                  <Button 
                    type="button" 
                    variant="outline"
                    onClick={() => setStep(1)}
                    className="rounded-full px-6"
                  >
                    Back
                  </Button>
                  <Button 
                    type="button" 
                    onClick={form.handleSubmit(() => setStep(3))}
                    className="bg-primary text-white rounded-full px-8"
                    disabled={!form.getValues().textbook_id}
                  >
                    Continue
                  </Button>
                </div>
              </div>
            )}
            
            {/* Step 3: Pages & Content */}
            {step === 3 && (
              <div>
                <h3 className="text-xl font-bold text-neutral-900 mb-6 text-center">Select Pages & Question Count</h3>
                
                <div className="space-y-4">
                  <FormField
                    control={form.control}
                    name="title"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Test Title</FormLabel>
                        <FormControl>
                          <Input {...field} className="rounded-lg" />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  
                  <div className="grid grid-cols-2 gap-6">
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
                              className="rounded-lg"
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
                              className="rounded-lg"
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
                            className="rounded-lg"
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </div>
                
                <div className="flex justify-between mt-8">
                  <Button 
                    type="button" 
                    variant="outline"
                    onClick={() => setStep(2)}
                    className="rounded-full px-6"
                  >
                    Back
                  </Button>
                  <Button 
                    type="button" 
                    onClick={form.handleSubmit(() => setStep(4))}
                    className="bg-primary text-white rounded-full px-8"
                  >
                    Continue
                  </Button>
                </div>
              </div>
            )}
            
            {/* Step 4: Scheduling */}
            {step === 4 && (
              <div>
                <h3 className="text-xl font-bold text-neutral-900 mb-6 text-center">Schedule Your Test</h3>
                
                <div className="space-y-6">
                  <FormField
                    control={form.control}
                    name="exam_date"
                    render={({ field }) => (
                      <FormItem className="flex flex-col">
                        <FormLabel>Test Date</FormLabel>
                        <Popover>
                          <PopoverTrigger asChild>
                            <FormControl>
                              <Button
                                variant="outline"
                                className={cn(
                                  "w-full rounded-lg pl-3 text-left font-normal",
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
                          <PopoverContent className="w-auto p-0" align="center">
                            <Calendar
                              mode="single"
                              selected={field.value || undefined}
                              onSelect={field.onChange}
                              disabled={(date) => date < new Date()}
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
                            className="h-5 w-5 accent-primary mt-1"
                          />
                        </FormControl>
                        <div className="space-y-1 leading-none">
                          <FormLabel>
                            Schedule daily reminders
                          </FormLabel>
                          <p className="text-sm text-muted-foreground">
                            Get daily notifications to help you prepare for this test
                          </p>
                        </div>
                      </FormItem>
                    )}
                  />
                </div>
                
                <div className="flex justify-between mt-8">
                  <Button 
                    type="button" 
                    variant="outline"
                    onClick={() => setStep(3)}
                    className="rounded-full px-6"
                  >
                    Back
                  </Button>
                  <Button 
                    type="submit" 
                    className="bg-primary text-white rounded-full px-8"
                    disabled={createTestMutation.isPending}
                  >
                    {createTestMutation.isPending ? (
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    ) : null}
                    Create Test
                  </Button>
                </div>
              </div>
            )}
          </form>
        </Form>
      </CardContent>
    </Card>
  );
}
