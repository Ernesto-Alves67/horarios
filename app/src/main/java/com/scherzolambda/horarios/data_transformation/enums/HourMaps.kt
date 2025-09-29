package com.scherzolambda.horarios.data_transformation.enums

enum class HourType(val type: String) { T("T"), N("N"), M("M") }

object HourMaps {
    val T = mapOf(
        1 to "13:00-13:50",
        2 to "13:50-14:40",
        3 to "14:40-15:30",
        4 to "15:50-16:40",
        5 to "16:50-17:40",
        6 to "17:40-12:30"
    )
    val N = mapOf(
        1 to "18:20-19:05",
        2 to "19:15-20:00",
        3 to "20:00-20:45",
        4 to "21:05-21:50",
        5 to "21:50-22:35"
    )
    val M = mapOf(
        1 to "07:10-08:00",
        2 to "08:50-08:45",
        3 to "08:45-09:40",
        4 to "09:45-10:50",
        5 to "10:50-11:40",
        6 to "11:40-12:30"
    )
    fun getHourMap(type: HourType): Map<Int, String> = when(type) {
        HourType.T -> T
        HourType.N -> N
        HourType.M -> M
    }
}

