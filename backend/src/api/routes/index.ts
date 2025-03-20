import { Express, Request, Response, NextFunction } from 'express';
import { createServer, Server } from 'http';
import { storage } from '../../db/storage';
import { z } from 'zod';

export function registerRoutes(app: Express): Server {
  // Middleware to check if user is authenticated
  const isAuthenticated = (req: Request, res: Response, next: NextFunction) => {
    if (req.isAuthenticated()) {
      return next();
    }
    res.status(401).json({ message: 'Authentication required' });
  };
  // Subject routes
  app.get('/api/subjects', async (req, res) => {
    try {
      const subjects = await storage.getSubjects();
      res.json(subjects);
    } catch (error) {
      console.error('Error fetching subjects:', error);
      res.status(500).json({ message: 'Failed to fetch subjects' });
    }
  });

  app.get('/api/subjects/:id', async (req, res) => {
    try {
      const subject = await storage.getSubject(Number(req.params.id));
      if (!subject) {
        return res.status(404).json({ message: 'Subject not found' });
      }
      res.json(subject);
    } catch (error) {
      console.error('Error fetching subject:', error);
      res.status(500).json({ message: 'Failed to fetch subject' });
    }
  });

  // Textbook routes
  app.get('/api/textbooks', async (req, res) => {
    try {
      const subjectId = req.query.subjectId ? Number(req.query.subjectId) : undefined;
      const textbooks = await storage.getTextbooks(subjectId);
      res.json(textbooks);
    } catch (error) {
      console.error('Error fetching textbooks:', error);
      res.status(500).json({ message: 'Failed to fetch textbooks' });
    }
  });

  app.get('/api/textbooks/:id', async (req, res) => {
    try {
      const textbook = await storage.getTextbook(Number(req.params.id));
      if (!textbook) {
        return res.status(404).json({ message: 'Textbook not found' });
      }
      res.json(textbook);
    } catch (error) {
      console.error('Error fetching textbook:', error);
      res.status(500).json({ message: 'Failed to fetch textbook' });
    }
  });

  app.post('/api/textbooks', isAuthenticated, async (req, res) => {
    try {
      const schema = z.object({
        title: z.string().min(1, 'Title is required'),
        author: z.string().optional(),
        subjectId: z.number().min(1, 'Subject ID is required'),
        gradeLevel: z.number().optional(),
        totalPages: z.number().optional(),
      });

      const validatedData = schema.parse(req.body);
      const textbook = await storage.createTextbook(validatedData);
      res.status(201).json(textbook);
    } catch (error) {
      console.error('Error creating textbook:', error);
      res.status(400).json({ message: 'Failed to create textbook', error });
    }
  });

  // Test routes
  app.get('/api/tests', isAuthenticated, async (req, res) => {
    try {
      const options = {
        userId: req.user?.id,
        subjectId: req.query.subjectId ? Number(req.query.subjectId) : undefined,
        completed: req.query.completed ? req.query.completed === 'true' : undefined,
      };
      
      const tests = await storage.getTests(options);
      res.json(tests);
    } catch (error) {
      console.error('Error fetching tests:', error);
      res.status(500).json({ message: 'Failed to fetch tests' });
    }
  });

  app.get('/api/tests/:id', isAuthenticated, async (req, res) => {
    try {
      const test = await storage.getTest(Number(req.params.id));
      if (!test) {
        return res.status(404).json({ message: 'Test not found' });
      }
      
      // Only allow access to tests created by the current user
      if (test.createdBy !== req.user?.id) {
        return res.status(403).json({ message: 'You do not have permission to view this test' });
      }
      
      res.json(test);
    } catch (error) {
      console.error('Error fetching test:', error);
      res.status(500).json({ message: 'Failed to fetch test' });
    }
  });

  app.post('/api/tests', isAuthenticated, async (req, res) => {
    try {
      const schema = z.object({
        title: z.string().min(1, 'Title is required'),
        subjectId: z.number().min(1, 'Subject ID is required'),
        textbookId: z.number().min(1, 'Textbook ID is required'),
        pagesFrom: z.number().min(1, 'Starting page is required'),
        pagesTo: z.number().min(1, 'Ending page is required'),
        questionCount: z.number().min(1, 'Question count is required'),
        examDate: z.string().optional().transform(val => val ? new Date(val) : undefined),
      });

      const validatedData = schema.parse(req.body);
      
      // Set the current user as the creator
      const testData = {
        ...validatedData,
        createdBy: req.user!.id,
      };
      
      const test = await storage.createTest(testData);
      res.status(201).json(test);
    } catch (error) {
      console.error('Error creating test:', error);
      res.status(400).json({ message: 'Failed to create test', error });
    }
  });

  app.patch('/api/tests/:id/complete', isAuthenticated, async (req, res) => {
    try {
      const testId = Number(req.params.id);
      const test = await storage.getTest(testId);
      
      if (!test) {
        return res.status(404).json({ message: 'Test not found' });
      }
      
      // Only allow the test creator to mark it as complete
      if (test.createdBy !== req.user?.id) {
        return res.status(403).json({ message: 'You do not have permission to update this test' });
      }
      
      const schema = z.object({
        score: z.number().min(0).max(100, 'Score must be between 0 and 100'),
      });
      
      const { score } = schema.parse(req.body);
      const updatedTest = await storage.completeTest(testId, score);
      
      res.json(updatedTest);
    } catch (error) {
      console.error('Error completing test:', error);
      res.status(400).json({ message: 'Failed to complete test', error });
    }
  });

  // Achievement routes
  app.get('/api/achievements', async (req, res) => {
    try {
      // Only return non-hidden achievements unless querying user is authenticated
      const includeHidden = req.isAuthenticated();
      const achievements = await storage.getAchievements(includeHidden);
      res.json(achievements);
    } catch (error) {
      console.error('Error fetching achievements:', error);
      res.status(500).json({ message: 'Failed to fetch achievements' });
    }
  });

  app.get('/api/user/achievements', isAuthenticated, async (req, res) => {
    try {
      const achievements = await storage.getUserAchievements(req.user!.id);
      res.json(achievements);
    } catch (error) {
      console.error('Error fetching user achievements:', error);
      res.status(500).json({ message: 'Failed to fetch user achievements' });
    }
  });

  // Leaderboard route
  app.get('/api/leaderboard', async (req, res) => {
    try {
      const limit = req.query.limit ? Number(req.query.limit) : 10;
      const leaderboard = await storage.getLeaderboard(limit);
      res.json(leaderboard);
    } catch (error) {
      console.error('Error fetching leaderboard:', error);
      res.status(500).json({ message: 'Failed to fetch leaderboard' });
    }
  });

  // User stats route
  app.get('/api/user/stats', isAuthenticated, async (req, res) => {
    try {
      const stats = await storage.getUserStats(req.user!.id);
      if (!stats) {
        return res.status(404).json({ message: 'User stats not found' });
      }
      res.json(stats);
    } catch (error) {
      console.error('Error fetching user stats:', error);
      res.status(500).json({ message: 'Failed to fetch user stats' });
    }
  });

  // Create HTTP server
  const httpServer = createServer(app);
  
  return httpServer;
}