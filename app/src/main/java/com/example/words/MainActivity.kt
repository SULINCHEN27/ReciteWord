package com.example.words

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.words.retrofit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val encrypt = findViewById<Button>(R.id.encrypt)
        val login = findViewById<Button>(R.id.loginBtn)
        val verifyCode = findViewById<Button>(R.id.verifyCode)
        val register = findViewById<Button>(R.id.Register)
        val modification = findViewById<Button>(R.id.modification)
        val book = findViewById<Button>(R.id.book)
        val read = findViewById<Button>(R.id.read)
        val getUserLearn = findViewById<Button>(R.id.getLearn)

        encrypt.setOnClickListener {
            val retrofit = Retrofit.Builder().baseUrl("http://81.68.100.77:7777/")
                    .addConverterFactory(GsonConverterFactory.create()).build()
            val appService = retrofit.create(AppService::class.java)

            appService.getMsg("1").enqueue(object : Callback<ResponseMsg> {
                override fun onFailure(call: Call<ResponseMsg>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(call: Call<ResponseMsg>, response: Response<ResponseMsg>) {
                    val body = response.body()
                    if (body != null) {
                        Log.d("status", body.status.toString())
                        Log.d("message", body.message)
                        Log.d("data", body.data.toString())
                    }
                }


            })


        }

        login.setOnClickListener {
            val retrofit = Retrofit.Builder().baseUrl("http://81.68.100.77:7777/")
                    .addConverterFactory(GsonConverterFactory.create()).build()
            val appService = retrofit.create(AppService::class.java)
            val getToken = getSharedPreferences("data", Context.MODE_PRIVATE)
            val token = getToken.getString("token", "")
            if (token != null) {
                appService.getRecentBook(token).enqueue(object : Callback<ResponseRecentBook> {
                    override fun onFailure(call: Call<ResponseRecentBook>, t: Throwable) {
                        t.printStackTrace()
                    }

                    override fun onResponse(call: Call<ResponseRecentBook>, response: Response<ResponseRecentBook>) {
                        val body = response.body()
                        if (body != null && body.status == 1) {
                            appService.getUserLearn(token,body.data,0).enqueue(object :Callback<ResponseUserLearn>{
                                override fun onFailure(call: Call<ResponseUserLearn>, t: Throwable) {
                                    t.printStackTrace()
                                }

                                override fun onResponse(call: Call<ResponseUserLearn>, response: Response<ResponseUserLearn>) {
                                    val a = response.body()
                                    if (a != null) {
                                        Log.d("data",a.data.record.get("${body.data}").toString())
                                    }
                                }

                            })
                        }
                    }
                }
                )

            }
        }
            verifyCode.setOnClickListener {
                val retrofit = Retrofit.Builder().baseUrl("http://81.68.100.77:7777/")
                        .addConverterFactory(GsonConverterFactory.create()).build()
                val appService = retrofit.create(AppService::class.java)

                appService.postCode("279126594@qq.com").enqueue(object : Callback<ResponseCode> {
                    override fun onFailure(call: Call<ResponseCode>, t: Throwable) {
                        t.printStackTrace()
                    }

                    override fun onResponse(call: Call<ResponseCode>, response: Response<ResponseCode>) {
                        val body = response.body()
                        Log.d("body", body.toString())
                        if (body != null) {
                            Log.d("status", body.status.toString())
                            Log.d("message", body.message)
                        }
                    }

                })
            }

            register.setOnClickListener {
                val retrofit = Retrofit.Builder().baseUrl("http://81.68.100.77:7777/")
                        .addConverterFactory(GsonConverterFactory.create()).build()
                val appService = retrofit.create(AppService::class.java)

                appService.postRegister("279126594@qq.com", "CHENSULIN", "123456", 3, 995590)
                        .enqueue(object : Callback<ResponseCode> {
                            override fun onFailure(call: Call<ResponseCode>, t: Throwable) {
                                t.printStackTrace()
                            }

                            override fun onResponse(call: Call<ResponseCode>, response: Response<ResponseCode>) {
                                val body = response.body()
                                Log.d("body", body.toString())
                                if (body != null) {
                                    Log.d("status", body.status.toString())
                                    Log.d("message", body.message)
                                }
                            }

                        })


            }

            modification.setOnClickListener {
                val retrofit = Retrofit.Builder().baseUrl("http://81.68.100.77:7777/")
                        .addConverterFactory(GsonConverterFactory.create()).build()
                val appService = retrofit.create(AppService::class.java)

                appService.putUser("1", "123456", "111111", "CHENSULIN", 3)
                        .enqueue(object : Callback<ResponseMsg> {
                            override fun onFailure(call: Call<ResponseMsg>, t: Throwable) {
                                t.printStackTrace()
                            }

                            override fun onResponse(call: Call<ResponseMsg>, response: Response<ResponseMsg>) {
                                val body = response.body()
                                if (body != null) {
                                    Log.d("status", body.status.toString())
                                    Log.d("message", body.message)
                                    Log.d("data", body.data.toString())
                                }
                            }

                        })
            }

            book.setOnClickListener {
                val retrofit = Retrofit.Builder().baseUrl("http://81.68.100.77:7777/")
                        .addConverterFactory(GsonConverterFactory.create()).build()
                val appService = retrofit.create(AppService::class.java)
                val getToken = getSharedPreferences("data", Context.MODE_PRIVATE)
                val token = getToken.getString("token", "")
                if (token != null) {
                    appService.bookWord(token, "CET4第1册").enqueue(object : Callback<ResponseWord> {
                        override fun onFailure(call: Call<ResponseWord>, t: Throwable) {
                            t.printStackTrace()
                        }

                        override fun onResponse(
                                call: Call<ResponseWord>,
                                response: Response<ResponseWord>
                        ) {
                            val body = response.body()
                            if (body != null) {
                                Log.d("message", body.message)
                                val output = openFileOutput("CET4第1册", Context.MODE_PRIVATE)
                                val writer = ObjectOutputStream(output)
                                writer.writeObject(body)
                                writer.close()


                                Log.d("data", body.data.size.toString())
                            }
                        }

                    })
                }


            }

            read.setOnClickListener {
                val input = openFileInput("CET4第2册乱序")
                val reader = ObjectInputStream(input)
                val body: ResponseWord = reader.readObject() as ResponseWord
                Log.d("body", body.data[0].content.trans.toString())
            }

            getUserLearn.setOnClickListener {
//            val retrofit = Retrofit.Builder().baseUrl("http://81.68.100.77:7777/")
//                    .addConverterFactory(GsonConverterFactory.create()).build()
//            val appService = retrofit.create(AppService::class.java)
//            val getToken = getSharedPreferences("data", Context.MODE_PRIVATE)
//            val token = getToken.getString("token", "")
//            val bookName = getToken.getString("bookName","")
//            if (token !=null&&bookName!=null){
//                appService.getUserLearn(token,bookName,1).enqueue(object :Callback<ResponseUserLearn>{
//                    override fun onFailure(call: Call<ResponseUserLearn>, t: Throwable) {
//                        t.printStackTrace()
//                    }
//
//                    override fun onResponse(call: Call<ResponseUserLearn>, response: Response<ResponseUserLearn>) {
//                        val body = response.body()
//                        if (body !=null){
//                            Log.d("status",body.status.toString())
//                            Log.d("message",body.message)
//                            Log.d("data",body.data.toString())
//                        }
//                    }
//
//                })
//            }
//            val storage = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
//            storage.putInt("totalLearn",10)
//            storage.apply()

                val speechUtils: SpeechUtils = SpeechUtils(this)
                speechUtils.speakText("communication")
            }


        }



}