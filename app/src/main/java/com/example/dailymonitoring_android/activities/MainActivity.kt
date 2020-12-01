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
import com.example.dailymonitoring_android.api.RetrofitService
import com.example.dailymonitoring_android.model.HTTPRequestBody
import com.example.dailymonitoring_android.model.QRNQ
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val layout: LinearLayout = findViewById<View>(R.id.activity_main) as LinearLayout

        //Récupère la question à traiter
        val thisQuestion = intent.getIntExtra("nextQuestionID", -1) // Récupère le paramètre nextQuestionID
        Log.i("---", "---")
        Log.i("ID question suivante :", "$thisQuestion")
        Log.i("---", "---")

        val body = HTTPRequestBody(thisQuestion.toString())

        val qrnq = RetrofitService.doctorService.getQRNQ(body)
        qrnq.enqueue(object : Callback<List<QRNQ>> {

            override fun onResponse(
                    call: Call<List<QRNQ>>,
                    response: Response<List<QRNQ>>
            ) {
                val qrnqResponse = response.body()
                qrnqResponse?.let { generateButtons(it, layout) }
            }

            override fun onFailure(call: Call<List<QRNQ>>, t: Throwable) {
                Log.e("erreur QRNQ question", "erreur : $t")
            }
        })
    }

    //Génére des boutons avec les réponses à la question
    fun generateButtons(listQRNQ: List<QRNQ>, layout: LinearLayout) {
        for (qrnq in listQRNQ) {
            val button = Button(this)
            button.text = qrnq.reponse
            button.id = generateViewId()
            button.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("nextQuestionID", qrnq.num_question_suivante) // Ajoute un paramètre nextQuestionID
                startActivity(intent)
            }
            layout.addView(button)
        }
    }

}