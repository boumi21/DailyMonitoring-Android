package com.example.dailymonitoring_android.api

import com.example.dailymonitoring_android.model.HTTPRequestBody
import com.example.dailymonitoring_android.model.QRNQ
import com.example.dailymonitoring_android.model.Question
import com.example.dailymonitoring_android.model.Questionnaire
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface DoctorService {
    @POST("question/getAllQuestions")
    fun getAllQuestions(): Call<List<Question>>

    @POST("question/getFirstQuestion")
    fun getFirstQuestion(): Call<List<Questionnaire>>

    @POST("question/getQRNQ")
    fun getQRNQ(@Body body: HTTPRequestBody): Call<List<QRNQ>>
}
