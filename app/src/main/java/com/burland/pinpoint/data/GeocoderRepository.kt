package com.burland.pinpoint.data

import com.burland.pinpoint.domain.LocationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeocoderRepository(private val api: GeocoderApi = NetworkModule.api) {
    suspend fun getCoordinates(address: String): List<LocationModel> {
        return withContext(Dispatchers.IO) {
            val results = mutableListOf<LocationModel>()

            // 1. ARCGIS ONLY (User Choice: Commercial Accuracy, Free Tier)
            try {
                val arcGisResponse = api.searchArcGis(address = address)
                val arcGisResults = arcGisResponse.candidates.map { candidate ->
                    // Round to 6 decimal places (~11cm precision) for presentation
                    val lat = String.format("%.6f", candidate.location.y)
                    val lon = String.format("%.6f", candidate.location.x)
                    
                    LocationModel(
                        latitude = lat,
                        longitude = lon,
                        displayName = candidate.attributes.matchAddr,
                        category = "commercial",
                        type = candidate.attributes.addrType
                    )
                }
                results.addAll(arcGisResults)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            results
        }
    }
}
