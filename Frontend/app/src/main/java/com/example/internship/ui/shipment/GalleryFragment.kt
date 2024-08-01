package com.example.internship.ui.shipment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.internship.databinding.FragmentGalleryBinding
import com.example.internship.model.Shipment
import com.example.internship.model.Warehouse
import com.example.internship.retrofit.ApiInterface
import com.example.internship.retrofit.Retrofit
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GalleryFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentGalleryBinding? = null
    private val apiInterface: ApiInterface = Retrofit.create(ApiInterface::class.java)
    private val binding get() = _binding!!
    private var warehouses: List<Warehouse> = emptyList()

    private var fromWarehouse: Warehouse? = null
    private var toWarehouse: Warehouse? = null
    private var fragile: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("GalleryFragment", "onViewCreated called")
        setupSpinners() //Call this if warehouses are already loaded

        sharedViewModel.warehouseList.observe(viewLifecycleOwner) { list ->
            Log.d("GalleryFragment", "Warehouse list updated, size: ${list.size}")
            warehouses = list
            setupSpinners()
        }
        binding.buttonCreate.setOnClickListener {
            createAndSendShipment()
        }
    }

    private fun setupSpinners() {
        if (warehouses.isNotEmpty()) {
            setupWarehouseSpinner(binding.dropdownMenu, warehouses, "Sender's Address") {
                fromWarehouse = it
            }
            setupWarehouseSpinner(binding.toWarehouse, warehouses, "Receiver's Address") {
                toWarehouse = it
            }
            setupBooleanSpinner(binding.bool)
        } else {
            Log.w("GalleryFragment", "Warehouse list is empty")
        }
    }

    private fun setupWarehouseSpinner(
        spinner: Spinner,
        warehouses: List<Warehouse>,
        hint: String,
        onSelect: (Warehouse?) -> Unit
    ) {
        val items = mutableListOf(hint) + warehouses.map { "${it.id} - ${it.name}" }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedWarehouse = if (position > 0) warehouses[position - 1] else null
                onSelect(selectedWarehouse)
                updateOtherSpinner(spinner, selectedWarehouse)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                onSelect(null)
            }
        }
    }

    private fun updateOtherSpinner(currentSpinner: Spinner, selectedWarehouse: Warehouse?) {
        val otherSpinner = if (currentSpinner == binding.dropdownMenu) binding.toWarehouse else binding.dropdownMenu
        val otherAdapter = otherSpinner.adapter as ArrayAdapter<*>

        for (i in 1 until otherAdapter.count) {
            val item = otherAdapter.getItem(i)
            val warehouse = warehouses[i - 1]
            otherAdapter.getView(i, null, otherSpinner).isEnabled = warehouse != selectedWarehouse
        }

        otherAdapter.notifyDataSetChanged()
    }

    private fun setupBooleanSpinner(spinner: Spinner) {
        val items = listOf("Select Fragility", "True", "False")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                fragile = when (position) {
                    1 -> true
                    2 -> false
                    else -> false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle case when nothing is selected
            }
        }
    }

    private fun createAndSendShipment() {
        val length = binding.length.text.toString()
        val width = binding.width.text.toString()
        val height = binding.height.text.toString()

        Log.d("GalleryFragment", "Attempting to create shipment")
        Log.d("GalleryFragment", "Length: $length, Width: $width, Height: $height")
        Log.d("GalleryFragment", "From Warehouse: ${fromWarehouse?.name}, To Warehouse: ${toWarehouse?.name}")
        Log.d("GalleryFragment", "Fragile: $fragile")

        if (length.isNotEmpty() && width.isNotEmpty() && height.isNotEmpty()
            && fromWarehouse != null && toWarehouse != null) {

            if (fromWarehouse == toWarehouse) {
                Toast.makeText(requireContext(), "From and To warehouses must be different", Toast.LENGTH_SHORT).show()
                return
            }

            val shipment = Shipment(
                height = height.toInt(),
                width = width.toInt(),
                length = length.toInt(),
                fromWarehouse = fromWarehouse!!,
                toWarehouse = toWarehouse!!,
                fragile = fragile
            )

            Log.d("GalleryFragment", "Shipment object created: $shipment")

            apiInterface.createShipment(shipment).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("GalleryFragment", "Shipment created successfully")
                        Toast.makeText(requireContext(), "Shipment created successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("GalleryFragment", "Failed to create shipment. Response code: ${response.code()}")
                        Log.e("GalleryFragment", "Error body: ${response.errorBody()?.string()}")
                        Toast.makeText(requireContext(), "Failed to create shipment: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("GalleryFragment", "Error creating shipment", t)
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Log.w("GalleryFragment", "Some fields are empty or warehouses not selected")
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun refreshWarehouses() {
        lifecycleScope.launch {
            val updatedWarehouses = sharedViewModel.refreshWarehouses()
            Log.d("GalleryFragment", "Warehouses refreshed, count: ${updatedWarehouses.size}")
            setupSpinners()
        }
    }
    override fun onResume() {
        super.onResume()
        refreshWarehouses()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}