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
import okhttp3.*
import java.io.IOException
import org.hl7.fhir.r4.formats.JsonParser
import org.hl7.fhir.r4.model.Bundle as FhirBundle

class GetAlexDocActivity : AppCompatActivity() {

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
      fetchIPS(edtNHI.text.toString());
    }
  }

  private fun fetchIPS(nhi : String) {

    val url = "https://alexapiuat.medtechglobal.com/FHIR/Patient/\$summary?identifier=https://standards.digital.health.nz/ns/nhi-id|"+nhi;
    val token = AlexTokens.TOKEN
    val facilityId = AlexTokens.FACILITY

    try {
      val client = OkHttpClient()
      val request = Request.Builder()
        .url(url)
        .addHeader("Authorization", "Bearer $token") // Add bearer token to Authorization header
        .addHeader("mt-facilityid", facilityId)
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
                i.component = ComponentName(this@GetAlexDocActivity, GetData::class.java)
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
    runOnUiThread { Toast.makeText(this@GetAlexDocActivity, message, Toast.LENGTH_LONG).show() }
  }
  private fun alert(message: String?) {
    showToast(this, "$message")
  }

}
