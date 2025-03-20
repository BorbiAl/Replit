import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

// Calculate level from points (example algorithm)
export function calculateLevel(points: number): number {
  return Math.floor(Math.sqrt(points / 10)) + 1;
}

// Calculate points needed for next level
export function pointsForNextLevel(currentLevel: number): number {
  return Math.pow(currentLevel, 2) * 10;
}

// Calculate progress towards next level (as percentage)
export function levelProgress(points: number, level: number): number {
  const currentLevelPoints = Math.pow(level - 1, 2) * 10;
  const nextLevelPoints = Math.pow(level, 2) * 10;
  const progress = ((points - currentLevelPoints) / (nextLevelPoints - currentLevelPoints)) * 100;
  return Math.min(100, Math.max(0, progress));
}

// Format date for displaying in UI
export function formatDate(date: Date | string): string {
  const d = typeof date === 'string' ? new Date(date) : date;
  return d.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  });
}

// Check if a date is today
export function isToday(date: Date | string): boolean {
  const d = typeof date === 'string' ? new Date(date) : date;
  const today = new Date();
  return d.getDate() === today.getDate() &&
    d.getMonth() === today.getMonth() &&
    d.getFullYear() === today.getFullYear();
}

// Generate an array for streak display dots
export function generateStreakDots(streakCount: number, maxDots: number = 7): boolean[] {
  const dots = [];
  const displayCount = Math.min(streakCount, maxDots);
  for (let i = 0; i < maxDots; i++) {
    dots.push(i < displayCount);
  }
  return dots;
}

// S Pen specific utility to convert pressure to a CSS scale value
export function pressureToScale(pressure: number): number {
  // Pressure usually ranges from 0 to 1
  return 1 + (pressure * 0.1); // Scale between 1.0 and 1.1
}

// S Pen specific utility to detect if the device supports S Pen
export function supportsS_Pen(): boolean {
  // Check for Samsung browser and S Pen API
  return typeof window !== 'undefined' && 
    'navigator' in window && 
    'userAgent' in navigator && 
    /Samsung|SM-[N|S|G]|Galaxy/i.test(navigator.userAgent) &&
    ('SPenGestureManager' in window || 'SPen' in window);
}