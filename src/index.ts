// Redirector to backend
const { spawnSync } = require('child_process');
const path = require('path');

console.log('Starting backend server from the proper location...');

// Start the actual backend process from the backend directory
const result = spawnSync('node', ['run-server.js'], {
  cwd: process.cwd(),
  stdio: 'inherit',
  shell: true
});

if (result.error) {
  console.error('Failed to start backend process:', result.error);
  process.exit(1);
}

process.exit(result.status);
