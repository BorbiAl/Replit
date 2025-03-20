package com.studyquest.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.studyquest.R
import com.studyquest.models.LeaderboardEntry

class LeaderboardAdapter : RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {
    
    private val entries = mutableListOf<LeaderboardEntry>()
    
    fun setEntries(newEntries: List<LeaderboardEntry>) {
        entries.clear()
        entries.addAll(newEntries)
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return LeaderboardViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val entry = entries[position]
        holder.bind(entry)
    }
    
    override fun getItemCount(): Int = entries.size
    
    inner class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val leaderboardCard: CardView = itemView.findViewById(R.id.leaderboard_card)
        private val rankTextView: TextView = itemView.findViewById(R.id.rank_text_view)
        private val usernameTextView: TextView = itemView.findViewById(R.id.username_text_view)
        private val scoreTextView: TextView = itemView.findViewById(R.id.score_text_view)
        
        fun bind(entry: LeaderboardEntry) {
            rankTextView.text = "#${entry.rank}"
            usernameTextView.text = entry.username
            scoreTextView.text = entry.score.toString()
            
            if (entry.isCurrentUser) {
                leaderboardCard.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.current_user_highlight))
                rankTextView.setTypeface(null, Typeface.BOLD)
                usernameTextView.setTypeface(null, Typeface.BOLD)
                scoreTextView.setTypeface(null, Typeface.BOLD)
            } else {
                leaderboardCard.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.card_background))
                rankTextView.setTypeface(null, Typeface.NORMAL)
                usernameTextView.setTypeface(null, Typeface.NORMAL)
                scoreTextView.setTypeface(null, Typeface.NORMAL)
            }
            
            // Set medal emoji for top 3
            when (entry.rank) {
                1 -> rankTextView.text = "🥇 #1"
                2 -> rankTextView.text = "🥈 #2"
                3 -> rankTextView.text = "🥉 #3"
            }
        }
    }
}