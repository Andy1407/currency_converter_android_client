package com.example.currency_converter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_endpoint.*

class MainActivity : AppCompatActivity() {
    var currency12 = "Rub"
    var currency22 = "Usd"
    var courseReplace = false
    var converterReplace = false

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val defaultEndpoint = getString(R.string.default_endpoint)

        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)

        current_endpoint.text = prefs.getString("endpoint", defaultEndpoint)


        fun parseResponse(response: String, key: String): String {
            var data = response.replace("{", "")
            data = data.replace("}", "")
            data = data.replace("\"", "")
            data = data.replace("\n", "")

            val data_list = data.split(",")

            for (d in data_list.indices) {
                if (data_list[d].split(":")[0] == key) {
                    return data_list[d].split(":")[1]
                }
            }

            return ""
        }


        fun updateScreen() {
            if (courseReplace) {
                currency1_Cource.text = currency12
                currency2_Cource.text = currency22
            } else {
                currency1_Cource.text = currency22
                currency2_Cource.text = currency12
            }

            if (converterReplace) {
                currency1_convert.text = currency12
                currency2_convert.text = currency22
            } else {
                currency1_convert.text = currency22
                currency2_convert.text = currency12
            }

        }

        @SuppressLint("SetTextI18n")
        fun sendRequest(courseValue: String, courseCurrency: String, input: String, from: String, to: String) {
            val queue = Volley.newRequestQueue(this)
//            val url = "http://192.168.1.49:8000/"
            val url = "${prefs.getString("endpoint", defaultEndpoint)}"

            Log.d("HELLO", "$url/?course=${courseValue}&coursecurrency=${courseCurrency}&input=${input}&from=${from}&to=${to}")

            send_button.text = getString(R.string.loading)

            val currencyRequest = StringRequest(
                Request.Method.GET, "$url?course=${courseValue}&coursecurrency=${courseCurrency}&input=${input}&from=${from}&to=${to}",
            Response.Listener { response ->
                Log.d("response", response)
                var responseValue = parseResponse(response, "result")

                if (responseValue != "") {

                    if (".0" in responseValue) {
                        responseValue = responseValue.replace(".0", "")
                    }
                    convert_value.text = responseValue
                }
                send_button.text = getString(R.string.send)

            },
            Response.ErrorListener {
                if (it.networkResponse != null) {
                    Log.e("hello", it.networkResponse.data.decodeToString())
                }
                send_button.text = "error"
            })

            queue.add(currencyRequest)

        }


        currency1.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                Log.d("hello", "Not yet implemented")
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("hello", "Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                currency12 = p0.toString()
                updateScreen()
            }

        })


        currency2.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                Log.d("hello", "Not yet implemented")
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("hello", "Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                currency22 = p0.toString()
                updateScreen()
            }

        })


        arrowCource.setOnClickListener {
            courseReplace = !courseReplace
            updateScreen()
        }

        arrowConverter.setOnClickListener {
            converterReplace = !converterReplace
            val value = input_value.text

            if (input_value.text.isNotEmpty() && convert_value.text != "0") {
                input_value.setText(convert_value.text)
                convert_value.text = value
            }

            updateScreen()
        }


        send_button.setOnClickListener {

            val input = if (input_value.text.isEmpty()) {
                "0"
            } else {
                input_value.text.toString()
            }


            val courseValue  = if (course.text.isEmpty()) {
                "1"
            } else {
                course.text.toString()
            }

            val courseCurrency = if (courseReplace) {
                currency12
            } else {
                currency22
            }

            var fromCurrency = currency12
            var toCurrency = currency22

            if (converterReplace) {
                fromCurrency = currency22
                toCurrency = currency12
            }

            sendRequest(courseValue, courseCurrency, input, fromCurrency, toCurrency)

        }

        send_button.setOnLongClickListener {
            val intent = Intent(this, EndpointActivity::class.java)
            startActivity(intent)

            true
        }
        current_endpoint.setOnClickListener {
            val intent = Intent(this, EndpointActivity :: class.java)
            startActivity(intent)
        }


    }
}