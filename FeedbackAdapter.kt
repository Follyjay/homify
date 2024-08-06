package com.example.homify

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

// Adapter class that handle feedback list and all associated functions
class FeedbackAdapter (private val context: Context, private var items: List<FeedbackCLass>):
    RecyclerView.Adapter<FeedbackAdapter.MyViewHolder>() {

    // ViewHolder class to hold the views for each item in the RecyclerView
    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val feedbackCid: TextView = itemView.findViewById(R.id.tvFeedbackCid)
        val feedbackName: TextView = itemView.findViewById(R.id.tvFeedbackName)
        val feedbackMobile: TextView = itemView.findViewById(R.id.tvFeedbackMobile)
        val feedbackMessage: TextView = itemView.findViewById(R.id.tvFeedbackMessage)

        private val deleteFeedback: TextView = itemView.findViewById(R.id.btnDeleteFeedback)

        init{

            // Listener function for delete feedback button
            deleteFeedback.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = items[position]
                    deleteMessage(user.cid.toString())
                }
            }

        }
    }

    // Inflate the item layout and create the ViewHolder instance
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feedback, parent, false)
        return MyViewHolder(view)
    }

    // Bind data to the view items
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.feedbackCid.text = item.cid.toString()
        holder.feedbackName.text = item.uname
        holder.feedbackMobile.text = item.telephone
        holder.feedbackMessage.text = item.message
    }

    // Returns the total number of items
    override fun getItemCount(): Int {
        return items.size
    }

    // Updates the dataset and notify the adapter
    fun setData(items: List<FeedbackCLass>) {
        this.items = items
        notifyDataSetChanged()
    }

    // Function to delete feedback
    private fun deleteMessage(cid: String){

        val url = context.getString(R.string.url) + "deleteFeedback.php"

        if (cid.isNotEmpty()) {
            val queue = Volley.newRequestQueue(context)
            val stringRequest = object : StringRequest(Method.POST, url,
                Response.Listener <String> { response ->
                    val result  = JSONObject(response).getString("status")
                    if (result == "Success"){
                        Toast.makeText(context, "Record Deleted Successfully",
                            Toast.LENGTH_LONG
                        ).show()

                        // Removing the review from recyclerview and updating it
                        items = items.filter { it.cid != cid.toInt() }
                        notifyDataSetChanged()

                    } else {
                        Toast.makeText(context, "Record Failed to Delete!!!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }, Response.ErrorListener {
                    fun onErrorResponse(error: VolleyError?) {
                        if (error != null) {
                            Toast.makeText(context, "Please Try Again !!!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["cid"] = cid

                    return params
                }
            }
            queue.add(stringRequest)
        }
    }
}