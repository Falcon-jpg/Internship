package com.example.internship.model

import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

data class Challan(
    val id: Long? = null,  // Nullable and defaulted to null for new challans
    val challanNo: String,
    val vehicleType: String,
    val vehicleNo: String,
    val fromWarehouse: Warehouse,
    val toWarehouse: Warehouse,
    val createdBy: String? = null,  // You might want to set this on the server side
    val createdAt: LocalDateTime ?= null,
    val shipments: List<Shipment> = emptyList()
)
