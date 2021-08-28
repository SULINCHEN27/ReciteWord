package com.example.words

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.afollestad.materialdialogs.MaterialDialog
import com.example.words.databinding.ActivityLoginBinding
import com.example.words.retrofit.ResponseLogin
import com.example.words.retrofit.ResponseMsg
import com.example.words.retrofit.Retrofit
import com.example.words.ui.dashboard.DashboardFragment
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        runBlocking {
            launch {
                val getToken = getSharedPreferences("data", Context.MODE_PRIVATE)
                val isChecked = getToken.getBoolean("isChecked", false)
                if (isChecked) {
                    val token = getToken.getString("token", "")
                    val appService = Retrofit().createRetrofit("http://81.68.100.77:7777/")
                    if (token != null) {
                        appService.getMsg(token).enqueue(object :Callback<ResponseMsg>{
                            override fun onFailure(call: Call<ResponseMsg>, t: Throwable) {
                                t.printStackTrace()
                            }

                            override fun onResponse(call: Call<ResponseMsg>, response: Response<ResponseMsg>) {
                                val information = response.body()
                                if (information != null) {
                                    if (information.status == 1) {
                                        startActivity(Intent(this@LoginActivity, MenuActivity::class.java))
                                        finish()
                                    }
                                }
                            }
                        })
                    }

                }
            }
        }
        super.onCreate(savedInstanceState)

        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.loginBtn.setOnClickListener {
            val account = binding.account.text.toString()
            val password = binding.password.text.toString()
            val appService = Retrofit().createRetrofit("http://81.68.100.77:7777/")
            val dialog = MaterialDialog.Builder(this)
                .title("Progress")
                .content("Please Wait...")
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();
            appService.postLogin(account,password).enqueue(object : Callback<ResponseLogin> {
                override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                    val body = response.body()
                    if (body != null) {
                        if (body.status == 1) {
                            dialog.dismiss()
                            val token = "Bearer "+body.token
                            Log.d("token",token)
                            val storage = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
                            storage.putString("token",token)
                            appService.getMsg(token).enqueue(object :Callback<ResponseMsg>{
                                override fun onFailure(call: Call<ResponseMsg>, t: Throwable) {
                                    t.printStackTrace()
                                }

                                override fun onResponse(call: Call<ResponseMsg>, response: Response<ResponseMsg>) {
                                    val information = response.body()
                                    storage.apply()
                                    if (information != null) {
                                        if (information.status == 1) {
                                            storage.putString("mailbox", information.data.mailbox)
                                            storage.putString("name", information.data.name)
                                            storage.putInt("education", information.data.education)
                                            storage.putInt(
                                                "learned_words_number",
                                                information.data.learned_words_number
                                            )
                                            storage.apply()
                                            if (binding.checkedBox.isChecked) {
                                                storage.putBoolean("isChecked", true)
                                                storage.apply()
                                            } else {
                                                storage.putBoolean("isChecked", false)
                                                storage.apply()
                                            }
                                            startActivity(Intent(this@LoginActivity,MenuActivity::class.java))
                                            finish()
                                        }else{
                                            dialog.dismiss()
                                            materialDialog("登录","获取用户失败，请重新登录","确认")
                                        }
                                    }else{
                                        dialog.dismiss()
                                        materialDialog("登录","获取用户失败，请重新登录","确认")
                                    }
                                }


                            })
//                            val information = appService.getMsg(body.token).await()
//                            storage.apply()
//                            if (information.status == 1) {
//                                storage.putString("mailbox", information.data.mailbox)
//                                storage.putString("name", information.data.name)
//                                storage.putInt("education", information.data.education)
//                                storage.putInt(
//                                    "learned_words_number",
//                                    information.data.learned_words_number
//                                )
//                                storage.apply()
//                                if (binding.checkedBox.isChecked) {
//                                    storage.putBoolean("isChecked", true)
//                                    storage.apply()
//                                } else {
//                                    storage.putBoolean("isChecked", false)
//                                    storage.apply()
//                                }
//                                startActivity(Intent(this@LoginActivity,MainActivity::class.java))
//                                finish()
//                            }else{
//                                materialDialog("登录","获取用户失败，请重新登录","确认")
//                            }
                        }else{
                            dialog.dismiss()
                            materialDialog("登录","登录失败!${body.message}","确认")
                        }
                    }
                }

            })
//            runBlocking {
//                coroutineScope {
//                    launch {
//                        val response = appService.postLogin(account, password).await()
//                        if (response.status == 1) {
//                            dialog.dismiss()
//                            val storage = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
//                            storage.putString("token", response.token)
//                            val information = appService.getMsg(response.token).await()
//                            storage.apply()
//                            if (information.status == 1) {
//                                storage.putString("mailbox", information.data.mailbox)
//                                storage.putString("name", information.data.name)
//                                storage.putInt("education", information.data.education)
//                                storage.putInt(
//                                    "learned_words_number",
//                                    information.data.learned_words_number
//                                )
//                                storage.apply()
//                                if (binding.checkedBox.isChecked) {
//                                    storage.putBoolean("isChecked", true)
//                                    storage.apply()
//                                } else {
//                                    storage.putBoolean("isChecked", false)
//                                    storage.apply()
//                                }
//                                startActivity(Intent(this@LoginActivity,MainActivity::class.java))
//                                finish()
//                            }else{
//                                materialDialog("登录","获取用户失败，请重新登录","确认")
//                            }
//                        }else{
//                            materialDialog("登录","登录失败，请检查用户名或密码","确认")
//                        }
//                    }
//                    }
//                }
            }

        }

    fun materialDialog(title:String,content:String,positiveText:String): MaterialDialog {
        return MaterialDialog.Builder(this)
            .title(title)
            .content(content)
            .positiveText(positiveText)
            .show()
        }
    }
