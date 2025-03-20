package com.studyquest.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.studyquest.R
import com.studyquest.models.Test
import java.text.SimpleDateFormat
import java.util.*

class TestAdapter : RecyclerView.Adapter<TestAdapter.TestViewHolder>() {
    
    private val tests = mutableListOf<Test>()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    
    fun setTests(newTests: List<Test>) {
        tests.clear()
        tests.addAll(newTests)
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_test, parent, false)
        return TestViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        val test = tests[position]
        holder.bind(test)
    }
    
    override fun getItemCount(): Int = tests.size
    
    inner class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val testCard: CardView = itemView.findViewById(R.id.test_card)
        private val titleTextView: TextView = itemView.findViewById(R.id.title_text_view)
        private val subjectTextView: TextView = itemView.findViewById(R.id.subject_text_view)
        private val pagesTextView: TextView = itemView.findViewById(R.id.pages_text_view)
        private val dateTextView: TextView = itemView.findViewById(R.id.date_text_view)
        private val statusTextView: TextView = itemView.findViewById(R.id.status_text_view)
        
        fun bind(test: Test) {
            titleTextView.text = test.title
            subjectTextView.text = "${test.subject.emoji} ${test.subject.name}"
            pagesTextView.text = "Pages ${test.pagesFrom}-${test.pagesTo}"
            
            if (test.examDate != null) {
                dateTextView.text = "Exam: ${dateFormat.format(test.examDate)}"
                dateTextView.visibility = View.VISIBLE
            } else {
                dateTextView.visibility = View.GONE
            }
            
            if (test.isCompleted) {
                statusTextView.text = "Completed" + (test.score?.let { " - Score: $it%" } ?: "")
                statusTextView.setTextColor(Color.parseColor("#34A853")) // Green
            } else {
                val today = Calendar.getInstance().time
                if (test.examDate != null && test.examDate.before(today)) {
                    statusTextView.text = "Overdue"
                    statusTextView.setTextColor(Color.parseColor("#EA4335")) // Red
                } else {
                    statusTextView.text = "Upcoming"
                    statusTextView.setTextColor(Color.parseColor("#4285F4")) // Blue
                }
            }
            
            try {
                testCard.setCardBackgroundColor(Color.parseColor(test.subject.colorHex + "20")) // Adding 20% opacity
            } catch (e: Exception) {
                testCard.setCardBackgroundColor(Color.parseColor("#F5F5F5")) // Default light gray
            }
            
            itemView.setOnClickListener {
                // Handle test item click
                // In a real app, this would navigate to the test details screen
            }
        }
    }
}