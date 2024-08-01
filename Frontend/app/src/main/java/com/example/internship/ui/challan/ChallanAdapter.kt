package com.example.internship.ui.challan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.internship.R
import com.example.internship.databinding.ItemChallanBinding
import com.example.internship.model.Challan
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChallanAdapter : RecyclerView.Adapter<ChallanAdapter.ChallanViewHolder>() {
    private var challans: List<Challan> = emptyList()

    class ChallanViewHolder(private val binding: ItemChallanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(challan: Challan) {
            binding.apply {
                tvChallanNo.text = challan.challanNo
                tvVehicle.text = "${challan.vehicleType} - ${challan.vehicleNo}"
                tvFromWarehouse.text = challan.fromWarehouse.name
                tvToWarehouse.text = challan.toWarehouse.name
                tvShipmentId.text = challan.shipments.map { it.id }.joinToString(", ")
                tvCreatedAt.text = challan.createdAt?.let { formatDateTime(it) }
            }
        }

        private fun formatDateTime(dateTime: LocalDateTime): String {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            return dateTime.format(formatter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallanViewHolder {
        val binding = ItemChallanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChallanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChallanViewHolder, position: Int) {
        holder.bind(challans[position])
    }

    override fun getItemCount() = challans.size

    fun updateChallans(newChallans: List<Challan>) {
        challans = newChallans
        notifyDataSetChanged()
    }
}