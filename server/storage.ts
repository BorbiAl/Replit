import { 
  users, 
  subjects, 
  grades, 
  textbooks, 
  tests, 
  type User, 
  type Subject,
  type Grade,
  type Textbook,
  type Test,
  type InsertUser,
  type InsertSubject,
  type InsertGrade,
  type InsertTextbook,
  type InsertTest
} from "@shared/schema";
import session from "express-session";
import createMemoryStore from "memorystore";

const MemoryStore = createMemoryStore(session);

export interface IStorage {
  // User operations
  getUser(id: number): Promise<User | undefined>;
  getUserByUsername(username: string): Promise<User | undefined>;
  createUser(user: InsertUser): Promise<User>;
  updateUserStreak(userId: number, streak: number): Promise<User | undefined>;
  updateUserXP(userId: number, xp: number): Promise<User | undefined>;
  
  // Subject operations
  getSubjects(): Promise<Subject[]>;
  getSubject(id: number): Promise<Subject | undefined>;
  createSubject(subject: InsertSubject): Promise<Subject>;
  
  // Grade operations
  getGrades(): Promise<Grade[]>;
  getGrade(id: number): Promise<Grade | undefined>;
  createGrade(grade: InsertGrade): Promise<Grade>;
  
  // Textbook operations
  getTextbooks(subjectId?: number, gradeId?: number): Promise<Textbook[]>;
  getTextbook(id: number): Promise<Textbook | undefined>;
  createTextbook(textbook: InsertTextbook): Promise<Textbook>;
  
  // Test operations
  getTests(userId: number): Promise<Test[]>;
  getTest(id: number): Promise<Test | undefined>;
  createTest(test: InsertTest): Promise<Test>;
  updateTest(id: number, data: Partial<Test>): Promise<Test | undefined>;
  getUpcomingTests(userId: number): Promise<Test[]>;
  
  sessionStore: session.SessionStore;
}

export class MemStorage implements IStorage {
  private users: Map<number, User>;
  private subjects: Map<number, Subject>;
  private grades: Map<number, Grade>;
  private textbooks: Map<number, Textbook>;
  private tests: Map<number, Test>;
  
  sessionStore: session.SessionStore;
  userIdCounter: number;
  subjectIdCounter: number;
  gradeIdCounter: number;
  textbookIdCounter: number;
  testIdCounter: number;

  constructor() {
    this.users = new Map();
    this.subjects = new Map();
    this.grades = new Map();
    this.textbooks = new Map();
    this.tests = new Map();
    
    this.userIdCounter = 1;
    this.subjectIdCounter = 1;
    this.gradeIdCounter = 1;
    this.textbookIdCounter = 1;
    this.testIdCounter = 1;
    
    this.sessionStore = new MemoryStore({
      checkPeriod: 86400000 // prune expired entries every 24h
    });
    
    // Initialize with sample data
    this.initializeSampleData();
  }

  private initializeSampleData() {
    // Add sample subjects
    const sampleSubjects: InsertSubject[] = [
      { name: "Mathematics", icon: "calculator", color: "#FF5757" },
      { name: "Physics", icon: "flask", color: "#4255FF" },
      { name: "Chemistry", icon: "beaker", color: "#57C902" },
      { name: "Literature", icon: "book", color: "#3B82F6" },
      { name: "History", icon: "clock", color: "#FFA51F" },
      { name: "Biology", icon: "leaf", color: "#8B5CF6" }
    ];
    
    sampleSubjects.forEach(subject => this.createSubject(subject));
    
    // Add sample grades
    const sampleGrades: InsertGrade[] = [
      { name: "Grade 9" },
      { name: "Grade 10" },
      { name: "Grade 11" },
      { name: "Grade 12" }
    ];
    
    sampleGrades.forEach(grade => this.createGrade(grade));
    
    // Add sample textbooks
    const sampleTextbooks: InsertTextbook[] = [
      { name: "Algebra Fundamentals", subject_id: 1, grade_id: 1, total_pages: 320 },
      { name: "Geometry Basics", subject_id: 1, grade_id: 2, total_pages: 280 },
      { name: "Physics: Mechanics", subject_id: 2, grade_id: 2, total_pages: 240 },
      { name: "Physics: Electricity", subject_id: 2, grade_id: 3, total_pages: 210 },
      { name: "Chemistry Essentials", subject_id: 3, grade_id: 2, total_pages: 260 },
      { name: "Organic Chemistry", subject_id: 3, grade_id: 3, total_pages: 290 },
      { name: "World Literature", subject_id: 4, grade_id: 2, total_pages: 310 },
      { name: "Modern History", subject_id: 5, grade_id: 2, total_pages: 340 },
      { name: "Human Biology", subject_id: 6, grade_id: 2, total_pages: 270 }
    ];
    
    sampleTextbooks.forEach(textbook => this.createTextbook(textbook));
  }

  // User operations
  async getUser(id: number): Promise<User | undefined> {
    return this.users.get(id);
  }

  async getUserByUsername(username: string): Promise<User | undefined> {
    return Array.from(this.users.values()).find(
      (user) => user.username === username,
    );
  }

  async createUser(insertUser: InsertUser): Promise<User> {
    const id = this.userIdCounter++;
    const user: User = { 
      ...insertUser, 
      id,
      streak: 0,
      xp: 0,
      level: 1
    };
    this.users.set(id, user);
    return user;
  }
  
  async updateUserStreak(userId: number, streak: number): Promise<User | undefined> {
    const user = await this.getUser(userId);
    if (!user) return undefined;
    
    const updatedUser = { ...user, streak };
    this.users.set(userId, updatedUser);
    return updatedUser;
  }
  
  async updateUserXP(userId: number, xp: number): Promise<User | undefined> {
    const user = await this.getUser(userId);
    if (!user) return undefined;
    
    // Calculate level (1 level per 100 XP)
    const level = Math.floor(xp / 100) + 1;
    
    const updatedUser = { ...user, xp, level };
    this.users.set(userId, updatedUser);
    return updatedUser;
  }
  
  // Subject operations
  async getSubjects(): Promise<Subject[]> {
    return Array.from(this.subjects.values());
  }
  
  async getSubject(id: number): Promise<Subject | undefined> {
    return this.subjects.get(id);
  }
  
  async createSubject(insertSubject: InsertSubject): Promise<Subject> {
    const id = this.subjectIdCounter++;
    const subject: Subject = { ...insertSubject, id };
    this.subjects.set(id, subject);
    return subject;
  }
  
  // Grade operations
  async getGrades(): Promise<Grade[]> {
    return Array.from(this.grades.values());
  }
  
  async getGrade(id: number): Promise<Grade | undefined> {
    return this.grades.get(id);
  }
  
  async createGrade(insertGrade: InsertGrade): Promise<Grade> {
    const id = this.gradeIdCounter++;
    const grade: Grade = { ...insertGrade, id };
    this.grades.set(id, grade);
    return grade;
  }
  
  // Textbook operations
  async getTextbooks(subjectId?: number, gradeId?: number): Promise<Textbook[]> {
    let textbooks = Array.from(this.textbooks.values());
    
    if (subjectId) {
      textbooks = textbooks.filter(textbook => textbook.subject_id === subjectId);
    }
    
    if (gradeId) {
      textbooks = textbooks.filter(textbook => textbook.grade_id === gradeId);
    }
    
    return textbooks;
  }
  
  async getTextbook(id: number): Promise<Textbook | undefined> {
    return this.textbooks.get(id);
  }
  
  async createTextbook(insertTextbook: InsertTextbook): Promise<Textbook> {
    const id = this.textbookIdCounter++;
    const textbook: Textbook = { ...insertTextbook, id };
    this.textbooks.set(id, textbook);
    return textbook;
  }
  
  // Test operations
  async getTests(userId: number): Promise<Test[]> {
    return Array.from(this.tests.values()).filter(test => test.user_id === userId);
  }
  
  async getTest(id: number): Promise<Test | undefined> {
    return this.tests.get(id);
  }
  
  async createTest(insertTest: InsertTest): Promise<Test> {
    const id = this.testIdCounter++;
    const now = new Date();
    
    const test: Test = { 
      ...insertTest, 
      id, 
      is_completed: false,
      score: null,
      created_at: now
    };
    
    this.tests.set(id, test);
    return test;
  }
  
  async updateTest(id: number, data: Partial<Test>): Promise<Test | undefined> {
    const test = await this.getTest(id);
    if (!test) return undefined;
    
    const updatedTest = { ...test, ...data };
    this.tests.set(id, updatedTest);
    return updatedTest;
  }
  
  async getUpcomingTests(userId: number): Promise<Test[]> {
    const allTests = await this.getTests(userId);
    const now = new Date();
    
    return allTests
      .filter(test => !test.is_completed && test.exam_date && new Date(test.exam_date) >= now)
      .sort((a, b) => {
        if (!a.exam_date || !b.exam_date) return 0;
        return new Date(a.exam_date).getTime() - new Date(b.exam_date).getTime();
      });
  }
}

export const storage = new MemStorage();
