package com.example.shoppinglist.settings

import android.R
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.shoppinglist.databinding.ActivitySettingsBinding


class AppSettings():
    AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val arrTextColor = arrayListOf("black", "white", "red", "green", "blue")
        binding.spTextColor.apply {
            adapter = ArrayAdapter(context, R.layout.simple_spinner_item, arrTextColor)
            setSelection(0)
        }

        readSharedPreferences()
        //Shared Preferences
        val sharedPreferences = getSharedPreferences("MySettings", Context.MODE_PRIVATE)
        val background = sharedPreferences.getBoolean("background", false)
        val textColor = sharedPreferences.getString("textColor", "black").toString()

        binding.swBackground.isChecked = background
        val index = arrTextColor.indexOf(textColor)
        binding.spTextColor.setSelection(index)

        binding.tvApply.setOnClickListener() {
            readSharedPreferences()
            //Shared Preferences
            val sharedPreferences = getSharedPreferences("MySettings", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val background = binding.swBackground.isChecked
            val textColor = binding.spTextColor.toString()
            // Store data
            editor.putBoolean("background", binding.swBackground.isChecked)
            editor.putString("textColor", binding.spTextColor.selectedItem.toString())
            editor.putBoolean("apply", true)
            editor.apply()
            Log.d("ActivitySettings", "Background:$background \n Text Color: $textColor")
            finish()
        }

        binding.tvCancel.setOnClickListener() {
            val sharedPreferences = getSharedPreferences("MySettings", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("apply", false)
            editor.apply()
            finish()
        }
    }

    private fun readSharedPreferences(){
        val sharedPreferences = getSharedPreferences("MySettings", Context.MODE_PRIVATE)
        // Retrieve data
        val background = sharedPreferences.getBoolean("background", false)
        val textColor = sharedPreferences.getString("textColor", "").toString()
        val applySettings = sharedPreferences.getBoolean("apply", false)
        Log.d("ActivitySettings", "On apply click\n Retrived datas:\n " +
                "Background color: $background\n" +
                "Text Color: $textColor\n" +
                "Apply Settings: $applySettings")
    }
}