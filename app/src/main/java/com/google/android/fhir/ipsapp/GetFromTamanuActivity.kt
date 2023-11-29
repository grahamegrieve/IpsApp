package com.google.android.fhir.ipsapp

import android.R.attr.duration
import android.R.attr.text
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.fhir.library.dataClasses.IPSDocument
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import org.hl7.fhir.r4.formats.JsonParser
import org.hl7.fhir.r4.model.Bundle as FhirBundle
import okhttp3.RequestBody.Companion.toRequestBody
class GetFromTamanuActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.fetchips)

    val cancelButton = findViewById<Button>(R.id.cancelButton)
    cancelButton.setOnClickListener {
      val intent = Intent(this, MainActivity::class.java)
      startActivity(intent)
      finish()
    }
    val fetchButton = findViewById<Button>(R.id.btnFetch)
    fetchButton.setOnClickListener {
      val edtNHI = findViewById<EditText>(R.id.edtNHI)
      getToken(edtNHI.text.toString());
    }
  }

  private fun getToken(nhi : String) {
    try {
      val client = OkHttpClient()
      val request = Request.Builder()
        .post("{\"email\":\"admin@tamanu.io\",\"password\":\"XXXXXXXXXX\"}".toRequestBody("application/json".toMediaTypeOrNull()))
        .url("https://central.ips.internal.tamanu.io/v1/login")
        .addHeader("X-Tamanu-Client", "mSupply")
        .addHeader("X-Version", "0.0.1")
        .addHeader("Accept", "application/json")
        .build()

      client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
          e.printStackTrace();
          alert(e.message);
        }

        override fun onResponse(call: Call, response: Response) {
          if (response.isSuccessful) {
            val responseBody = response.body
            if (responseBody != null) {
              var j = responseBody.string();
              try {
                var obj = com.google.gson.JsonParser.parseString(j);
                fetchIPS(nhi, obj.asJsonObject.get("token").asString);
              } catch (e : Exception) {
                alert("Error reading Token Response: "+e.message);
              }
            } else {
              alert("Response contained no content")
            }
          } else {
            alert(response.message+" ("+response.code+")");
          }
        }
      })
    } catch (e : Throwable) {
      e.printStackTrace()
    }

  }
  private fun fetchIPS(nhi : String, token : String) {
    val url = "https://central.ips.internal.tamanu.io/v1/integration/fhir/mat/Patient/0f93970a-a1d7-44a6-88d2-645d3e79b0e1/\$summary";
//    val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1laWRlbnRpZmllciI6IjMwMyIsImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkvY2xhaW1zL25hbWUiOiJpcHNkZW1vQG1lZGljYWxlcnQuY28ubnoiLCJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9lbWFpbGFkZHJlc3MiOiJpcHNkZW1vQG1lZGljYWxlcnQuY28ubnoiLCJBc3BOZXQuSWRlbnRpdHkuU2VjdXJpdHlTdGFtcCI6IjRQSUJMVVJaUlpBWUxUSE9aVjRaRkZUN1A0NldRVFZaIiwiaHR0cDovL3NjaGVtYXMubWljcm9zb2Z0LmNvbS93cy8yMDA4LzA2L2lkZW50aXR5L2NsYWltcy9yb2xlIjoiVXNlciIsImh0dHA6Ly93d3cuYXNwbmV0Ym9pbGVycGxhdGUuY29tL2lkZW50aXR5L2NsYWltcy90ZW5hbnRJZCI6IjEwIiwic3ViIjoiMzAzIiwianRpIjoiM2E4OGIxNzQtZDg0OC00MjRmLTg0ZjItMWY3ZDNlNDk3NzA4IiwiaWF0IjoxNzAxMDU2MjE4LCJ0b2tlbl92YWxpZGl0eV9rZXkiOiIyNTgwNGMyNi1mZGVkLTQ3YTItYWQwYS01ZjhmNGNjZmNmZjkiLCJ1c2VyX2lkZW50aWZpZXIiOiIzMDNAMTAiLCJ0b2tlbl90eXBlIjoiMCIsInJlZnJlc2hfdG9rZW5fdmFsaWRpdHlfa2V5IjoiZmVlYTNiZDQtOTI0NS00YTA5LTgyOWEtMTY2NzVhMzZjZDhlIiwibmJmIjoxNzAxMDU2MjE4LCJleHAiOjE3MDEwNTc0MTgsImlzcyI6Ik1lZGljQWxlcnROeiIsImF1ZCI6Ik1lZGljQWxlcnROeiJ9.iuW35qMbEMMlOTs1qVyKjfJblFA7QtEKd4JDiym4dJg";

    try {
      val client = OkHttpClient()
      val request = Request.Builder()
        .url(url)
        .addHeader("Authorization", "Bearer $token") // Add bearer token to Authorization header
        .addHeader("Accept", "application/fhir+json")
        .build()

      client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
          e.printStackTrace();
          alert(e.message);
        }

        override fun onResponse(call: Call, response: Response) {
          if (response.isSuccessful) {
            val responseBody = response.body
            if (responseBody != null) {
              val bytes = responseBody.bytes()
              try {
                val bundle = JsonParser().parse(bytes) as FhirBundle;
                val doc = IPSDocument(bundle);
                val i = Intent()
                i.component = ComponentName(this@GetFromTamanuActivity, GetData::class.java)
                i.putExtra("doc", doc as java.io.Serializable)
                startActivity(i)
              } catch (e : Exception) {
                alert("Error reading IPS: "+e.message);
              }
            } else {
              alert("Response contained no content")
            }
          } else {
            alert(response.message+" ("+response.code+")");
          }
        }
      })
    } catch (e : Throwable) {
      e.printStackTrace()
    }
  }
  private fun showToast(context: Context, message: String) {
    runOnUiThread { Toast.makeText(this@GetFromTamanuActivity, message, Toast.LENGTH_LONG).show() }
  }
  private fun alert(message: String?) {
    showToast(this, "$message")
  }

}
