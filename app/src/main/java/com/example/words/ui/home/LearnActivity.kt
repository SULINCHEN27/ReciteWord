package com.example.words.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.ActionBar
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.example.words.*
import com.example.words.databinding.ActivityLearnBinding
import com.example.words.retrofit.AppService
import com.example.words.retrofit.ResponseMsg
import com.example.words.retrofit.ResponseWord
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ObjectInputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LearnActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLearnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.negative.rotationY=-90f
        val token = getSharedPreferences("data", Context.MODE_PRIVATE).getString("token","")
        val totalLearn = getSharedPreferences("data", Context.MODE_PRIVATE).getInt("totalLearn",0)
        val bookName = getSharedPreferences("data", Context.MODE_PRIVATE).getString("bookName","")

        val input = openFileInput(bookName)
        val reader = ObjectInputStream(input)
        val body: ResponseWord = reader.readObject() as ResponseWord
        val noList = ArrayList<Int>()
        //初始化
        var num=1
        var count =0

        updatePositive(body.data[totalLearn+num-1])

        binding.remember.setOnClickListener {
            if (num==10){
                if (noList.size==0){

                    var str = "${totalLearn+1}"
                    for (i in 2 until 11){
                        str +=",${totalLearn+i}"
                    }
                    val total =totalLearn+10

                    MaterialDialog.Builder(this)
                        .title("学习单词")
                        .content("您已完成该阶段背诵单词任务")
                        .positiveText("确认")
                        .onPositive{ materialDialog: MaterialDialog, dialogAction: DialogAction ->
                            val retrofit = Retrofit.Builder().baseUrl("http://81.68.100.77:7777/")
                                    .addConverterFactory(GsonConverterFactory.create()).build()
                            val appService = retrofit.create(AppService::class.java)
                            if (token != null && bookName !=null){
                                appService.postWord(token,bookName,10).enqueue(object : Callback<ResponseMsg> {
                                    override fun onFailure(call: Call<ResponseMsg>, t: Throwable) {
                                        Toast.makeText(this@LearnActivity,"加载数据失败1",Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onResponse(call: Call<ResponseMsg>, response: Response<ResponseMsg>) {
                                        val body = response.body()
                                        Log.d("body",body.toString())
                                        if (body != null && body.status==1) {
                                                storage(total)
                                                val intent = Intent(this@LearnActivity,MenuActivity::class.java)
                                                startActivity(intent)
                                        }else{
                                            Toast.makeText(this@LearnActivity,"加载数据失败2",Toast.LENGTH_SHORT).show()
                                        }

                                    }

                                })

                        }else{
                                Toast.makeText(this@LearnActivity,"加载数据失败",Toast.LENGTH_SHORT).show()
                            }

                        }.show()

                }else{
                    updatePositive(body.data[noList[0]])
                    noList.remove(noList[0])
                }

            }else{
                num++
                updatePositive(body.data[totalLearn+num-1])
            }

        }

        binding.noremember.setOnClickListener {

            binding.remember.visibility= View.GONE
            binding.noremember.visibility= View.GONE
            binding.next.visibility = View.VISIBLE
            if (num==10){
                if (count==0){
                    noList.add(totalLearn+num-1)
                    flipAnimation()
                    updateNegative(body.data[totalLearn+num-1])
                    count =1
                }else{
                    noList.add(noList[0])
                    flipAnimation()
                    updateNegative(body.data[noList[0]])
                }
            }else{
                noList.add(totalLearn+num-1)
                flipAnimation()
                updateNegative(body.data[totalLearn+num-1])
            }

        }

        binding.next.setOnClickListener {
            binding.remember.visibility= View.VISIBLE
            binding.noremember.visibility= View.VISIBLE
            binding.next.visibility = View.GONE
            if (num==10){
                if (noList.size==0){
                    var str = "${totalLearn+1}"
                    for (i in 2 until 11){
                        str +=",${totalLearn+i}"
                    }
                    val total =totalLearn+num
                    storage(total)

                    MaterialDialog.Builder(this)
                        .title("学习单词")
                        .content("您已完成该阶段背诵单词任务")
                        .positiveText("确认")
                        .onPositive{ materialDialog: MaterialDialog, dialogAction: DialogAction ->
                            val intent = Intent(this,MenuActivity::class.java)
                            startActivity(intent)
                        }.show()
                }else{
                    flipAnimation()
                    updatePositive(body.data[noList[0]])
                }
            }else{
                num++

                flipAnimation()
                updatePositive(body.data[totalLearn+num-1])
            }

        }

    }

    private fun flipAnimation():Boolean{

        val positive = findViewById<LinearLayout>(R.id.positive)
        val negative = findViewById<LinearLayout>(R.id.negative)

        var visibletext=positive
        var invisibletext = negative

        if (positive.visibility== View.GONE){
            visibletext=negative
            invisibletext = positive
        }

        val visToInvis : ObjectAnimator = ObjectAnimator.ofFloat(visibletext,"rotationY",0f,90f)
        visToInvis.duration=10
        visToInvis.interpolator= AccelerateInterpolator()
        val invisToVis : ObjectAnimator = ObjectAnimator.ofFloat(invisibletext,"rotationY",-90f,0f)
        invisToVis.duration=10
        invisToVis.interpolator= DecelerateInterpolator()
        visToInvis.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                visibletext.visibility=View.GONE

                invisibletext.visibility=View.VISIBLE
                invisToVis.start()
            }
        })

        visToInvis.start()

        return true
    }

    private fun updatePositive(data: ResponseWord.Word){
        val word = findViewById<TextView>(R.id.word1)
        val soundmark = findViewById<TextView>(R.id.soundmark1)
        word.setText(data.headWord)
        soundmark.setText("["+data.content.phone+"]")
    }

    private fun updateNegative(data: ResponseWord.Word){

        val headWord2 = findViewById<TextView>(R.id.headWord2)
        headWord2.setText(data.headWord)
        val phone2 = findViewById<TextView>(R.id.phone2)
        phone2.setText("["+data.content.phone+"]")
        val pos = findViewById<TextView>(R.id.pos)
        pos.setText(data.content.trans[0].pos)
        val desCn = findViewById<TextView>(R.id.descCn)
        desCn.setText(data.content.trans[0].tranCn)
        val tranOther = findViewById<TextView>(R.id.tranOther)
        tranOther.setText(data.content.trans[0].tranOther)

        //短语
        val phraseDesc = findViewById<TextView>(R.id.phraseDesc)
        val four = findViewById<LinearLayout>(R.id.four)
        if (data.content.phrase!=null){
            four.visibility=View.VISIBLE
            phraseDesc.setText(data.content.phrase.desc)
            val phraseList = ArrayList<Phrase>()
            for (i in 0 until data.content.phrase.phrases.size){
                phraseList.add(Phrase(data.content.phrase.phrases[i].pCn,
                        data.content.phrase.phrases[i].pContent))
            }
            val layoutManager = LinearLayoutManager(this)
            val recyclerViewPhrase = findViewById<RecyclerView>(R.id.recyclerViewPhrase)

            if (data.content.phrase.phrases.size>2){
                recyclerViewPhrase.layoutParams.height=190
            }else{
                recyclerViewPhrase.layoutParams.height=ActionBar.LayoutParams.WRAP_CONTENT
            }

            recyclerViewPhrase.layoutManager = layoutManager
            val adapter = PhraseAdapter(phraseList)
            recyclerViewPhrase.adapter = adapter
        }else{
            four.visibility=View.GONE
        }

        val relwordDesc =findViewById<TextView>(R.id.relWordDesc)
        val five = findViewById<LinearLayout>(R.id.five)
        if (data.content.relWord !=null){
            five.visibility=View.VISIBLE
            relwordDesc.setText(data.content.relWord.desc)

            val relwordList = ArrayList<Relword>()
            for (i in 0 until data.content.relWord.rels.size){
                relwordList.add(
                    Relword(data.content.relWord.rels[i].pos,
                        data.content.relWord.rels[i].words[0].hwd,
                        data.content.relWord.rels[i].words[0].tran)
                )
            }
            val layoutManager = LinearLayoutManager(this)
            val recyclerViewRelword = findViewById<RecyclerView>(R.id.recyclerViewRelword)

            if (data.content.relWord.rels.size>1){
                recyclerViewRelword.layoutParams.height=110
            }else{
                recyclerViewRelword.layoutParams.height=ActionBar.LayoutParams.WRAP_CONTENT
            }

            recyclerViewRelword.layoutManager = layoutManager
            val adapter = RelwordAdapter(relwordList)
            recyclerViewRelword.adapter = adapter
        }else{
            five.visibility=View.GONE
        }

        val sentenceDesc = findViewById<TextView>(R.id.sentenceDesc)
        val six = findViewById<LinearLayout>(R.id.six)
        if (data.content.sentence !=null){
            five.visibility=View.VISIBLE
            sentenceDesc.setText(data.content.sentence.desc)

            val sentenseList = ArrayList<Sentence>()
            for (i in 0 until  data.content.sentence.sentences.size){
                sentenseList.add(
                    Sentence(data.content.sentence.sentences[i].sCn,
                        data.content.sentence.sentences[i].sContent)
                )
            }

            val layoutManager = LinearLayoutManager(this)
            val recyclerViewSentence = findViewById<RecyclerView>(R.id.recyclerViewSentense)

            if (data.content.sentence.sentences.size>1){
                recyclerViewSentence.layoutParams.height=250
            }else{
                recyclerViewSentence.layoutParams.height=ActionBar.LayoutParams.WRAP_CONTENT
            }

            recyclerViewSentence.layoutManager=layoutManager
            val adapter = SentenseAdapter(sentenseList)
            recyclerViewSentence.adapter=adapter
        }else{
            six.visibility=View.GONE
        }


        val seven = findViewById<LinearLayout>(R.id.seven)
        if (data.content.remMethod !=null){
            seven.visibility=View.VISIBLE

            val remMethodDesc = findViewById<TextView>(R.id.remMethodDesc)
            remMethodDesc.setText(data.content.remMethod.desc)

            val remMethodVal = findViewById<TextView>(R.id.remMethodVal)
            remMethodVal.setText(data.content.remMethod.reVal)
        }else{
            seven.visibility=View.GONE
        }
    }

    private fun storage(total:Int){
        val storage = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
        storage.putInt("totalLearn",total)

        val learned_words_number = getSharedPreferences("data", Context.MODE_PRIVATE).getInt("learned_words_number",0)+10
        storage.putInt("learned_words_number",learned_words_number)
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
    }
}