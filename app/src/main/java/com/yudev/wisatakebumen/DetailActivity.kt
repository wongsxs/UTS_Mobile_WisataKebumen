package com.yudev.wisatakebumen

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import coil.load

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val data = intent.getParcelableExtra<Wisata>("data")

        val lat = data?.lat ?: 0.0
        val lng = data?.lng ?: 0.0

        val userLat = intent.getDoubleExtra("userLat", 0.0)
        val userLng = intent.getDoubleExtra("userLng", 0.0)

        findViewById<TextView>(R.id.tvNama).text = data?.nama
        findViewById<TextView>(R.id.tvDeskripsi).text = data?.deskripsi
        findViewById<ImageView>(R.id.imgDetail).load(data?.image)

        val tvJarak = findViewById<TextView>(R.id.tvJarak)

        if (userLat != 0.0 && userLng != 0.0) {
            val result = FloatArray(1)
            Location.distanceBetween(userLat, userLng, lat, lng, result)
            tvJarak.text = String.format("📍 %.2f km", result[0] / 1000)
        } else {
            tvJarak.text = "📍 Lokasi tidak tersedia"
        }

        val btnMap = findViewById<Button>(R.id.btnMap)
        val btnNav = findViewById<Button>(R.id.btnNav)

        if (lat == 0.0 || lng == 0.0) {
            btnMap.isEnabled = false
            btnNav.isEnabled = false
            Toast.makeText(this, "Koordinat belum diisi", Toast.LENGTH_SHORT).show()
        }

        btnMap.setOnClickListener {
            val uri = Uri.parse("https://www.google.com/maps?q=$lat,$lng")
            val intent = Intent(Intent.ACTION_VIEW, uri)

            startActivity(intent) // 🔥 TANPA setPackage
        }

        btnNav.setOnClickListener {

            val gmapsIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=$lat,$lng")
            )

            if (gmapsIntent.resolveActivity(packageManager) != null) {
                startActivity(gmapsIntent)
            } else {
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng")
                )
                startActivity(webIntent)
            }
        }
    }
}