package com.example.projectgroup.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.projectgroup.R
import com.example.projectgroup.ui.common.GameFilterReceiver
import com.example.projectgroup.ui.disclaimer.DisclaimerFragment
import com.example.projectgroup.ui.favourites.FavouritesFragment
import com.example.projectgroup.ui.filter.FilterBottomSheet
import com.example.projectgroup.ui.home.HomeFragment
import com.example.projectgroup.ui.historical.HistoricalFragment
import com.example.projectgroup.ui.live.LiveFragment
import com.example.projectgroup.ui.players.PlayersFragment
import com.example.projectgroup.ui.settings.SettingsFragment
import com.example.projectgroup.ui.ticket.TicketBookingFragment
import com.example.projectgroup.ui.upcoming.UpcomingFragment
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

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        val top = findViewById<MaterialToolbar>(R.id.topAppBar)
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)

        // ---------- 1) İlk açılış ----------
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, homeFragment)
                .commit()
            bottom.selectedItemId = R.id.nav_home
            top.setTitle(R.string.title_home)
        }

        // ---------- 2) BottomNav geçişleri ----------
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

        // ---------- 4) Toolbar üst menü ----------
        top.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_filter -> {
                    FilterBottomSheet().show(supportFragmentManager, "filter_sheet")
                    true
                }
                R.id.action_profile -> {
                    Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        // ---------- 5) Hamburger menü Drawer açsın ----------
        top.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // ---------- 6) Drawer menü tıklamaları ----------
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_players -> {
                    openDrawerFragment(PlayersFragment(), "Players")
                }

                R.id.nav_historical -> {
                    openDrawerFragment(HistoricalFragment(), "Historical Matches")
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

    // Drawer üzerinden açılan sayfalarda bottom nav değişmesin
    private fun openDrawerFragment(fragment: androidx.fragment.app.Fragment, title: String) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragmentContainer, fragment)
            .commit()

        findViewById<MaterialToolbar>(R.id.topAppBar).title = title
    }
}
