package com.example.internship.ui.challan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.internship.R
import com.example.internship.databinding.FragmentChallanListing2Binding
import com.example.internship.model.Challan
import com.example.internship.retrofit.ApiInterface
import com.example.internship.retrofit.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChallanListing : Fragment() {
    private var _binding: FragmentChallanListing2Binding? = null
    private val binding get() = _binding!!
    private lateinit var challanAdapter : ChallanAdapter
    private val apiInterface: ApiInterface = Retrofit.create(ApiInterface::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChallanListing2Binding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        loadChallans()
    }

    private fun setupRecyclerView() {
        challanAdapter = ChallanAdapter()
        binding.recyclerViewChallans.apply {
            adapter = challanAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupSearch() {
        binding.buttonSearch.setOnClickListener {
            val challanId = binding.editTextSearch.text.toString().toLongOrNull()
            if (challanId != null) {
                searchChallan(challanId)
            } else {
                Toast.makeText(context, "Please enter a valid Challan ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadChallans() {
        apiInterface.getAllChallans().enqueue(object : Callback<List<Challan>> {
            override fun onResponse(call: Call<List<Challan>>, response: Response<List<Challan>>) {
                if (response.isSuccessful) {
                    challanAdapter.updateChallans(response.body() ?: emptyList())
                } else {
                    Toast.makeText(context, "Failed to load challans", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Challan>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun searchChallan(id: Long) {
        apiInterface.getChallanById(id).enqueue(object : Callback<Challan> {
            override fun onResponse(call: Call<Challan>, response: Response<Challan>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        challanAdapter.updateChallans(listOf(it))
                    }
                } else {
                    Toast.makeText(context, "Challan not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Challan>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}