import { pgTable, text, serial, integer, boolean, timestamp, date } from "drizzle-orm/pg-core";
import { createInsertSchema } from "drizzle-zod";
import { z } from "zod";

export const users = pgTable("users", {
  id: serial("id").primaryKey(),
  username: text("username").notNull().unique(),
  password: text("password").notNull(),
  firstName: text("first_name").notNull(),
  lastName: text("last_name").notNull(),
  streak: integer("streak").default(0).notNull(),
  xp: integer("xp").default(0).notNull(),
  level: integer("level").default(1).notNull(),
});

export const subjects = pgTable("subjects", {
  id: serial("id").primaryKey(),
  name: text("name").notNull(),
  icon: text("icon").notNull(),
  color: text("color").notNull(),
});

export const grades = pgTable("grades", {
  id: serial("id").primaryKey(),
  name: text("name").notNull(),
});

export const textbooks = pgTable("textbooks", {
  id: serial("id").primaryKey(),
  name: text("name").notNull(),
  subject_id: integer("subject_id").notNull(),
  grade_id: integer("grade_id").notNull(),
  total_pages: integer("total_pages").notNull(),
});

export const tests = pgTable("tests", {
  id: serial("id").primaryKey(),
  title: text("title").notNull(),
  user_id: integer("user_id").notNull(),
  subject_id: integer("subject_id").notNull(),
  grade_id: integer("grade_id").notNull(),
  textbook_id: integer("textbook_id").notNull(),
  pages_from: integer("pages_from").notNull(),
  pages_to: integer("pages_to").notNull(),
  question_count: integer("question_count").notNull(),
  exam_date: date("exam_date"),
  is_completed: boolean("is_completed").default(false).notNull(),
  score: integer("score"),
  created_at: timestamp("created_at").defaultNow().notNull(),
  scheduled_reminders: boolean("scheduled_reminders").default(true).notNull(),
});

export const insertUserSchema = createInsertSchema(users).pick({
  username: true,
  password: true,
  firstName: true,
  lastName: true,
});

export const insertSubjectSchema = createInsertSchema(subjects);
export const insertGradeSchema = createInsertSchema(grades);
export const insertTextbookSchema = createInsertSchema(textbooks);

export const insertTestSchema = createInsertSchema(tests).omit({
  id: true,
  is_completed: true,
  score: true,
  created_at: true,
});

export type InsertUser = z.infer<typeof insertUserSchema>;
export type InsertSubject = z.infer<typeof insertSubjectSchema>;
export type InsertGrade = z.infer<typeof insertGradeSchema>;
export type InsertTextbook = z.infer<typeof insertTextbookSchema>;
export type InsertTest = z.infer<typeof insertTestSchema>;

export type User = typeof users.$inferSelect;
export type Subject = typeof subjects.$inferSelect;
export type Grade = typeof grades.$inferSelect;
export type Textbook = typeof textbooks.$inferSelect;
export type Test = typeof tests.$inferSelect;
