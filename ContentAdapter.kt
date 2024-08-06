package com.example.homify

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

class ContentAdapter(private val context: Context, private var items: MutableList<ContentClass>) :
    RecyclerView.Adapter<ContentAdapter.MyViewHolder>() {

    // ViewHolder class to hold the views for each item in the RecyclerView
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val vpgSlider: ViewPager2 = itemView.findViewById(R.id.vpgSlider)
        val contentTitle: TextView = itemView.findViewById(R.id.tvContentTitle)
        val contentType: TextView = itemView.findViewById(R.id.tvContentType)
        val contentCity: TextView = itemView.findViewById(R.id.tvContentCity)
        val contentPostCode: TextView = itemView.findViewById(R.id.tvContentPostCode)
        val contentAddress: TextView = itemView.findViewById(R.id.tvContentAddress)
        val contentBed: TextView = itemView.findViewById(R.id.tvContentBed)
        val contentBath: TextView = itemView.findViewById(R.id.tvContentBath)
        val contentRent: TextView = itemView.findViewById(R.id.tvContentRent)

        private val contentPhone: TextView = itemView.findViewById(R.id.tvContentPhone)
        private val contentMail: TextView = itemView.findViewById(R.id.tvContentMail)

        init{

            // click listener to trigger dialogue for telephone and email
            contentPhone.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = items[position]
                    telephoneDialogue(user.uid.toString())
                }
            }
            contentMail.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = items[position]
                    emailDialog(user.pid.toString())
                }
            }
        }
    }

    // Inflate the item layout and create the ViewHolder instance
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.content, parent, false)
        return MyViewHolder(view)
    }

    // Bind data to the view items
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.contentTitle.text = item.cnt_form
        holder.contentType.text = item.cnt_type
        holder.contentCity.text = item.cnt_town
        holder.contentPostCode.text = item.cnt_code
        holder.contentAddress.text = item.cnt_address
        holder.contentBed.text = item.cnt_bed.toString()
        holder.contentBath.text = item.cnt_bath.toString()
        holder.contentRent.text = "" + item.cnt_rent

        val imageSliderAdapter = SliderAdapter(context, item.files)
        holder.vpgSlider.adapter = imageSliderAdapter
    }

    // Returns the number of items
    override fun getItemCount(): Int {
        return items.size
    }

    // Updates the dataset and notify the adapter
    fun setData(newItems: List<ContentClass>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    // Function to show telephone dialog with landlord's number
    private fun telephoneDialogue(uid: String) {
        val builder = AlertDialog.Builder(context)
        val dialogLayout = LayoutInflater.from(context).inflate(R.layout.telephone_dialogue, null)
        val telephone: TextView = dialogLayout.findViewById(R.id.tvTelephone)

        val url = context.getString(R.string.url) + "getRecord.php"
        val queue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val result = jsonArray.getJSONObject(i)
                        if (result.getString("uid") == uid) {
                            val l_mobile = result.getString("mobile")

                            telephone.setText(l_mobile)

                            with(builder) {
                                setTitle("Call Landlord")

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
                params["uid"] = uid
                return params
            }
        }

        queue.add(stringRequest)
    }

    // Function to show email dialog for sending notification
    private fun emailDialog(pid: String) {

        val builder = AlertDialog.Builder(context)
        val dialogLayout = LayoutInflater.from(context).inflate(R.layout.contact_landlord_dialogue, null)
        val requestFirst: TextView = dialogLayout.findViewById(R.id.requestFirstName)
        val requestLast: TextView = dialogLayout.findViewById(R.id.requestLastName)
        val requestMobile: EditText = dialogLayout.findViewById(R.id.requestMobile)
        val requestEmail: EditText = dialogLayout.findViewById(R.id.requestEmail)
        val requestMessage: EditText = dialogLayout.findViewById(R.id.requestMessage)
        val requestView: CheckBox = dialogLayout.findViewById(R.id.requestView)

        var viewValue: String = "no"

        requestView.setOnCheckedChangeListener { _, isChecked ->
            viewValue = if (isChecked) "yes" else "no"
        }

        with(builder) {
            setTitle(context.getString(R.string.feedback_title))
            setPositiveButton(context.getString(R.string.send_feedback)) { dialog, _ ->
                val fName = requestFirst.text.toString()
                val lName = requestLast.text.toString()
                val tel = requestMobile.text.toString()
                val mail = requestEmail.text.toString()
                val msg = requestMessage.text.toString()
                val vv = viewValue

                sendRequest(fName, lName, tel, mail, msg, pid, vv)
                dialog.dismiss()
            }
            setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            setView(dialogLayout)
            show()
        }
    }

    // Function to send users notifications server
    private fun sendRequest(fName: String, lName: String, tel: String, mail: String, msg: String, pid: String, vv: String) {
        val url = context.getString(R.string.url) + "user_request.php"

        if (fName.isNotEmpty() && lName.isNotEmpty() && tel.isNotEmpty()
            && mail.isNotEmpty() && msg.isNotEmpty()) {

            val queue = Volley.newRequestQueue(context)
            val stringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener <String> { response ->
                    val status = JSONObject(response).getString("status")

                    if (status == "Success"){
                        Toast.makeText(context, context.getString(R.string.request_successful),
                            Toast.LENGTH_LONG
                        ).show()

                    } else {
                        Toast.makeText(context, context.getString(R.string.request_failed),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }, Response.ErrorListener {
                    fun onErrorResponse(error: VolleyError?) {
                        if (error != null) {
                            Toast.makeText(context, context.getString(R.string.try_again),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["fullname"] = "$fName $lName"
                    params["mobile"] = tel
                    params["email"] = mail
                    params["message"] = msg
                    params["pid"] = pid
                    params["view"] = vv
                    return params
                }
            }
            queue.add(stringRequest)
        }else{
            Toast.makeText(context, context.getString(R.string.require_all_fields),
                Toast.LENGTH_LONG
            ).show()
        }
    }

}