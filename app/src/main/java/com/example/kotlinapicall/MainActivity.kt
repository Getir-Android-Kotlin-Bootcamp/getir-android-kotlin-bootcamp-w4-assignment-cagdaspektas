package com.example.kotlinapicall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {
    private val baseUrl="https://espresso-food-delivery-backend-cc3e106e2d34.herokuapp.com"
    private var profileText=""
    private var loginText=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //network arka planda yapılsın demek
        lifecycleScope.launch(Dispatchers.IO) {
            login()
            withContext(Dispatchers.Main) {
                findViewById<TextView>(R.id.login).text = getString(R.string.userid, loginText)
            }
            getProfile(loginText)
            withContext(Dispatchers.Main){
                findViewById<TextView>(R.id.profile).text = profileText

            }
        }



    }





    private  fun login() {
        val url = URL("$baseUrl/login")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")

        val postData = "{\"email\":\"fatih1@gmail.com\",\"password\":\"Gptmap123\"}"
        val postDataBytes = postData.toByteArray(StandardCharsets.UTF_8)

        connection.doOutput = true
        connection.outputStream.use { outputStream ->
            outputStream.write(postDataBytes)
        }

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val response = StringBuilder()

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            loginText= response.toString()


        } else {
            loginText= "Error: Unable to fetch data from the API. Response code: $responseCode"

        }

        connection.disconnect()
    }
    private fun getProfile(userId:String){
        val url = URL("$baseUrl/profile/$userId")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val response = StringBuilder()

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            profileText= "Profile datasına ulaşıldı  $responseCode "


        } else {
            profileText= "Error: Unable to fetch data from the API. Response code: $responseCode"

        }

        connection.disconnect()
    }
}