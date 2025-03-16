import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import com.fasterxml.jackson.core.type.TypeReference

import hu.fkv.config.AppConfig
import hu.fkv.entity.vehiclepositiondata.VehicleGpsDisplayData
import hu.fkv.service.VehiclePositionDataService
import hu.fkv.util.MyConstant.objectMapper

//Unit teszt osztály a VehiclePositionDataService szolgáltatás tesztelésére
@ExtendWith(SpringExtension::class, MockitoExtension::class)
@ContextConfiguration(classes = [AppConfig::class])
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class VehiclePositionDataServiceTest {

    companion object {
        private val logger: Logger = LogManager.getLogger(VehiclePositionDataServiceTest::class.java)
    }

    private lateinit var context: ApplicationContext
    private lateinit var appConfig: AppConfig

    @InjectMocks
    private lateinit var vehiclePositionDataService: VehiclePositionDataService

    //Azért szükséges, mert konfigurációs fájlból olvas ki értékeket
    @BeforeEach
    fun setup() {
        context = AnnotationConfigApplicationContext(AppConfig::class.java)
        appConfig = context.getBean(AppConfig::class.java)

        //VehiclePositionDataService inicializálása AppConfig beállításokkal
        with(vehiclePositionDataService) {
            testApiUrl = appConfig.testApiUrl
            testApiUser = appConfig.testApiUser
            testApiPass = appConfig.testApiPass
        }
    }

    /**
     * Segédfüggvény fájl tartalmának beolvasására az erőforrások közül
     *
     * @param fileName A fájl neve pl text.txt
     * @return A fájl tartalma
     */
    private fun readFile(fileName: String): String =
        javaClass.getResource(fileName)?.readText() ?: throw IllegalArgumentException("File not found: $fileName")

    /**
     * JSON adatokat alakít át VehicleGpsDisplayData listává
     *
     * @param json Ezt fogja átalakítani osztályá
     * @return A VehicleGpsDisplayData listát adja ki
     */
    private fun parseVehicleGpsDisplayData(json: String): List<VehicleGpsDisplayData> =
        objectMapper.readValue(json, object : TypeReference<List<VehicleGpsDisplayData>>() {})

    //Ellenőrzi, hogy az API hívás sikeresen lefut és érvényes választ ad vissza
    @Test
    @Order(1)
    fun checkVehicleApiRequestSucceeds() {
        val vehResp = vehiclePositionDataService.downloadVehiclePositionData()

        assertNotNull(vehResp, "The answer cannot be null")
        assertNotNull(vehResp.body, "The answer body cannot be null")
        assertEquals(HttpStatus.OK, vehResp.statusCode, "The http status code is incorrect")
        assertTrue(vehResp.body!!.isNotEmpty(), "The answer body is empty")
    }

    //Ellenőrzi, hogy az API által kapott adatok megfelelnek a várt tesztadatoknak.
    //A request_data.json-t fel kell tölteni a várt adatokkal.
    @Test
    @Order(2)
    fun testReceivedDataValidity() {
        //API-ból kapott adatok
        val apiRequestDataList = vehiclePositionDataService.getAggregateVehicleGpsDataJson()

        //Tesztadatok beolvasása fájlból
        val testFileRequestDataList = parseVehicleGpsDisplayData(readFile("request_data.json"))

        //Validáció
        assertNotNull(apiRequestDataList, "The data returned by the api cannot be null")
        assertNotNull(testFileRequestDataList, "The test data returned by the api cannot be null")
        assertEquals(testFileRequestDataList, apiRequestDataList, "The api answer is different")
    }

    //Ellenőrzi, hogy a feldolgozott adatok megfelelnek az előre definiált elvárásoknak.
    //A processed_vehicle_gps_data_result.json fel kell tölteni a várt feldolgozott adatokkal.
    @Test
    @Order(3)
    fun testProcessedDataAccuracy() {
        //API-ból kapott adatok JSON formátumban
        val apiRequestDataJson =
            objectMapper.writeValueAsString(vehiclePositionDataService.getAggregateVehicleGpsDataJson())

        //Várt eredmény beolvasása és normalizálása (sortörések eltávolítása)
        val testFileRequestDataJson =
            readFile("processed_vehicle_gps_data_result.json").replace(Regex("[\r\n]"), "")

        //Összehasonlítás
        assertEquals(
            testFileRequestDataJson,
            apiRequestDataJson,
            "The processed data does not match the expected result"
        )
    }
}
