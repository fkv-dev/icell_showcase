package hu.fkv.entity

/**
 * Az excel konfigurációját képzi le.
 */
data class VehicleGpsDataExcelConfig(
    val fileName: String,
    val sheetName: String,
    val headers: List<String>,
)

