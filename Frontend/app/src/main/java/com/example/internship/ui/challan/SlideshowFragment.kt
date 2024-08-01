package com.example.internship.ui.challan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.internship.databinding.FragmentSlideshowBinding
import com.example.internship.model.Challan
import com.example.internship.model.Shipment
import com.example.internship.model.Warehouse
import com.example.internship.retrofit.ApiInterface
import com.example.internship.retrofit.Retrofit
import com.example.internship.ui.shipment.SharedViewModel
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SlideshowFragment : Fragment() {
    private lateinit var spinnerFromWarehouse: Spinner
    private lateinit var spinnerToWarehouse: Spinner
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!
    private val apiInterface: ApiInterface = Retrofit.create(ApiInterface::class.java)

    private var warehouses: List<Warehouse> = emptyList()
    private var fromWarehouse: Warehouse? = null
    private var toWarehouse: Warehouse? = null
    private var untaggedShipments: List<Shipment> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            spinnerFromWarehouse = binding.spinnerFromWarehouse
            spinnerToWarehouse = binding.spinnerToWarehouse

            // Initialize spinners with empty adapter
            val emptyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, emptyList<String>())
            spinnerFromWarehouse.adapter = emptyAdapter
            spinnerToWarehouse.adapter = emptyAdapter

            setupSpinners()
            setupShipmentList()

            sharedViewModel.warehouseList.observe(viewLifecycleOwner) { list ->
                warehouses = list
                setupSpinners()
            }

            binding.buttonCreateChallan.setOnClickListener {
                createAndSendChallan()
            }
        } catch (e: Exception) {
            Log.e("ChallanFragment", "Error in onViewCreated: ${e.message}", e)
        }
    }

    private fun setupSpinners() {
        if (warehouses.isNotEmpty()) {
            Log.d("ChallanFragment", "Setting up spinners with warehouses: $warehouses")
            val items = listOf("Select Warehouse") + warehouses.map { "${it.id} - ${it.name}" }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)

            spinnerFromWarehouse.adapter = adapter
            spinnerToWarehouse.adapter = adapter

            spinnerFromWarehouse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    if (position > 0) {
                        fromWarehouse = warehouses[position - 1]
                        fetchUntaggedShipments(fromWarehouse?.id)
                    } else {
                        fromWarehouse = null
                        clearShipmentList()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    fromWarehouse = null
                    clearShipmentList()
                }
            }

            spinnerToWarehouse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    toWarehouse = if (position > 0) warehouses[position - 1] else null
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    toWarehouse = null
                }
            }
        } else {
            Log.d("ChallanFragment", "No warehouses available")
            val emptyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf("No warehouses available"))
            spinnerFromWarehouse.adapter = emptyAdapter
            spinnerToWarehouse.adapter = emptyAdapter
        }
    }


    private fun setupShipmentList() {
        if (untaggedShipments.isNotEmpty()) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_multiple_choice, untaggedShipments.map { it.id.toString() })
            binding.listViewShipments.adapter = adapter
            binding.listViewShipments.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        } else {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listOf("No untagged shipments"))
            binding.listViewShipments.adapter = adapter
            binding.listViewShipments.choiceMode = ListView.CHOICE_MODE_NONE
        }
    }

    private fun fetchUntaggedShipments(warehouseId: Long?) {
        Log.d("ChallanFragment", "Fetching untagged shipments for warehouse ID: $warehouseId")
        apiInterface.getUntaggedShipments(warehouseId).enqueue(object : Callback<List<Shipment>> {
            override fun onResponse(call: Call<List<Shipment>>, response: Response<List<Shipment>>) {
                if (response.isSuccessful) {
                    untaggedShipments = response.body() ?: emptyList()
                    Log.d("ChallanFragment", "Successfully fetched untagged shipments: ${untaggedShipments.size}")
                    setupShipmentList()
                } else {
                    Log.e("ChallanFragment", "Failed to fetch untagged shipments. Code: ${response.code()}, Message: ${response.message()}")
                    Toast.makeText(requireContext(), "Failed to fetch untagged shipments: ${response.code()}", Toast.LENGTH_SHORT).show()
                    clearShipmentList()
                }
            }

            override fun onFailure(call: Call<List<Shipment>>, t: Throwable) {
                Log.e("ChallanFragment", "Error fetching untagged shipments", t)
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                clearShipmentList()
            }
        })
    }

    private fun clearShipmentList() {
        untaggedShipments = emptyList()
        setupShipmentList()
    }

    private fun createAndSendChallan() {
        val challanNo = binding.editTextChallanNo.text.toString()
        val vehicleType = binding.editTextVehicleType.text.toString()
        val vehicleNo = binding.editTextVehicleNo.text.toString()

        if (challanNo.isNotEmpty() && vehicleType.isNotEmpty() && vehicleNo.isNotEmpty()
            && fromWarehouse != null && toWarehouse != null) {

            if (fromWarehouse == toWarehouse) {
                Toast.makeText(requireContext(), "From and To warehouses must be different", Toast.LENGTH_SHORT).show()
                return
            }

            val selectedShipments = getSelectedShipments()
            val shipmentIds = selectedShipments.mapNotNull { it.id }

            val challan = Challan(
                challanNo = challanNo,
                vehicleType = vehicleType,
                vehicleNo = vehicleNo,
                fromWarehouse = fromWarehouse!!,
                toWarehouse = toWarehouse!!,
                shipments = emptyList()  // We'll send shipment IDs separately
            )

            // Log the request details
            val gson = GsonBuilder().setPrettyPrinting().create()
            val challanJson = gson.toJson(challan)
            Log.d("ChallanCreation", "Challan JSON: $challanJson")
            Log.d("ChallanCreation", "Shipment IDs: $shipmentIds")

            apiInterface.createChallan(challan, shipmentIds).enqueue(object : Callback<Challan> {
                override fun onResponse(call: Call<Challan>, response: Response<Challan>) {
                    if (response.isSuccessful) {
                        val createdChallan = response.body()
                        Log.d("ChallanCreation", "Challan created successfully: $createdChallan")
                        Toast.makeText(requireContext(), "Challan created successfully", Toast.LENGTH_SHORT).show()
                        clearForm()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("ChallanCreation", "Failed to create challan. Code: ${response.code()}, Body: $errorBody")
                        Toast.makeText(requireContext(), "Failed to create challan: ${response.message() ?: errorBody}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Challan>, t: Throwable) {
                    Log.e("ChallanCreation", "Error creating challan", t)
                    Log.d("ChallanCreation", "Request URL: ${call.request().url()}")
                    Log.d("ChallanCreation", "Request Body: ${call.request().body()?.toString()}")
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSelectedShipments(): List<Shipment> {
        val selectedShipments = mutableListOf<Shipment>()
        for (i in 0 until binding.listViewShipments.count) {
            if (binding.listViewShipments.isItemChecked(i)) {
                selectedShipments.add(untaggedShipments[i])
            }
        }
        return selectedShipments
    }

    private fun clearForm() {
        binding.editTextChallanNo.text?.clear()
        binding.editTextVehicleType.text?.clear()
        binding.editTextVehicleNo.text?.clear()
        spinnerFromWarehouse.setSelection(0)
        spinnerToWarehouse.setSelection(0)
        for (i in 0 until binding.listViewShipments.count) {
            binding.listViewShipments.setItemChecked(i, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}