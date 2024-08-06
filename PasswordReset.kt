package com.example.homify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class PasswordReset : Fragment() {

    //Variable declaration
    private lateinit var oldPass: EditText
    private lateinit var newPass: EditText
    private lateinit var retypePass: EditText

    private lateinit var btnReset: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password_reset, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //fetching the user-id of the current user
        val uid = arguments?.getString("uid")

        //initializing all the declared variables
        oldPass = view.findViewById(R.id.edtOldPassword)
        newPass = view.findViewById(R.id.edtNewPassword)
        retypePass = view.findViewById(R.id.edtRetypePassword)

        btnReset = view.findViewById(R.id.btnReset)

        btnReset.setOnClickListener{
            //calling the resetPassword function
            resetPassword(uid.toString())
        }
    }

    // Function to reset/update the user's password on server
    private fun resetPassword(uid: String) {
        val old = oldPass.text.toString()
        val new = newPass.text.toString()
        val retype = retypePass.text.toString()

        val url = getString(R.string.url) + "reset_password.php"

        //checking for empty field(s)
        if (old.isNotEmpty() || new.isNotEmpty() || retype.isNotEmpty()) {

            val queue = Volley.newRequestQueue(requireContext())
            val stringRequest = object : StringRequest(Method.POST, url,
                Response.Listener<String> { response ->
                    Log.d("MESSAGE","Response:{$response}")

                    //getting the status and message from the server response
                    val status = JSONObject(response).getString("status")
                    val message = JSONObject(response).getString("message")

                    //condition to check if response returns success
                    if (status.equals("Success")) {
                        //clear all inputs
                        clearInputFields()

                        //display message on screen if response returns success
                        Toast.makeText(
                            requireContext(), "$message",
                            Toast.LENGTH_LONG
                        ).show()

                        // Navigate to MainActivity
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } else if (status.equals("Error")) { //condition to check if response returns error
                        //display message on screen if response returns error
                        Toast.makeText(
                            requireContext(), "$message",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }, Response.ErrorListener { error ->
                    Toast.makeText(
                        requireContext(), "Error fetching users: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }) {
                // function to send parameterized values to the server
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["uid"] = uid
                    params["oldpassword"] = old
                    params["newpassword"] = new
                    params["retypepassword"] = retype

                    return params
                }
            }
            queue.add(stringRequest)

        } else {
            //display message on screen if any field is empty
            Toast.makeText(
                requireContext(), "All Fields Are Required",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    //function to clear all input fields
    private fun clearInputFields(){
        oldPass.setText("")
        newPass.setText("")
        retypePass.setText("")
    }
}