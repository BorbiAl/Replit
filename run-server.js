const { execSync } = require('child_process');
const path = require('path');
const fs = require('fs');

// Determine the backend directory
const backendDir = path.join(__dirname, 'backend');

console.log('Starting backend server from the proper location...');
console.log(`Starting backend server from: ${backendDir}`);

// Execute the backend server
try {
  const command = `cd ${backendDir} && npm run dev`;
  console.log(`Executing: ${command}`);
  execSync(command, { stdio: 'inherit' });
} catch (error) {
  console.error('Error starting the server:', error);
  process.exit(1);
}