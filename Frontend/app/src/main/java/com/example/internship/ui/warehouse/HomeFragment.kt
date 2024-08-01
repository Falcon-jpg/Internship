package com.example.internship.ui.warehouse

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.android.gms.location.FusedLocationProviderClient
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.internship.R
import com.example.internship.databinding.FragmentHomeBinding
import com.example.internship.model.Warehouse
import com.example.internship.retrofit.ApiInterface
import com.example.internship.retrofit.Retrofit
import com.example.internship.ui.shipment.SharedViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recView: RecyclerView
    private var warehouses = mutableListOf<Warehouse>()
    private lateinit var adapter: CustomAdapter
    private val apiInterface: ApiInterface = Retrofit.create(ApiInterface::class.java)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView() // Set up RecyclerView immediately

        // Observe the warehouseList
        sharedViewModel.warehouseList.observe(viewLifecycleOwner) { warehouses ->
            this.warehouses = warehouses.toMutableList()
            adapter.updateData(warehouses) // Update adapter data directly
            Log.d("HomeFragment", "Warehouse list updated, size: ${warehouses.size}")
        }

        binding.button.setOnClickListener {
            showCreateWarehouseDialog()
        }

        // Trigger a refresh of warehouses
        viewLifecycleOwner.lifecycleScope.launch {
            val updatedWarehouses = sharedViewModel.refreshWarehouses()
            Log.d("HomeFragment", "Warehouses refreshed, count: ${updatedWarehouses.size}")
        }
    }


    private fun setupRecyclerView() {
        adapter = CustomAdapter(mutableListOf(), this)
        adapter.setOnItemClickListener(object : CustomAdapter.OnItemClickListener {
            override fun onItemClick(warehouse: Warehouse) {
                // Handle item click, e.g., show details or edit
                Toast.makeText(context, "Clicked: ${warehouse.name}", Toast.LENGTH_SHORT).show()
            }
        })
        binding.table.adapter = adapter
        binding.table.layoutManager = LinearLayoutManager(context)
    }

    private fun fetchWarehouses() {
        apiInterface.getWarehouses().enqueue(object : Callback<List<Warehouse>> {
            override fun onResponse(p0: Call<List<Warehouse>>, response: Response<List<Warehouse>>) {
                if (response.isSuccessful) {
                    val warehousesFromServer = response.body() ?: emptyList()
                    Log.d("WarehouseFragment", "Fetched warehouses: ${warehouses.size}")
                    adapter.updateData(warehousesFromServer)
                } else {
                    showError("Error: ${response.message()}")
                }
            }
            override fun onFailure(p0: Call<List<Warehouse>>, t: Throwable) {
                Log.e("WarehouseFragment", "Network error", t)
                showError("Failure: ${t.message}")
            }
        })
    }

    private fun showCreateWarehouseDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.alert_dialog_warehouse, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.editTextName)
        val locationEditText = dialogView.findViewById<EditText>(R.id.editTextLocation)

        AlertDialog.Builder(requireContext())
            .setTitle("Create Warehouse")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val name = nameEditText.text.toString()
                val location = locationEditText.text.toString()
                if (name.isNotBlank() && location.isNotBlank()) {
                    createWarehouse(name, location)
                } else {
                    showError("Name and location cannot be empty")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createWarehouse(name: String, location: String) {
        val newWarehouse = Warehouse(name = name, location = location)
        lifecycleScope.launch {
            try {
                Log.d("HomeFragment", "Attempting to create warehouse: $name, $location")
                val response = withContext(Dispatchers.IO) {
                    apiInterface.createWarehouse(newWarehouse).execute()
                }
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Warehouse created successfully", Toast.LENGTH_SHORT).show()
                    }
                    val updatedWarehouses = sharedViewModel.refreshWarehouses()
                    Log.d("HomeFragment", "Warehouses after refresh: ${updatedWarehouses.size}")

                    withContext(Dispatchers.Main) {
                        // Immediately update the adapter
                        adapter.updateData(updatedWarehouses)

                        // Force a UI update
                        binding.table.post {
                            adapter.notifyDataSetChanged()
                        }
                    }
                } else {
                    Log.e("HomeFragment", "Error: ${response.errorBody()?.string()}")
                    withContext(Dispatchers.Main) {
                        showError("Failed to create warehouse")
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Network error", e)
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showError("Network error: ${e.javaClass.simpleName} - ${e.message}")
                }
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

