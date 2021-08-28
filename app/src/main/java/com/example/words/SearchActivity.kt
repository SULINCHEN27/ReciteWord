package com.example.words

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.words.R
import com.example.words.databinding.ActivitySearchBinding
import com.example.words.retrofit.ResponseWord
import java.io.ObjectInputStream
import java.util.Locale.filter

class SearchActivity : AppCompatActivity() {
    private val searchList = ArrayList<Search>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bookName = getSharedPreferences("data", Context.MODE_PRIVATE).getString("bookName","")
        if (bookName !=""){
            val input = openFileInput(bookName)
            val reader = ObjectInputStream(input)
            val body: ResponseWord = reader.readObject() as ResponseWord
            initWord(body)
//        searchList.add(Search("accept",0))
//        searchList.add(Search("accident",1))
//        searchList.add(Search("bowl",2))
//        searchList.add(Search("boy",3))
//        searchList.add(Search("toy",4))
            binding.searchRecyclerview.layoutManager=LinearLayoutManager(this)
            var adapter = SearchAdapter(searchList)
            binding.searchRecyclerview.adapter = adapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                Toast.makeText(this@SearchActivity,p0,Toast.LENGTH_SHORT).show()
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                val list = ArrayList<Search>()
                if (p0!=null){
                    searchList.filter {it.word.contains(p0)}.forEach{
                        list.add(it)
                    }
                }
                adapter = SearchAdapter(list)
                binding.searchRecyclerview.adapter=adapter


                return false
            }
        })
        }else{
            Toast.makeText(this,"请选择单词书",Toast.LENGTH_SHORT).show()
        }

    }

    private fun initWord(body:ResponseWord){
        for (i in 0 until body.data.size){
            searchList.add(Search(body.data[i].headWord,body.data[i]))
        }
    }
}