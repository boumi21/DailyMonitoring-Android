package com.example.dailymonitoring_android.activities

import android.content.Intent
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

        val nextQuestionID = intent.getStringExtra("nextQuestionID") // Récupère le paramètre nextQuestionID
        Log.i("---", "---")
        Log.i("ID question suivante :", "$nextQuestionID")
        Log.i("---", "---")

        val body = HTTPRequestBody("2")

        val QRNQ = service.getQRNQ(body)
        QRNQ.enqueue(object : Callback<List<QRNQ>> {

            override fun onResponse(
                    call: Call<List<QRNQ>>,
                    response: Response<List<QRNQ>>
            ) {
                val allTanStop = response.body()
                allTanStop?.let { generateButtons(it, layout) }
            }

            override fun onFailure(call: Call<List<QRNQ>>, t: Throwable) {
                Log.e("erreur QRNQ question", "errrreuuuuuuuuuurrrrr : $t")
            }
        })
        //Génére des boutons avec les réponses à la question
    }

    fun generateButtons(listQRNQ: List<QRNQ>, layout: LinearLayout) {
        for (qrnq in listQRNQ) {
            val button = Button(this)
            button.text = qrnq.reponse
            button.id = generateViewId()
            button.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("nextQuestionID", "${qrnq.num_question_suivante}") // Ajoute un paramètre nextQuestionID
                startActivity(intent)
            }
            layout.addView(button)
        }
    }
}