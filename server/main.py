
import os
from flask import Flask, request, jsonify, session, send_from_directory
from functools import wraps
from typing import Optional, Dict, List
import hashlib
import secrets
from dataclasses import dataclass, asdict
from dataclasses_json import dataclass_json
from datetime import datetime
import json

app = Flask(__name__, static_folder='../client/dist')
app.secret_key = os.getenv('SESSION_SECRET', 'studyquest-secret-key')

# Data models
@dataclass_json
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

@dataclass_json
@dataclass
class Subject:
    id: int
    name: str
    icon: str
    color: str

@dataclass_json
@dataclass
class Grade:
    id: int
    name: str

@dataclass_json
@dataclass
class Textbook:
    id: int
    name: str
    subject_id: int
    grade_id: int
    total_pages: int

@dataclass_json
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
    exam_date: Optional[str]
    is_completed: bool = False
    score: Optional[int] = None
    created_at: str = datetime.now().isoformat()
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

        sample_grades = ["Grade 9", "Grade 10", "Grade 11", "Grade 12"]
        for i, grade in enumerate(sample_grades, 1):
            self.grades[i] = Grade(id=i, name=grade)

storage = Storage()

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

@app.route('/api/register', methods=['POST'])
def register():
    data = request.json
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
    return jsonify({k: v for k, v in asdict(user).items() if k != 'password'}), 201

@app.route('/api/login', methods=['POST'])
def login():
    data = request.json
    user = next((u for u in storage.users.values() if u.username == data['username']), None)
    if not user or not verify_password(data['password'], user.password):
        return jsonify({"message": "Invalid credentials"}), 401
    
    session['user_id'] = user.id
    return jsonify({k: v for k, v in asdict(user).items() if k != 'password'})

@app.route('/api/subjects', methods=['GET'])
def get_subjects():
    return jsonify([subject.to_dict() for subject in storage.subjects.values()])

@app.route('/api/grades', methods=['GET'])
def get_grades():
    return jsonify([grade.to_dict() for grade in storage.grades.values()])

@app.route('/api/textbooks', methods=['GET'])
def get_textbooks():
    subject_id = request.args.get('subject_id', type=int)
    grade_id = request.args.get('grade_id', type=int)
    
    textbooks = storage.textbooks.values()
    if subject_id:
        textbooks = [t for t in textbooks if t.subject_id == subject_id]
    if grade_id:
        textbooks = [t for t in textbooks if t.grade_id == grade_id]
    
    return jsonify([textbook.to_dict() for textbook in textbooks])

@app.route('/api/tests', methods=['GET'])
@login_required
def get_tests():
    user_tests = [t for t in storage.tests.values() if t.user_id == session['user_id']]
    return jsonify([test.to_dict() for test in user_tests])

@app.route('/', defaults={'path': ''})
@app.route('/<path:path>')
def serve(path):
    if path and os.path.exists(app.static_folder + '/' + path):
        return send_from_directory(app.static_folder, path)
    return send_from_directory(app.static_folder, 'index.html')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
