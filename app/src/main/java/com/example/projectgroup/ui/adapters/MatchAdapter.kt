package com.example.projectgroup.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectgroup.R
import com.example.projectgroup.data.Match
import com.google.android.material.chip.Chip

class MatchAdapter(
    private var matches: List<Match>,
    private val onMatchClick: (Match) -> Unit,
    private val onRemindClick: ((Match) -> Unit)? = null
) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    inner class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgBackground: ImageView = itemView.findViewById(R.id.imgBackground)
        val game: TextView = itemView.findViewById(R.id.game)
        val title: TextView = itemView.findViewById(R.id.title)
        val subtitle: TextView = itemView.findViewById(R.id.subtitle)
        val status: Chip = itemView.findViewById(R.id.status)
        val btnRemind: ImageButton = itemView.findViewById(R.id.btnRemind)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_match, parent, false)
        return MatchViewHolder(view)
    }

    override fun getItemCount(): Int = matches.size

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matches[position]

        holder.game.text = match.game
        holder.title.text = match.title
        holder.subtitle.text = match.subtitle
        holder.status.text = match.status

        // Background images
        val bgRes = when (match.game.lowercase()) {
            "lol" -> R.drawable.bg_lol
            "valorant" -> R.drawable.bg_valorant
            "cs2" -> R.drawable.bg_cs2
            else -> R.drawable.bg_default
        }
        holder.imgBackground.setImageResource(bgRes)

        // Chip color
        when {
            match.status.contains("live", true) -> holder.status.setChipBackgroundColorResource(R.color.red)
            match.status.contains("upcoming", true) -> holder.status.setChipBackgroundColorResource(R.color.blue)
            match.status.contains("finished", true) -> holder.status.setChipBackgroundColorResource(R.color.green)
            else -> holder.status.setChipBackgroundColorResource(R.color.gray)
        }

        // Whole card click -> open match detail
        holder.itemView.setOnClickListener { onMatchClick(match) }

        // Reminder button only for upcoming AND only if provided
        val showRemind = onRemindClick != null && match.status.contains("upcoming", true)
        holder.btnRemind.visibility = if (showRemind) View.VISIBLE else View.GONE
        holder.btnRemind.setOnClickListener { onRemindClick?.invoke(match) }
    }

    fun updateList(newList: List<Match>) {
        matches = newList
        notifyDataSetChanged()
    }
}
