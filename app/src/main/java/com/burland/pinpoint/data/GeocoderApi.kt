package com.burland.pinpoint.data

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// OSM Nominatim Response format
data class PhotonResponse(
    val features: List<PhotonFeature>
)

data class PhotonFeature(
    val geometry: PhotonGeometry,
    val properties: PhotonProperties
)

data class PhotonGeometry(
    val coordinates: List<Double> // [lon, lat]
)

data class PhotonProperties(
    val name: String?,
    val street: String?,
    val housenumber: String?,
    val city: String?,
    val state: String?,
    val postcode: String?,
    val country: String?,
    val osm_key: String?,
    val osm_value: String?
) {
    fun toDisplayName(): String {
        return listOfNotNull(name, housenumber, street, city, state, country).joinToString(", ")
    }
}

// Keeping Nominatim types for reference/fallback if needed, but primary is now Photon
data class NominatimResponse(
    @SerializedName("lat") val lat: String,
    @SerializedName("lon") val lon: String,
    @SerializedName("display_name") val displayName: String,
    @SerializedName("class") val category: String,
    @SerializedName("type") val type: String
)

// US Census Geocoder Response
data class CensusResponse(
    val result: CensusResult
)

data class CensusResult(
    val addressMatches: List<CensusMatch>
)

data class CensusMatch(
    val coordinates: CensusCoordinates,
    val addressComponents: CensusAddressComponents,
    val matchedAddress: String
)

data class CensusCoordinates(
    val x: Double, // Longitude
    val y: Double  // Latitude
)

data class CensusAddressComponents(
    val fromAddress: String?,
    val toAddress: String?,
    val preQualifier: String?,
    val preDirection: String?,
    val preType: String?,
    val streetName: String?,
    val suffixType: String?,
    val suffixDirection: String?,
    val suffixQualifier: String?,
    val city: String?,
    val state: String?,
    val zip: String?
)

data class ArcGisResponse(
    val candidates: List<ArcGisCandidate>
)

data class ArcGisCandidate(
    val address: String,
    val location: ArcGisLocation,
    val attributes: ArcGisAttributes
)

data class ArcGisLocation(
    val x: Double,
    val y: Double
)

data class ArcGisAttributes(
    @SerializedName("Match_addr") val matchAddr: String,
    @SerializedName("Addr_type") val addrType: String
)

interface GeocoderApi {
    // Photon API
    @GET("https://photon.komoot.io/api/")
    suspend fun searchPhoton(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5
    ): PhotonResponse

    // US Census Geocoder API
    @GET("https://geocoding.geo.census.gov/geocoder/locations/onelineaddress")
    suspend fun searchCensus(
        @Query("address") address: String,
        @Query("benchmark") benchmark: String = "Public_AR_Current",
        @Query("format") format: String = "json"
    ): CensusResponse

    // ArcGIS World Geocoding Service (Free tier, no token needed for simple search)
    @GET("https://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer/findAddressCandidates")
    suspend fun searchArcGis(
        @Query("singleLine") address: String,
        @Query("f") format: String = "json",
        @Query("outFields") outFields: String = "Match_addr,Addr_type",
        @Query("maxLocations") maxLocations: Int = 5
    ): ArcGisResponse

    @GET("search")
    suspend fun searchAddress(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("dedupe") dedupe: Int = 0,
        @Query("limit") limit: Int = 5,
        @Header("User-Agent") userAgent: String = "Pinpoint-Private-Geocoder/1.0"
    ): List<NominatimResponse>
}
