package com.google.android.fhir.ipsapp


import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private val cameraPermissionRequest = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val openCameraButton = findViewById<Button>(R.id.scanQRButton)
        openCameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                openScanner()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    cameraPermissionRequest
                )
            }
        }

        val generateQRButton = findViewById<Button>(R.id.generateQRButton)
        generateQRButton.setOnClickListener {
            val i = Intent()
            i.component = ComponentName(this@MainActivity, SelectIndividualResources::class.java)
            startActivity(i)
        }
        val getFromAlexButton = findViewById<Button>(R.id.getFromAlexButton)
        getFromAlexButton.setOnClickListener {
            val i = Intent()
            i.component = ComponentName(this@MainActivity, GetAlexDocActivity::class.java)
            startActivity(i)
        }
    }

    // when the button is pressed, go to the QR scanner activity
    private fun openScanner() {
        val i = Intent(this@MainActivity, ScannerActivity::class.java)
        startActivity(i)
    }

    // ask for permission to use the camera
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraPermissionRequest) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openScanner()
            }
        }
    }
}
