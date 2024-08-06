package com.example.homify

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.homify.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    // Variable declaration
    private lateinit var adapter: ContentAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Hides the action bar for the activity
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize views using binding
        val btnFeedback = binding.sendFeedback
        val btnLogin = binding.userLogin
        val rclContent = binding.rclContents

        // Set up RecyclerView
        rclContent.layoutManager = LinearLayoutManager(this)
        adapter = ContentAdapter(this, mutableListOf())
        rclContent.adapter = adapter

        // Listener event function for login button
        // Opens the Login activity
        btnLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Listener event function for feedback button
        // Opens feedback dialogue
        btnFeedback.setOnClickListener{
            feedbackDialogue()
        }
        fetchProperties()
    }

    // Function to display feedback dialog
    private fun feedbackDialogue() {
        val builder = AlertDialog.Builder(this)
        val dialogLayout = LayoutInflater.from(this).inflate(R.layout.feedback_dialogue, null)
        val firstname: EditText = dialogLayout.findViewById(R.id.feedbackFirstName)
        val lastname: EditText = dialogLayout.findViewById(R.id.feedbackLastName)
        val mobile: EditText = dialogLayout.findViewById(R.id.feedbackMobile)
        val email: EditText = dialogLayout.findViewById(R.id.feedbackEmail)
        val message: EditText = dialogLayout.findViewById(R.id.feedbackMessage)
        val feedbackContact: CheckBox = dialogLayout.findViewById(R.id.feedbackContact)

        var contactValue: String = "no"

        feedbackContact.setOnCheckedChangeListener { _, isChecked ->
            contactValue = if (isChecked) "yes" else "no"
        }

        with(builder) {
            setTitle(getString(R.string.feedback_title))
            setPositiveButton(getString(R.string.send_feedback)) { dialog, _ ->
                val fName = firstname.text.toString()
                val lName = lastname.text.toString()
                val tel = mobile.text.toString()
                val mail = email.text.toString()
                val msg = message.text.toString()
                val cv = contactValue

                // Calls the function to save user feedback on server
                sendFeedback(fName, lName, tel, mail, msg, cv)
                // Closes the dialog
                dialog.dismiss()
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            setView(dialogLayout)
            show()
        }
    }

    // Function to send and store user feedback on the server
    private fun sendFeedback(fName: String, lName: String, tel: String, mail: String, msg: String, cv: String) {

        val url = getString(R.string.url) + "add_feedback.php"

        if (fName.isNotEmpty() && lName.isNotEmpty() && tel.isNotEmpty()
            && mail.isNotEmpty() && msg.isNotEmpty()) {

            val queue = Volley.newRequestQueue(this)
            val stringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener <String> { response ->
                    val status = JSONObject(response).getString("status")

                    if (status == "Success"){
                        Toast.makeText(this, getString(R.string.feedback_successful),
                            Toast.LENGTH_LONG
                        ).show()

                    } else {
                        Toast.makeText(this, "Fail to send feedback !!!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }, Response.ErrorListener {
                    fun onErrorResponse(error: VolleyError?) {
                        if (error != null) {
                            Toast.makeText(this, getString(R.string.try_again),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["firstname"] = fName
                    params["lastname"] = lName
                    params["telephone"] = tel
                    params["email"] = mail
                    params["feedback"] = msg
                    params["areacode"] = ""
                    params["approve"] = cv
                    params["contactoption"] = "email"
                    return params
                }
            }
            queue.add(stringRequest)
        }else{
            Toast.makeText(this, getString(R.string.require_all_fields),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Function to load all properties on server to the users screen
    private fun fetchProperties() {
        val url = getString(R.string.url) + "display_apartment.php"
        val baseUrl = getString(R.string.base) + "uploads/"

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val properties = mutableListOf<ContentClass>()
                try{
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val filesString = jsonObject.getString("file")
                        val filesArray = JSONArray(filesString)
                        val filesList = mutableListOf<String>()
                        for (j in 0 until filesArray.length()) {
                            filesList.add(baseUrl + filesArray.getString(j))
                        }
                        val property = ContentClass(
                            pid = jsonObject.getInt("pid"),
                            uid = jsonObject.getInt("landlord"),
                            cnt_type = jsonObject.getString("ptype"),
                            cnt_form = jsonObject.getString("pform"),
                            cnt_town = jsonObject.getString("town"),
                            cnt_code = jsonObject.getString("postcode"),
                            cnt_address = jsonObject.getString("address"),
                            cnt_bath = jsonObject.getInt("bath"),
                            cnt_bed = jsonObject.getInt("lounge"),
                            cnt_rent = jsonObject.getString("rent"),
                            files = filesList
                        )
                        properties.add(property)
                    }

                    adapter.setData(properties)

                } catch (e: Exception) {
                    Log.e("fetchProperties", "Error parsing JSON response", e)
                    Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("fetchProperties", "Error: ${error.message}", error)
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

}