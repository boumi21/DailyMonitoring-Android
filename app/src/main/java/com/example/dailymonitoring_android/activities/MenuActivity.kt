package com.example.dailymonitoring_android.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.dailymonitoring_android.R
import com.example.dailymonitoring_android.api.RetrofitService
import com.example.dailymonitoring_android.model.Questionnaire
import kotlinx.android.synthetic.main.activity_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)


        button_quit.setOnClickListener {
            finishAffinity()
        }

        button_start.setOnClickListener {
            getFirstQuestion()
        }

    }

    fun getFirstQuestion(){
        val getFirstQuestion = RetrofitService.doctorService.getFirstQuestion()
        getFirstQuestion.enqueue(object : Callback<List<Questionnaire>> {

            override fun onResponse(
                call: Call<List<Questionnaire>>,
                response: Response<List<Questionnaire>>
            ) {
                val firstQuestion = response.body()?.get(0)?.NUM_PREMIERE_QUESTION
                firstQuestion?.let { startMainActivity(firstQuestion) }
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

}