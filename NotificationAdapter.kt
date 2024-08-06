package com.example.homify

import android.content.Context
import android.content.Intent
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

class NotificationAdapter (private val context: Context, private var items: List<NotificationClass>):
    RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {

    // ViewHolder class to hold the views for each item in the RecyclerView
    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val notifyID: TextView = itemView.findViewById(R.id.tvNotificationID)
        val notifyName: TextView = itemView.findViewById(R.id.tvNotificationName)
        val notifyEmail: TextView = itemView.findViewById(R.id.tvNotificationEmail)
        val notifyMessage: TextView = itemView.findViewById(R.id.tvNotificationMessage)

        private val btnDeleteNotification: TextView = itemView.findViewById(R.id.btnDeleteNotification)

        init{

            // click listener to trigger delete notification function
            btnDeleteNotification.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = items[position]
                    deleteNotification(user.rid.toString())
                }
            }
        }
    }

    // Inflate the item layout and create the ViewHolder instance
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notifications, parent, false)
        return MyViewHolder(view)
    }

    // Binds data to each view item
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.notifyID.text = item.rid.toString()
        holder.notifyName.text = item.name
        holder.notifyEmail.text = item.email
        holder.notifyMessage.text = item.message
    }

    // Returns total number of view items
    override fun getItemCount(): Int {
        return items.size
    }

    // Updates dataset and notify the adapter
    fun setData(items: List<NotificationClass>) {
        this.items = items
        notifyDataSetChanged()
    }

    // Function to delete notification from server
    private fun deleteNotification(id: String){

        val url = context.getString(R.string.url) + "delete_notification.php"

        if (id.isNotEmpty()) {
            val queue = Volley.newRequestQueue(context)
            val stringRequest = object : StringRequest(Method.POST, url,
                Response.Listener <String> { response ->
                    val result = JSONObject(response).getString("status")
                    if (result == "Success"){
                        Toast.makeText(context, "Record Deleted Successfully",
                            Toast.LENGTH_LONG
                        ).show()

                        // Removing the review from recyclerview and updating it
                        items = items.filter { it.rid != id.toInt() }
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
                    params["rid"] = id
                    return params
                }
            }
            queue.add(stringRequest)
        }
    }
}