package com.uwindsor.calaj.fitupapp

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
