package hu.fkv.service

import java.util.*
import java.io.ByteArrayOutputStream
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.*
import com.fasterxml.jackson.core.type.TypeReference

import hu.fkv.entity.vehiclepositiondata.VehicleGpsData
import hu.fkv.entity.vehiclepositiondata.excelconfig.VehicleGpsDataExcelConfig
import hu.fkv.entity.vehiclepositiondata.VehicleGpsDisplayData
import hu.fkv.util.ExcelManager.getExcelFormatForKey
import hu.fkv.util.MyConstant.objectMapper
import hu.fkv.util.NumberProcessor

@Service
class VehiclePositionDataService {
    companion object {
        private val logger: Logger = LogManager.getLogger(VehiclePositionDataService::class.java)
    }

    @Value("\${app.test.api.url}")
    lateinit var testApiUrl: String
    @Value("\${app.test.api.user}")
    lateinit var testApiUser: String
    @Value("\${app.test.api.pass}")
    lateinit var testApiPass: String

    private var restTemplate: RestTemplate = RestTemplate()

    /**
     * Letölti a járművek gps adatait, utána feldolgozza összesített formába
     */
    fun getAggregateVehicleGpsDataJson(): List<VehicleGpsDisplayData> {
        //Lekéri az adatokat
        val vehResp = downloadVehiclePositionData()
        if (vehResp.statusCode != HttpStatus.OK) return emptyList()
        val vehRespData = vehResp.body ?: run {
            logger.error("Vehicle response body is null")
            return emptyList()
        }

        //Osztályá alakítja a json szöveget
        val parsedVehList: List<VehicleGpsData> = parseVehicleDataJson(vehRespData)
        //Feldolgozza az adatokat
        val vehGpsDisList: List<VehicleGpsDisplayData> = processVehicleGpsData(parsedVehList)
        return vehGpsDisList
    }

    /**
     * Letöli a járművek akatait
     *
     * @return A hívás eredményét adja vissza, az adatok benne szövegesek.
     */
    fun downloadVehiclePositionData(): ResponseEntity<String> {
        return try {
            val headers = HttpHeaders()
            val auth = "$testApiUser:$testApiPass"
            val encodedAuth = Base64.getEncoder().encodeToString(auth.toByteArray())
            headers.set("Authorization", "Basic $encodedAuth")
            val entity = HttpEntity<String>(headers)
            val response = restTemplate.exchange(testApiUrl, HttpMethod.GET, entity, String::class.java)
            response
        } catch (e: Exception) {
            logger.error("Unexpected error occurred during API call", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: ${e.message}")
        }
    }

    /**
     * A excel konfigurációs json-t olvasssa be.
     *
     * @return A konfigurációs osztályát adja ki.
     */
    fun readVehicleGpsDataExcelConfigJson(): VehicleGpsDataExcelConfig =
        objectMapper.readValue(
            ClassPathResource("vehicleGpsDataExcelConfig.json").inputStream.bufferedReader().use { it.readText() },
            VehicleGpsDataExcelConfig::class.java
        )

    /**
     * A String jsont átalakítja osztályá, ami a megfelelő adatsruktúrát veszi fel
     *
     * @param vehRespDataJson Az api hívás által visszaadaott json
     * @return Az osztályá alakított adatok listája
     */
    private fun parseVehicleDataJson(vehRespDataJson: String): List<VehicleGpsData> =
        objectMapper.readValue(vehRespDataJson, object : TypeReference<List<VehicleGpsData>>() {})


    /**
     * A megkapott járművek gps adatait feldolgozza egy összesített formába
     *
     * @param rawVehList Feldolgozatlan jármű adatok listáját kéri
     * @return A végül megjelenításre kerülő sorok listáját adja ki
     */
    private fun processVehicleGpsData(rawVehList: List<VehicleGpsData>): List<VehicleGpsDisplayData> {
        //Rendszámonként végigmegy minden járművön
        return rawVehList.flatMap { vehicle ->
            //Az adott jármű napjait szétbontja
            val positionsByDay = vehicle.positions.groupBy { it.t.toLocalDate() }
            //Megcsinálja a feldolgozást és eredménylistát készít.
            positionsByDay.mapNotNull { (eventDay, positions) ->
                val maxDateClass = positions.maxByOrNull { it.t }
                val minDateClass = positions.minByOrNull { it.t }

                if (maxDateClass != null && minDateClass != null) {
                    val dailyDistanceTravelled = maxDateClass.totalDist - minDateClass.totalDist
                    val gpsReceptionQuality = NumberProcessor.calculateGoodPercentage(
                        positions.count { it.gpsFix },
                        positions.count { !it.gpsFix }
                    )
                    val maximumSpeed = positions.maxOfOrNull { it.gpsSpeed } ?: 0
                    val currentPosition = buildString {
                        append(maxDateClass.city)
                        append(", ${maxDateClass.road ?: "-"}")
                        append(" | lat:${maxDateClass.gpsLat}/lon:${maxDateClass.gpsLon}")
                    }
                    //Eredmény egy sora
                    VehicleGpsDisplayData(
                        vehicle.licenseNo,
                        eventDay,
                        gpsReceptionQuality,
                        dailyDistanceTravelled,
                        maximumSpeed,
                        currentPosition
                    )
                } else null
            }
        }
    }

    /**
     * Excelt generál a kapott adatokból
     *
     * @param vehGpsDisList A megjelenítendő lista
     * @param excelConfig Excel konfigurációja. Tartalma: Oszlopnév, oszlop típusa, értékhezköthető kulcs stb.
     * @return az excel fájlt adja ki
     */
    fun getVehicleGpsDataExcel(
        vehGpsDisList: List<VehicleGpsDisplayData>,
        excelConfig: VehicleGpsDataExcelConfig,
    ): ByteArray {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet(excelConfig.sheetName)
        val headerRow = sheet.createRow(0)
        //Fejléc stílusa
        val headerStyle = workbook.createCellStyle()
        headerStyle.fillForegroundColor = IndexedColors.GREY_25_PERCENT.getIndex()
        headerStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
        headerStyle.alignment = HorizontalAlignment.CENTER
        headerStyle.verticalAlignment = VerticalAlignment.CENTER
        val font = workbook.createFont()
        font.fontHeightInPoints = 14
        headerStyle.setFont(font)
        //Fejléc adatok feltöltése adattal és stílussal.
        val tableHeader = excelConfig.headers.map { it.name }
        tableHeader.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = headerStyle
        }
        //Adatok beszúrása a cellákba
        vehGpsDisList.forEachIndexed { index, vehicle ->
            val row = sheet.createRow(index + 1)
            var colIndex = excelConfig.getKeyPosition("licenseNo")
            row.createCell(colIndex).apply {
                setCellValue(vehicle.licenseNo)
                cellStyle = getExcelFormatForKey(excelConfig.headers[colIndex].columnFormat, workbook)
            }
            colIndex = excelConfig.getKeyPosition("eventDay")
            row.createCell(colIndex).apply {
                setCellValue(vehicle.eventDay)
                cellStyle = getExcelFormatForKey(excelConfig.headers[colIndex].columnFormat, workbook)
            }
            colIndex = excelConfig.getKeyPosition("gpsReceptionQuality")
            row.createCell(colIndex).apply {
                setCellValue(vehicle.gpsReceptionQualityExcelValue()) //Mert az excel felszorozza 100-zal
                cellStyle = getExcelFormatForKey(excelConfig.headers[colIndex].columnFormat, workbook)
            }
            colIndex = excelConfig.getKeyPosition("dailyDistanceTravelled")
            row.createCell(colIndex).apply {
                setCellValue(vehicle.dailyDistanceTravelled.toDouble())
                cellStyle = getExcelFormatForKey(excelConfig.headers[colIndex].columnFormat, workbook)
            }
            colIndex = excelConfig.getKeyPosition("maximumSpeed")
            row.createCell(colIndex).apply {
                setCellValue(vehicle.maximumSpeed.toDouble())
                cellStyle = getExcelFormatForKey(excelConfig.headers[colIndex].columnFormat, workbook)
            }
            colIndex = excelConfig.getKeyPosition("currentPosition")
            row.createCell(colIndex).apply {
                setCellValue(vehicle.currentPosition)
                cellStyle = getExcelFormatForKey(excelConfig.headers[colIndex].columnFormat, workbook)
            }
        }
        //Cellákat méretezi, úgy hogy olvasható legyen
        for (i in 0 until sheet.getRow(0).lastCellNum) {
            sheet.autoSizeColumn(i)
            //10%-kal növeli a szélességet, mert nem jól számolja ki alapból a könyvtár
            val currentWidth = sheet.getColumnWidth(i)
            sheet.setColumnWidth(i, (currentWidth * 1.1).toInt())
        }
        //Fájl generálása
        val outputStream = ByteArrayOutputStream()
        workbook.write(outputStream)
        workbook.close()
        //Excel kiadása
        return outputStream.toByteArray()
    }
}
