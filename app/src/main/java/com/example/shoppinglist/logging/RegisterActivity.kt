package com.example.shoppinglist.logging

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.shoppinglist.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.btnCancel.setOnClickListener {
            finish()
        }
        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser(){
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch{
                try {
                    Log.d("RegisterUser","Coroutine is running 2")
                    auth.createUserWithEmailAndPassword(email,password).await()
                    Log.d("RegisterUser","Coroutine is finished")
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@RegisterActivity,
                            "User registered successfully",
                            Toast.LENGTH_LONG)
                    }
                    finish()
                }
                catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@RegisterActivity,
                            "Cannot register:" + e.message,
                            Toast.LENGTH_LONG)
                    }
                }
            }
        }
    }

}