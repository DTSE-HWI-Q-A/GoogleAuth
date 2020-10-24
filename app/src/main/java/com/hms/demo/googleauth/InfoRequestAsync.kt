package com.hms.demo.googleauth

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class InfoRequestAsync(val accessToken:String) : AsyncTask<Void, Void, JSONObject>() {
    override fun doInBackground(vararg params: Void?): JSONObject? {
        try {
            val url = URL("https://www.googleapis.com/oauth2/v3/userinfo")
            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("Authorization", String.format("Bearer %s", accessToken))
            val userInfo = convertStreamToString(conn.inputStream)
            return JSONObject(userInfo)
        } catch (e: Exception) {
            return null
        }
    }

    override fun onPostExecute(result: JSONObject?) {
        super.onPostExecute(result)
        if (result != null) {
            Log.e("JSON", result.toString())
        }
    }

    fun convertStreamToString(input: InputStream): String {
        val reader = BufferedReader(InputStreamReader(input))
        val sb = StringBuilder()
        var line: String?
        try {
            while (reader.readLine().also { line = it } != null) {
                sb.append(line).append('\n')
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                input.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }
}