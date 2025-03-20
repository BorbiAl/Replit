from flask import Flask, render_template, jsonify
import os

app = Flask(__name__, template_folder='../templates')

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/api/subjects')
def get_subjects():
    subjects = [
        {"id": 1, "name": "Mathematics", "icon": "calculator", "color": "#FF5757"},
        {"id": 2, "name": "Physics", "icon": "flask", "color": "#4255FF"},
        {"id": 3, "name": "Chemistry", "icon": "beaker", "color": "#57C902"},
        {"id": 4, "name": "Literature", "icon": "book", "color": "#3B82F6"},
        {"id": 5, "name": "History", "icon": "clock", "color": "#FFA51F"},
        {"id": 6, "name": "Biology", "icon": "leaf", "color": "#8B5CF6"}
    ]
    return jsonify(subjects)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)