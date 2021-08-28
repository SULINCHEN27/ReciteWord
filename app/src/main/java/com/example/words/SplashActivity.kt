package com.example.words

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.words.retrofit.ResponseMsg
import com.example.words.retrofit.Retrofit
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thread {
            Thread.sleep(1500)
            val it = Intent(applicationContext, LoginActivity::class.java)
            startActivity(it)
            finish();
        }



    }


}