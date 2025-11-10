package com.example.projectgroup.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectgroup.R
import com.example.projectgroup.ui.common.GameFilterReceiver
import com.example.projectgroup.ui.filter.FilterBottomSheet
import com.example.projectgroup.ui.home.HomeFragment
import com.example.projectgroup.ui.live.LiveFragment
import com.example.projectgroup.ui.upcoming.UpcomingFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val liveFragment = LiveFragment()
    private val upcomingFragment = UpcomingFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val top = findViewById<MaterialToolbar>(R.id.topAppBar)
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)

        // İlk açılış
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, homeFragment)
                .commit()
            bottom.selectedItemId = R.id.nav_home
            top.setTitle(R.string.title_home)
        }

        // Sekmeler arası geçiş + başlık
        bottom.setOnItemSelectedListener { item ->
            val (fragment, titleRes) = when (item.itemId) {
                R.id.nav_home     -> homeFragment to R.string.title_home
                R.id.nav_live     -> liveFragment to R.string.title_live
                R.id.nav_upcoming -> upcomingFragment to R.string.title_upcoming
                else              -> homeFragment to R.string.title_home
            }

            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
                .replace(R.id.fragmentContainer, fragment)
                .commit()

            top.setTitle(titleRes)
            true
        }

        // BottomSheet sonucunu aktif fragmente ilet
        supportFragmentManager.setFragmentResultListener("filter_request", this) { _, bundle ->
            val filter = bundle.getString("filter") ?: "ALL"
            val current = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (current is GameFilterReceiver) current.onFilterSelected(filter)
        }

        // Menü tıklamaları (Filter güvenli gösterim + Profile)
        top.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_filter -> {
                    FilterBottomSheet().show(supportFragmentManager, "filter_sheet")
                    true
                }
                R.id.action_profile -> {
                    Toast.makeText(this, getString(R.string.msg_profile_clicked), Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        // (opsiyonel) sol logo tıklaması
        top.setNavigationOnClickListener {
            Toast.makeText(this, "Logo clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
