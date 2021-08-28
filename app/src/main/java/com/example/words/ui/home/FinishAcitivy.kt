package com.example.words.ui.home

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.example.words.R
import java.text.SimpleDateFormat
import java.util.*

class FinishAcitivy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_acitivy)

        val total = intent.getIntExtra("total",0)
        val str = intent.getStringExtra("str")
        val storage = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
        storage.putInt("learned_words_number",total)

        val day = SimpleDateFormat("yyyy-MM-dd")
        val outputDay =day.format(Date())
        val reDay = getSharedPreferences("data", Context.MODE_PRIVATE).getString("date","")
        if (outputDay==reDay){
            val todayLearn = getSharedPreferences("data", Context.MODE_PRIVATE).getInt("todayLearn",0)
            storage.putInt("todayLearn",todayLearn+10)
        }else{
            storage.putString("date",outputDay)
            storage.putInt("todayLearn",10)
        }
        storage.apply()

        MaterialDialog.Builder(this)
            .title("学习单词")
            .content("您已完成该阶段背诵单词任务")
            .positiveText("确认")
            .onPositive{ materialDialog: MaterialDialog, dialogAction: DialogAction ->
                Toast.makeText(this,"111",Toast.LENGTH_SHORT).show()
            }.show()

    }
}