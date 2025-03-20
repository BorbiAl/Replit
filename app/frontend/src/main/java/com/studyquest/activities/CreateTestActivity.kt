package com.studyquest.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.studyquest.R
import com.studyquest.models.Subject
import com.studyquest.models.Textbook
import java.text.SimpleDateFormat
import java.util.*

class CreateTestActivity : AppCompatActivity() {
    
    private lateinit var gradeSpinner: Spinner
    private lateinit var subjectSpinner: Spinner
    private lateinit var textbookSpinner: Spinner
    private lateinit var titleEditText: EditText
    private lateinit var pagesFromEditText: EditText
    private lateinit var pagesToEditText: EditText
    private lateinit var questionCountEditText: EditText
    private lateinit var examDateEditText: EditText
    private lateinit var scheduleReminderSwitch: Switch
    private lateinit var createButton: Button
    
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    
    private val grades = listOf("Grade 9", "Grade 10", "Grade 11", "Grade 12")
    private val subjects = mutableListOf<Subject>()
    private val textbooks = mutableListOf<Textbook>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_test)
        
        // Initialize views
        gradeSpinner = findViewById(R.id.grade_spinner)
        subjectSpinner = findViewById(R.id.subject_spinner)
        textbookSpinner = findViewById(R.id.textbook_spinner)
        titleEditText = findViewById(R.id.title_edit_text)
        pagesFromEditText = findViewById(R.id.pages_from_edit_text)
        pagesToEditText = findViewById(R.id.pages_to_edit_text)
        questionCountEditText = findViewById(R.id.question_count_edit_text)
        examDateEditText = findViewById(R.id.exam_date_edit_text)
        scheduleReminderSwitch = findViewById(R.id.schedule_reminder_switch)
        createButton = findViewById(R.id.create_button)
        
        // Set up spinners
        setupGradeSpinner()
        loadSubjects()
        setupSubjectSpinner()
        setupTextbookSpinner()
        
        // Set up date picker
        setupDatePicker()
        
        // Set up create button
        createButton.setOnClickListener {
            if (validateInputs()) {
                createTest()
            }
        }
    }
    
    private fun setupGradeSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, grades)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        gradeSpinner.adapter = adapter
    }
    
    private fun loadSubjects() {
        // In a real app, this would load subjects from the API or local database
        subjects.clear()
        subjects.addAll(
            listOf(
                Subject(id = 1, name = "Math", emoji = "🔢", colorHex = "#4285F4"),
                Subject(id = 2, name = "Science", emoji = "🔬", colorHex = "#34A853"),
                Subject(id = 3, name = "History", emoji = "📜", colorHex = "#FBBC05"),
                Subject(id = 4, name = "English", emoji = "📚", colorHex = "#8E44AD"),
                Subject(id = 5, name = "Physics", emoji = "⚛️", colorHex = "#EA4335")
            )
        )
    }
    
    private fun setupSubjectSpinner() {
        val subjectNames = subjects.map { "${it.emoji} ${it.name}" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, subjectNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        subjectSpinner.adapter = adapter
        
        subjectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                loadTextbooks(subjects[position].id)
            }
            
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
    
    private fun loadTextbooks(subjectId: Int) {
        // In a real app, this would load textbooks from the API or local database based on subject
        textbooks.clear()
        
        val subjectTextbooks = when (subjectId) {
            1 -> listOf( // Math
                Textbook(id = 1, title = "Algebra 101", author = "John Smith", subjectId = 1),
                Textbook(id = 2, title = "Geometry Basics", author = "Emma Johnson", subjectId = 1),
                Textbook(id = 3, title = "Calculus Fundamentals", author = "David Williams", subjectId = 1)
            )
            2 -> listOf( // Science
                Textbook(id = 4, title = "Biology Essentials", author = "Sarah Parker", subjectId = 2),
                Textbook(id = 5, title = "Chemistry 101", author = "Michael Brown", subjectId = 2)
            )
            3 -> listOf( // History
                Textbook(id = 6, title = "Modern History", author = "James Wilson", subjectId = 3),
                Textbook(id = 7, title = "Ancient Civilizations", author = "Laura Garcia", subjectId = 3)
            )
            4 -> listOf( // English
                Textbook(id = 8, title = "Literary Classics", author = "Robert Johnson", subjectId = 4),
                Textbook(id = 9, title = "Grammar and Composition", author = "Jennifer Lee", subjectId = 4)
            )
            5 -> listOf( // Physics
                Textbook(id = 10, title = "Physics Fundamentals", author = "Daniel Brown", subjectId = 5),
                Textbook(id = 11, title = "Advanced Physics", author = "Sophia Miller", subjectId = 5)
            )
            else -> listOf()
        }
        
        textbooks.addAll(subjectTextbooks)
        setupTextbookSpinner()
    }
    
    private fun setupTextbookSpinner() {
        val textbookTitles = textbooks.map { it.title }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, textbookTitles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        textbookSpinner.adapter = adapter
    }
    
    private fun setupDatePicker() {
        examDateEditText.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            
            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    calendar.set(Calendar.YEAR, selectedYear)
                    calendar.set(Calendar.MONTH, selectedMonth)
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay)
                    examDateEditText.setText(dateFormat.format(calendar.time))
                },
                year,
                month,
                day
            )
            
            // Set minimum date to today
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }
    }
    
    private fun validateInputs(): Boolean {
        if (titleEditText.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
            return false
        }
        
        if (pagesFromEditText.text.toString().trim().isEmpty() || pagesToEditText.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter page range", Toast.LENGTH_SHORT).show()
            return false
        }
        
        val pagesFrom = pagesFromEditText.text.toString().toIntOrNull() ?: 0
        val pagesTo = pagesToEditText.text.toString().toIntOrNull() ?: 0
        
        if (pagesFrom >= pagesTo) {
            Toast.makeText(this, "Page range is invalid", Toast.LENGTH_SHORT).show()
            return false
        }
        
        if (questionCountEditText.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter question count", Toast.LENGTH_SHORT).show()
            return false
        }
        
        val questionCount = questionCountEditText.text.toString().toIntOrNull() ?: 0
        if (questionCount <= 0) {
            Toast.makeText(this, "Question count must be positive", Toast.LENGTH_SHORT).show()
            return false
        }
        
        if (examDateEditText.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select an exam date", Toast.LENGTH_SHORT).show()
            return false
        }
        
        return true
    }
    
    private fun createTest() {
        // In a real app, this would send the test data to the API
        
        Toast.makeText(this, "Test created successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }
}