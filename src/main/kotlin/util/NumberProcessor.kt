package hu.fkv.util

import java.math.RoundingMode

//TODO lehetne top-level is, de így csoportosítva van.

/**
 * Az általános számokkal való műveleteket tárplja
 */
object NumberProcessor{
    /**
     * Kiszámolja mennyire volt jó százalékosan az adott eset
     *
     * @param good a jó esetek száma
     * @param bad a rossz esetek száma
     * @param scale a tizedes helyiértéke
     */
    fun calculateGoodPercentage(good: Int, bad: Int, scale: Int = 2): Double {
        val total = good + bad
        if (total <= 0 && scale < 0) return 0.0
        return ((good.toDouble() / total) * 100).toBigDecimal().setScale(scale, RoundingMode.UP).toDouble()
    }
}
