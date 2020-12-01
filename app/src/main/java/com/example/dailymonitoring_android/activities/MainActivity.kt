package com.example.dailymonitoring_android.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.generateViewId
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.dailymonitoring_android.R
import com.example.dailymonitoring_android.api.DoctorService
import com.example.dailymonitoring_android.model.HTTPRequestBody
import com.example.dailymonitoring_android.model.QRNQ
import com.example.dailymonitoring_android.model.Question
import com.example.dailymonitoring_android.model.Questionnaire
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private val apiUrl = "http://10.0.2.2:8080" //Remplacer par http://10.0.2.2:8080 ou adresse ngrok

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val layout: LinearLayout = findViewById<View>(R.id.activity_main) as LinearLayout



        val retrofit = Retrofit.Builder()
            .baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(DoctorService::class.java)

        val body = HTTPRequestBody("2")

        val QRNQ = service.getQRNQ(body)
        QRNQ.enqueue(object : Callback<List<QRNQ>> {

            override fun onResponse(
                    call: Call<List<QRNQ>>,
                    response: Response<List<QRNQ>>
            ) {
                val allTanStop = response.body()
                allTanStop?.let {
                    for (tanStop in it) {
                        Log.i("TEST QRNQ", "${tanStop.question}")
                    }
                }
            }

            override fun onFailure(call: Call<List<QRNQ>>, t: Throwable) {
                Log.e("erreur QRNQ question", "errrreuuuuuuuuuurrrrr : $t")
            }
        })

        val allQuestions = service.getAllQuestions()


        allQuestions.enqueue(object : Callback<List<Question>> {

            override fun onResponse(
                    call: Call<List<Question>>,
                    response: Response<List<Question>>
            ) {
                val allTanStop = response.body()
                allTanStop?.let { generateButtons(it, layout) }
                allTanStop?.let {
                    for (tanStop in it) {
                        Log.d("AllQuestions", "${tanStop.QUESTION}")
                    }
                }
            }

            override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                Log.e("test", "errrreuuuuuuuuuurrrrr : $t")
            }
        })

        val firstQuestion = service.getFirstQuestion()
        firstQuestion.enqueue(object : Callback<List<Questionnaire>> {

            override fun onResponse(
                    call: Call<List<Questionnaire>>,
                    response: Response<List<Questionnaire>>
            ) {
                val allTanStop = response.body()
                Log.i("firstQuestion", "premiere question : ${allTanStop?.get(0)?.NUM_PREMIERE_QUESTION}")
            }

            override fun onFailure(call: Call<List<Questionnaire>>, t: Throwable) {
                Log.e("erreur First question", "errrreuuuuuuuuuurrrrr : $t")
            }
        })


    }

    /**
     * Génére des boutons avec toutes les questsions de la bdd
     *
     * @param listQuestions Liste de toutes les questions de la bdd
     * @param layout layout actuel
     */
    fun generateButtons(listQuestions: List<Question>, layout: LinearLayout){
        for (question in listQuestions){
            val button = Button(this)
            button.text = question.QUESTION
            button.id = generateViewId() // <-- Génère un id non utilisé pour le bouton
            layout.addView(button)
        }
    }
}