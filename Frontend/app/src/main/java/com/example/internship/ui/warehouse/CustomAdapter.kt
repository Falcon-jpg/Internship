package com.example.internship.ui.warehouse

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.internship.R
import com.example.internship.model.Warehouse

class CustomAdapter(
    private var warehouseList: MutableList<Warehouse>,
    private val context: Fragment
) : RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {

    private var myListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(warehouse: Warehouse)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        myListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.each_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currItem = warehouseList[position]
        holder.bind(currItem)
    }

    override fun getItemCount(): Int = warehouseList.size


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val id: TextView = itemView.findViewById(R.id.ID)
        private val name: TextView = itemView.findViewById(R.id.name)
        private val location: TextView = itemView.findViewById(R.id.location)

        fun bind(warehouse: Warehouse) {
            id.text = warehouse.id.toString()
            name.text = warehouse.name
            location.text = warehouse.location

            itemView.setOnClickListener {
                myListener?.onItemClick(warehouse)
            }
        }
    }
    fun updateData(newWarehouses: List<Warehouse>) {
        val diffResult = DiffUtil.calculateDiff(WarehouseDiffCallback(warehouseList, newWarehouses))
        warehouseList.clear()
        warehouseList.addAll(newWarehouses)
        diffResult.dispatchUpdatesTo(this)
        Log.d("CustomAdapter", "Data updated, new size: ${warehouseList.size}")
    }

    class WarehouseDiffCallback(
        private val oldList: List<Warehouse>,
        private val newList: List<Warehouse>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
