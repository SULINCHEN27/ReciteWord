package com.example.words.retrofit

import java.io.Serializable

class ResponseMsg(val status:Int, val message:String, val data: Data){
    data class Data(
        val mailbox:String,
        val name: String,
        val education: Int,
        val learned_words_number:Int
    )
}

class ResponseOneBook(val status:Int, val message:String, val data: Int)

class ResponseRecentBook(val status:Int, val message:String, val data: String)

class ResponseLogin(val status:Int, val message:String,val token:String, val data: Data){
    data class Data(
            val msg:String
    )
}

class ResponseCode(val status:Int, val message:String):Serializable

class ResponseBookDir(val data:ArrayList<String>,val message: String,val status: Int)

class ResponseWord (val status: Int,val message:String,val data:ArrayList<Word>):Serializable  {
    data class Word(
        val content: Content,
        val headWord: String,
        val wordId: String,
        val wordRank: String
    ):Serializable {
        data class Content(
            val phone: String, val phrase: Phrase, val relWord: RelWord,val remMethod:RemMethod,
            val sentence: Sentence, val trans: ArrayList<Translate>
        ):Serializable {
            data class Phrase(val desc: String, val phrases: ArrayList<Phrases>):Serializable {
                data class Phrases(val pCn: String, val pContent: String):Serializable
            }

            data class RelWord(val desc: String, val rels: ArrayList<Rels>):Serializable {
                data class Rels(val pos: String, val words: ArrayList<Words>):Serializable {
                    data class Words(val hwd: String, val tran: String):Serializable
                }
            }

            data class RemMethod(val desc: String,val reVal:String):Serializable

            data class Sentence(val desc: String, val sentences: ArrayList<Sentences>):Serializable {
                data class Sentences(val sCn: String, val sContent: String):Serializable {

                }

            }

            data class Translate(
                val descCn: String, val descOther: String,
                val pos: String, val tranCn: String, val tranOther: String
            ):Serializable
        }
    }
}

class ResponseUserLearn(val status: Int,val message: String,val data:Data){
    data class Data(val record:Map<String,Int>,val sum:Int)
}













