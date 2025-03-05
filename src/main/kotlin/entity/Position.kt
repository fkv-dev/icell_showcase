package hu.fkv.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class Position(
    @JsonProperty("gps_fix") val gpsFix: Boolean,
    val country: String? = null,
    val city: String,
    @JsonProperty("gps_lat") val gpsLat: Double,
    @JsonProperty("gps_speed") val gpsSpeed: Int,
    @JsonProperty("total_dist") val totalDist: Int,
    val ign: Boolean? = null,
    val t: LocalDateTime,
    //val t: String? = null,
    @JsonProperty("gps_alt") val gpsAlt: Int,
    val milage: Int? = null,
    val road: String? = null,
    @JsonProperty("gps_lon") val gpsLon: Double,
    @JsonProperty("gps_heading") val gpsHeading: Int? = null,
)
