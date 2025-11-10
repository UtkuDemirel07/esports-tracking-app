package com.example.projectgroup.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectgroup.R
import com.example.projectgroup.data.Match
import com.google.android.material.chip.Chip

class MatchAdapter(private var matches: List<Match>) :
    RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    inner class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgBackground: ImageView = itemView.findViewById(R.id.imgBackground)
        val game: TextView = itemView.findViewById(R.id.game)
        val title: TextView = itemView.findViewById(R.id.title)
        val subtitle: TextView = itemView.findViewById(R.id.subtitle)
        val status: Chip = itemView.findViewById(R.id.status)
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

        // 🔹 Arka plan görseli seçimi
        val bgRes = when (match.game.lowercase()) {
            "lol" -> R.drawable.bg_lol
            "valorant" -> R.drawable.bg_valorant
            "cs2" -> R.drawable.bg_cs2
            else -> R.drawable.bg_default
        }
        holder.imgBackground.setImageResource(bgRes)

        // 🔹 Chip rengi duruma göre
        val context = holder.itemView.context
        val chipColor = when {
            match.status.contains("live", true) -> context.getColor(R.color.red)
            match.status.contains("upcoming", true) -> context.getColor(R.color.blue)
            match.status.contains("finished", true) -> context.getColor(R.color.green)
            else -> context.getColor(R.color.gray)
        }
        holder.status.setChipBackgroundColorResource(
            when {
                match.status.contains("live", true) -> R.color.red
                match.status.contains("upcoming", true) -> R.color.blue
                match.status.contains("finished", true) -> R.color.green
                else -> R.color.gray
            }
        )
    }

    // 🔹 Yeni listeyi güncellemek için (HomeFragment'taki updateList çağrısı için)
    fun updateList(newList: List<Match>) {
        updateMatches(newList)
    }

    // 🔹 Asıl liste güncelleme metodu
    fun updateMatches(newMatches: List<Match>) {
        matches = newMatches
        notifyDataSetChanged()
    }
}
