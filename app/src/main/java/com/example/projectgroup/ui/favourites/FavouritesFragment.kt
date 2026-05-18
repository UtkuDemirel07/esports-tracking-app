package com.example.projectgroup.ui.favourites

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectgroup.R
import com.example.projectgroup.data.FavouritesManager
import com.example.projectgroup.data.model.HistoricalMatch
import com.example.projectgroup.ui.historical.HistoricalMatchAdapter

class FavouritesFragment : Fragment(R.layout.fragment_favourites) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: View   // this matches the LinearLayout @+id/tvEmpty

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvFavourites)
        emptyView = view.findViewById(R.id.tvEmpty)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadFavourites()
    }

    private fun loadFavourites() {
        // Load favourites from shared storage (no context parameter)
        val favourites: List<HistoricalMatch> = FavouritesManager.getFavourites()

        if (favourites.isEmpty()) {
            // Show empty state, hide list
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            // Show list, hide empty state
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE

            val adapter = HistoricalMatchAdapter(favourites) { match, isFavourite ->
                // Heart toggled in adapter
                if (!isFavourite) {
                    Toast.makeText(
                        requireContext(),
                        "Removed: ${match.title}",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadFavourites() // refresh list after unfavourite
                }
            }

            recyclerView.adapter = adapter
        }
    }
}
