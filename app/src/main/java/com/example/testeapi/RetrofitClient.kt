package com.example.testeapi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

object RetrofitClient {
    // 10.0.2.2 é o endereço especial do Android Emulator para acessar o localhost da máquina hospedeira
    private const val BASE_URL = "http://10.0.2.2:8000/"

    val api: CarApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CarApi::class.java)
    }
}