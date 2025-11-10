package com.example.projectgroup.ui.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectgroup.R
import com.example.projectgroup.data.Match
import com.example.projectgroup.ui.adapters.MatchAdapter
import com.example.projectgroup.ui.common.GameFilterReceiver
import com.example.projectgroup.ui.common.VerticalSpaceItemDecoration

class LiveFragment : Fragment(), GameFilterReceiver {

    private lateinit var recyclerView: RecyclerView
    private lateinit var matchAdapter: MatchAdapter

    private var allMatches: List<Match> = emptyList()
    private var baseMatches: List<Match> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_live, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewLive)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(VerticalSpaceItemDecoration(24))

        // ✅ Dummy veriler
        allMatches = listOf(
            Match("VALORANT", "FNATIC vs NAVI", "live", "live", "Now", "—"),
            Match("CS2", "Faze vs G2", "live", "live", "Now", "1-1"),
            Match("LOL", "T1 vs GenG", "live", "live", "Now", "0-0")
        )

        baseMatches = allMatches

        matchAdapter = MatchAdapter(baseMatches)
        recyclerView.adapter = matchAdapter

        return view
    }

    // ✅ Filtre buraya düşer (HomeFragment ile aynı mantık)
    override fun onFilterSelected(filter: String) {

        baseMatches = if (filter.equals("ALL", true)) {
            allMatches
        } else {
            allMatches.filter { it.game.equals(filter, true) }
        }

        matchAdapter.updateList(baseMatches)
        recyclerView.scheduleLayoutAnimation()  // hoş animasyon
    }
}
