package com.example.offlinerouteguide.Retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitCient {
    private const val PhotonBaseUrl = "https://photon.komoot.io/"
    private const val ValhallaBaseUrl = "https://valhalla1.openstreetmap.de/"

    private const val StadiaRoutingUrl = "https://api.stadiamaps.com/"

    val apiService: GeocodingApi by lazy{
        Retrofit.Builder()
            .baseUrl(PhotonBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(GeocodingApi::class.java)
    }
    val client = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .protocols(listOf(okhttp3.Protocol.HTTP_1_1)) // Fallback to HTTP 1.1 to bypass HTTP/2 bugs
        .build()

    val valhallaApi: ValhallaRoutingApi by lazy {
        Retrofit.Builder().baseUrl(ValhallaBaseUrl).client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(
            ValhallaRoutingApi::class.java)
    }

    val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        val authenticatedRequest = originalRequest.newBuilder()
            // Attach the Stadia API Key securely in the header
            .header("Authorization", "YOUR-API-KEY")
            .build()

        chain.proceed(authenticatedRequest)
    }

    // 2. Build your Client
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor) // Applies to all requests automatically
        .build()

    val stadiaApi: ValhallaRoutingApi by lazy {
        Retrofit.Builder().baseUrl(StadiaRoutingUrl).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(
                ValhallaRoutingApi::class.java)
    }

}