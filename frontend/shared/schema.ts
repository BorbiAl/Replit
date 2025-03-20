// This file will contain all the data model schemas for our application

import { createInsertSchema } from "drizzle-zod";
import { z } from "zod";

// We'll define schemas and types for all entities here
// Example:
/*
export const userTable = pgTable("users", {
  id: serial("id").primaryKey(),
  username: text("username").notNull().unique(),
  email: text("email").notNull().unique(),
  password: text("password").notNull(),
  createdAt: timestamp("created_at").defaultNow().notNull(),
});

export type User = typeof userTable.$inferSelect;
export const insertUserSchema = createInsertSchema(userTable).omit({ id: true });
export type InsertUser = z.infer<typeof insertUserSchema>;
*/

// Add actual schema definitions below, based on the application needs