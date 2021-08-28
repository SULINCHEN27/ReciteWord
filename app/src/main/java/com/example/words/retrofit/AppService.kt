package com.example.words.retrofit

import retrofit2.Call
import retrofit2.http.*

interface AppService {


    @POST("auth/")
    @FormUrlEncoded
    fun postLogin(@Field("mailbox")mailbox:String,
                  @Field("password")password:String): Call<ResponseLogin>

    @GET("auth")
    fun getMsg(@Header("Authorization") token:String):Call<ResponseMsg>

    @POST("auth/identify")
    @FormUrlEncoded
    fun postCode(@Field("mailbox") mailbox:String):Call<ResponseCode>

    @POST("auth/register")
    @FormUrlEncoded
    fun postRegister(@Field("mailbox")mailbox:String,
                     @Field("user_name")user_name:String,
                     @Field("password")password:String,
                     @Field("education")education:Int,
                     @Field("identify_code")identify_code:Int):Call<ResponseCode>

    @PUT("auth")
    @FormUrlEncoded
    fun putUser(@Query("token")token: String,
                @Field("password")password:String,
                @Field("new_password")new_password:String,
                @Field("user_name")user_name:String,
                @Field("education")education:Int):Call<ResponseMsg>


    @GET("book/dir")
    fun bookDir(@Header("Authorization") token:String):Call<ResponseBookDir>

    @GET("book/word")
    fun bookWord(@Header("Authorization") token:String,
                 @Query("book")book:String):Call<ResponseWord>

    @GET("word/")
    fun getUserLearn(@Header("Authorization") token:String,
                     @Query("book")book:String,
                     @Query("all")all:Int):Call<ResponseUserLearn>

    @GET("word/")
    fun oneBookWord(@Header("Authorization") token:String,
                    @Query("book")book:String):Call<ResponseOneBook>

    @POST("book/")
    @FormUrlEncoded
    fun postBook(@Header("Authorization") token:String,
                 @Field("book")book:String):Call<ResponseMsg>

    @POST("word/")
    @FormUrlEncoded
    fun postWord(@Header("Authorization") token:String,
                 @Field("book")book:String,
                 @Field("size")size:Int):Call<ResponseMsg>

    @GET("book/recent")
    fun getRecentBook(@Header("Authorization") token:String):Call<ResponseRecentBook>


}