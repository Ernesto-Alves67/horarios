package com.scherzolambda.horarios.data_transformation.enums

// OBSOLETO: Use HourMaps e HourType em vez deste enum.
enum class HourMEnum(val hourMap: Map<Int, String>) {
    // Exemplo de mapeamento de horários
    PADRAO_M(
        mapOf(
            1 to "07:10-08:00",
            2 to "08:50-08:45",
            3 to "08:45-09:40",
            4 to "09:45-10:50",
            5 to "10:50-11:40",
            6 to "11:40-12:30",
        )
    );

    companion object {
        fun getHourMap(type: HourMEnum = PADRAO_M): Map<Int, String> = type.hourMap
    }
}