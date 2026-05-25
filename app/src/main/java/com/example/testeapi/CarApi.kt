package com.example.testeapi

import retrofit2.Call
import retrofit2.http.*

interface CarApi {
    @GET("cars")
    fun getCars(): Call<List<Car>>

    @POST("cars")
    fun createCar(@Body car: Car): Call<Car>

    @DELETE("cars/{id}")
    fun deleteCar(@Path("id") id: Int): Call<Void>

    @PUT("cars/{id}")
    fun updateCar(@Path("id") id: Int, @Body car: Car): Call<Car>
}