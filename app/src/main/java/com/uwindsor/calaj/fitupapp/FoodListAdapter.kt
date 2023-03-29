package com.uwindsor.calaj.fitupapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodListAdapter(val context: Context, val foodItems: List<FoodItem>) :
    RecyclerView.Adapter<FoodListAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_food, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val foodItem = foodItems[position]
            holder.tvFoodItemName.text = "${foodItem.name} (${foodItem.calories}cal): "
            holder.tvFoodItemProtein.text = "${foodItem.protein}g(P)"
            holder.tvFoodItemCarbs.text = " - ${foodItem.carbs}g(C) - "
            holder.tvFoodItemFat.text = "${foodItem.fat}g(F)"
        }

        override fun getItemCount() = foodItems.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvFoodItemName: TextView = itemView.findViewById(R.id.tvFoodItemName)
            val tvFoodItemProtein: TextView = itemView.findViewById(R.id.tvFoodItemProtein)
            val tvFoodItemCarbs: TextView = itemView.findViewById(R.id.tvFoodItemCarbs)
            val tvFoodItemFat: TextView = itemView.findViewById(R.id.tvFoodItemFat)
        }
    }