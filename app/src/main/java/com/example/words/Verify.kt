package com.example.words

import com.afollestad.materialdialogs.MaterialDialog

class Verify {
    fun verifyMailbox(mailbox:String):Boolean{
        val regex = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$".toRegex()
        val matched = regex.matches(mailbox)
        return matched

    }

    fun verfiyUsername(username:String):Boolean{
        // 6-18位数字,字母,下划线_
        val regex = "^[\\w]{6,18}$".toRegex()
        val matched = regex.matches(username)
        return matched
    }

    fun verfiyPassword(password:String):Boolean{
        // 6--20位数字,字母,任意字符
        val regex = "^^[\\W\\da-zA-Z_]{6,20}\$".toRegex()
        val matched = regex.matches(password)
        return matched
    }


}