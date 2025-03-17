import type { Express } from "express";
import { createServer, type Server } from "http";
import { storage } from "./storage";
import { z } from "zod";
import { setupAuth } from "./auth";
import { insertTestSchema } from "@shared/schema";

export async function registerRoutes(app: Express): Promise<Server> {
  // Set up authentication routes
  setupAuth(app);

  // Get all subjects
  app.get("/api/subjects", async (req, res) => {
    const subjects = await storage.getSubjects();
    res.json(subjects);
  });

  // Get all grades
  app.get("/api/grades", async (req, res) => {
    const grades = await storage.getGrades();
    res.json(grades);
  });

  // Get textbooks (optionally filtered by subject and grade)
  app.get("/api/textbooks", async (req, res) => {
    const subjectId = req.query.subject_id ? Number(req.query.subject_id) : undefined;
    const gradeId = req.query.grade_id ? Number(req.query.grade_id) : undefined;
    
    const textbooks = await storage.getTextbooks(subjectId, gradeId);
    res.json(textbooks);
  });

  // Get a specific textbook
  app.get("/api/textbooks/:id", async (req, res) => {
    const id = Number(req.params.id);
    const textbook = await storage.getTextbook(id);
    
    if (!textbook) {
      return res.status(404).json({ message: "Textbook not found" });
    }
    
    res.json(textbook);
  });

  // Get all tests for the current user
  app.get("/api/tests", async (req, res) => {
    if (!req.isAuthenticated()) {
      return res.status(401).json({ message: "Unauthorized" });
    }
    
    const tests = await storage.getTests(req.user.id);
    res.json(tests);
  });

  // Get upcoming tests for the current user
  app.get("/api/tests/upcoming", async (req, res) => {
    if (!req.isAuthenticated()) {
      return res.status(401).json({ message: "Unauthorized" });
    }
    
    const tests = await storage.getUpcomingTests(req.user.id);
    res.json(tests);
  });

  // Get a specific test
  app.get("/api/tests/:id", async (req, res) => {
    if (!req.isAuthenticated()) {
      return res.status(401).json({ message: "Unauthorized" });
    }
    
    const id = Number(req.params.id);
    const test = await storage.getTest(id);
    
    if (!test) {
      return res.status(404).json({ message: "Test not found" });
    }
    
    if (test.user_id !== req.user.id) {
      return res.status(403).json({ message: "Forbidden" });
    }
    
    res.json(test);
  });

  // Create a new test
  app.post("/api/tests", async (req, res) => {
    if (!req.isAuthenticated()) {
      return res.status(401).json({ message: "Unauthorized" });
    }
    
    try {
      const testData = insertTestSchema.parse(req.body);
      const test = await storage.createTest({
        ...testData,
        user_id: req.user.id
      });
      
      res.status(201).json(test);
    } catch (error) {
      if (error instanceof z.ZodError) {
        return res.status(400).json({ message: "Invalid test data", errors: error.errors });
      }
      throw error;
    }
  });

  // Update a test (complete test, update score)
  app.patch("/api/tests/:id", async (req, res) => {
    if (!req.isAuthenticated()) {
      return res.status(401).json({ message: "Unauthorized" });
    }
    
    const id = Number(req.params.id);
    const test = await storage.getTest(id);
    
    if (!test) {
      return res.status(404).json({ message: "Test not found" });
    }
    
    if (test.user_id !== req.user.id) {
      return res.status(403).json({ message: "Forbidden" });
    }
    
    const schema = z.object({
      is_completed: z.boolean().optional(),
      score: z.number().min(0).max(100).optional(),
    });
    
    try {
      const data = schema.parse(req.body);
      const updatedTest = await storage.updateTest(id, data);
      
      // If test was completed with a score, award XP
      if (data.is_completed && data.score !== undefined) {
        // Award XP based on score (1 point per percentage)
        const xpToAdd = data.score;
        const currentUser = await storage.getUser(req.user.id);
        
        if (currentUser) {
          const newXP = currentUser.xp + xpToAdd;
          await storage.updateUserXP(req.user.id, newXP);
        }
      }
      
      res.json(updatedTest);
    } catch (error) {
      if (error instanceof z.ZodError) {
        return res.status(400).json({ message: "Invalid test data", errors: error.errors });
      }
      throw error;
    }
  });

  // Update user streak
  app.post("/api/streak", async (req, res) => {
    if (!req.isAuthenticated()) {
      return res.status(401).json({ message: "Unauthorized" });
    }
    
    const schema = z.object({
      streak: z.number().min(0).int()
    });
    
    try {
      const { streak } = schema.parse(req.body);
      const updatedUser = await storage.updateUserStreak(req.user.id, streak);
      
      if (!updatedUser) {
        return res.status(404).json({ message: "User not found" });
      }
      
      res.json(updatedUser);
    } catch (error) {
      if (error instanceof z.ZodError) {
        return res.status(400).json({ message: "Invalid streak data", errors: error.errors });
      }
      throw error;
    }
  });

  const httpServer = createServer(app);
  return httpServer;
}
