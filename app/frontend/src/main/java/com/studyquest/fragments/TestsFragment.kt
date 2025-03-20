package com.studyquest.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.studyquest.R
import com.studyquest.adapters.TestAdapter
import com.studyquest.models.Subject
import com.studyquest.models.Test
import java.text.SimpleDateFormat
import java.util.*

class TestsFragment : Fragment() {
    
    private lateinit var testsRecyclerView: RecyclerView
    private lateinit var testAdapter: TestAdapter
    private lateinit var subjectSpinner: Spinner
    private lateinit var completionSpinner: Spinner
    
    private val allTests = mutableListOf<Test>()
    private var filteredTests = mutableListOf<Test>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tests, container, false)
        
        // Initialize views
        testsRecyclerView = view.findViewById(R.id.tests_recycler_view)
        subjectSpinner = view.findViewById(R.id.subject_spinner)
        completionSpinner = view.findViewById(R.id.completion_spinner)
        
        // Set up RecyclerView
        testsRecyclerView.layoutManager = LinearLayoutManager(context)
        testAdapter = TestAdapter()
        testsRecyclerView.adapter = testAdapter
        
        // Set up spinners
        setupSubjectSpinner()
        setupCompletionSpinner()
        
        // Load tests
        loadTests()
        
        return view
    }
    
    private fun setupSubjectSpinner() {
        val subjects = listOf("All Subjects", "Math", "Science", "History", "English", "Physics")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjects)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        subjectSpinner.adapter = adapter
        
        subjectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                filterTests()
            }
            
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
    
    private fun setupCompletionSpinner() {
        val completionStatus = listOf("All Tests", "Completed", "Upcoming")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, completionStatus)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        completionSpinner.adapter = adapter
        
        completionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                filterTests()
            }
            
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
    
    private fun loadTests() {
        // In a real app, this would load tests from the API or local database
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        
        val tests = listOf(
            Test(
                id = 1,
                title = "Algebra Basics",
                subject = Subject(id = 1, name = "Math", emoji = "🔢", colorHex = "#4285F4"),
                textbook = "Algebra 101",
                pagesFrom = 10,
                pagesTo = 25,
                examDate = dateFormat.parse("2025-04-10"),
                isCompleted = false
            ),
            Test(
                id = 2,
                title = "Newton's Laws",
                subject = Subject(id = 2, name = "Physics", emoji = "⚛️", colorHex = "#EA4335"),
                textbook = "Physics Fundamentals",
                pagesFrom = 45,
                pagesTo = 60,
                examDate = dateFormat.parse("2025-04-15"),
                isCompleted = false
            ),
            Test(
                id = 3,
                title = "Cell Biology",
                subject = Subject(id = 3, name = "Science", emoji = "🔬", colorHex = "#34A853"),
                textbook = "Biology Essentials",
                pagesFrom = 20,
                pagesTo = 35,
                examDate = dateFormat.parse("2025-03-25"),
                isCompleted = true
            ),
            Test(
                id = 4,
                title = "World War II",
                subject = Subject(id = 4, name = "History", emoji = "📜", colorHex = "#FBBC05"),
                textbook = "Modern History",
                pagesFrom = 78,
                pagesTo = 95,
                examDate = dateFormat.parse("2025-04-05"),
                isCompleted = false
            ),
            Test(
                id = 5,
                title = "Shakespeare's Hamlet",
                subject = Subject(id = 5, name = "English", emoji = "📚", colorHex = "#8E44AD"),
                textbook = "Literary Classics",
                pagesFrom = 120,
                pagesTo = 150,
                examDate = dateFormat.parse("2025-04-20"),
                isCompleted = false
            )
        )
        
        allTests.clear()
        allTests.addAll(tests)
        filterTests()
    }
    
    private fun filterTests() {
        val selectedSubject = subjectSpinner.selectedItem.toString()
        val selectedCompletion = completionSpinner.selectedItem.toString()
        
        filteredTests = allTests.filter { test ->
            val subjectMatch = selectedSubject == "All Subjects" || test.subject.name == selectedSubject
            val completionMatch = when (selectedCompletion) {
                "All Tests" -> true
                "Completed" -> test.isCompleted
                "Upcoming" -> !test.isCompleted
                else -> true
            }
            
            subjectMatch && completionMatch
        }.toMutableList()
        
        testAdapter.setTests(filteredTests)
    }
}