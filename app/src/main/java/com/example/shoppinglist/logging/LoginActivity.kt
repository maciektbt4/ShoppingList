package com.example.shoppinglist.logging

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.shoppinglist.databinding.ActivityLoginBinding
import com.example.shoppinglist.ui.shoppinglist.ShoppingActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        binding.tvRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.btnLogin.setOnClickListener {
            loginUser()
        }
    }

    override fun onStart() {
        super.onStart()
        if(checkLoggedInStat()){
            val intent = Intent(this, ShoppingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(){
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch{
                try {
                    Log.d("LoginUser","Coroutine is running")
                    auth.signInWithEmailAndPassword(email,password).await()
                    Log.d("LoginUser","Coroutine is finished")
                    Log.d("LoginUser","Current user is ${auth.currentUser}")

                    if(auth.currentUser != null){
                        val intent = Intent(this@LoginActivity,ShoppingActivity::class.java)
                        startActivity(intent)
                    }
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@LoginActivity,
                            "User login successfully",
                            Toast.LENGTH_LONG)
                    }
                }
                catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@LoginActivity,
                            "Cannot login:" + e.message,
                            Toast.LENGTH_LONG)
                    }
                }
            }
        }
    }

    private fun checkLoggedInStat():Boolean {
        if(auth.currentUser == null){
            binding.tvLoggedInStatus.text = "You are not logged in!"
            return false
        }
        else{
            binding.tvLoggedInStatus.text = "You are logged in!"
            return true
        }
    }
}