import session from 'express-session';
import createMemoryStore from 'memorystore';

const MemoryStore = createMemoryStore(session);

// User interface
export interface User {
  id: number;
  username: string;
  password: string;
  name?: string;
  email?: string;
  createdAt: Date;
  streakCount: number;
  points: number;
  level: number;
  lastActive?: Date;
}

export interface InsertUser {
  username: string;
  password: string;
  name?: string;
  email?: string;
}

// Subject interface
export interface Subject {
  id: number;
  name: string;
  emoji: string;
  colorHex: string;
}

export interface InsertSubject {
  name: string;
  emoji: string;
  colorHex: string;
}

// Textbook interface
export interface Textbook {
  id: number;
  title: string;
  author?: string;
  subjectId: number;
  gradeLevel?: number;
  totalPages?: number;
}

export interface InsertTextbook {
  title: string;
  author?: string;
  subjectId: number;
  gradeLevel?: number;
  totalPages?: number;
}

// Test interface
export interface Test {
  id: number;
  title: string;
  createdBy: number;
  subjectId: number;
  textbookId: number;
  pagesFrom: number;
  pagesTo: number;
  questionCount: number;
  createdAt: Date;
  examDate?: Date;
  isCompleted: boolean;
  score?: number;
}

export interface InsertTest {
  title: string;
  createdBy: number;
  subjectId: number;
  textbookId: number;
  pagesFrom: number;
  pagesTo: number;
  questionCount: number;
  examDate?: Date;
}

// Achievement interface
export interface Achievement {
  id: number;
  name: string;
  description: string;
  emoji: string;
  requiredPoints: number;
  isHidden: boolean;
}

export interface InsertAchievement {
  name: string;
  description: string;
  emoji: string;
  requiredPoints: number;
  isHidden: boolean;
}

// User Achievement interface
export interface UserAchievement {
  id: number;
  userId: number;
  achievementId: number;
  earnedAt: Date;
}

export interface InsertUserAchievement {
  userId: number;
  achievementId: number;
}

// Storage interface
export interface IStorage {
  sessionStore: session.Store;
  
  // User operations
  getUser(id: number): Promise<User | null>;
  getUserByUsername(username: string): Promise<User | null>;
  createUser(user: InsertUser): Promise<User>;
  updateUser(id: number, updates: Partial<User>): Promise<User | null>;
  getUserStats(id: number): Promise<{ points: number, streak: number, level: number } | null>;
  
  // Subject operations
  getSubjects(): Promise<Subject[]>;
  getSubject(id: number): Promise<Subject | null>;
  createSubject(subject: InsertSubject): Promise<Subject>;
  
  // Textbook operations
  getTextbooks(subjectId?: number): Promise<Textbook[]>;
  getTextbook(id: number): Promise<Textbook | null>;
  createTextbook(textbook: InsertTextbook): Promise<Textbook>;
  
  // Test operations
  getTests(options?: { userId?: number, subjectId?: number, completed?: boolean }): Promise<Test[]>;
  getTest(id: number): Promise<Test | null>;
  createTest(test: InsertTest): Promise<Test>;
  updateTest(id: number, updates: Partial<Test>): Promise<Test | null>;
  completeTest(id: number, score: number): Promise<Test | null>;
  
  // Achievement operations
  getAchievements(includeHidden?: boolean): Promise<Achievement[]>;
  getAchievement(id: number): Promise<Achievement | null>;
  getUserAchievements(userId: number): Promise<(Achievement & { earnedAt: Date })[]>;
  awardAchievement(userId: number, achievementId: number): Promise<UserAchievement>;
  
  // Leaderboard operations
  getLeaderboard(limit?: number): Promise<{ userId: number, username: string, name?: string, points: number, level: number, streak: number, rank: number }[]>;
}

// In-memory storage implementation
export class MemStorage implements IStorage {
  sessionStore: session.Store;
  private users: User[] = [];
  private subjects: Subject[] = [];
  private textbooks: Textbook[] = [];
  private tests: Test[] = [];
  private achievements: Achievement[] = [];
  private userAchievements: UserAchievement[] = [];
  
  private nextUserId = 1;
  private nextSubjectId = 1;
  private nextTextbookId = 1;
  private nextTestId = 1;
  private nextAchievementId = 1;
  private nextUserAchievementId = 1;
  
  constructor() {
    this.sessionStore = new MemoryStore({
      checkPeriod: 86400000, // Prune expired entries every 24h
    });
    
    // Initialize some default data asynchronously
    // We don't await this because constructors can't be async
    // The data will be available by the time it's needed
    Promise.resolve().then(() => this.initializeDefaultData())
      .catch(err => console.error('Failed to initialize default data:', err));
  }
  
  private async initializeDefaultData() {
    try {
      // Create default subjects
      const mathSubject = await this.createSubject({
        name: 'Mathematics', 
        emoji: '🧮',
        colorHex: '#4286f4'
      });
      
      const scienceSubject = await this.createSubject({
        name: 'Science',
        emoji: '🔬',
        colorHex: '#41d95d'
      });
      
      const englishSubject = await this.createSubject({
        name: 'English',
        emoji: '📚',
        colorHex: '#e83a78'
      });
      
      const historySubject = await this.createSubject({
        name: 'History',
        emoji: '🏛️',
        colorHex: '#f19a3e'
      });
      
      // Create default achievements
      await this.createAchievement({
        name: 'Getting Started',
        description: 'Complete your first test',
        emoji: '🚀',
        requiredPoints: 0,
        isHidden: false
      });
      
      await this.createAchievement({
        name: 'Streak Master',
        description: 'Maintain a 7-day streak',
        emoji: '🔥',
        requiredPoints: 50,
        isHidden: false
      });
      
      await this.createAchievement({
        name: 'Math Wizard',
        description: 'Complete 5 Math tests with perfect scores',
        emoji: '🧙‍♂️',
        requiredPoints: 100,
        isHidden: false
      });
      
      await this.createAchievement({
        name: 'Bookworm',
        description: 'Study from 10 different textbooks',
        emoji: '📖',
        requiredPoints: 150,
        isHidden: false
      });
      
      await this.createAchievement({
        name: 'Ultimate Scholar',
        description: 'Reach level 10',
        emoji: '👑',
        requiredPoints: 500,
        isHidden: true
      });
    } catch (error) {
      console.error('Error initializing default data:', error);
    }
  }
  
  // User operations
  async getUser(id: number): Promise<User | null> {
    return this.users.find(user => user.id === id) || null;
  }
  
  async getUserByUsername(username: string): Promise<User | null> {
    return this.users.find(user => user.username === username) || null;
  }
  
  async createUser(userData: InsertUser): Promise<User> {
    const newUser: User = {
      id: this.nextUserId++,
      ...userData,
      createdAt: new Date(),
      streakCount: 0,
      points: 0,
      level: 1,
      lastActive: new Date()
    };
    this.users.push(newUser);
    return newUser;
  }
  
  async updateUser(id: number, updates: Partial<User>): Promise<User | null> {
    const userIndex = this.users.findIndex(user => user.id === id);
    if (userIndex === -1) return null;
    
    const updatedUser = { ...this.users[userIndex], ...updates };
    this.users[userIndex] = updatedUser;
    return updatedUser;
  }
  
  async getUserStats(id: number): Promise<{ points: number, streak: number, level: number } | null> {
    const user = await this.getUser(id);
    if (!user) return null;
    
    return {
      points: user.points,
      streak: user.streakCount,
      level: user.level
    };
  }
  
  // Subject operations
  async getSubjects(): Promise<Subject[]> {
    return this.subjects;
  }
  
  async getSubject(id: number): Promise<Subject | null> {
    return this.subjects.find(subject => subject.id === id) || null;
  }
  
  async createSubject(subjectData: InsertSubject): Promise<Subject> {
    const newSubject: Subject = {
      id: this.nextSubjectId++,
      ...subjectData
    };
    this.subjects.push(newSubject);
    return newSubject;
  }
  
  // Textbook operations
  async getTextbooks(subjectId?: number): Promise<Textbook[]> {
    if (subjectId) {
      return this.textbooks.filter(textbook => textbook.subjectId === subjectId);
    }
    return this.textbooks;
  }
  
  async getTextbook(id: number): Promise<Textbook | null> {
    return this.textbooks.find(textbook => textbook.id === id) || null;
  }
  
  async createTextbook(textbookData: InsertTextbook): Promise<Textbook> {
    const newTextbook: Textbook = {
      id: this.nextTextbookId++,
      ...textbookData
    };
    this.textbooks.push(newTextbook);
    return newTextbook;
  }
  
  // Test operations
  async getTests(options?: { userId?: number, subjectId?: number, completed?: boolean }): Promise<Test[]> {
    let filteredTests = this.tests;
    
    if (options?.userId) {
      filteredTests = filteredTests.filter(test => test.createdBy === options.userId);
    }
    
    if (options?.subjectId) {
      filteredTests = filteredTests.filter(test => test.subjectId === options.subjectId);
    }
    
    if (options?.completed !== undefined) {
      filteredTests = filteredTests.filter(test => test.isCompleted === options.completed);
    }
    
    return filteredTests;
  }
  
  async getTest(id: number): Promise<Test | null> {
    return this.tests.find(test => test.id === id) || null;
  }
  
  async createTest(testData: InsertTest): Promise<Test> {
    const newTest: Test = {
      id: this.nextTestId++,
      ...testData,
      createdAt: new Date(),
      isCompleted: false
    };
    this.tests.push(newTest);
    return newTest;
  }
  
  async updateTest(id: number, updates: Partial<Test>): Promise<Test | null> {
    const testIndex = this.tests.findIndex(test => test.id === id);
    if (testIndex === -1) return null;
    
    const updatedTest = { ...this.tests[testIndex], ...updates };
    this.tests[testIndex] = updatedTest;
    return updatedTest;
  }
  
  async completeTest(id: number, score: number): Promise<Test | null> {
    const testIndex = this.tests.findIndex(test => test.id === id);
    if (testIndex === -1) return null;
    
    const test = this.tests[testIndex];
    const updatedTest = { 
      ...test, 
      isCompleted: true, 
      score 
    };
    
    this.tests[testIndex] = updatedTest;
    
    // Update user stats
    const user = await this.getUser(test.createdBy);
    if (user) {
      const pointsEarned = Math.round(score * 10); // Points based on score
      await this.updateUser(user.id, {
        points: user.points + pointsEarned,
        lastActive: new Date()
      });
      
      // Check if we need to update streak
      const now = new Date();
      const lastActive = user.lastActive ? new Date(user.lastActive) : new Date(0);
      const daysSinceLastActive = Math.floor((now.getTime() - lastActive.getTime()) / (1000 * 60 * 60 * 24));
      
      if (daysSinceLastActive === 0) {
        // Same day, no streak update
      } else if (daysSinceLastActive === 1) {
        // Next day, increment streak
        await this.updateUser(user.id, {
          streakCount: user.streakCount + 1
        });
      } else {
        // More than a day, reset streak
        await this.updateUser(user.id, {
          streakCount: 1
        });
      }
      
      // Check for level-up
      const newLevel = Math.floor(Math.sqrt(user.points + pointsEarned) / 10) + 1;
      if (newLevel > user.level) {
        await this.updateUser(user.id, {
          level: newLevel
        });
      }
      
      // Check for achievements
      await this.checkAchievements(user.id);
    }
    
    return updatedTest;
  }
  
  // Achievement operations
  async getAchievements(includeHidden: boolean = false): Promise<Achievement[]> {
    if (includeHidden) {
      return this.achievements;
    }
    return this.achievements.filter(achievement => !achievement.isHidden);
  }
  
  async getAchievement(id: number): Promise<Achievement | null> {
    return this.achievements.find(achievement => achievement.id === id) || null;
  }
  
  async createAchievement(achievementData: InsertAchievement): Promise<Achievement> {
    const newAchievement: Achievement = {
      id: this.nextAchievementId++,
      ...achievementData
    };
    this.achievements.push(newAchievement);
    return newAchievement;
  }
  
  async getUserAchievements(userId: number): Promise<(Achievement & { earnedAt: Date })[]> {
    const userAchievements = this.userAchievements.filter(ua => ua.userId === userId);
    
    return userAchievements.map(ua => {
      const achievement = this.achievements.find(a => a.id === ua.achievementId);
      if (!achievement) throw new Error(`Achievement ${ua.achievementId} not found`);
      return {
        ...achievement,
        earnedAt: ua.earnedAt
      };
    });
  }
  
  async awardAchievement(userId: number, achievementId: number): Promise<UserAchievement> {
    // Check if already awarded
    const existing = this.userAchievements.find(
      ua => ua.userId === userId && ua.achievementId === achievementId
    );
    if (existing) return existing;
    
    const newUserAchievement: UserAchievement = {
      id: this.nextUserAchievementId++,
      userId,
      achievementId,
      earnedAt: new Date()
    };
    
    this.userAchievements.push(newUserAchievement);
    return newUserAchievement;
  }
  
  private async checkAchievements(userId: number) {
    const user = await this.getUser(userId);
    if (!user) return;
    
    // Check for "Getting Started" achievement
    const testsCompleted = (await this.getTests({ userId, completed: true })).length;
    if (testsCompleted > 0) {
      const startedAchievement = this.achievements.find(a => a.name === 'Getting Started');
      if (startedAchievement) {
        await this.awardAchievement(userId, startedAchievement.id);
      }
    }
    
    // Check for "Streak Master" achievement
    if (user.streakCount >= 7 && user.points >= 50) {
      const streakAchievement = this.achievements.find(a => a.name === 'Streak Master');
      if (streakAchievement) {
        await this.awardAchievement(userId, streakAchievement.id);
      }
    }
    
    // Check for "Math Wizard" achievement
    const mathSubject = this.subjects.find(s => s.name === 'Mathematics');
    if (mathSubject) {
      const mathTests = await this.getTests({ userId, subjectId: mathSubject.id, completed: true });
      const perfectMathTests = mathTests.filter(test => test.score === 100).length;
      
      if (perfectMathTests >= 5 && user.points >= 100) {
        const mathAchievement = this.achievements.find(a => a.name === 'Math Wizard');
        if (mathAchievement) {
          await this.awardAchievement(userId, mathAchievement.id);
        }
      }
    }
    
    // Check for "Bookworm" achievement
    const userTests = await this.getTests({ userId });
    const uniqueTextbooks = new Set(userTests.map(test => test.textbookId)).size;
    
    if (uniqueTextbooks >= 10 && user.points >= 150) {
      const bookwormAchievement = this.achievements.find(a => a.name === 'Bookworm');
      if (bookwormAchievement) {
        await this.awardAchievement(userId, bookwormAchievement.id);
      }
    }
    
    // Check for "Ultimate Scholar" achievement
    if (user.level >= 10 && user.points >= 500) {
      const scholarAchievement = this.achievements.find(a => a.name === 'Ultimate Scholar');
      if (scholarAchievement) {
        await this.awardAchievement(userId, scholarAchievement.id);
      }
    }
  }
  
  // Leaderboard operations
  async getLeaderboard(limit: number = 10): Promise<{ userId: number, username: string, name?: string, points: number, level: number, streak: number, rank: number }[]> {
    const sortedUsers = [...this.users].sort((a, b) => b.points - a.points);
    
    return sortedUsers.slice(0, limit).map((user, index) => ({
      userId: user.id,
      username: user.username,
      name: user.name,
      points: user.points,
      level: user.level,
      streak: user.streakCount,
      rank: index + 1
    }));
  }
}

export const storage = new MemStorage();