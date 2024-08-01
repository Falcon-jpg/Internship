package com.example.internship.retrofit

import com.example.internship.model.Challan
import com.example.internship.model.Response
import com.example.internship.model.Shipment
import com.example.internship.model.User
import com.example.internship.model.Warehouse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @POST("/public/sign-up")
    fun createUsers(@Body user: User) :Call<Response>

    @POST("/public/login")
    fun authenticateUser(@Body user: User):Call<String>

    @POST("/warehouse")
    fun createWarehouse(@Body warehouse: Warehouse):Call<Warehouse>

    @GET("/warehouse/show")
    fun getWarehouses():Call<List<Warehouse>>

    @POST("/shipment")
    fun createShipment(@Body shipment: Shipment):Call<Void>

    @GET("/shipment/show")
    fun getShipment():Call<List<Shipment>>

    @POST("api/challans")
    fun createChallan(@Body challan: Challan, @Query("shipmentIds") shipmentIds: List<Long>): Call<Challan>

    @DELETE("api/challans/{challanId}/shipments/{shipmentId}")
    fun removeShipmentFromChallan(@Path("challanId") challanId: Long, @Path("shipmentId") shipmentId: Long): Call<Void>

    @GET("api/challans")
    fun getAllChallans(): Call<List<Challan>>

    @GET("api/challans/{id}")
    fun getChallanById(@Path("id") id: Long): Call<Challan>

    @GET("api/challans/untagged-shipments")
    fun getUntaggedShipments(): Call<List<Shipment>>

    @GET("api/challans/untagged-shipments")
    fun getUntaggedShipments(@Query("warehouseId") warehouseId: Long?): Call<List<Shipment>>

}