import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { 
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Test } from "@shared/schema";
import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { apiRequest, queryClient } from "@/lib/queryClient";
import { useToast } from "@/hooks/use-toast";
import { Loader2 } from "lucide-react";

interface TestPreviewProps {
  test: Test;
}

// Mock question types for the test preview
const questionTypes = [
  {
    type: "multiple-choice",
    question: "What is Newton's First Law of Motion?",
    options: [
      "An object at rest stays at rest, and an object in motion stays in motion unless acted upon by an external force.",
      "Force equals mass times acceleration.",
      "For every action, there is an equal and opposite reaction.",
      "Energy cannot be created or destroyed, only transformed."
    ],
    correctAnswer: 0
  },
  {
    type: "fill-in-blank",
    question: "The unit of force in the International System of Units is the ____________.",
    correctAnswer: "Newton"
  },
  {
    type: "matching",
    question: "Match each quantity with its correct SI unit:",
    pairs: [
      { item: "Force", match: "Newton (N)" },
      { item: "Energy", match: "Joule (J)" },
      { item: "Power", match: "Watt (W)" }
    ],
    options: ["Newton (N)", "Joule (J)", "Watt (W)", "Pascal (Pa)"]
  }
];

export default function TestPreview({ test }: TestPreviewProps) {
  const [userAnswers, setUserAnswers] = useState<Record<number, any>>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { toast } = useToast();
  
  const completeMutation = useMutation({
    mutationFn: async (data: { is_completed: boolean; score?: number }) => {
      const res = await apiRequest("PATCH", `/api/tests/${test.id}`, data);
      return res.json();
    },
    onSuccess: () => {
      toast({
        title: "Test completed",
        description: "Your test has been submitted successfully.",
      });
      queryClient.invalidateQueries({ queryKey: ["/api/tests"] });
      queryClient.invalidateQueries({ queryKey: ["/api/tests/upcoming"] });
    },
    onError: (error) => {
      toast({
        title: "Error",
        description: error.message,
        variant: "destructive",
      });
    }
  });
  
  const handleSubmit = () => {
    setIsSubmitting(true);
    // In a real implementation, we would calculate the actual score based on answers
    // For now, we'll use a random score between 60-100
    const randomScore = Math.floor(Math.random() * 41) + 60;
    
    completeMutation.mutate({
      is_completed: true,
      score: randomScore
    });
  };
  
  const handleAnswerChange = (questionIndex: number, answer: any) => {
    setUserAnswers({
      ...userAnswers,
      [questionIndex]: answer
    });
  };
  
  // Generate a set of questions based on the test configuration
  const generateQuestions = () => {
    // For this framework, we'll just use the mock question types
    // and generate the appropriate number of questions
    const questions = [];
    
    for (let i = 0; i < Math.min(test.question_count, 5); i++) {
      // Cycle through the question types
      const questionType = questionTypes[i % questionTypes.length];
      questions.push({
        ...questionType,
        id: i
      });
    }
    
    return questions;
  };
  
  const questions = generateQuestions();
  
  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
        <CardTitle className="text-xl font-bold">{test.title}</CardTitle>
        <div className="flex items-center space-x-2">
          <span className="text-sm text-neutral-700">Time: 30 minutes</span>
          {!test.is_completed && (
            <Button 
              onClick={handleSubmit}
              disabled={completeMutation.isPending || isSubmitting}
              className="bg-primary text-white text-sm font-bold rounded-full hover:bg-primary/90"
            >
              {completeMutation.isPending || isSubmitting ? (
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              ) : null}
              Submit Test
            </Button>
          )}
        </div>
      </CardHeader>
      <CardContent>
        <div className="space-y-8">
          {questions.map((question, index) => (
            <div key={index} className="border border-neutral-200 rounded-lg p-4">
              <div className="flex items-start mb-4">
                <span className="w-6 h-6 rounded-full bg-primary/10 text-primary text-sm flex items-center justify-center font-bold mr-2">
                  {index + 1}
                </span>
                <h3 className="font-semibold text-neutral-900">{question.question}</h3>
              </div>
              
              {question.type === "multiple-choice" && (
                <div className="pl-8 space-y-2">
                  <RadioGroup 
                    value={userAnswers[index]?.toString()} 
                    onValueChange={(value) => handleAnswerChange(index, parseInt(value))}
                    disabled={test.is_completed}
                  >
                    {question.options.map((option, optionIndex) => (
                      <div className="flex items-center space-x-2" key={optionIndex}>
                        <RadioGroupItem value={optionIndex.toString()} id={`q${index}-${optionIndex}`} />
                        <Label htmlFor={`q${index}-${optionIndex}`}>{option}</Label>
                      </div>
                    ))}
                  </RadioGroup>
                </div>
              )}
              
              {question.type === "fill-in-blank" && (
                <div className="pl-8">
                  <Input
                    placeholder="Your answer"
                    value={userAnswers[index] || ""}
                    onChange={(e) => handleAnswerChange(index, e.target.value)}
                    disabled={test.is_completed}
                    className="w-full md:w-64"
                  />
                </div>
              )}
              
              {question.type === "matching" && (
                <div className="pl-8 grid md:grid-cols-2 gap-4">
                  <div className="space-y-3">
                    {question.pairs.map((pair, pairIndex) => (
                      <div className="flex items-center" key={pairIndex}>
                        <span className="font-semibold mr-2">{String.fromCharCode(65 + pairIndex)}.</span>
                        <span>{pair.item}</span>
                      </div>
                    ))}
                  </div>
                  <div className="space-y-3">
                    {question.pairs.map((pair, pairIndex) => (
                      <Select
                        key={pairIndex}
                        value={userAnswers[index]?.[pairIndex] || ""}
                        onValueChange={(value) => {
                          const currentPairAnswers = userAnswers[index] || {};
                          handleAnswerChange(index, {
                            ...currentPairAnswers,
                            [pairIndex]: value
                          });
                        }}
                        disabled={test.is_completed}
                      >
                        <SelectTrigger>
                          <SelectValue placeholder="-- Select unit --" />
                        </SelectTrigger>
                        <SelectContent>
                          {question.options.map((option, optionIndex) => (
                            <SelectItem key={optionIndex} value={option}>
                              {option}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    ))}
                  </div>
                </div>
              )}
              
              {test.is_completed && (
                <div className="pl-8 mt-4 pt-4 border-t border-dashed border-neutral-200">
                  <p className="text-sm font-semibold text-neutral-700">
                    Sample Answer: <span className="text-primary">{
                      question.type === "multiple-choice" 
                        ? question.options[question.correctAnswer]
                        : question.type === "matching"
                          ? "Match the pairs correctly"
                          : question.correctAnswer
                    }</span>
                  </p>
                </div>
              )}
            </div>
          ))}
          
          {test.is_completed && test.score !== null && (
            <div className="bg-green-50 border border-green-200 rounded-lg p-4 text-center">
              <h3 className="font-bold text-lg text-green-800">Test Completed</h3>
              <p className="text-green-700">Your score: <span className="text-2xl font-bold">{test.score}%</span></p>
              <p className="text-sm text-green-600 mt-2">You've earned {test.score} XP!</p>
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  );
}
