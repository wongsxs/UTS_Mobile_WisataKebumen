package com.yudev.wisatakebumen

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var btnTambah: Button
    private lateinit var tvTitle: TextView
    private lateinit var etSearch: EditText

    private lateinit var db: AppDatabase
    private lateinit var list: MutableList<Wisata>
    private lateinit var adapter: WisataAdapter

    private var clickCount = 0

    var userLat = 0.0
    var userLng = 0.0

    private var isAdmin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv = findViewById(R.id.rvWisata)
        btnTambah = findViewById(R.id.btnTambah)
        tvTitle = findViewById(R.id.tvTitle)
        etSearch = findViewById(R.id.etSearch)

        db = AppDatabase.getDatabase(this)
        list = db.wisataDao().getAll().toMutableList()

        if (list.isEmpty()) {
            val dummy = listOf(
                Wisata(nama="Pantai Menganti", deskripsi="Pantai indah di Kebumen", image="https://cdn.idntimes.com/content-images/community/2019/04/dsc00979-70d4661cecc9fd598e65b0f9b2ba2233.JPG", lat=-7.7700656, lng=109.4128993),
                Wisata(nama="Benteng Van Der Wijck", deskripsi="Wisata sejarah", image="https://image.idntimes.com/post/20230130/whatsapp-image-2023-01-30-at-202923-28598b7784be39d2a0e7a69c686d02c1-9d914481869f2a3edbe08d8e35838865.jpg", lat=-7.5993425, lng=109.5175898),
                Wisata(nama="Goa Jatijajar", deskripsi="Wisata goa terkenal", image="https://th.bing.com/th/id/R.fec22b686de841964776c3fd1c5c0c20?rik=Zj12pez2XiL0hA&riu=http%3a%2f%2ffacebumen.com%2fwp-content%2fuploads%2f2014%2f04%2fgoa-jatijajar-kebumen.jpg&ehk=i49C%2bt4wRSPsHDs5%2fSqC1fG1IfQP9dkufjFqdr8DTlY%3d&risl=&pid=ImgRaw&r=0", lat=-7.6691026, lng=109.4254372),
                Wisata(nama="Pantai Logending", deskripsi="Pantai dekat hutan jati", image="https://tse3.mm.bing.net/th/id/OIP.3gzCDjqltS0Z5dijqgPWQAHaE8?rs=1&pid=ImgDetMain&o=7&rm=3", lat=-7.7276315, lng=109.3940374),
                Wisata(nama="Pantai Suwuk", deskripsi="Pantai luas keluarga", image="https://www.fankymedia.com/wp-content/uploads/2020/07/Pemandangan-Pantai-Suwuk.jpg", lat=-7.7603166, lng=109.4852592),
                Wisata(nama="Pantai Petanahan", deskripsi="Pantai populer", image="https://magelangekspres.disway.id/upload/16a08a6c6ea9891a884b3b121309f43b.jpg", lat=-7.7744012, lng=109.5806612),
                Wisata(nama="Waduk Sempor", deskripsi="View pegunungan", image="https://4.bp.blogspot.com/-6hDaM4n9LSA/VPUkCv1gzpI/AAAAAAAAASU/2o0vrLYSST4/s1600/waduk-sempor3.jpg", lat=-7.5654120, lng=109.4919259),
                Wisata(nama="Goa Petruk", deskripsi="Stalaktit indah", image="https://wisatarakyat.com/wp-content/uploads/2020/10/Goa-petruk-Kebumen-1.jpg", lat=-7.7037608, lng=109.3980795),
                Wisata(nama="Bukit Pentulu", deskripsi="Spot sunrise", image="https://1.bp.blogspot.com/-uUzZ49g8lw0/X2YK6GXv6kI/AAAAAAAAEww/wHPqNurXi-sJGnEKcJf0k2_WpvaK1BBagCLcBGAsYHQ/s16000/Tempat%2BWisata%2BPentulu%2BIndah.jpg", lat=-7.5413014, lng=109.6699335),
                Wisata(nama="Pantai Karang Bolong", deskripsi="Batu bolong ikonik", image="https://tse4.mm.bing.net/th/id/OIP.nwFT5_P7FJUFowYFF6tQBQHaE9?rs=1&pid=ImgDetMain&o=7&rm=3", lat=-7.7583150, lng=109.4679664)
            )

            dummy.forEach { db.wisataDao().insert(it) }
            list.addAll(db.wisataDao().getAll())
        }

        adapter = WisataAdapter(list, false, this@MainActivity)

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        btnTambah.visibility = View.GONE

        // 🔍 SEARCH
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
        })

        tvTitle.setOnClickListener {
            clickCount++
            if (clickCount >= 10) {
                clickCount = 0
                showLogin()
            }
        }

        btnTambah.setOnClickListener {
            startActivityForResult(Intent(this, TambahActivity::class.java), 1)
        }


        getUserLocation()
    }

    private fun getUserLocation() {
        val fused = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        fused.lastLocation.addOnSuccessListener {
            if (it != null) {
                userLat = it.latitude
                userLng = it.longitude
            }
        }
    }

    private fun showLogin() {
        val input = EditText(this)

        AlertDialog.Builder(this)
            .setTitle("Login Admin")
            .setView(input)
            .setPositiveButton("Login") { _, _ ->

                if (input.text.toString() == "admin123") {

                    isAdmin = true // 🔥 TAMBAHAN

                    btnTambah.visibility = View.VISIBLE
                    adapter = WisataAdapter(list, true, this@MainActivity)
                    rv.adapter = adapter

                    Toast.makeText(this, "Admin aktif 🔥", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Password salah ❌", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {

            val wisata = Wisata(
                nama = data?.getStringExtra("nama") ?: "",
                deskripsi = data?.getStringExtra("deskripsi") ?: "",
                image = data?.getStringExtra("image") ?: "",
                lat = data?.getDoubleExtra("lat", 0.0) ?: 0.0,
                lng = data?.getDoubleExtra("lng", 0.0) ?: 0.0
            )

            db.wisataDao().insert(wisata)

            list.clear()
            list.addAll(db.wisataDao().getAll())

            adapter.notifyDataSetChanged()
        }
    }


    override fun onBackPressed() {

        if (isAdmin) {
            isAdmin = false

            btnTambah.visibility = View.GONE
            adapter = WisataAdapter(list, false, this@MainActivity)
            rv.adapter = adapter

            Toast.makeText(this, "Keluar dari mode admin", Toast.LENGTH_SHORT).show()
        } else {
            super.onBackPressed()
        }
    }
}