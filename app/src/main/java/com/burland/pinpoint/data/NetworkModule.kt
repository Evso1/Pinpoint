package com.burland.pinpoint.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val BASE_URL = "https://nominatim.openstreetmap.org/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: GeocoderApi by lazy {
        retrofit.create(GeocoderApi::class.java)
    }
}
