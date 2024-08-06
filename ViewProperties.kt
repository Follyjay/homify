package com.example.homify

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class ViewProperties : Fragment() {

    //Variable declaration
    private lateinit var rclProperties: RecyclerView
    private lateinit var adapter: PropertyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_properties, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //fetching the user-id of the current user
        val uid = arguments?.getString("uid")

        rclProperties = view.findViewById(R.id.rclProperties)
        rclProperties.layoutManager = LinearLayoutManager(context)

        adapter = PropertyAdapter(requireContext(), mutableListOf())
        rclProperties.adapter = adapter

        //calling the fetchProperties function
        fetchProperties(uid.toString())
    }

    //function to fetch the properties advertised by the current user
    private fun fetchProperties(id: String) {
        val url = getString(R.string.url) + "get_properties.php"

        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                val ppty = mutableListOf<PropertyClass>()
                Log.d("TAG", "$response")
                try{
                    if (response == "empty") {
                        Toast.makeText(context, "No Property to view/n Add a new property",
                            Toast.LENGTH_SHORT).show()

                    }else {
                        val jsonArray = JSONArray(response)
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)

                            val property = PropertyClass(
                                pid = jsonObject.getInt("pid"),
                                cnt_type = jsonObject.getString("ptype"),
                                cnt_form = jsonObject.getString("pform"),
                                cnt_town = jsonObject.getString("town"),
                                cnt_code = jsonObject.getString("postcode"),
                                cnt_address = jsonObject.getString("address"),
                                cnt_bath = jsonObject.getInt("bath"),
                                cnt_bed = jsonObject.getInt("lounge"),
                                cnt_rent = jsonObject.getString("rent")
                            )
                            ppty.add(property)
                        }

                        adapter.setData(ppty)

                    }

                } catch (e: Exception) {
                    Log.e("fetchProperties", "Error parsing JSON response", e)
                    Toast.makeText(context, "Error parsing response", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("fetchProperties", "Error: ${error.message}", error)
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ){
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["uid"] = id
                return params
            }
        }

        queue.add(stringRequest)
    }
}