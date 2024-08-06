package com.example.homify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class ViewLandlords : Fragment() {

    // Variable declaration
    private lateinit var rclLandlords: RecyclerView
    private lateinit var adapter: LandlordAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_landlords, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initializes declared variables
        rclLandlords = view.findViewById(R.id.rclLandlords)
        rclLandlords.layoutManager = LinearLayoutManager(context)

        adapter = LandlordAdapter(requireContext(), emptyList())
        rclLandlords.adapter = adapter

        fetchUsers()
    }

    // Function to load all landlords from the server
    private fun fetchUsers() {
        val url = getString(R.string.url) + "table_one.php"

        val queue = Volley.newRequestQueue(context)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("LANDLORD", "Response: $response")
                val landlord = mutableListOf<LandlordClass>()
                for (i in 0 until response.length()) {
                    try {
                        val usersObject = response.getJSONObject(i)
                        val i = usersObject.getString("uid").toInt()
                        val f = usersObject.getString("firstname")
                        val l = usersObject.getString("lastname")
                        val m = usersObject.getString("mobile")
                        val e = usersObject.getString("email")

                        landlord.add(LandlordClass(i, f, l, m, e))

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                adapter.setData(landlord)

            }, { error ->
                Toast.makeText(
                    context, "Error fetching users: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
        queue.add(jsonArrayRequest)

    }
}