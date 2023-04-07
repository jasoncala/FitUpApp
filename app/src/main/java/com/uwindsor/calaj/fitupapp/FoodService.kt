package com.uwindsor.calaj.fitupapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

public interface FoodService {
    @GET("parser")
    fun searchFoods(
        @Query("app_id") appID: String,
        @Query("app_key") authHeader: String,
        @Query("ingr") searchTerm: String,
        @Query("nutrition-type") nutritionType: String = "logging",
        @Query("category") category: String = "generic-meals"
        ): Call<FoodSearchResult>
}