package com.scherzolambda.horarios.data_transformation.enums

// OBSOLETO: Use HourMaps e HourType em vez deste enum.
enum class HourTEnum(val hourMap: Map<Int, String>) {
    // Exemplo de mapeamento de horários
    PADRAO_T(
        mapOf(
            1 to "13:00-13:50",
            2 to "13:50-14:40",
            3 to "14:40-15:30",
            4 to "15:50-16:40",
            5 to "16:50-17:40",
            6 to "17:40-12:30",
        )
    );

    companion object {
        fun getHourMap(type: HourTEnum = PADRAO_T): Map<Int, String> = type.hourMap
    }
}