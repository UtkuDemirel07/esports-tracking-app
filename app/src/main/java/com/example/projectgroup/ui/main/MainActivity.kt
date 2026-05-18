package com.example.projectgroup.ui.main

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.projectgroup.R
import com.example.projectgroup.data.FavouritesManager
import com.example.projectgroup.ui.common.GameFilterReceiver
import com.example.projectgroup.ui.common.SearchReceiver
import com.example.projectgroup.ui.disclaimer.DisclaimerFragment
import com.example.projectgroup.ui.favourites.FavouritesFragment
import com.example.projectgroup.ui.filter.FilterBottomSheet
import com.example.projectgroup.ui.home.HomeFragment
import com.example.projectgroup.ui.live.LiveFragment
import com.example.projectgroup.ui.matches.WatchFragment
import com.example.projectgroup.ui.settings.SettingsFragment
import com.example.projectgroup.ui.ticket.TicketBookingFragment
import com.example.projectgroup.ui.upcoming.UpcomingFragment
import com.example.projectgroup.ui.teams.TeamsFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private val homeFragment = HomeFragment()
    private val liveFragment = LiveFragment()
    private val upcomingFragment = UpcomingFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialise favourites storage once for the app
        FavouritesManager.init(applicationContext)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        val top = findViewById<MaterialToolbar>(R.id.topAppBar)
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)

        // ---------- 0) Wire up SearchView in the toolbar (THIS FIXES HOME SEARCH) ----------
        val searchItem = top.menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.queryHint = getString(R.string.search_hint)
        if (searchView != null) styleSearchView(searchView)

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val current = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
                if (current is SearchReceiver) {
                    current.onSearchQuery(newText.orEmpty())
                }
                return true
            }
        })

        searchView?.setOnCloseListener {
            val current = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (current is SearchReceiver) {
                current.onSearchClosed()
            }
            false
        }

        // ---------- 1) first opening ----------
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, homeFragment)
                .commit()
            bottom.selectedItemId = R.id.nav_home
            top.setTitle(R.string.title_home)
        }

        // ---------- 2) BottomNav  ----------
        bottom.setOnItemSelectedListener { item ->
            val (fragment, titleRes) = when (item.itemId) {
                R.id.nav_home     -> homeFragment to R.string.title_home
                R.id.nav_live     -> liveFragment to R.string.title_live
                R.id.nav_upcoming -> upcomingFragment to R.string.title_upcoming
                else              -> homeFragment to R.string.title_home
            }

            supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragmentContainer, fragment)
                .commit()

            top.setTitle(titleRes)
            true
        }

        // ---------- 3) Filter BottomSheet ----------
        supportFragmentManager.setFragmentResultListener("filter_request", this) { _, bundle ->
            val filter = bundle.getString("filter") ?: "ALL"
            val current = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (current is GameFilterReceiver) current.onFilterSelected(filter)
        }


        // ---------- 5) open Hamburger menu Drawer ----------
        top.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // ---------- 6) Drawer menu clicks ----------
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {

                // Matches Page
                R.id.nav_matches -> {
                    openDrawerFragment(WatchFragment(), "Matches")
                }

                // Combined Teams & Players screen
                R.id.nav_teams_players -> {
                    openDrawerFragment(TeamsFragment(), "Teams & Players")
                }

                R.id.nav_favourites -> {
                    openDrawerFragment(FavouritesFragment(), "Favourites")
                }

                R.id.nav_settings -> {
                    openDrawerFragment(SettingsFragment(), "Settings")
                }

                R.id.nav_disclaimer -> {
                    openDrawerFragment(DisclaimerFragment(), "Disclaimer")
                }

                R.id.nav_ticket -> {
                    openDrawerFragment(TicketBookingFragment(), "Ticket Booking")
                }
            }

            drawerLayout.closeDrawers()
            true
        }
    }

    private fun openDrawerFragment(fragment: androidx.fragment.app.Fragment, title: String) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragmentContainer, fragment)
            .commit()

        findViewById<MaterialToolbar>(R.id.topAppBar).title = title
    }

    // Same styling you used before, just applied in MainActivity (where the toolbar SearchView actually lives)
    private fun styleSearchView(sv: SearchView) {
        sv.maxWidth = Int.MAX_VALUE
        sv.isSubmitButtonEnabled = false

        val plate = sv.findViewById<android.view.View>(androidx.appcompat.R.id.search_plate)
        plate?.setBackgroundResource(R.drawable.bg_search_rounded)
        (plate?.parent as? android.view.View)?.setPadding(8, 4, 8, 4)

        val txtId = androidx.appcompat.R.id.search_src_text
        sv.findViewById<TextView>(txtId)?.apply {
            setTextColor(getColor(R.color.text_primary))
            setHintTextColor(getColor(R.color.text_secondary))
            textSize = 14f
        }

        val iconColor = getColor(android.R.color.white)
        sv.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)?.setColorFilter(iconColor)
        sv.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)?.setColorFilter(iconColor)
        sv.findViewById<ImageView>(androidx.appcompat.R.id.search_go_btn)?.setColorFilter(iconColor)
        sv.findViewById<ImageView>(androidx.appcompat.R.id.search_voice_btn)?.setColorFilter(iconColor)
    }
}
