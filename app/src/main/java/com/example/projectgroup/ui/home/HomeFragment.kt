package com.example.projectgroup.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.projectgroup.R
import com.example.projectgroup.data.Match
import com.example.projectgroup.ui.adapters.MatchAdapter
import com.example.projectgroup.ui.common.GameFilterReceiver
import com.example.projectgroup.ui.common.SearchReceiver
import com.example.projectgroup.ui.common.VerticalSpaceItemDecoration
import com.example.projectgroup.ui.matches.MatchDetailFragment

class HomeFragment : Fragment(), GameFilterReceiver, SearchReceiver {

    private lateinit var rv: RecyclerView
    private lateinit var srl: SwipeRefreshLayout
    private lateinit var adapter: MatchAdapter

    private var allItems: List<Match> = emptyList()
    private var baseItems: List<Match> = emptyList()
    private var currentQuery: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv = view.findViewById(R.id.rvHome)
        srl = view.findViewById(R.id.srlHome)

        adapter = MatchAdapter(
            matches = emptyList(),
            onMatchClick = { match -> openMatchDetail(match) }
        )

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
        rv.addItemDecoration(VerticalSpaceItemDecoration(24))

        // Mock data (demo)
        allItems = listOf(
            Match("LoL", "T1 vs GenG", "upcoming • 18:00", "upcoming", time = "18:00"),
            Match("Valorant", "FNATIC vs NAVI", "live", "live"),
            Match("CS2", "G2 vs Vitality", "finished • 2-1", "finished", score = "2-1"),
            Match("LoL", "BLG vs JDG", "finished • 2-0", "finished", score = "2-0")
        )

        baseItems = allItems
        applySearchAndShow()

        // Pull-to-refresh (visual only)
        srl.setColorSchemeResources(R.color.chip_upcoming, R.color.chip_live, R.color.chip_done)
        srl.setOnRefreshListener {
            view.postDelayed({
                srl.isRefreshing = false
                rv.scheduleLayoutAnimation()
            }, 800)
        }
    }

    private fun openMatchDetail(match: Match) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragmentContainer, MatchDetailFragment.newInstance(match))
            .addToBackStack(null)
            .commit()
    }

    private fun applySearchAndShow() {
        var shown = baseItems

        if (currentQuery.isNotBlank()) {
            val q = currentQuery.trim().lowercase()
            shown = shown.filter {
                it.game.lowercase().contains(q) ||
                        it.title.lowercase().contains(q) ||
                        it.subtitle.lowercase().contains(q)
            }
        }

        adapter.updateList(shown)
    }

    // Called from MainActivity SearchView
    override fun onSearchQuery(query: String) {
        currentQuery = query
        applySearchAndShow()
    }

    override fun onSearchClosed() {
        currentQuery = ""
        applySearchAndShow()
    }

    // Your existing filter bottom-sheet integration stays working
    override fun onFilterSelected(filter: String) {
        baseItems =
            if (filter.equals("ALL", true)) allItems
            else allItems.filter { it.game.equals(filter, true) }

        applySearchAndShow()
        rv.scheduleLayoutAnimation()
    }
}
