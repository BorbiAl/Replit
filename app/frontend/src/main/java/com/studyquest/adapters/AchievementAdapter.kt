package com.studyquest.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.studyquest.R
import com.studyquest.models.Achievement

class AchievementAdapter : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {
    
    private val achievements = mutableListOf<Achievement>()
    
    fun setAchievements(newAchievements: List<Achievement>) {
        achievements.clear()
        achievements.addAll(newAchievements)
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_achievement, parent, false)
        return AchievementViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievements[position]
        holder.bind(achievement)
    }
    
    override fun getItemCount(): Int = achievements.size
    
    inner class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val achievementCard: CardView = itemView.findViewById(R.id.achievement_card)
        private val emojiTextView: TextView = itemView.findViewById(R.id.emoji_text_view)
        private val nameTextView: TextView = itemView.findViewById(R.id.name_text_view)
        
        fun bind(achievement: Achievement) {
            emojiTextView.text = achievement.emoji
            nameTextView.text = achievement.name
            
            if (achievement.isEarned) {
                achievementCard.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.achievement_earned))
                nameTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
            } else {
                achievementCard.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.achievement_locked))
                nameTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_secondary))
            }
            
            itemView.setOnClickListener {
                // Show achievement details in a dialog or tooltip
            }
        }
    }
}