package com.example.projectgroup.data

data class Match(
    val game: String,
    val title: String,
    val subtitle: String,
    val status: String,      // "upcoming", "live", "finished"
    val time: String = "",
    val score: String = ""
)
