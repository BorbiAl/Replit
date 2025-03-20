import express from 'express';
import cors from 'cors';
import { setupAuth } from './middleware/auth';
import { registerRoutes } from './api/routes';
import path from 'path';

const app = express();
const PORT = parseInt(process.env.PORT || '5000', 10);

// Middleware
app.use(express.json());
app.use(cors({
  origin: process.env.NODE_ENV === 'production' ? false : ['http://localhost:3000', 'http://localhost:5173'],
  credentials: true
}));

// Authentication setup
const { isAuthenticated } = setupAuth(app);

// Register API routes
const server = registerRoutes(app);

// Start server
server.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});