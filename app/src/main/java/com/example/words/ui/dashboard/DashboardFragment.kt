package com.example.words.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.words.MainActivity
import com.example.words.R
import com.example.words.retrofit.ResponseMsg
import com.example.words.retrofit.Retrofit
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonObject
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.wait
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception
import javax.security.auth.callback.Callback
import kotlin.concurrent.thread

class DashboardFragment : Fragment() {

  private lateinit var dashboardViewModel: DashboardViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
//    val textView: TextView = root.findViewById(R.id.text_dashboard)
//    dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
//      textView.text = it
//    })


    val btn = root.findViewById<FloatingActionButton>(R.id.fabBtn)
    val textView = root.findViewById<TextView>(R.id.translate)
      val clearBtn = root.findViewById<Button>(R.id.clear)

      clearBtn.setOnClickListener {
          val text = root.findViewById<EditText>(R.id.translateText)
          text.setText("")
      }
    btn.setOnClickListener {
      val text = root.findViewById<EditText>(R.id.translateText).text.toString()
      if (text !=null){
          Log.d("text",text)
          val a = text.replace("\n","%20%0A%20")
          val information = retrofit(a)
        textView.text = information
      }
    }
    return root
  }

  fun retrofit(text:String):String{
    var information=""
    try {
    val t = thread {

        val client = OkHttpClient()
        val request = Request.Builder().url("http://fanyi.youdao.com/translate?&doctype=json&type=AUTO&i=${text}").build()
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        val jsonArray = JSONObject(responseBody)
        val translateResult = jsonArray.getJSONArray("translateResult")
        for (i in 0 until translateResult.length()){
        val a = translateResult.getJSONArray(i)
            information+="\n"
        for (j in 0 until a.length()){
        val b = a.getJSONObject(j)
        val tgt = b.getString("tgt")
            Log.d(j.toString(),tgt)
        information+=tgt
      }
        }
    }
      t.join()
        return information
    }catch (e:Exception){
      information="请检查网络连接"
      return information
    }

  }
}


