package com.example.homify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    // Variable Declaration
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var login: Button
    lateinit var errorMsg: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //initializing variables
        login = findViewById(R.id.btnLogin)
        errorMsg = findViewById(R.id.tvError)

        //setting an onClick event function
        login.setOnClickListener{
            loginAction()
        }

    }

    //password authentication and validation
    private fun loginAction(){
        //Initializing variables
        email = findViewById(R.id.edtUsername)
        password = findViewById(R.id.edtPassword)
        errorMsg = findViewById(R.id.tvError)

        val email = email.text.toString()
        val password = password.text.toString()
        val url = getString(R.string.url) + "login.php"

        if( (email.isNotEmpty() && password.isNotEmpty()) ) {

            val queue = Volley.newRequestQueue(this)
            val stringRequest = object : StringRequest(Method.POST, url,
                Response.Listener<String> { response ->
                    Log.d("LOGIN", "Response: $response")
                    try {
                        val status = JSONObject(response).getString("status")
                        val role = JSONObject(response).getString("role")
                        if (status == "Success") {

                            val uid = JSONObject(response).getString("uid")

                            Toast.makeText(
                                this, "Login Successful",
                                Toast.LENGTH_LONG
                            ).show()

                            intent = if (role == "admin") {
                                Intent(this, AdminDashboard::class.java)
                            } else {
                                Intent(this, LandlordDashboard::class.java)
                            }

                            intent.putExtra("uid", uid)
                            startActivity(intent)
                            finish()

                        } else{
                            errorMsg.text = "Username/Password is not correct"
                            errorMsg.visibility = View.VISIBLE
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.d("LOGIN", "Invalid Credentials")
                        Toast.makeText(
                            this, "Invalid Credentials",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }, Response.ErrorListener { error ->
                    errorMsg.text = error.message
                    //println(error.message)
                    errorMsg.visibility = View.VISIBLE
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = email
                    params["password"] = password
                    return params
                }
            }
            queue.add(stringRequest)

        }else{
            errorMsg.text = "Username/Password Cannot be Empty"
            errorMsg.visibility = View.VISIBLE
        }
    }

}