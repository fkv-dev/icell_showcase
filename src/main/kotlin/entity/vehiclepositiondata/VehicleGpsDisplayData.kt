package hu.fkv.entity.vehiclepositiondata

import java.time.LocalDate
import java.util.*

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
    fun gpsReceptionQualityExcelValue(): Double {
        return gpsReceptionQuality / 100
    }

    override fun hashCode(): Int {
        return Objects.hash(
            licenseNo +
                    eventDay +
                    gpsReceptionQuality +
                    dailyDistanceTravelled +
                    maximumSpeed +
                    currentPosition
        )
    }

    override fun equals(other: Any?) = (other is VehicleGpsDisplayData)
            && licenseNo == other.licenseNo
            && eventDay == other.eventDay
            && gpsReceptionQuality == other.gpsReceptionQuality
            && dailyDistanceTravelled == other.dailyDistanceTravelled
            && maximumSpeed == other.maximumSpeed
            && currentPosition == other.currentPosition
}



