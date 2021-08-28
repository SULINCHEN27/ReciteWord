package com.example.words.ui.notifications

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.example.words.databinding.ActivityChooseBookBinding
import com.example.words.retrofit.ResponseBookDir
import com.example.words.retrofit.Retrofit
import com.example.words.BookAdapter
import retrofit2.Call
import retrofit2.Response

class ChooseBookActivity : AppCompatActivity() {
    private val bookList = ArrayList<ChooseBook>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityChooseBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toorbar)
            val materialDialog = MaterialDialog.Builder(this)
                .title("选择单词书")
                .positiveText("确认").onPositive { dialog, which ->
                    val token = getSharedPreferences("data", Context.MODE_PRIVATE)
                        ?.getString("token","")
                    if (token != null){
                        val appService = Retrofit().createRetrofit("http://81.68.100.77:7777/")
                        appService.bookDir(token).enqueue(object :retrofit2.Callback<ResponseBookDir>{
                            override fun onFailure(call: Call<ResponseBookDir>, t: Throwable) {
                                Toast.makeText(this@ChooseBookActivity,"获取失败", Toast.LENGTH_SHORT).show()
                                finish()
                            }

                            override fun onResponse(
                                call: Call<ResponseBookDir>,
                                response: Response<ResponseBookDir>
                            ) {
                                val body = response.body()
                                if (body != null) {
                                    for (i in 0 until body.data.size){
                                        bookList.add(ChooseBook(body.data[i]))
                                    }

                                    val adaper =
                                        BookAdapter(bookList)
                                    binding.recyclerViewChoose.adapter = adaper
                                }


                            }

                        })
                    }
                }
        materialDialog.show()
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerViewChoose.layoutManager = layoutManager
    }

}