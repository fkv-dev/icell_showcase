package hu.fkv.entity

import java.time.LocalDate

/**
 * A végül megjelenített sorok értékeit tárolja el
 */
class VehicleGpsDisplayData(
    val licenseNo: String?,
    val eventDay: LocalDate?,
    val gpsReceptionQuality: String,
    val dailyDistanceTravelled: Int,
    val maximumSpeed: Int,
    val currentPosition: String,
)



