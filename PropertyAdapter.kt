package com.example.homify

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

// Adapter class for displaying property items in a RecyclerView
class PropertyAdapter(private val context: Context, private var items: MutableList<PropertyClass>) :
    RecyclerView.Adapter<PropertyAdapter.MyViewHolder>() {

    private lateinit var propertyTypes: Array<String>
    private lateinit var propertyForms: Array<String>
    private lateinit var selectedType: String
    private lateinit var selectedForm: String

    // ViewHolder class to hold the view for each property item
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val propertyID: TextView = itemView.findViewById(R.id.tvPropertyID)
        val propertyType: TextView = itemView.findViewById(R.id.tvPropertyType)
        val propertyLocation: TextView = itemView.findViewById(R.id.tvPropertyLocation)
        val propertyRent: TextView = itemView.findViewById(R.id.tvPropertyRent)

        private val btnDeleteProperty: TextView = itemView.findViewById(R.id.btnDeleteProperty)
        private val btnEditProperty: TextView = itemView.findViewById(R.id.btnEditProperty)

        init{
            // Set click listener for delete button
            btnDeleteProperty.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = items[position]
                    deleteProperty(user.pid.toString())
                }
            }
            // Set click listener for edit button
            btnEditProperty.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = items[position]
                    showEditPropertyDialogue(user.pid.toString())
                }
            }
        }
    }

    // Inflating the view for each of the items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.properties, parent, false)
        return MyViewHolder(view)
    }

    // Binding data to each view item
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.propertyID.text = item.pid.toString()
        holder.propertyType.text = item.cnt_form + " " + item.cnt_type
        holder.propertyLocation.text = item.cnt_code + " " + item.cnt_town
        holder.propertyRent.text = item.cnt_rent
    }

    // Returning the total count of items
    override fun getItemCount(): Int {
        return items.size
    }

    // Updating data in the adapter
    fun setData(newItems: List<PropertyClass>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    // Function to delete a property
    private fun deleteProperty(id: String){

        val url = context.getString(R.string.url) + "deleteProperty.php"

        if (id.isNotEmpty()) {
            val queue = Volley.newRequestQueue(context)
            val stringRequest = object : StringRequest(Method.POST, url,
                Response.Listener <String> { response ->

                    if (response == "success"){
                        Toast.makeText(context, "Record Deleted Successfully",
                            Toast.LENGTH_LONG
                        ).show()

                        val intent = Intent(context, LandlordDashboard::class.java)
                        context.startActivity(intent)
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
                    params["pid"] = id

                    return params
                }
            }
            queue.add(stringRequest)
        }
    }

    // Function to show the dialog for editing a property
    private fun showEditPropertyDialogue(id: String) {
        val builder = AlertDialog.Builder(context)
        val dialogLayout = LayoutInflater.from(context).inflate(R.layout.edit_property_dialogue, null)
        val spnEditType: Spinner = dialogLayout.findViewById(R.id.spnEditType)
        val spnEditForm: Spinner = dialogLayout.findViewById(R.id.spnEditForm)
        val edtEditBath: EditText = dialogLayout.findViewById(R.id.edtEditBath)
        val edtEditLounge: EditText = dialogLayout.findViewById(R.id.edtEditLounge)
        val edtEditRent: EditText = dialogLayout.findViewById(R.id.edtEditRent)
        val edtEditDescription: EditText = dialogLayout.findViewById(R.id.edtEditDescription)
        val edtEditAddress: EditText = dialogLayout.findViewById(R.id.edtEditAddress)
        val edtEditPostcode: EditText = dialogLayout.findViewById(R.id.edtEditPostcode)
        val edtEditTown: EditText = dialogLayout.findViewById(R.id.edtEditTown)

        val url = context.getString(R.string.url) + "get_properties.php"
        val queue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                try {
                    Log.d("TAG", "$response")
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val result = jsonArray.getJSONObject(i)
                        if (result.getString("pid") == id) {
                            val pType = result.getString("ptype")
                            val pForm = result.getString("pform")
                            val bath = result.getString("bath")
                            val lounge = result.getString("lounge")
                            val rent = result.getString("rent")
                            val description = result.getString("description")
                            val address = result.getString("address")
                            val postCode = result.getString("postcode")
                            val town = result.getString("town")

                            propertyForms = arrayOf(pForm, "1 Bedroom", "2 Bedroom", "3 Bedroom", "4 Bedroom")
                            propertyTypes = arrayOf(pType, "Detached", "Semi-detached", "Duplex")

                            val typeAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, propertyTypes)
                            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spnEditType.adapter = typeAdapter

                            val formAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, propertyForms)
                            formAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spnEditForm.adapter = formAdapter

                            spnEditType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                                    selectedType = propertyTypes[position]
                                }

                                override fun onNothingSelected(parent: AdapterView<*>) {}
                            }

                            spnEditForm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                                    selectedForm = propertyForms[position]
                                }

                                override fun onNothingSelected(parent: AdapterView<*>) {}
                            }

                            setSpinnerValue(spnEditType, pType, propertyTypes)
                            setSpinnerValue(spnEditForm, pForm, propertyForms)
                            edtEditBath.setText(bath)
                            edtEditLounge.setText(lounge)
                            edtEditRent.setText(rent)
                            edtEditDescription.setText(description)
                            edtEditAddress.setText(address)
                            edtEditPostcode.setText(postCode)
                            edtEditTown.setText(town)

                            with(builder) {
                                setTitle("Edit Property")
                                setPositiveButton("Update") { dialog, _ ->
                                    val type = selectedType
                                    val form = selectedForm
                                    val bat = edtEditBath.text.toString()
                                    val lge = edtEditLounge.text.toString()
                                    val rnt = edtEditRent.text.toString()
                                    val desc = edtEditDescription.text.toString()
                                    val adr = edtEditAddress.text.toString()
                                    val pcode = edtEditPostcode.text.toString()
                                    val twn = edtEditTown.text.toString()

                                    updateProperty(id, type, form, bat, lge, rnt, desc, adr, pcode, twn)
                                    dialog.dismiss()
                                }
                                setNegativeButton("Cancel") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                setView(dialogLayout)
                                show()
                            }
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
                params["pid"] = id
                return params
            }
        }

        queue.add(stringRequest)
    }

    // Function to set the default value of each spinner item based on server output
    private fun setSpinnerValue(spinner: Spinner, value: String, content: Array<String>) {

        val contentAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, content)
        contentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = contentAdapter

        for (i in 0 until contentAdapter.count) {
            if (contentAdapter.getItem(i).toString() == value) {
                return spinner.setSelection(i)
            }
        }
    }

    // Function to Update a property on the server
    private fun updateProperty(id: String, type: String, form: String, bat: String,
                               lge: String, rnt: String, desc: String, adr: String,
                               pcode: String, twn: String){

        val url = context.getString(R.string.url) + "update_property.php"

        if (type.isNotEmpty() && form.isNotEmpty() && bat.isNotEmpty() &&
            lge.isNotEmpty() && rnt.isNotEmpty() && desc.isNotEmpty() &&
            adr.isNotEmpty() && pcode.isNotEmpty() && twn.isNotEmpty()) {

            val queue = Volley.newRequestQueue(context)
            val stringRequest = object : StringRequest(Method.POST, url,
                Response.Listener <String> { response ->
                    val status = JSONObject(response).getString("status")
                    if (status == "Success"){
                        Toast.makeText(context, context.getString(R.string.update_successful),
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

                    params["ppt-type"] = type
                    params["ppt-form"] = form
                    params["bath"] = bat
                    params["lounge"] = lge
                    params["rent"] = rnt
                    params["description"] = desc
                    params["address"] = adr
                    params["town"] = twn
                    params["postcode"] = pcode
                    params["pid"] = id

                    return params
                }
            }
            queue.add(stringRequest)
        }
    }

}