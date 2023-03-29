package com.uwindsor.calaj.fitupapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ArchivedFoodsAdapter (val context: Context, val archivedFoods: List<FoodItem>, private val clickListener: (FoodItem) -> Unit) :
    RecyclerView.Adapter<ArchivedFoodsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_newfood, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ArchivedFoodsAdapter.ViewHolder, position: Int) {
            val foodItem = archivedFoods[position]
            holder.tvFoodItemName.text = "${foodItem.name}"
            holder.tvFoodItemCalories.text = "${foodItem.calories} Calories"
            holder.tvFoodItemProtein.text = "${foodItem.protein}g Protein"
            holder.tvFoodItemCarbs.text = "${foodItem.carbs}g Carbs"
            holder.tvFoodItemFat.text = "${foodItem.fat}g Fat"
            holder.itemView.setOnClickListener {
                clickListener(foodItem)
            }

            // possibly edit picture here
        }

        override fun getItemCount() = archivedFoods.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvFoodItemName: TextView = itemView.findViewById(R.id.tvNewFoodName)
            val tvFoodItemCalories: TextView = itemView.findViewById(R.id.tvNewFoodCalories)
            val tvFoodItemProtein: TextView = itemView.findViewById(R.id.tvNewFoodProtein)
            val tvFoodItemCarbs: TextView = itemView.findViewById(R.id.tvNewFoodCarbs)
            val tvFoodItemFat: TextView = itemView.findViewById(R.id.tvNewFoodFat)
        }
}