package com.example.internship

import android.content.Intent
import com.google.gson.Gson
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.internship.databinding.ActivitySignUpBinding
import com.example.internship.model.User
import com.example.internship.retrofit.ApiInterface
import com.example.internship.retrofit.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUp : AppCompatActivity() {
    private lateinit var apiInterface: ApiInterface
    private lateinit var binding : ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the ApiInterface
        apiInterface = Retrofit.create(ApiInterface::class.java)

        binding.login.setOnClickListener(){
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
        }

        binding.signUpButton.setOnClickListener(){
            val userName = binding.etName.text.toString()
            val password = binding.etPass.text.toString()
            val email = binding.etMail.text.toString()

            val user = User(userName,password,email)
            Log.d("SignUp", "Sending user: $user")

            // Make network call to create user
            val call = apiInterface.createUsers(user)
            Log.d("SignUp",call.toString())
            call.enqueue(object : Callback<com.example.internship.model.Response> {


                override fun onResponse(
                    p0: Call<com.example.internship.model.Response>,
                    p1: Response<com.example.internship.model.Response>
                ) {
                    when (p1.code()) {
                        201 -> {
                            Toast.makeText(this@SignUp, "User created successfully", Toast.LENGTH_SHORT).show()
                        }
                        400 -> { // BAD REQUEST
                            Toast.makeText(this@SignUp, "Repeated UserName: Bad Request", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this@SignUp, "Unexpected response: ${p1.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(
                    p0: Call<com.example.internship.model.Response>,
                    p1: Throwable
                ) {
                    Log.e("SignUp", "Network error", p1)
                    Toast.makeText(this@SignUp, "Network error: ${p1.message}", Toast.LENGTH_SHORT).show()
                }
            })


        }
    }
}