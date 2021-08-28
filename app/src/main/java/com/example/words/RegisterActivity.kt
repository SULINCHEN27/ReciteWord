package com.example.words

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.example.words.retrofit.AppService
import com.example.words.databinding.ActivityRegisterBinding
import com.example.words.retrofit.ResponseCode
import com.example.words.retrofit.Retrofit
import com.weiwangcn.betterspinner.library.BetterSpinner
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var learn:Int = -1;

        val  mItems=resources.getStringArray(R.array.spinnerLearn)

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, mItems);
        val textView : BetterSpinner = binding.spinnerLearn
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
       binding.spinnerLearn!!.setAdapter(adapter)

        //学习层次

        binding.spinnerLearn.onItemClickListener=object  : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                learn = position
                Log.d("learn",learn.toString())
            }

        }


        binding.identifyCodeBtn.setOnClickListener {
            val mailbox = binding.mailbox.text.toString()
            MaterialDialog.Builder(this)
                .title("发送验证码")
                .content("验证码将发送至${mailbox}")
                .positiveText("确认")
                .onPositive { _: MaterialDialog, dialogAction: DialogAction ->
                    if (!Verify().verifyMailbox(mailbox)){
                        materialDialog("提示","邮箱格式不正确","确认")
                    }else{
                        val appService = Retrofit().createRetrofit("http://81.68.100.77:7777/")
                        appService.postCode(mailbox).enqueue(object : Callback<ResponseCode>{
                            override fun onFailure(call: Call<ResponseCode>, t: Throwable) {
                                t.printStackTrace()
                            }

                            override fun onResponse(call: Call<ResponseCode>, response: Response<ResponseCode>) {
                                val body = response.body()
                                Log.d("body",body.toString())
                                if (body != null) {
                                    if (body.status==1){
                                        Toast.makeText(this@RegisterActivity,"验证码发送成功",Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(this@RegisterActivity,body.message,Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    Toast.makeText(this@RegisterActivity,"验证码发送失败，请重新发一次",Toast.LENGTH_SHORT).show()
                                }
                            }

                        })
                    }

                }
                .negativeText("取消")
                .positiveColor(resources.getColor(R.color.blue))
                .negativeColor(resources.getColor(R.color.blue))
                .show()
        }

        binding.registerBtn.setOnClickListener {
            val mailbox = binding.mailbox.text.toString()
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            val identifyCode = binding.identifyCode.text.toString()
            val education = learn
            Log.d("education",education.toString())
            if (!Verify().verifyMailbox(mailbox)){
                materialDialog("提示","邮箱格式不正确","确认")
            }else if (!Verify().verfiyUsername(username)){
                materialDialog("提示","用户名格式不正确","确认")
            }else if(!Verify().verfiyPassword(password)){
                materialDialog("提示","密码格式不正确","确认")
            }else if(education==-1){
                materialDialog("提示","请选择学习层次","确认")
            }else {
                try {
                    val code = identifyCode.toInt()
                    val dialog = MaterialDialog.Builder(this)
                        .title("Progress")
                        .content("Please Wait...")
                        .progress(true, 0)
                        .progressIndeterminateStyle(true)
                        .show();
                    val appService = Retrofit().createRetrofit("http://81.68.100.77:7777/")

                    appService.postRegister(mailbox,username,password,education,code)
                        .enqueue(object : Callback<ResponseCode> {
                            override fun onFailure(call: Call<ResponseCode>, t: Throwable) {
                                dialog.dismiss();
                                t.printStackTrace()
                            }
                            override fun onResponse(call: Call<ResponseCode>, response: Response<ResponseCode>) {
                                val body = response.body()
                                if (body != null) {
                                    if (body.status==1){
                                        dialog.dismiss();
                                        MaterialDialog.Builder(this@RegisterActivity)
                                            .title("注册")
                                            .content("注册成功！点击确认跳转至登录")
                                            .positiveText("确认")
                                            .positiveColor(resources.getColor(R.color.blue))
                                            .onPositive(MaterialDialog.SingleButtonCallback(){ materialDialog: MaterialDialog, dialogAction: DialogAction ->
                                                startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
                                            })
                                            .show()

                                        finish()
                                    }else{
                                        dialog.dismiss();
                                        val materialDialog= materialDialog("注册","注册失败！${body.message}","确认")

                                    }
                                }
                            }

                        })
//                runBlocking { coroutineScope{ launch {
//
//                    val response = appService.postRegister(mailbox,username,password,education,code).await()
//                    if (response.status==1){
//                        dialog.dismiss();
//                      val materialDialog= materialDialog("注册","注册成功！点击确认跳转至登录","确认")
//                    materialDialog.builder.onPositive { _: MaterialDialog, dialogAction: DialogAction ->
//                        runBlocking { coroutineScope{ launch {
//                            startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
//                        } } }
//                    }
//                        finish()
//                    }else{
//                        dialog.dismiss();
//                        val materialDialog= materialDialog("注册","注册失败！${response.message}","确认")
//
//                    }
//                } } }
                }catch (e:Exception){
            materialDialog("提示","验证码只包含数字","确认")
        }
            }
        }

    }


    fun materialDialog(title:String,content:String,positiveText:String):MaterialDialog{
        return MaterialDialog.Builder(this)
            .title(title)
            .content(content)
            .positiveText(positiveText)
            .positiveColor(resources.getColor(R.color.blue))
            .show()
    }

    suspend fun getCode(appService: AppService, identifyCode:String)= coroutineScope{
        launch {
            try {
                val response = appService.postCode(identifyCode).await()
                if (response.status==1){
                    Toast.makeText(this@RegisterActivity,"验证码发送成功",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@RegisterActivity,response.message,Toast.LENGTH_SHORT).show()
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

    }
}