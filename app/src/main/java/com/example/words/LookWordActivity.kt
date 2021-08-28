package com.example.words

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.words.R
import com.example.words.retrofit.ResponseWord
import com.example.words.ui.home.Phrase
import com.example.words.ui.home.Relword
import com.example.words.ui.home.Sentence

class LookWordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_look_word)

        val word = intent.getSerializableExtra("word") as ResponseWord.Word
        updateNegative(word)
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
            four.visibility= View.VISIBLE
            phraseDesc.setText(data.content.phrase.desc)
            val phraseList = ArrayList<Phrase>()
            for (i in 0 until data.content.phrase.phrases.size){
                phraseList.add(
                    Phrase(data.content.phrase.phrases[i].pCn,
                    data.content.phrase.phrases[i].pContent)
                )
            }
            val layoutManager = LinearLayoutManager(this)
            val recyclerViewPhrase = findViewById<RecyclerView>(R.id.recyclerViewPhrase)

//            if (data.content.phrase.phrases.size>2){
//                recyclerViewPhrase.layoutParams.height=190
//            }else{
//                recyclerViewPhrase.layoutParams.height= ActionBar.LayoutParams.WRAP_CONTENT
//            }

            recyclerViewPhrase.layoutManager = layoutManager
            val adapter = PhraseAdapter(phraseList)
            recyclerViewPhrase.adapter = adapter
        }else{
            four.visibility= View.GONE
        }

        val relwordDesc =findViewById<TextView>(R.id.relWordDesc)
        val five = findViewById<LinearLayout>(R.id.five)
        if (data.content.relWord !=null){
            five.visibility= View.VISIBLE
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

//            if (data.content.relWord.rels.size>1){
//                recyclerViewRelword.layoutParams.height=110
//            }else{
//                recyclerViewRelword.layoutParams.height= ActionBar.LayoutParams.WRAP_CONTENT
//            }

            recyclerViewRelword.layoutManager = layoutManager
            val adapter = RelwordAdapter(relwordList)
            recyclerViewRelword.adapter = adapter
        }else{
            five.visibility= View.GONE
        }

        val sentenceDesc = findViewById<TextView>(R.id.sentenceDesc)
        val six = findViewById<LinearLayout>(R.id.six)
        if (data.content.sentence !=null){
            five.visibility= View.VISIBLE
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

//            if (data.content.sentence.sentences.size>1){
//                recyclerViewSentence.layoutParams.height=250
//            }else{
//                recyclerViewSentence.layoutParams.height= ActionBar.LayoutParams.WRAP_CONTENT
//            }

            recyclerViewSentence.layoutManager=layoutManager
            val adapter = SentenseAdapter(sentenseList)
            recyclerViewSentence.adapter=adapter
        }else{
            six.visibility= View.GONE
        }


        val seven = findViewById<LinearLayout>(R.id.seven)
        if (data.content.remMethod !=null){
            seven.visibility= View.VISIBLE

            val remMethodDesc = findViewById<TextView>(R.id.remMethodDesc)
            remMethodDesc.setText(data.content.remMethod.desc)

            val remMethodVal = findViewById<TextView>(R.id.remMethodVal)
            remMethodVal.setText(data.content.remMethod.reVal)
        }else{
            seven.visibility= View.GONE
        }
    }
}