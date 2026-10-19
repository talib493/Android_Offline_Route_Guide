package com.example.offlinerouteguide.Retrofit

import com.example.offlinerouteguide.dataStructures.GeocodingDataStruct
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("api")
    suspend fun search(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("lang") lang: String = "en"

    ): GeocodingDataStruct
    @GET("reverse")
    suspend fun reverseSearch(
        @Query("lon") lon: Double,
        @Query("lat") lat : Double
    ): GeocodingDataStruct
}