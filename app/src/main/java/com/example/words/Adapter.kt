package com.example.words

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.example.words.retrofit.AppService
import com.example.words.retrofit.ResponseMsg
import com.example.words.retrofit.ResponseOneBook
import com.example.words.retrofit.ResponseWord
import com.example.words.ui.home.Phrase
import com.example.words.ui.home.Relword
import com.example.words.ui.home.Sentence
import com.example.words.ui.notifications.ChooseBook
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ObjectOutputStream

class PhraseAdapter(val phraseList: List<Phrase>):
        RecyclerView.Adapter<PhraseAdapter.ViewHolder>(){

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val pCn = view.findViewById<TextView>(R.id.pCn)
        val pContent = view.findViewById<TextView>(R.id.pContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.phrase_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = phraseList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val phrase = phraseList[position]
        holder.pCn.setText(phrase.pCn)
        holder.pContent.setText(phrase.pContent)
    }
}

class RelwordAdapter(val relwordList: List<Relword>):
        RecyclerView.Adapter<RelwordAdapter.ViewHolder>(){

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val relwordPos = view.findViewById<TextView>(R.id.relwordPos)
        val relwordHwd = view.findViewById<TextView>(R.id.relwordHwd)
        val relwordTran = view.findViewById<TextView>(R.id.relwordTran)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.relword_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int =relwordList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val relword = relwordList[position]
        holder.relwordPos.setText(relword.relwordPos)
        holder.relwordHwd.setText(relword.relwordHwd)
        holder.relwordTran.setText(relword.relwordTran)
    }
}

class SentenseAdapter(val sentenceList:List<Sentence>):
        RecyclerView.Adapter<SentenseAdapter.ViewHolder>(){

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val sCn = view.findViewById<TextView>(R.id.sCn)
        val sContent = view.findViewById<TextView>(R.id.sContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sentence_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int =sentenceList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sentence = sentenceList[position]
        holder.sCn.setText(sentence.sCn)
        holder.sContent.setText(sentence.sContent)
    }
}

class BookAdapter(val bookList:List<ChooseBook>):
    RecyclerView.Adapter<BookAdapter.ViewHolder>(){
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val bookName : TextView = view.findViewById(R.id.bookName)
        val item : SwitchCompat = view.findViewById(R.id.item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item,parent,false)
        val viewHolder = ViewHolder(view)
        viewHolder.item.setOnClickListener {
            val position = viewHolder.adapterPosition
            val book = bookList[position]

            val dialog = MaterialDialog.Builder(parent.context)
                .title("Progress")
                .content("Please Wait...")
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();

            val retrofit = Retrofit.Builder().baseUrl("http://81.68.100.77:7777/")
                .addConverterFactory(GsonConverterFactory.create()).build()
            val appService = retrofit.create(AppService::class.java)
            val getToken = parent.context.getSharedPreferences("data", Context.MODE_PRIVATE)
            val token = getToken.getString("token", "")
            if (token != null){
                appService.bookWord(token,book.bookName).enqueue(object : Callback<ResponseWord> {
                    override fun onFailure(call: Call<ResponseWord>, t: Throwable) {
                        Toast.makeText(parent.context,"获取失败", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                        call: Call<ResponseWord>,
                        response: Response<ResponseWord>
                    ) {
                        val body = response.body()
                        if (body !=null){
                            val bookName = getToken.edit().putString("bookName",book.bookName)
                            val retrofit = Retrofit.Builder().baseUrl("http://81.68.100.77:7777/")
                                .addConverterFactory(GsonConverterFactory.create()).build()
                            val appService = retrofit.create(AppService::class.java)
                            appService.postBook(token,book.bookName).enqueue(object :
                                Callback<ResponseMsg> {
                                override fun onFailure(call: Call<ResponseMsg>, t: Throwable) {
                                    dialog.dismiss()
                                    Toast.makeText(parent.context,"获取失败，请重新获取", Toast.LENGTH_SHORT).show()
                                }

                                override fun onResponse(call: Call<ResponseMsg>, response: Response<ResponseMsg>) {
                                    val body1 = response.body()
                                    if (body1!=null){
                                        val status = body1.status
                                        val message = body1.message
                                        if (status ==1){
                                            bookName.apply()
                                            val output = parent.context.openFileOutput(book.bookName, Context.MODE_PRIVATE)
                                            val writer = ObjectOutputStream(output)
                                            writer.writeObject(body)
                                            writer.close()
                                            val storage = parent.context.getSharedPreferences("data", Context.MODE_PRIVATE).edit()
                                            storage.putInt("totalLearn",0)
                                            storage.apply()
                                            Toast.makeText(parent.context,"获取成功", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(parent.context, MenuActivity::class.java)
                                            parent.context.startActivity(intent)
                                        }else if(message.equals("BookExist")){
                                            appService.oneBookWord(token,book.bookName).enqueue(object :Callback<ResponseOneBook>{
                                                override fun onFailure(
                                                    call: Call<ResponseOneBook>,
                                                    t: Throwable
                                                ) {
                                                    Toast.makeText(parent.context,"获取失败，请重新获取", Toast.LENGTH_SHORT).show()
                                                    val intent = Intent(parent.context, MenuActivity::class.java)
                                                    parent.context.startActivity(intent)

                                                }

                                                override fun onResponse(
                                                    call: Call<ResponseOneBook>,
                                                    response: Response<ResponseOneBook>
                                                ) {
                                                    val oneBookBody = response.body()
                                                    if (oneBookBody != null) {
                                                        val storage = parent.context.getSharedPreferences("data", Context.MODE_PRIVATE).edit()
                                                        if (oneBookBody.data==null){
                                                            bookName.apply()
                                                            storage.putInt("totalLearn",0)
                                                            Toast.makeText(parent.context,"获取成功", Toast.LENGTH_SHORT).show()
                                                            val intent = Intent(parent.context, MenuActivity::class.java)
                                                            parent.context.startActivity(intent)

                                                        }else{
                                                            bookName.apply()
                                                            val n = oneBookBody.data
                                                            storage.putInt("totalLearn",n)
                                                            Toast.makeText(parent.context,"获取成功", Toast.LENGTH_SHORT).show()
                                                            val intent = Intent(parent.context, MenuActivity::class.java)
                                                            parent.context.startActivity(intent)
                                                        }
                                                        storage.apply()
                                                    }else{
                                                        Toast.makeText(parent.context,"获取失败", Toast.LENGTH_SHORT).show()
                                                        val intent = Intent(parent.context, MenuActivity::class.java)
                                                        parent.context.startActivity(intent)
                                                    }

                                                }

                                            })
                                        }else{
                                            dialog.dismiss()
                                            Toast.makeText(parent.context,"获取失败，请重新获取", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                }

                            })



                        }
                    }

                })
            }        }
        return viewHolder
    }

    override fun getItemCount() = bookList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = bookList[position]
        holder.item.isChecked = false
        holder.bookName.text = book.bookName
    }

    private fun addBook(bookName:String,token:String):Int{
        var i=-1
        val retrofit = Retrofit.Builder().baseUrl("http://81.68.100.77:7777/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val appService = retrofit.create(AppService::class.java)
        appService.postBook(token,bookName).enqueue(object : Callback<ResponseMsg> {
            override fun onFailure(call: Call<ResponseMsg>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(call: Call<ResponseMsg>, response: Response<ResponseMsg>) {
                val body = response.body()
                if (body!=null){
                }

            }

        })
        return i
    }
}

class Search(val word:String,val whole:ResponseWord.Word)

class SearchAdapter(val searchList:List<Search>):
        RecyclerView.Adapter<SearchAdapter.ViewHolder>(){
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val searchText = view.findViewById<TextView>(R.id.searchText)
        val searchLayout = view.findViewById<LinearLayout>(R.id.searchLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item,parent,false)
        val viewHolder = ViewHolder(view)
        viewHolder.searchLayout.setOnClickListener {
            val position = viewHolder.adapterPosition
            val search = searchList[position]
            val intent = Intent(parent.context, LookWordActivity::class.java)
            intent.putExtra("word",search.whole)
            parent.context.startActivity(intent)
        }
        return viewHolder
    }

    override fun getItemCount(): Int=searchList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val search = searchList[position]
        holder.searchText.text = search.word
    }
}