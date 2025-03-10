package hu.fkv.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import hu.fkv.entity.vehiclepositiondata.VehicleGpsDisplayData
import hu.fkv.service.VehiclePositionDataService

@RestController
@RequestMapping("/vehicle-data")
class VehiclePositionDataController(private val vehiclePositionDataService: VehiclePositionDataService) {
    companion object {
        private val logger: Logger = LogManager.getLogger(VehiclePositionDataController::class.java)
    }

    /**
     * Letölti és feldolgozza a jármű adatokat
     * @return json szövegként adja ki
     */
    @GetMapping("/raw-json")
    fun getData(): ResponseEntity<List<VehicleGpsDisplayData>> {
        return ResponseEntity.ok(vehiclePositionDataService.getAggregateVehicleGpsDataJson())
    }

    /**
     * Letölti és feldolgozza a jármű adatokat
     * @return Excelben adja ki
     */
    @GetMapping("/excel")
    fun downloadVehicleGpsDataExcel(): ResponseEntity<ByteArray> {
        val vehGpsDisList = vehiclePositionDataService.getAggregateVehicleGpsDataJson()
        return vehiclePositionDataService.getVehicleGpsDataExcel(vehGpsDisList)
    }
}
