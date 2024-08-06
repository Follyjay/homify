package com.example.homify

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

/*abstract class VolleyMultipartRequest(
    url: String,
    private val listener: Response.Listener<NetworkResponse>,
    errorListener: Response.ErrorListener,
    private val fileUploads: Map<String, File>,
    private val params: Map<String, String>? = null
) : Request<NetworkResponse>(Method.POST, url, errorListener) {

    private val boundary = "apiclient-" + System.currentTimeMillis()
    private val mimeType = "multipart/form-data;boundary=$boundary"
    private val delimiter = "\r\n--$boundary\r\n"

    override fun getHeaders(): MutableMap<String, String> {
        val headers = mutableMapOf<String, String>()
        headers["Content-Type"] = mimeType
        return headers
    }

    @Throws(IOException::class)
    override fun getBody(): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val dataOutputStream = DataOutputStream(byteArrayOutputStream)

        try {
            // Write params
            params?.let { params ->
                for ((key, value) in params) {
                    writeStringPart(dataOutputStream, key, value)
                }
            }

            // Write file data
            fileUploads.forEach { (key, file) ->
                writeFilePart(dataOutputStream, key, file)
            }

            // End boundary
            dataOutputStream.writeBytes(delimiter + boundary + delimiter + "\r\n")
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            dataOutputStream.close()
        }

        return byteArrayOutputStream.toByteArray()
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<NetworkResponse> {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response))
    }

    override fun deliverResponse(response: NetworkResponse) {
        listener.onResponse(response)
    }

    private fun writeStringPart(dataOutputStream: DataOutputStream, key: String, value: String) {
        dataOutputStream.writeBytes(delimiter + boundary + "\r\n")
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"$key\"\r\n")
        dataOutputStream.writeBytes("\r\n")
        dataOutputStream.writeBytes(value + "\r\n")
    }

    @Throws(IOException::class)
    private fun writeFilePart(dataOutputStream: DataOutputStream, key: String, file: File) {
        val fileName = file.name
        dataOutputStream.writeBytes(delimiter + boundary + "\r\n")
        dataOutputStream.writeBytes(
            "Content-Disposition: form-data; name=\"$key\"; filename=\"$fileName\"\r\n"
        )
        dataOutputStream.writeBytes("\r\n")

        val fileInputStream = FileInputStream(file)
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var bytesRead: Int
        while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
            dataOutputStream.write(buffer, 0, bytesRead)
        }
        fileInputStream.close()
        dataOutputStream.writeBytes("\r\n")
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 1024 * 4
    }
}
*/