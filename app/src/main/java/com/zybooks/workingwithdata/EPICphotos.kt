package com.zybooks.workingwithdata

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.BuildConfig
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class EPICphotos : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var imageDataSet: ArrayList<ImageData>
    lateinit var imageCustomAdapter: EarthImageCustomAdapter
    lateinit var latitudeEditText: EditText
    lateinit var longitudeEditText: EditText
    lateinit var dateEditText: EditText

    data class ImageData(val url: String, val description: String = "", val date: String = "") {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_epicphotos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.epicPhotos)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val searchButton:Button = findViewById(R.id.searchButton)
        searchButton.setOnClickListener {
            searchEarth()
        }

        val clearButton: Button = findViewById(R.id.clearButton)
        clearButton.setOnClickListener {
            clearEditTextFields()
        }

        imageDataSet = arrayListOf(ImageData("https://apod.nasa.gov/apod/image/1908/EtnaCloudsMoon_Giannobile_960.jpg"),
            ImageData("https://apod.nasa.gov/apod/image/1908/PerseidsPloughCow1024.jpg"),
            ImageData("https://apod.nasa.gov/apod/image/2210/GrbRings_SwiftMiller_960.jpg"))
        imageCustomAdapter = EarthImageCustomAdapter(imageDataSet)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = imageCustomAdapter
    }

    // Create and make request
    // Create a new JsonObjectRequest that requests available subjects
    private fun searchEarth() {
        // Building URL for request.
        // logic here also ensures request is built correctly
        // and to get the correct response format.
        var base_url = "https://api.nasa.gov/planetary/earth/imagery"
        var url = base_url +
                "?thumbs=true&api_key=3oEjtiLDsNQwNVQBcLJGRbwdeVtJHf5HSF6gQ8cA"
        if (latitudeEditText.text.isEmpty()) {
            Toast.makeText(this, "Please enter a latitude", Toast.LENGTH_SHORT).show()
        }
        if (longitudeEditText.text.isEmpty()) {
            Toast.makeText(this,"Please enter a longitude", Toast.LENGTH_SHORT).show()
        }
        else {
            url += "&lon=${longitudeEditText.text}"
            url += "&lat=${latitudeEditText.text}"
            if (dateEditText.text.isNotEmpty()) {
                val date = dateEditText.text.toString()
                url += "&date=$date"
            }
        }

        val queue: RequestQueue = Volley.newRequestQueue(applicationContext)

        val requestObj = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response -> processRequest(response) },
            { error -> Log.d(TAG, "Error: $error") })

        queue.add(requestObj)
    }

    private fun processRequest(response: JSONArray) {
        Log.d(TAG, response.toString())
        for (index in 0 .. response.length() - 1) {
            var jsonObject = response.getJSONObject(index)
            var url = jsonObject.getString("url")
            var explanation = jsonObject.getString("explanation")
            imageDataSet.add(ImageData(url, explanation))
        }
        imageCustomAdapter.notifyDataSetChanged()
    }


    private fun clearEditTextFields() {
        longitudeEditText.text.clear()
        latitudeEditText.text.clear()
        dateEditText.text.clear()
    }
}