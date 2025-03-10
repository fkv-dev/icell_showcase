package hu.fkv.util

import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFWorkbook

object ExcelManager {
    /**
     * Létrehoz egy Excel stílust a megadott szöveg alapján
     * A stílus a szöveg típusának megfelelő formázást alkalmazza
     *
     * @param colType A formázandó szöveg típusa (pl. "Number", "Date", stb.)
     * @param workbook Az Excel munkafüzet, amelyhez a stílust létrehozzuk.
     *  Azért kell, mert különben hibás stílust ad ki.
     * @return Az Excel cella stílusa a megfelelő formázással
     */
    fun getExcelFormatForKey(colType: String, workbook: XSSFWorkbook): XSSFCellStyle {
        val cellStyle = workbook.createCellStyle()
        val dataFormat = workbook.createDataFormat()

        val formatKey: String = when (colType) {
            "Number" -> "0.00"
            "Date" -> "yyyy-MM-dd"
            "Percentage" -> "0.00%"
            "Text" -> "@"
            else -> "@"
        }
        cellStyle.dataFormat = dataFormat.getFormat(formatKey)
        return cellStyle
    }
}
