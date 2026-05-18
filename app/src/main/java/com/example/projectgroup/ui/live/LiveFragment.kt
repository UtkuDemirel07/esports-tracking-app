package com.example.projectgroup.ui.live

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
import com.example.projectgroup.R
import com.example.projectgroup.data.Match
import com.example.projectgroup.ui.adapters.MatchAdapter
import com.example.projectgroup.ui.common.VerticalSpaceItemDecoration
import com.example.projectgroup.ui.matches.MatchDetailFragment
import com.google.android.material.appbar.MaterialToolbar

class LiveFragment : Fragment(R.layout.fragment_live) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MatchAdapter

    private var allItems: List<Match> = emptyList()
    private var currentQuery: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).title = "Live"

        recyclerView = view.findViewById(R.id.recyclerViewLive)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(VerticalSpaceItemDecoration(24))

        allItems = listOf(
            Match("Valorant", "FNATIC vs NAVI", "live", "live"),
            Match("CS2", "FaZe vs G2", "live", "live")
        )

        adapter = MatchAdapter(
            matches = emptyList(),
            onMatchClick = { match -> openMatchDetail(match) }
        )

        recyclerView.adapter = adapter
        applySearchAndShow() // initial load

        // --- Search (same pattern as HomeFragment) ---
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

                sv.setOnCloseListener {
                    currentQuery = ""
                    applySearchAndShow()
                    false
                }
            }

            override fun onMenuItemSelected(item: MenuItem) = false
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun openMatchDetail(match: Match) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragmentContainer, MatchDetailFragment.newInstance(match))
            .addToBackStack(null)
            .commit()
    }

    private fun applySearchAndShow() {
        var shown = allItems
        if (currentQuery.isNotBlank()) {
            val q = currentQuery.lowercase()
            shown = shown.filter {
                it.game.lowercase().contains(q) ||
                        it.title.lowercase().contains(q) ||
                        it.subtitle.lowercase().contains(q)
            }
        }
        adapter.updateList(shown)
        recyclerView.scheduleLayoutAnimation()
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
