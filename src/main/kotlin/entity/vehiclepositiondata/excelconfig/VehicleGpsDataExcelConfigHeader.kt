package hu.fkv.entity.vehiclepositiondata.excelconfig

import com.fasterxml.jackson.annotation.JsonProperty

data class VehicleGpsDataExcelConfigHeader(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("columnFormat")
    val columnFormat: String,
    @JsonProperty("key")
    val key: String
)
