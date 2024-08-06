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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class ViewNotification : Fragment() {

    // Variable declaration
    private lateinit var rclNotify: RecyclerView
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Getting the ID of the user
        val uid = arguments?.getString("uid")

        // Initialize declared variable
        rclNotify = view.findViewById(R.id.rclNotification)
        rclNotify.layoutManager = LinearLayoutManager(context)

        adapter = NotificationAdapter(requireContext(), emptyList())
        rclNotify.adapter = adapter

        // Calls the function to load notification based on user-id
        getNotifications(uid.toString())
    }

    // Function to load notification from server based on user-id
    private fun getNotifications(id: String) {
        val url = getString(R.string.url) + "get_notifications.php"

        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                val notify = mutableListOf<NotificationClass>()
                try {
                    Log.d("LOAD", "Response: $response")
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val usersObject = jsonArray.getJSONObject(i)
                        val rid = usersObject.getInt("rid")
                        val name = usersObject.getString("name")
                        val email = usersObject.getString("email")
                        val mobile = usersObject.getString("mobile")
                        val msg = usersObject.getString("message") // Assuming you meant 'message' here
                        val pid = usersObject.getInt("pid")
                        val uid = usersObject.getInt("uid")

                        notify.add(NotificationClass(rid, name, email, mobile, msg, pid, uid))

                    }
                    adapter.setData(notify)

                } catch (e: Exception){
                    e.printStackTrace()
                }
            },
            { error ->
                Log.d("Error", "Response: ${error.message}")
                Toast.makeText(context, "Error fetching users: ${error.message}", Toast.LENGTH_LONG)
                    .show()
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