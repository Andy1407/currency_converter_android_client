package com.example.currency_converter

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_endpoint.*


class EndpointActivity : AppCompatActivity() {

    private fun saveEndpoint() {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("endpoint", endpoint.text.toString()).apply()
    }

    private fun getEndpoint(default: String): String? {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        return prefs.getString("endpoint", default)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_endpoint)

        val defaultEndpoint = getString(R.string.default_endpoint)
        val currentEndpoint = getEndpoint(defaultEndpoint)
        if (currentEndpoint != null) {
            endpoint.setText(currentEndpoint)
        }



        back_button.setOnClickListener {
            saveEndpoint()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
    }

    override fun onPause() {
        super.onPause()
        saveEndpoint()
    }


    override fun onStop() {
        super.onStop()
        saveEndpoint()
    }

}