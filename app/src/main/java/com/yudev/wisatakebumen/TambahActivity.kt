package com.yudev.wisatakebumen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class TambahActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah)

        val etNama = findViewById<EditText>(R.id.etNama)
        val etDeskripsi = findViewById<EditText>(R.id.etDeskripsi)
        val etImage = findViewById<EditText>(R.id.etImage)
        val etLat = findViewById<EditText>(R.id.etLat)
        val etLng = findViewById<EditText>(R.id.etLng)

        findViewById<Button>(R.id.btnSimpan).setOnClickListener {

            val intent = Intent()

            intent.putExtra("nama", etNama.text.toString())
            intent.putExtra("deskripsi", etDeskripsi.text.toString())
            intent.putExtra("image", etImage.text.toString())
            intent.putExtra("lat", etLat.text.toString().toDoubleOrNull() ?: 0.0)
            intent.putExtra("lng", etLng.text.toString().toDoubleOrNull() ?: 0.0)

            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}