package com.example.projectgroup.ui.home

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.projectgroup.R
import com.example.projectgroup.data.Match
import com.example.projectgroup.ui.adapters.MatchAdapter
import com.example.projectgroup.ui.common.GameFilterReceiver
import com.example.projectgroup.ui.common.VerticalSpaceItemDecoration

class HomeFragment : Fragment(), GameFilterReceiver {

    private lateinit var rv: RecyclerView
    private lateinit var srl: SwipeRefreshLayout
    private val adapter = MatchAdapter(emptyList())

    private var allItems: List<Match> = emptyList()
    private var baseItems: List<Match> = emptyList()
    private var currentQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rv = view.findViewById(R.id.rvHome)
        srl = view.findViewById(R.id.srlHome)

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
        rv.addItemDecoration(VerticalSpaceItemDecoration(24))

        allItems = listOf(
            Match("LoL", "T1 vs GenG",         "upcoming • 18:00", "upcoming", time = "18:00"),
            Match("Valorant", "FNATIC vs NAVI","live",              "live"),
            Match("CS2", "G2 vs Vitality",     "finished • 2-1",    "finished", score = "2-1"),
            Match("LoL", "BLG vs JDG",         "finished • 2-0",    "finished", score = "2-0")
        )
        baseItems = allItems
        adapter.updateList(baseItems)

        srl.setColorSchemeResources(R.color.chip_upcoming, R.color.chip_live, R.color.chip_done)
        srl.setOnRefreshListener {
            view.postDelayed({ srl.isRefreshing = false; rv.scheduleLayoutAnimation() }, 800)
        }

        val host: MenuHost = requireActivity()
        host.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                val item = menu.findItem(R.id.action_search) ?: return
                val sv = item.actionView as? SearchView ?: return
                sv.queryHint = getString(R.string.search_hint)
                styleSearchView(sv)

                if (currentQuery.isNotBlank()) {
                    item.expandActionView()
                    sv.setQuery(currentQuery, false)
                    sv.clearFocus()
                }

                sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(q: String?) = false
                    override fun onQueryTextChange(newText: String?): Boolean {
                        currentQuery = newText.orEmpty()
                        applySearchAndShow()
                        return true
                    }
                })
                sv.setOnCloseListener { currentQuery = ""; applySearchAndShow(); false }
            }
            override fun onMenuItemSelected(item: MenuItem) = false
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun applySearchAndShow() {
        var shown = baseItems
        if (currentQuery.isNotBlank()) {
            val q = currentQuery.lowercase()
            shown = shown.filter {
                it.game.lowercase().contains(q) ||
                        it.title.lowercase().contains(q) ||
                        it.subtitle.lowercase().contains(q)
            }
        }
        adapter.updateList(shown)
    }

    override fun onFilterSelected(filter: String) {
        baseItems = if (filter.equals("ALL", true)) allItems
        else allItems.filter { it.game.equals(filter, true) }
        applySearchAndShow()
        rv.scheduleLayoutAnimation()
    }

    private fun styleSearchView(sv: SearchView) {
        sv.maxWidth = Int.MAX_VALUE
        sv.isSubmitButtonEnabled = false

        val plate = sv.findViewById<View>(androidx.appcompat.R.id.search_plate)
        plate?.setBackgroundResource(R.drawable.bg_search_rounded)
        (plate?.parent as? View)?.setPadding(8, 4, 8, 4)

        val txtId = androidx.appcompat.R.id.search_src_text
        sv.findViewById<TextView>(txtId)?.apply {
            setTextColor(requireContext().getColor(R.color.text_primary))
            setHintTextColor(requireContext().getColor(R.color.text_secondary))
            textSize = 14f
        }
        val iconColor = requireContext().getColor(android.R.color.white)
        sv.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)?.setColorFilter(iconColor)
        sv.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)?.setColorFilter(iconColor)
        sv.findViewById<ImageView>(androidx.appcompat.R.id.search_go_btn)?.setColorFilter(iconColor)
        sv.findViewById<ImageView>(androidx.appcompat.R.id.search_voice_btn)?.setColorFilter(iconColor)
    }
}
