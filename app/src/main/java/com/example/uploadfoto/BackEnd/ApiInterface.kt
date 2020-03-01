package com.example.uploadfoto.BackEnd

import com.example.uploadfoto.Model.ResponseData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiInterface {

    @Multipart
    @POST("kirimData.php")
    fun uploadData(
        @Part("nama") nim: RequestBody,
        @Part("alamat") nama: RequestBody,
        @Part foto: MultipartBody.Part
    ): Call<ResponseData>


}