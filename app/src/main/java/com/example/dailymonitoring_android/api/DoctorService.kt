package com.example.dailymonitoring_android.api

import com.example.dailymonitoring_android.model.Question
import com.example.dailymonitoring_android.model.Questionnaire
import retrofit2.Call
import retrofit2.http.POST


interface DoctorService {
    @POST("question/getAllQuestions")
    fun getAllQuestions(): Call<List<Question>>

    @POST("question/getFirstQuestion")
    fun getFirstQuestion(): Call<List<Questionnaire>>
}