package com.example.projectgroup.ui.upcoming

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectgroup.R
import com.example.projectgroup.data.Match
import com.example.projectgroup.ui.adapters.MatchAdapter
import com.example.projectgroup.ui.common.GameFilterReceiver
import com.example.projectgroup.ui.common.ReminderScheduler
import com.example.projectgroup.ui.common.VerticalSpaceItemDecoration
import com.example.projectgroup.ui.matches.MatchDetailFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class UpcomingFragment : Fragment(), GameFilterReceiver {

    private lateinit var recyclerView: RecyclerView
    private lateinit var matchAdapter: MatchAdapter

    private var allMatches: List<Match> = emptyList()
    private var baseMatches: List<Match> = emptyList()
    private var currentQuery: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_upcoming, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewUpcoming)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(VerticalSpaceItemDecoration(24))

        // Android 13+ notification permission
        if (Build.VERSION.SDK_INT >= 33) {
            val granted = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        val now = System.currentTimeMillis()
        allMatches = listOf(
            Match(
                id = 101,
                game = "VALORANT",
                title = "FNATIC vs NAVI",
                subtitle = "upcoming • 18:00",
                status = "upcoming",
                startTimeMillis = now + 5 * 60 * 1000L,
                time = "18:00",
                score = ""
            ),
            Match(
                id = 102,
                game = "LOL",
                title = "T1 vs GenG",
                subtitle = "upcoming • 20:00",
                status = "upcoming",
                startTimeMillis = now + 10 * 60 * 1000L,
                time = "20:00",
                score = ""
            ),
            Match(
                id = 103,
                game = "CS2",
                title = "Faze vs Vitality",
                subtitle = "upcoming • 21:00",
                status = "upcoming",
                startTimeMillis = now + 15 * 60 * 1000L,
                time = "21:00",
                score = ""
            )
        )

        baseMatches = allMatches

        matchAdapter = MatchAdapter(
            matches = emptyList(),
            onMatchClick = { match -> openMatchDetail(match) },
            onRemindClick = { match -> showReminderOptions(match) }
        )

        recyclerView.adapter = matchAdapter
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
        var shown = baseMatches
        if (currentQuery.isNotBlank()) {
            val q = currentQuery.lowercase()
            shown = shown.filter {
                it.game.lowercase().contains(q) ||
                        it.title.lowercase().contains(q) ||
                        it.subtitle.lowercase().contains(q)
            }
        }
        matchAdapter.updateList(shown)
        recyclerView.scheduleLayoutAnimation()
    }

    override fun onFilterSelected(filter: String) {
        baseMatches = if (filter.equals("ALL", true)) allMatches
        else allMatches.filter { it.game.equals(filter, true) }

        applySearchAndShow()
    }

    private fun showReminderOptions(match: Match) {
        val options = arrayOf(
            "At the time of the event",
            "5 minutes before",
            "15 minutes before",
            "30 minutes before",
            "1 hour before",
            "2 hours before",
            "1 day before",
            "Test (5 seconds)"
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Set reminder")
            .setItems(options) { _, which -> scheduleReminderForChoice(match, which) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun scheduleReminderForChoice(match: Match, which: Int) {
        val now = System.currentTimeMillis()

        if (which == 7) {
            val triggerAt = now + 5_000L
            scheduleNotification(match, triggerAt, "Test reminder set (5 seconds)")
            return
        }

        if (match.startTimeMillis <= 0L) {
            Toast.makeText(requireContext(), "This match has no time set", Toast.LENGTH_SHORT).show()
            return
        }

        val offsetMillis = when (which) {
            0 -> 0L
            1 -> 5 * 60 * 1000L
            2 -> 15 * 60 * 1000L
            3 -> 30 * 60 * 1000L
            4 -> 60 * 60 * 1000L
            5 -> 2 * 60 * 60 * 1000L
            6 -> 24 * 60 * 60 * 1000L
            else -> 0L
        }

        var triggerAt = match.startTimeMillis - offsetMillis

        if (triggerAt <= now + 1_000L) {
            triggerAt = now + 3_000L
            Toast.makeText(requireContext(), "Time already passed — reminder in 3 seconds", Toast.LENGTH_SHORT).show()
        }

        scheduleNotification(match, triggerAt, "Reminder set")
    }

    private fun scheduleNotification(match: Match, triggerAtMillis: Long, toastMsg: String) {
        val id = if (match.id != 0) match.id else match.title.hashCode()

        ReminderScheduler.schedule(
            context = requireContext(),
            matchId = id,
            title = "Match reminder",
            message = "${match.title} is starting soon!",
            triggerAtMillis = triggerAtMillis
        )

        Toast.makeText(requireContext(), toastMsg, Toast.LENGTH_SHORT).show()
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
