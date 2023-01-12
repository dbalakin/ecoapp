package com.example.myapplication.data

import java.util.*

interface Codes {
    val code: String
        get() {
            val superName = this.javaClass.superclass!!.simpleName.toLowerCase()
            val name = this.javaClass.simpleName.toLowerCase()
            return "$superName.$name"
        }
}
// format - "maps|time"
object MapsHelper {
    fun parseMaps(name: String): Codes {
        val (map, time) = name.split('|').toMutableList().apply { if (size != 2) add("null") }
        return when(map) {
            "Воздушное" -> {
                if (time == "6:00") {
                    Air.Morning
                } else {
                    Air.Evening
                }
            }
            "Звуковое" -> Sound.Smooth
            "Зелёные" -> Green.Smooth
            "Радиационное" -> Rad.Smooth
            else -> throw NotImplementedError("no map for this type and time: $name")
        }
    }

    fun getTimes(name: String): List<String> {
        return when(name) {
            "Воздушное" -> listOf("6:00", "19:00")
            else -> emptyList()
        }
    }

    fun parseName(name: String): String {
        return when(name) {
            "Воздушное" -> "air"
            "Звуковое" -> "sound"
            "Зелёные" -> "green"
            "Радиационное" -> "rad"
            else -> throw NotImplementedError("no map for this type and time: $name")
        }
    }

    fun parseNameRU(name: String): String {
        return when(name) {
            "air1", "air2" -> "Воздушное"
            "sound" -> "Звуковое"
            "green" -> "Зелёные"
            "rad" -> "Радиационное"
            else -> throw NotImplementedError("no ru name this type and time: $name")
        }
    }
}

abstract class Air: Codes {
    object Morning: Air()
    object Evening: Air()
}

abstract class Green: Codes {
    object Smooth: Green()
}

abstract class Sound {
    object Smooth: Sound(), Codes {
        const val SUPERMARKET = "supermarket"
        const val REPAIRING = "repairing"
        const val DEPO = "depo"
    }
}

abstract class Rad {
    object Smooth: Rad(), Codes {
        const val TPP = "тэс"
        const val BOILER_HOUSE = "котельная"
        const val FACTORY = "завод"
        const val CHP = "тэц"
        const val NUCLEAR_INDUSTRY = "ядерная промышленность"
        const val CHEMISTRY_INDUSTRY = "химическая промышленность"
        const val RESEARCH_INSTITUTE = "нии"
        const val RADON_PLANT = "комбинат радон"
        const val MEDICAL_CENTER = "медцентр"
        const val RADIATION_PRODUCTION = "радиац производство"
        const val DISPOSAL_OF_RADIATION_WASTE = "радиац производство"
    }
}

