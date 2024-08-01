package com.example.internship.ui.shipment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.internship.model.Warehouse
import com.example.internship.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class SharedViewModel : ViewModel() {
    private val _warehouseList = MutableLiveData<List<Warehouse>>()
    val warehouseList: LiveData<List<Warehouse>> = _warehouseList

    private val apiInterface: ApiInterface = com.example.internship.retrofit.Retrofit.create(ApiInterface::class.java)

    init {
        Log.d("SharedViewModel", "Initializing SharedViewModel")
        fetchWarehouses()
    }

    private fun fetchWarehouses() {
        Log.d("SharedViewModel", "Fetching warehouses")
        apiInterface.getWarehouses().enqueue(object : Callback<List<Warehouse>> {
            override fun onResponse(call: Call<List<Warehouse>>, response: Response<List<Warehouse>>) {
                if (response.isSuccessful) {
                    val warehouses = response.body() ?: emptyList()
                    Log.d("SharedViewModel", "Warehouses fetched successfully. Count: ${warehouses.size}")
                    _warehouseList.value = warehouses
                } else {
                    Log.e("SharedViewModel", "Failed to fetch warehouses. Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Warehouse>>, t: Throwable) {
                Log.e("SharedViewModel", "Exception while fetching warehouses", t)
            }
        })
    }
    // You can also add a function to manually trigger warehouse fetching if needed
    suspend fun refreshWarehouses(): List<Warehouse> {
        return withContext(Dispatchers.IO) {
            try {
                val warehouses = apiInterface.getWarehouses().execute().body() ?: emptyList()
                _warehouseList.postValue(warehouses)
                Log.d("SharedViewModel", "Warehouses refreshed, count: ${warehouses.size}")
                warehouses
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Error refreshing warehouses", e)
                emptyList()
            }
        }
    }

    fun updateWarehouses(warehouses: List<Warehouse>) {
        _warehouseList.value = warehouses
    }
}

