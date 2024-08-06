package com.example.homify

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.net.toFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

// Helper class for adding new property (both data and images) to the server
class UploadHelper(private val context: Context, baseUrl: String) {

    // Declare and Initializes a variable of type API service using Retrofit client
    private val apiService: ApiService = RetrofitClient.getClient(baseUrl).create(ApiService::class.java)

    // Function to upload property data and images
    fun uploadProperty(
        uid: String, pt: String, pf: String, l: String, b: String, r: String,
        d: String, a: String, t: String, p: String, imageUris: List<Uri>
    ) {
        // Maps RequestBody for the property data
        val partMap = mutableMapOf<String, RequestBody>()
        partMap["uid"] = createPartFromString(uid)
        partMap["ppt-type"] = createPartFromString(pt)
        partMap["ppt-form"] = createPartFromString(pf)
        partMap["lounge"] = createPartFromString(l)
        partMap["bath"] = createPartFromString(b)
        partMap["rent"] = createPartFromString(r)
        partMap["description"] = createPartFromString(d)
        partMap["address"] = createPartFromString(a)
        partMap["town"] = createPartFromString(t)
        partMap["postcode"] = createPartFromString(p)

        // Creates a list of MultipartBody.Part for the images
        val imageParts = mutableListOf<MultipartBody.Part>()
        for (uri in imageUris) {
            val filePath = handleUri(context, uri)
            if (filePath != null) {
                val file = File(filePath)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("imageFiles[]", file.name, requestFile)
                imageParts.add(body)
            } else {
                Log.e("UPLOAD", "Failed to get file path for URI: $uri")
            }
        }

        // API Service call to upload the data and images
        val call = apiService.uploadProperty(partMap, imageParts)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val responseBody = response.body()?.string()

                        Log.d("UPLOAD", "Success: $responseBody")
                        val jsonObject = JSONObject(responseBody)
                        val status = jsonObject.getString("status")
                        val message = jsonObject.getString("message")
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Log.e("UPLOAD", "Error parsing response: ${e.message}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("UPLOAD", "Failure: $errorBody")
                    Toast.makeText(context, "Upload failed: $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("UPLOAD", "Error: ${t.message}")
            }
        })
    }

    // Function to create a RequestBody from user inputs
    private fun createPartFromString(partString: String): RequestBody {
        return RequestBody.create("multipart/form-data".toMediaTypeOrNull(), partString)
    }

    private fun handleUri(context: Context, uri: Uri): String? {
        context.apply {
            val type = when (contentResolver.getType(uri)) {
                "image/jpeg" -> ".jpeg"
                "image/png" -> ".png"
                "image/jpg" -> ".jpg"
                "image/JPEG" -> ".jpeg"
                "image/PNG" -> ".png"
                "image/jPG" -> ".JPG"

                else -> return null
            }
            val dir = File(cacheDir, "uploaded_images").apply { mkdir() }
            val file = File(dir, "${System.currentTimeMillis()}$type")
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            copyStreamToFile(inputStream, file)
            return file.absolutePath
        }
    }

    private fun copyStreamToFile(inputStream: InputStream, outputFile: File) {
        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }

}
