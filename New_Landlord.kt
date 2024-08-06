package com.example.homify

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

// Class to add a new landlord on the server
class New_Landlord : Fragment() {

    //Variable declaration
    private lateinit var fname: EditText
    private lateinit var lname: EditText
    private lateinit var address: EditText
    private lateinit var pcode: EditText
    private lateinit var town: EditText
    private lateinit var mobile: EditText
    private lateinit var email: EditText

    private lateinit var submit: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new__landlord, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializing all the declared variables
        fname = view.findViewById(R.id.edtFirstName)
        lname = view.findViewById(R.id.edtLastName)
        address = view.findViewById(R.id.edtAddress)
        pcode = view.findViewById(R.id.edtPostCode)
        town = view.findViewById(R.id.edtTown)
        mobile = view.findViewById(R.id.edtMobile)
        email = view.findViewById(R.id.edtEmail)

        submit = view.findViewById(R.id.btnAddLandlord)

        submit.setOnClickListener{
            //calling the new landlord function
            addNewLandlord()
        }
    }

    //function to add new landlord to the database
    private fun addNewLandlord() {

        // Initializing variables
        val firstname = fname.text.toString()
        val lastname = lname.text.toString()
        val newAddress = address.text.toString()
        val postcode = pcode.text.toString()
        val newTown = town.text.toString()
        val newMobile = mobile.text.toString()
        val newEmail = email.text.toString()

        val url = getString(R.string.url)  + "new_landlord.php"

        // Checks if all fields are properly filled
        if (firstname.isNotEmpty() || lastname.isNotEmpty() || newEmail.isNotEmpty()
            || newAddress.isNotEmpty() || postcode.isNotEmpty() || newTown.isNotEmpty()
            || newMobile.isNotEmpty() //checking for empty field(s)
        ) {
            val queue = Volley.newRequestQueue(requireContext())
            val stringRequest = object : StringRequest(Method.POST, url,
                Response.Listener<String> { response ->
                    Log.d("MESSAGE","Response:{$response}")
                    if (response.equals("success")) {
                        //display message on screen if successful
                        Toast.makeText(
                            requireContext(), "Registration Successful",
                            Toast.LENGTH_LONG
                        ).show()

                        //clear all inputs
                        emptyInputFields()

                    } else if (response.equals("fail")) {
                        //display message on screen if unsuccessful
                        Toast.makeText(
                            requireContext(), "Registration Failed",
                            Toast.LENGTH_LONG
                        ).show()

                    } else if (response.equals("Already Exist")) {
                        //display message on screen if user already exist
                        Toast.makeText(
                            requireContext(), "User Already Exist",
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
                    params["firstname"] = firstname
                    params["lastname"] = lastname
                    params["addressline1"] = newAddress
                    params["addressline2"] = ""
                    params["postcode"] = postcode
                    params["town"] = newTown
                    params["mobilenumber"] = newMobile
                    params["emailaddress"] = newEmail
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
    private fun emptyInputFields(){
        fname.text.clear()
        lname.text.clear()
        address.text.clear()
        pcode.text.clear()
        town.text.clear()
        mobile.text.clear()
        email.text.clear()
    }
}