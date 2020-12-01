package com.example.dailymonitoring_android.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {

    private val apiUrl = "http://10.0.2.2:8080" //Remplacer par http://10.0.2.2:8080 ou adresse ngrok

    private fun retrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val doctorService: DoctorService by lazy {
        retrofit().create(DoctorService::class.java)
    }
}