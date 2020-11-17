package com.example.dailymonitoring_android.api

import com.example.dailymonitoring_android.model.Question
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST


interface DoctorService {
    @POST("question/getAllQuestions")
    fun getAllQuestions(): Call<List<Question>>
}