
import os
from flask import Flask, request, jsonify, session
from functools import wraps
from typing import Optional, Dict, List
import hashlib
import secrets
import time
from dataclasses import dataclass
from datetime import datetime

app = Flask(__name__)
app.secret_key = os.getenv('SESSION_SECRET', 'studyquest-secret-key')

# Data models
@dataclass
class User:
    id: int
    username: str
    password: str
    firstName: str
    lastName: str
    streak: int = 0
    xp: int = 0
    level: int = 1

@dataclass
class Subject:
    id: int
    name: str
    icon: str
    color: str

@dataclass
class Grade:
    id: int
    name: str

@dataclass
class Textbook:
    id: int
    name: str
    subject_id: int
    grade_id: int
    total_pages: int

@dataclass
class Test:
    id: int
    title: str
    user_id: int
    subject_id: int
    grade_id: int
    textbook_id: int
    pages_from: int
    pages_to: int
    question_count: int
    exam_date: Optional[datetime]
    is_completed: bool = False
    score: Optional[int] = None
    created_at: datetime = datetime.now()
    scheduled_reminders: bool = True

# In-memory storage
class Storage:
    def __init__(self):
        self.users: Dict[int, User] = {}
        self.subjects: Dict[int, Subject] = {}
        self.grades: Dict[int, Grade] = {}
        self.textbooks: Dict[int, Textbook] = {}
        self.tests: Dict[int, Test] = {}
        self._init_sample_data()

    def _init_sample_data(self):
        # Add sample subjects
        sample_subjects = [
            {"name": "Mathematics", "icon": "calculator", "color": "#FF5757"},
            {"name": "Physics", "icon": "flask", "color": "#4255FF"},
            {"name": "Chemistry", "icon": "beaker", "color": "#57C902"},
            {"name": "Literature", "icon": "book", "color": "#3B82F6"},
            {"name": "History", "icon": "clock", "color": "#FFA51F"},
            {"name": "Biology", "icon": "leaf", "color": "#8B5CF6"}
        ]
        for i, subj in enumerate(sample_subjects, 1):
            self.subjects[i] = Subject(id=i, **subj)

        # Add sample grades
        sample_grades = ["Grade 9", "Grade 10", "Grade 11", "Grade 12"]
        for i, grade in enumerate(sample_grades, 1):
            self.grades[i] = Grade(id=i, name=grade)

storage = Storage()

# Authentication helper
def login_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if 'user_id' not in session:
            return jsonify({"message": "Unauthorized"}), 401
        return f(*args, **kwargs)
    return decorated_function

def hash_password(password: str) -> str:
    salt = secrets.token_hex(8)
    h = hashlib.scrypt(password.encode(), salt=salt.encode(), n=16384, r=8, p=1)
    return f"{h.hex()}.{salt}"

def verify_password(password: str, hashed: str) -> bool:
    stored_hash, salt = hashed.split('.')
    h = hashlib.scrypt(password.encode(), salt=salt.encode(), n=16384, r=8, p=1)
    return secrets.compare_digest(h.hex(), stored_hash)

# Routes
@app.route('/api/register', methods=['POST'])
def register():
    data = request.json
    if storage.users.get(data['username']):
        return jsonify({"message": "Username already exists"}), 400
    
    user_id = len(storage.users) + 1
    user = User(
        id=user_id,
        username=data['username'],
        password=hash_password(data['password']),
        firstName=data['firstName'],
        lastName=data['lastName']
    )
    storage.users[user_id] = user
    session['user_id'] = user_id
    return jsonify({k: v for k, v in user.__dict__.items() if k != 'password'}), 201

@app.route('/api/login', methods=['POST'])
def login():
    data = request.json
    user = next((u for u in storage.users.values() if u.username == data['username']), None)
    if not user or not verify_password(data['password'], user.password):
        return jsonify({"message": "Invalid credentials"}), 401
    
    session['user_id'] = user.id
    return jsonify({k: v for k, v in user.__dict__.items() if k != 'password'})

@app.route('/api/subjects', methods=['GET'])
def get_subjects():
    return jsonify(list(storage.subjects.values()))

@app.route('/api/grades', methods=['GET'])
def get_grades():
    return jsonify(list(storage.grades.values()))

@app.route('/api/textbooks', methods=['GET'])
def get_textbooks():
    subject_id = request.args.get('subject_id', type=int)
    grade_id = request.args.get('grade_id', type=int)
    
    textbooks = storage.textbooks.values()
    if subject_id:
        textbooks = [t for t in textbooks if t.subject_id == subject_id]
    if grade_id:
        textbooks = [t for t in textbooks if t.grade_id == grade_id]
    
    return jsonify(list(textbooks))

@app.route('/api/tests', methods=['GET'])
@login_required
def get_tests():
    user_tests = [t for t in storage.tests.values() if t.user_id == session['user_id']]
    return jsonify(user_tests)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
