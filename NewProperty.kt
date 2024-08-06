package com.example.homify

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File

// Class to add new property to server
class NewProperty : Fragment() {

    // Variable declaration
    private lateinit var propertyType: Spinner
    private lateinit var propertyForm: Spinner
    private lateinit var lounge: EditText
    private lateinit var bath: EditText
    private lateinit var rent: EditText
    private lateinit var description: EditText
    private lateinit var address: EditText
    private lateinit var town: EditText
    private lateinit var postcode: EditText

    private lateinit var selectFile: Button
    private lateinit var submit: Button

    private lateinit var selectedType: String
    private lateinit var selectedForm: String

    private val PICK_IMAGES_REQUEST = 1
    private val imageUris: ArrayList<Uri> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_new_property, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize declared variables
        propertyType = view.findViewById(R.id.spnPropertyType)
        propertyForm = view.findViewById(R.id.spnPropertyForm)

        lounge = view.findViewById(R.id.edtLounge)
        bath = view.findViewById(R.id.edtBath)
        description = view.findViewById(R.id.edtDescription)
        rent = view.findViewById(R.id.edtRent)
        address = view.findViewById(R.id.edtAddress)
        postcode = view.findViewById(R.id.edtPostcode)
        town = view.findViewById(R.id.edtTown)

        selectFile = view.findViewById(R.id.btnSelectFiles)
        submit = view.findViewById(R.id.btnAddProperty)

        // Declare and initialize variables of array type
        val propertyForms = arrayOf("1 Bedroom", "2 Bedroom", "3 Bedroom", "4 Bedroom")
        val propertyTypes = arrayOf("Detached", "Semi-detached", "Duplex")

        // Binds the items of declared variables of array type to each spinner adapter
        val typeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, propertyTypes)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        propertyType.adapter = typeAdapter

        val formAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, propertyForms)
        formAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        propertyForm.adapter = formAdapter

        // Select listener function to choose items from the list of items in spinner
        propertyType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedType = propertyTypes[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        propertyForm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedForm = propertyForms[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Listener function to select images from media
        selectFile.setOnClickListener {
            /*if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PICK_IMAGES_REQUEST)
            } else {*/
                imageSelect()
            //}
        }

        // Listener function to initiate the add new property function
        submit.setOnClickListener {
            addProperty()
        }
    }


    // Function to select images
    private fun imageSelect() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGES_REQUEST)
    }

    // FUnction to request permission for access to media files
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PICK_IMAGES_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            imageSelect()
        }
    }

    // Function to save/add each selected image file to an arraylist
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK) {
            //val imageUris = mutableListOf<Uri>()
            if (data?.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    imageUris.add(imageUri)
                }
            } else if (data?.data != null) {
                imageUris.add(data.data!!)
            }
        }
    }


    // Function to add new property kto server
    private fun addProperty() {
        // initialize variables
        val uid = arguments?.getString("uid").toString() // getting the ID of the user/landlord
        val pt = selectedType
        val pf = selectedForm
        val l = lounge.text.toString()
        val b = bath.text.toString()
        val r = rent.text.toString()
        val d = description.text.toString()
        val a = address.text.toString()
        val t = town.text.toString()
        val p = postcode.text.toString()

        // checks if no field is empty
        if (propertyType.isNotEmpty() && propertyForm.isNotEmpty() && b.isNotEmpty() &&
            l.isNotEmpty() && r.isNotEmpty() && d.isNotEmpty() &&
            a.isNotEmpty() && t.isNotEmpty() && p.isNotEmpty()) {

            Log.d("RESPONSE", uid)
            Log.d("RESPONSE", imageUris.toString())
            // checks if number of selected image files is less than six
            if (imageUris.size >= 5) {
                val uploadHelper = UploadHelper(requireContext(), getString(R.string.url))
                uploadHelper.uploadProperty(uid, pt, pf, l, b, r, d, a, t, p, imageUris)

                clearText()
            } else {
                Toast.makeText(requireContext(), "Select at least 6 images", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(
                requireContext(), "All Fields Are Required",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun clearText(){
        lounge.setText("")
        bath.setText("")
        rent.setText("")
        description.setText("")
        address.setText("")
        town.setText("")
        postcode.setText("")
    }

}
