package com.example.quizapp_boukenze

object CampusValidator {
    val emsiCampuses = listOf(
        Campus("EMSI Centre (Casablanca)"),
        Campus("EMSI Maarif (Casablanca)"),
        Campus("EMSI Moulay Youssef (Casablanca)"),
        Campus("EMSI Les Orangers (Rabat)"),
        Campus("EMSI Agdal (Rabat)"),
        Campus("EMSI Gueliz (Marrakech)"),
        Campus("EMSI Tanger")
    )

    fun allCampusNames(): Array<String> = emsiCampuses.map { it.name }.toTypedArray()
}
