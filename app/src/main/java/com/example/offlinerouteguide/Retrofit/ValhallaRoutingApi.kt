package com.example.offlinerouteguide.Retrofit

import com.example.offlinerouteguide.dataStructures.ValhallaRequest
import com.example.offlinerouteguide.dataStructures.ValhallaResponse
import com.example.offlinerouteguide.dataStructures.ValhallaTurnByTurnRequest
import com.example.offlinerouteguide.dataStructures.ValhallaTurnByTurnResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ValhallaRoutingApi {
    @POST("route")
    suspend fun getRoute(
        @Body request: ValhallaRequest
    ): Response<ValhallaResponse>

    @POST("route/v1")
    suspend fun getStadiaRoute(
        @Body request: ValhallaRequest
    ): Response<ValhallaResponse>

    @POST("route/v1")
    suspend fun getStadiaTurnByTurnRoute(
        @Body request: ValhallaTurnByTurnRequest
    ): Response<ValhallaTurnByTurnResponse>
}