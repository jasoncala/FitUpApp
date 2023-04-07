package com.uwindsor.calaj.fitupapp

import com.google.gson.annotations.SerializedName

data class FoodItem(
    val name: String,
    val calories: Int,
    val carbs: Int,
    val fat: Int,
    val protein: Int
){
    override fun toString(): String {
        return name
    }
}

data class FoodSearchResult(
    @SerializedName("text") val text: String,
    @SerializedName("hints") val foods: List<Any>
)
