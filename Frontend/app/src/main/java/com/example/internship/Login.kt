package com.example.internship

import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.internship.databinding.ActivityLoginBinding
import com.example.internship.model.User
import com.example.internship.retrofit.ApiInterface
import com.example.internship.retrofit.Retrofit
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

class Login : AppCompatActivity() {
    private lateinit var  retrofit: Retrofit
    private lateinit var binding : ActivityLoginBinding
    private lateinit var apiInterface: ApiInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpText.setOnClickListener(){
            val intent = Intent(this,SignUp::class.java)
            startActivity(intent)
        }
        apiInterface = Retrofit.create(ApiInterface::class.java)

        binding.signIn.setOnClickListener(){
            val userName = binding.name.text.toString()
            val password = binding.pass.text.toString()
            val user = User(userName,password,"")
            val call = apiInterface.authenticateUser(user)

            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val jwt = response.body()
                        if (!jwt.isNullOrEmpty()) {
                            // Store the JWT token and proceed with login
                            Toast.makeText(this@Login, "Login successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@Login,MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@Login, "Invalid response from server", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(this@Login, "Login failed: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("LoginError", "Network error", t)
                    Toast.makeText(this@Login, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }


}