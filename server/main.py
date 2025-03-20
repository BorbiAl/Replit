from flask import Flask, render_template, request, jsonify, session
from dataclasses import dataclass
from typing import Dict, List, Optional
from datetime import datetime
import hashlib
import secrets
import os

app = Flask(__name__, template_folder='templates')
app.secret_key = os.getenv('SESSION_SECRET', 'studyquest-secret-key')

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

def hash_password(password: str) -> str:
    salt = secrets.token_hex(8)
    h = hashlib.scrypt(password.encode(), salt=salt.encode(), n=16384, r=8, p=1)
    return f"{h.hex()}.{salt}"

def verify_password(password: str, hashed: str) -> bool:
    stored_hash, salt = hashed.split('.')
    h = hashlib.scrypt(password.encode(), salt=salt.encode(), n=16384, r=8, p=1)
    return secrets.compare_digest(h.hex(), stored_hash)

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/api/register', methods=['POST'])
def register():
    data = request.json
    if any(u.username == data['username'] for u in storage.users.values()):
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

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)