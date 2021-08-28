package com.example.words

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.lang.Exception

class SimpleWorker(context: Context,params:WorkerParameters):Worker(context,params){
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val sonContext = context
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        try {
            notification(manager,sonContext)
            return Result.success()
        }catch (e:Exception){
            return Result.failure()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun notification(manager: NotificationManager,context: Context){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.BASE){
            val channel = NotificationChannel("normal","每日提醒",NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
            val notification = NotificationCompat.Builder(context,"normal")
                .setContentTitle("ReciteWord")
                .setContentText("每日提醒：请开始你的背单词之旅")
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources,R.drawable.logo))
                .build()
            manager.notify(1,notification)
        }
    }

}