package hu.fkv.entity.vehiclepositiondata.excelconfig

/**
 * Az excel konfigurációját képzi le.
 */
data class VehicleGpsDataExcelConfig(
    val fileName: String,
    val sheetName: String,
    val headers: List<VehicleGpsDataExcelConfigHeader>,
){
    /**
     * Meghatározza egy adott kulcs pozícióját a headers listában
     * Ha a kulcs nem található, akkor egy nagy értéket ad vissza
     *
     * @param key A keresett kulcs
     * @return A kulcs pozícióját adja meg, vagy ezret, ha nem található, ez azért van, mert valahova rakni kell az adaot.
     */
    fun getKeyPosition(key: String) = headers.indexOfFirst { it.key == key }.takeIf { it != -1 } ?: 1000
}

