package com.example.homify

import android.app.AlertDialog
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
import org.json.JSONArray
import org.json.JSONException

class LandlordAdapter(private val context: Context, private var items: List<LandlordClass>):
    RecyclerView.Adapter<LandlordAdapter.MyViewHolder>() {

    // ViewHolder class to hold the views for each item in the RecyclerView
    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val landlordUid: TextView = itemView.findViewById(R.id.tvLandlordUid)
        val landlordName: TextView = itemView.findViewById(R.id.tvLandlordName)
        val landlordMobile: TextView = itemView.findViewById(R.id.tvLandlordMobile)
        val landlordEmail: TextView = itemView.findViewById(R.id.tvLandlordEmail)

        private val deleteLandlord: TextView = itemView.findViewById(R.id.btnDeleteLandlord)
        private val editLandlord: TextView = itemView.findViewById(R.id.btnEditLandlord)

        init{

            // click listener to handle delete landlord function
            deleteLandlord.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = items[position]
                    deleteLandlord(user.uid.toString())
                }
            }

            // click listener to trigger dialogue for editing and updating landlord's information
            editLandlord.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = items[position]
                    showEditDialog(user.uid.toString())
                }
            }
        }
    }

    // Inflate the item layout and create the ViewHolder instance
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LandlordAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.landlord, parent, false)
        return MyViewHolder(view)
    }

    // Binds data to the view items
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.landlordUid.text = item.uid.toString()
        holder.landlordName.text = item.first_name + " " + item.last_name
        holder.landlordMobile.text = item.mobile
        holder.landlordEmail.text = item.email
    }

    // Returns the number of items
    override fun getItemCount(): Int {
        return items.size
    }

    // Updates the dataset and notify the adapter
    fun setData(items: List<LandlordClass>) {
        this.items = items
        notifyDataSetChanged()
    }

    // Function to delete a landlord from server
    private fun deleteLandlord(id: String){

        val url = context.getString(R.string.url) + "delete.php"

        if (id.isNotEmpty()) {
            val queue = Volley.newRequestQueue(context)
            val stringRequest = object : StringRequest(Method.POST, url,
                Response.Listener <String> { response ->
                    if (response == "success"){
                        Toast.makeText(context, "Record Deleted Successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        // Removing the item from recyclerview and updating it
                        items = items.filter { it.uid != id.toInt() }
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
                    params["uid"] = id

                    return params
                }
            }
            queue.add(stringRequest)
        }
    }

    // Function to display dialog for editing a landlord's information
    private fun showEditDialog(id: String) {
        val builder = AlertDialog.Builder(context)
        val dialogLayout = LayoutInflater.from(context).inflate(R.layout.edit_landlord, null)
        val edtFirst: TextView = dialogLayout.findViewById(R.id.edtFirstName)
        val edtLast: TextView = dialogLayout.findViewById(R.id.edtLastName)
        val edtAddress: EditText = dialogLayout.findViewById(R.id.edtAddress)
        val edtPostCode: EditText = dialogLayout.findViewById(R.id.edtPostCode)
        val edtTown: EditText = dialogLayout.findViewById(R.id.edtTown)
        val edtMobile: EditText = dialogLayout.findViewById(R.id.edtMobile)
        val edtEmail: TextView = dialogLayout.findViewById(R.id.edtEmail)

        val url = context.getString(R.string.url) + "getRecord.php"
        val queue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val result = jsonArray.getJSONObject(i)
                        if (result.getString("uid") == id) {
                            val fName = result.getString("firstname")
                            val lName = result.getString("lastname")
                            val l_address = result.getString("address")
                            val post_code = result.getString("postcode")
                            val l_town = result.getString("town")
                            val l_mobile = result.getString("mobile")
                            val l_email = result.getString("email")

                            edtFirst.text = fName
                            edtLast.text = lName
                            edtAddress.setText(l_address)
                            edtPostCode.setText(post_code)
                            edtTown.setText(l_town)
                            edtMobile.setText(l_mobile)
                            edtEmail.text = l_email

                            with(builder) {
                                setTitle("Edit Record")
                                setPositiveButton("Update") { dialog, _ ->
                                    val addr = edtAddress.text.toString()
                                    val pcode = edtPostCode.text.toString()
                                    val town = edtTown.text.toString()
                                    val mobile = edtMobile.text.toString()
                                    updateLandlord(id, addr, pcode, town, mobile)
                                    dialog.dismiss()
                                }
                                setNegativeButton("Cancel") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                setView(dialogLayout)
                                show()
                            }
                            break
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_LONG).show()
                }
            }, Response.ErrorListener {
                Toast.makeText(context, "Server Time Out", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["uid"] = id
                return params
            }
        }

        queue.add(stringRequest)
    }

    // Function to update the edited landlord's on server
    private fun updateLandlord(id: String, add: String, post: String, town: String, phone: String){

        val url = context.getString(R.string.url) + "update.php"

        if (add.isNotEmpty() && post.isNotEmpty() && town.isNotEmpty()) {
            val queue = Volley.newRequestQueue(context)
            val stringRequest = object : StringRequest(Method.POST, url,
                Response.Listener <String> { response ->
                    if (response == "success"){
                        Toast.makeText(context, "Record Updated Successfully",
                            Toast.LENGTH_LONG
                        ).show()

                    } else {
                        Toast.makeText(context, "Record Failed to Update!!!",
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
                    params["uid"] = id
                    params["addressline1"] = add
                    params["addressline2"] = ""
                    params["postcode"] = post
                    params["town"] = town
                    params["mobilenumber"] = phone
                    return params
                }
            }
            queue.add(stringRequest)
        }
    }
}