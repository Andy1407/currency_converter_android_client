package com.example.currency_converter

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity() {
    var rub = "Rub"
    var usd = "Usd"
    var courseReplace = false
    var converterReplace = false

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


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
                usdCource.text = rub
                rubCource.text = usd
            } else {
                usdCource.text = usd
                rubCource.text = rub
            }

            if (converterReplace) {
                usd_convert.text = rub
                rub_converter.text = usd
            } else {
                usd_convert.text = usd
                rub_converter.text = rub
            }

        }

        @SuppressLint("SetTextI18n")
        fun sendRequest(courseValue: String, courseCurrency: String, input: String, from: String, to: String) {
            val queue = Volley.newRequestQueue(this)
            val url = "http://192.168.1.49:8000/"

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


        from_currency.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                Log.d("hello", "Not yet implemented")
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("hello", "Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                rub = p0.toString()
                updateScreen()
            }

        })


        to_currency.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                Log.d("hello", "Not yet implemented")
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("hello", "Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                usd = p0.toString()
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
                "0"
            } else {
                course.text.toString()
            }

            val courseCurrency = if (courseReplace) {
                rub
            } else {
                usd
            }

            var fromCurrency = rub
            var toCurrency = usd

            if (converterReplace) {
                fromCurrency = usd
                toCurrency = rub
            }

            sendRequest(courseValue, courseCurrency, input, fromCurrency, toCurrency)

        }


    }
}