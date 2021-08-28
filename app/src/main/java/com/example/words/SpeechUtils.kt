package com.example.words

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import java.util.*


/**
 * Created by zhenqiang on 2016/12/9.
 */
class SpeechUtils(private val context: Context?) {
    private var textToSpeech: TextToSpeech?=null// TTS对象



    companion object {
        private const val TAG = "SpeechUtils"
        private var singleton: SpeechUtils? = null
        fun getInstance(context: Context?): SpeechUtils? {
            if (singleton == null) {
                synchronized(SpeechUtils::class.java) {
                    if (singleton == null) {
                        singleton = SpeechUtils(context)
                    }
                }
            }
            return singleton
        }
    }



    init {
        textToSpeech = TextToSpeech(context, OnInitListener { i ->
            if (i == TextToSpeech.SUCCESS) {
                textToSpeech!!.language = Locale.US
                textToSpeech!!.setPitch(1.0f) // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
                textToSpeech!!.setSpeechRate(1.0f)
            }
        })
    }

    fun speakText(text: String?) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null)
    }
}