package com.example.myapplication.data.common

object Constants {
    const val EXTRA_ARTICLE = "article"
    const val EXTRA_EXPERT_OPINION = "expert_opinion"

    val menuItems = listOf(
        MarkersInfo("markers_work", "Дорожные работы"),
        MarkersInfo("markers_build", "Стройки"),
        MarkersInfo("markers_oil", "Заправки"),
        MarkersInfo("markers_park", "Парковки"),
        MarkersInfo("markers_market", "Супермаркеты"),
        MarkersInfo("markers_eco", "Станции ЭКО мониторинга"),
        MarkersInfo("markers_factory", "Заводы"),
        MarkersInfo("markers_electro", "Электростанции"),
        MarkersInfo("markers_atomic", "Источники радиации"),
    )
}

