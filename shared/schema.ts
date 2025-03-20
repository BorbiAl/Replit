import { z } from 'zod';

// User Schema
export const userSchema = z.object({
  id: z.number(),
  username: z.string().min(3).max(50),
  password: z.string().min(6),
  name: z.string().min(1).max(100).optional(),
  email: z.string().email().optional(),
  createdAt: z.coerce.date(),
  streakCount: z.number().default(0),
  points: z.number().default(0),
  level: z.number().default(1),
  lastActive: z.coerce.date().optional(),
});

export const insertUserSchema = userSchema.omit({ 
  id: true,
  createdAt: true,
  streakCount: true,
  points: true,
  level: true,
  lastActive: true
});

export type User = z.infer<typeof userSchema>;
export type InsertUser = z.infer<typeof insertUserSchema>;

// Login Schema
export const loginSchema = z.object({
  username: z.string().min(3).max(50),
  password: z.string().min(6)
});

export type LoginData = z.infer<typeof loginSchema>;

// Profile Schema (User without sensitive data)
export const profileSchema = userSchema.omit({ password: true });
export type Profile = z.infer<typeof profileSchema>;

// Subject Schema
export const subjectSchema = z.object({
  id: z.number(),
  name: z.string().min(1).max(100),
  emoji: z.string().min(1).max(10),
  colorHex: z.string().regex(/^#[0-9A-F]{6}$/i)
});

export const insertSubjectSchema = subjectSchema.omit({ id: true });

export type Subject = z.infer<typeof subjectSchema>;
export type InsertSubject = z.infer<typeof insertSubjectSchema>;

// Textbook Schema
export const textbookSchema = z.object({
  id: z.number(),
  title: z.string().min(1).max(200),
  author: z.string().max(100).optional(),
  subjectId: z.number(),
  gradeLevel: z.number().optional(),
  totalPages: z.number().optional()
});

export const insertTextbookSchema = textbookSchema.omit({ id: true });

export type Textbook = z.infer<typeof textbookSchema>;
export type InsertTextbook = z.infer<typeof insertTextbookSchema>;

// Test Schema
export const testSchema = z.object({
  id: z.number(),
  title: z.string().min(1).max(200),
  createdBy: z.number(),
  subjectId: z.number(),
  textbookId: z.number(),
  pagesFrom: z.number().min(1),
  pagesTo: z.number().min(1),
  questionCount: z.number().min(1),
  createdAt: z.coerce.date(),
  examDate: z.coerce.date().optional(),
  isCompleted: z.boolean().default(false),
  score: z.number().min(0).max(100).optional()
});

export const insertTestSchema = testSchema.omit({ 
  id: true,
  createdAt: true,
  isCompleted: true,
  score: true
});

export type Test = z.infer<typeof testSchema>;
export type InsertTest = z.infer<typeof insertTestSchema>;

// Achievement Schema
export const achievementSchema = z.object({
  id: z.number(),
  name: z.string().min(1).max(100),
  description: z.string().min(1).max(500),
  emoji: z.string().min(1).max(10),
  requiredPoints: z.number().min(0),
  isHidden: z.boolean().default(false)
});

export const insertAchievementSchema = achievementSchema.omit({ id: true });

export type Achievement = z.infer<typeof achievementSchema>;
export type InsertAchievement = z.infer<typeof insertAchievementSchema>;

// User Achievement Schema
export const userAchievementSchema = z.object({
  id: z.number(),
  userId: z.number(),
  achievementId: z.number(),
  earnedAt: z.coerce.date()
});

export const insertUserAchievementSchema = userAchievementSchema.omit({ id: true, earnedAt: true });

export type UserAchievement = z.infer<typeof userAchievementSchema>;
export type InsertUserAchievement = z.infer<typeof insertUserAchievementSchema>;

// Leaderboard Entry
export const leaderboardEntrySchema = z.object({
  userId: z.number(),
  username: z.string(),
  name: z.string().optional(),
  points: z.number(),
  level: z.number(),
  streak: z.number(),
  rank: z.number()
});

export type LeaderboardEntry = z.infer<typeof leaderboardEntrySchema>;