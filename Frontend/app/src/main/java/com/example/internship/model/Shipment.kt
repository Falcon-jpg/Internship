package com.example.internship.model

data class Shipment(
    val id: Long? = null,
    val height: Int,
    val width: Int,
    val length: Int,
    val fromWarehouse: Warehouse,
    val toWarehouse: Warehouse,
    val status: String = "CREATED",
    val fragile: Boolean
)