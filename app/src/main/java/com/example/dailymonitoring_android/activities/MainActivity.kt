package com.example.dailymonitoring_android.activities

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.View.generateViewId
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dailymonitoring_android.R
import com.example.dailymonitoring_android.api.RetrofitService
import com.example.dailymonitoring_android.model.HTTPRequestBody
import com.example.dailymonitoring_android.model.QRNQ
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MainActivity : AppCompatActivity() {

    // Code pour Speech To Text
    companion object {
        private const val REQUEST_CODE_STT = 1
    }

    private val textToSpeechEngine: TextToSpeech by lazy {
        TextToSpeech(this
        ) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeechEngine.language = Locale.FRANCE
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val layout: LinearLayout = findViewById<View>(R.id.layout_responses) as LinearLayout

        //Récupère la question à traiter
        val thisQuestion = intent.getIntExtra("nextQuestionID", -1) // Récupère le paramètre nextQuestionID
        Log.i("---", "---")
        Log.i("ID question suivante :", "$thisQuestion")
        Log.i("---", "---")

        if (thisQuestion == null || thisQuestion == -1) {
            generateEndingScreen(layout)
        }
        else {
            generateQuestionScreen(thisQuestion, layout)
        }

        listenSpeechToText()
        speakTextToSpeech()

    }

    // Bouton qui lance le Text To Speech
    fun speakTextToSpeech(){
        button_tts.setOnClickListener {
            val text = text_stt.text.toString().trim()
            if (text.isNotEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeechEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts1")
                } else {
                    textToSpeechEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null)
                }
            } else {
                Toast.makeText(this, "Il n'y a pas de texte à lire", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Bouton qui lance le Speech To Text
    fun listenSpeechToText(){
        button_stt.setOnClickListener {
            val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            sttIntent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR")
            sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Parlez maintenant!")

            try {
                startActivityForResult(sttIntent, REQUEST_CODE_STT)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                Toast.makeText(this, "Votre appareil n'est pas compabible.", Toast.LENGTH_LONG).show()
            }
        }
    }

    //Récupère le résultat du Speech To Text
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_STT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    result?.let {
                        val recognizedText = it[0]
                        text_stt.text = recognizedText
                    }
                }
            }
        }
    }

    //Génère une page avec une question et ses réponses
    fun generateQuestionScreen(thisQuestion: Int, layout: LinearLayout){
        val body = HTTPRequestBody(thisQuestion.toString())
        val qrnq = RetrofitService.doctorService.getQRNQ(body)

        qrnq.enqueue(object : Callback<List<QRNQ>> {

            override fun onResponse(
                    call: Call<List<QRNQ>>,
                    response: Response<List<QRNQ>>
            ) {
                val qrnqResponse = response.body()
                qrnqResponse?.let {
                    generateButtons(it, layout)
                    generateTextQuestion(it[0].question)
                }
            }
            override fun onFailure(call: Call<List<QRNQ>>, t: Throwable) {
                Log.e("erreur QRNQ question", "erreur : $t")
            }
        })
    }

    // Génère une page de fin de questionnaire
    fun generateEndingScreen(layout: LinearLayout){
        generateTextQuestion("Merci d'avoir répondu à ce questionnaire")
        val button = Button(this)
        button.text = "Quitter"
        button.id = generateViewId()
        button.setBackgroundColor(Color.BLUE)
        button.setOnClickListener {
            finishAffinity()
        }
        layout.addView(button)
    }


    //Génére des boutons avec les réponses à la question
    fun generateButtons(listQRNQ: List<QRNQ>, layout: LinearLayout) {
        for (qrnq in listQRNQ) {
            val button = Button(this)
            button.text = qrnq.reponse
            button.id = generateViewId()
            button.setBackgroundColor(Color.GREEN)
            button.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("nextQuestionID", qrnq.num_question_suivante) // Ajoute un paramètre nextQuestionID
                startActivity(intent)
            }
            layout.addView(button)
        }
    }

    //Associe le texte de la question au textView
    fun generateTextQuestion(textQuestion: String?){
        text_question.text = textQuestion
    }


    override fun onPause() {
        textToSpeechEngine.stop()
        super.onPause()
    }

    override fun onDestroy() {
        textToSpeechEngine.shutdown()
        super.onDestroy()
    }


}