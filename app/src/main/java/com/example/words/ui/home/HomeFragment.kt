package com.example.words.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.example.words.R
import com.example.words.SearchActivity
import com.example.words.SearchAdapter
import com.example.words.retrofit.ResponseWord
import com.example.words.ui.notifications.ChooseBookActivity
import java.io.ObjectInputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

  private lateinit var homeViewModel: HomeViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_home, container, false)
    val learnBtn:Button = root.findViewById(R.id.learnBtn)
    val searchBtn:Button = root.findViewById(R.id.search)
    val todayLearn:TextView = root.findViewById(R.id.todayLearnText)
    val refresh:ImageView = root.findViewById(R.id.refresh)
    val english:TextView = root.findViewById(R.id.english)
    val chinese:TextView = root.findViewById(R.id.chinese)
    activity?.getSharedPreferences("data", Context.MODE_PRIVATE)?.getInt("totalLearn",0)?.let {
      todayLearn.setText(it.toString())
    }

    val totalLearn:TextView = root.findViewById(R.id.totalLearnText)
    activity?.getSharedPreferences("data", Context.MODE_PRIVATE)?.getInt("learned_words_number",0)?.let {
      totalLearn.setText(it.toString())
    }

    val month = SimpleDateFormat("MMM", Locale.ENGLISH);
    val day = SimpleDateFormat("dd", Locale.ENGLISH)
    val outputMonth =month.format(Date())
    val outputDay = day.format(Date())
    val dateBtn = root.findViewById<TextView>(R.id.date)
    val str = "<b>${outputMonth}</b> <br>&nbsp;${outputDay}"
    dateBtn.setText(Html.fromHtml(str))
    val bookName = activity?.getSharedPreferences("data", Context.MODE_PRIVATE)
      ?.getString("bookName","")

    var re :ResponseWord? = null
    learnBtn.setOnClickListener {
      val dialog = this.context?.let { it1 ->
        MaterialDialog.Builder(it1)
                .title("Progress")
                .content("Please Wait...")
                .progress(true, 0)
                .progressIndeterminateStyle(true)

      }?.show()
      if (bookName==""){
        dialog?.dismiss()
        Toast.makeText(this.context,"请选择单词书",Toast.LENGTH_SHORT).show()
      }else{
        dialog?.dismiss()
        val intent = Intent(activity, LearnActivity::class.java)
        startActivity(intent)
      }

    }

    searchBtn.setOnClickListener{
      val dialog = this.context?.let { it1 ->
        MaterialDialog.Builder(it1)
                .title("Progress")
                .content("Please Wait...")
                .progress(true, 0)
                .progressIndeterminateStyle(true)

      }?.show()
      if (bookName==""){
        dialog?.dismiss()
        Toast.makeText(this.context,"请选择单词书",Toast.LENGTH_SHORT).show()
      }else{
        dialog?.dismiss()
        val intent = Intent(activity, SearchActivity::class.java)
        startActivity(intent)
      }
    }

    refresh.setOnClickListener {
      try {
        if (re==null){
          val bookName = activity?.getSharedPreferences("data", Context.MODE_PRIVATE)?.getString("bookName","")
          val input = activity?.openFileInput(bookName)
          val reader = ObjectInputStream(input)
          val body: ResponseWord = reader.readObject() as ResponseWord
          re=body
        }
        val random = Random()
        val r = random.nextInt(re!!.data.size)
        english.setText(re!!.data[r].headWord)
        chinese.setText(re!!.data[r].content.trans[0].pos+" "+re!!.data[r].content.trans[0].tranCn)

      }catch (e:Exception){
        e.printStackTrace()
      }

    }
//    homeViewModel.text.observe(viewLifecycleOwner, Observer {
//      textView.text = it
//    })
    return root
  }
}