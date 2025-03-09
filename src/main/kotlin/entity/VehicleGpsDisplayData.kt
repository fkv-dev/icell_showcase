package hu.fkv.entity

import java.time.LocalDate

/**
 * A végül megjelenített sorok értékeit tárolja el
 */
class VehicleGpsDisplayData(
    val licenseNo: String,
    val eventDay: LocalDate,
    val gpsReceptionQuality: Double,
    val dailyDistanceTravelled: Int,
    val maximumSpeed: Int,
    val currentPosition: String){

    fun getGpsReceptionQualityExcelValue(): Double {
        return gpsReceptionQuality / 100
    }
}



