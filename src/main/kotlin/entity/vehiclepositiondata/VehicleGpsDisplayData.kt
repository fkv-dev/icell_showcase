package hu.fkv.entity.vehiclepositiondata

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

    /**
     * Azért van rá szükség, mert az excel felszorozza százzal
     */
    fun getGpsReceptionQualityExcelValue(): Double {
        return gpsReceptionQuality / 100
    }
}



