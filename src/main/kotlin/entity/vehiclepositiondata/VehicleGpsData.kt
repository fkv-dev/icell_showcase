package hu.fkv.entity.vehiclepositiondata

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class VehicleGpsData(
    @JsonProperty("license_no") val licenseNo: String,
    val positions: List<Position>,
)
