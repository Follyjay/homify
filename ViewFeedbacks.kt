package com.example.homify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class ViewFeedbacks : Fragment() {

    // Variable declaration
    private lateinit var rclFeedbacks: RecyclerView
    private lateinit var adapter: FeedbackAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_feedbacks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize declared variables
        rclFeedbacks = view.findViewById(R.id.rclFeedbacks)
        rclFeedbacks.layoutManager = LinearLayoutManager(context)

        adapter = FeedbackAdapter(requireContext(), emptyList())
        rclFeedbacks.adapter = adapter

        getFeedbacks()
    }

    // Function to get all users feedback from server
    private fun getFeedbacks() {
        val url = getString(R.string.url) + "feedback_table.php"

        val queue = Volley.newRequestQueue(context)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val feedback = mutableListOf<FeedbackCLass>()
                for (i in 0 until response.length()) {
                    try {
                        val usersObject = response.getJSONObject(i)
                        val i = usersObject.getString("cid").toInt()
                        val n = usersObject.getString("name")
                        val t = usersObject.getString("telephone")
                        val c = usersObject.getString("contact_option")
                        val f = usersObject.getString("feedback")

                        feedback.add(FeedbackCLass(i, n, t, c, f))

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                adapter.setData(feedback)

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