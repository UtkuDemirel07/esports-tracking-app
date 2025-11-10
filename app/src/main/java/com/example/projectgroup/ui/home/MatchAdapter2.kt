package com.example.projectgroup.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectgroup.R
import com.example.projectgroup.data.Match

class MatchAdapter2(private var matchList: List<Match>) :
    RecyclerView.Adapter<MatchAdapter2.MatchViewHolder>() {

    inner class MatchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gameText: TextView = view.findViewById(R.id.game)
        val titleText: TextView = view.findViewById(R.id.title)
        val subtitleText: TextView = view.findViewById(R.id.subtitle)
        val statusChip: TextView = view.findViewById(R.id.status)
        val timeText: TextView = view.findViewById(R.id.time)
        val scoreText: TextView = view.findViewById(R.id.score)
        val backgroundImage: ImageView = view.findViewById(R.id.imgBackground)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_match, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matchList[position]

        holder.gameText.text = match.game
        holder.titleText.text = match.title
        holder.subtitleText.text = match.subtitle
        holder.statusChip.text = match.status
        holder.timeText.text = match.time
        holder.scoreText.text = match.score

        // 🔹 Arka plan (oyuna göre resim seçimi)
        val bgRes = when (match.game.lowercase()) {
            "lol" -> R.drawable.bg_lol
            "valorant" -> R.drawable.bg_valorant
            "cs2" -> R.drawable.bg_cs2
            else -> R.drawable.bg_default
        }

        holder.backgroundImage.setImageResource(bgRes)

        // 🔹 Erişilebilirlik için açıklama (strings.xml'deki match_background kullanılır)
        holder.backgroundImage.contentDescription =
            holder.itemView.context.getString(R.string.match_background)
    }

    override fun getItemCount(): Int = matchList.size

    fun updateList(newList: List<Match>) {
        matchList = newList
        notifyDataSetChanged()
    }
}
