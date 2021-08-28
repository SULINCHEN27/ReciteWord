package com.example.words.ui.notifications

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.afollestad.materialdialogs.MaterialDialog
import com.example.words.R
import com.example.words.SimpleWorker
import com.example.words.retrofit.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ObjectOutputStream
import java.lang.Exception
import java.util.concurrent.TimeUnit
import javax.security.auth.callback.Callback

class NotificationsFragment : Fragment() {

  private lateinit var notificationsViewModel: NotificationsViewModel

  @RequiresApi(Build.VERSION_CODES.N)
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_notifications, container, false)
    val chooseBookBtn = root.findViewById<RelativeLayout>(R.id.chooseBook)
    val limitBtn = root.findViewById<RelativeLayout>(R.id.limits)
    val remindBtn = root.findViewById<RelativeLayout>(R.id.remind)
    val recoverBtn = root.findViewById<RelativeLayout>(R.id.recover)
    chooseBookBtn.setOnClickListener {
      val intent = Intent(activity,ChooseBookActivity::class.java)
      startActivity(intent)
    }

    recoverBtn.setOnClickListener {
      val dialog = this.context?.let { it1 ->
        MaterialDialog.Builder(it1)
                .title("Progress")
                .content("Please Wait...")
                .progress(true, 0)
                .progressIndeterminateStyle(true)

      }?.show()
      val retrofit = retrofit2.Retrofit.Builder().baseUrl("http://81.68.100.77:7777/")
              .addConverterFactory(GsonConverterFactory.create()).build()
      val appService = retrofit.create(AppService::class.java)
      val getToken = activity?.getSharedPreferences("data", Context.MODE_PRIVATE)
      val token = getToken?.getString("token", "")
      if (token != null) {
        appService.getRecentBook(token).enqueue(object : retrofit2.Callback<ResponseRecentBook> {
          override fun onFailure(call: Call<ResponseRecentBook>, t: Throwable) {
            dialog?.dismiss()
            Toast.makeText(context,"恢复数据失败，请重新恢复",Toast.LENGTH_SHORT).show()
          }

          override fun onResponse(call: Call<ResponseRecentBook>, response: Response<ResponseRecentBook>) {
            val body = response.body()
            if (body != null && body.status == 1) {
              appService.getUserLearn(token,body.data,0).enqueue(object : retrofit2.Callback<ResponseUserLearn> {
                override fun onFailure(call: Call<ResponseUserLearn>, t: Throwable) {
                  dialog?.dismiss()
                  Toast.makeText(context,"恢复数据失败，请重新恢复",Toast.LENGTH_SHORT).show()

                }

                override fun onResponse(call: Call<ResponseUserLearn>, response: Response<ResponseUserLearn>) {
                  val a = response.body()
                  if (a != null) {
                    val storage = activity?.getSharedPreferences("data", Context.MODE_PRIVATE)?.edit()
                    val todayLearn = a.data.record.get("${body.data}")
                    if (storage != null&&todayLearn !=null) {
                      appService.bookWord(token,body.data).enqueue(object : retrofit2.Callback<ResponseWord>{
                        override fun onFailure(call: Call<ResponseWord>, t: Throwable) {
                          dialog?.dismiss()
                          Toast.makeText(context,"恢复数据失败，请重新恢复",Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(call: Call<ResponseWord>, response: Response<ResponseWord>) {

                          try {
                            val words = response.body()
                            dialog?.dismiss()
                            val output = activity?.openFileOutput(body.data, Context.MODE_PRIVATE)
                            val writer = ObjectOutputStream(output)
                            writer.writeObject(words)
                            writer.close()
                            storage.putInt("todayLearn",todayLearn)
                            storage.putInt("learned_words_number",a.data.sum)
                            storage.putString("bookName",body.data)
                            storage.apply()
                            Toast.makeText(context,"成功恢复本地数据",Toast.LENGTH_SHORT).show()
                          }catch (e:Exception){
                            dialog?.dismiss()
                            Toast.makeText(context,"恢复数据失败，请重新恢复",Toast.LENGTH_SHORT).show()                          }

                        }

                      })


                    }else{
                      dialog?.dismiss()
                      Toast.makeText(context,"恢复数据失败，请重新恢复",Toast.LENGTH_SHORT).show()

                    }
                  }else{
                    dialog?.dismiss()
                    Toast.makeText(context,"恢复数据失败，请重新恢复",Toast.LENGTH_SHORT).show()

                  }
                }

              })
            }else{
              dialog?.dismiss()
              Toast.makeText(context,"恢复数据失败，请重新恢复",Toast.LENGTH_SHORT).show()
            }
          }
        }
        )

      }else{
        dialog?.dismiss()
        Toast.makeText(context,"恢复数据失败，请重新恢复",Toast.LENGTH_SHORT).show()
      }
    }

    remindBtn.setOnClickListener{
      val a = manager.areNotificationsEnabled()
      if (!a){
        Toast.makeText(context,"请设置通知栏权限",Toast.LENGTH_SHORT).show()
      }else{
        val request = PeriodicWorkRequest.Builder(SimpleWorker::class.java,1, TimeUnit.DAYS).build()
        WorkManager.getInstance(requireContext()).enqueue(request)
        Toast.makeText(context,"已设置每日学习提醒",Toast.LENGTH_SHORT).show()
      }
    }

    limitBtn.setOnClickListener{
      val a = manager.areNotificationsEnabled()

      try {
        if (!a) {
          val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
          val uri = Uri.fromParts("package", context?.packageName, null);
          intent.setData(uri)
          startActivity(intent)
        }else{
          Toast.makeText(context,"已打开权限",Toast.LENGTH_SHORT).show()
        }
      }catch (e:Exception){
        Toast.makeText(context,"无法获取权限，请手动设置",Toast.LENGTH_SHORT).show()
      }

    }


    return root
  }
}


