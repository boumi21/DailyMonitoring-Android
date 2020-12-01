package com.example.dailymonitoring_android.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.dailymonitoring_android.R
import com.example.dailymonitoring_android.api.DoctorService
import com.example.dailymonitoring_android.model.Questionnaire
import kotlinx.android.synthetic.main.activity_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MenuActivity : AppCompatActivity() {

    private val apiUrl = "http://10.0.2.2:8080" //Remplacer par http://10.0.2.2:8080 ou adresse ngrok

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        //Récupère le service (pour l'accès à l'API)
        val service = getService()





        button_quit.setOnClickListener {
            finishAffinity()
        }

        button_start.setOnClickListener {

            val firstQuestion = getFirstQuestion(service)

        }

    }

    fun getFirstQuestion(service: DoctorService){
        val getFirstQuestion = service.getFirstQuestion()
        getFirstQuestion.enqueue(object : Callback<List<Questionnaire>> {

            override fun onResponse(
                call: Call<List<Questionnaire>>,
                response: Response<List<Questionnaire>>
            ) {
                val firstQuestion = response.body()?.get(0)?.NUM_PREMIERE_QUESTION
                firstQuestion?.let { startMainActivity(firstQuestion) }
                //Log.i("firstQuestion", "premiere question : ${firstQuestion?.get(0)?.NUM_PREMIERE_QUESTION}")
            }

            override fun onFailure(call: Call<List<Questionnaire>>, t: Throwable) {
                Log.e("erreur First question", "errrreuuuuuuuuuurrrrr : $t")
            }
        })
    }

    fun startMainActivity(firstQuestion: Int){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("nextQuestionID", firstQuestion)
        startActivity(intent)
    }

    fun getService(): DoctorService {
        val retrofit = Retrofit.Builder()
            .baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(DoctorService::class.java)
    }
}