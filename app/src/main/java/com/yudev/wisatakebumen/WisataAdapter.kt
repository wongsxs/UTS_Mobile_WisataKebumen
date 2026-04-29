package com.yudev.wisatakebumen

import android.app.AlertDialog
import android.content.Intent
import android.location.Location
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import coil.load

class WisataAdapter(
    private val list: MutableList<Wisata>,
    private val isAdmin: Boolean,
    private val mainActivity: MainActivity
) : RecyclerView.Adapter<WisataAdapter.ViewHolder>() {

    private var filteredList = list.toMutableList()

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val nama: TextView = v.findViewById(R.id.tvNama)
        val deskripsi: TextView = v.findViewById(R.id.tvDeskripsi)
        val img: ImageView = v.findViewById(R.id.imgWisata)
        val jarak: TextView = v.findViewById(R.id.tvJarak)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wisata, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = filteredList[position]

        holder.nama.text = data.nama
        holder.deskripsi.text = data.deskripsi
        holder.img.load(data.image)

        val userLat = mainActivity.userLat
        val userLng = mainActivity.userLng

        if (userLat != 0.0 && userLng != 0.0) {
            val result = FloatArray(1)
            Location.distanceBetween(userLat, userLng, data.lat, data.lng, result)
            val km = result[0] / 1000
            holder.jarak.text = "📍 ${"%.2f".format(km)} km"
        } else {
            holder.jarak.text = "📍 - km"
        }

        holder.itemView.setOnClickListener {
            val context = it.context

            if (isAdmin) {
                showAdminDialog(context, position)
            } else {
                val intent = Intent(context, DetailActivity::class.java)

                // 🔥 kirim object
                intent.putExtra("data", data)

                // 🔥 kirim lokasi user
                intent.putExtra("userLat", mainActivity.userLat)
                intent.putExtra("userLng", mainActivity.userLng)

                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = filteredList.size

    fun filter(q: String) {
        filteredList = if (q.isEmpty()) {
            list.toMutableList()
        } else {
            list.filter {
                it.nama.contains(q, true) || it.deskripsi.contains(q, true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    private fun showAdminDialog(context: android.content.Context, position: Int) {
        val options = arrayOf("✏️ Edit", "🗑️ Hapus")

        AlertDialog.Builder(context)
            .setTitle("Mode Admin")
            .setItems(options) { _, which ->

                when (which) {

                    // ✏️ EDIT
                    0 -> showEditDialog(context, position)

                    // 🗑️ HAPUS
                    1 -> {
                        val realItem = filteredList[position]

                        val db = AppDatabase.getDatabase(context)
                        db.wisataDao().delete(realItem)

                        list.remove(realItem)
                        filter("")

                        Toast.makeText(context, "Data dihapus", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }

    private fun showEditDialog(context: android.content.Context, position: Int) {

        val data = filteredList[position]

        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(32, 16, 32, 16)

        val etNama = EditText(context)
        etNama.hint = "Nama"
        etNama.setText(data.nama)

        val etDeskripsi = EditText(context)
        etDeskripsi.hint = "Deskripsi"
        etDeskripsi.setText(data.deskripsi)

        val etImage = EditText(context)
        etImage.hint = "URL Gambar"
        etImage.setText(data.image)

        val etLat = EditText(context)
        etLat.hint = "Latitude"
        etLat.setText(data.lat.toString())

        val etLng = EditText(context)
        etLng.hint = "Longitude"
        etLng.setText(data.lng.toString())

        layout.addView(etNama)
        layout.addView(etDeskripsi)
        layout.addView(etImage)
        layout.addView(etLat)
        layout.addView(etLng)

        AlertDialog.Builder(context)
            .setTitle("Edit Wisata")
            .setView(layout)
            .setPositiveButton("Simpan") { _, _ ->

                val db = AppDatabase.getDatabase(context)

                val updated = Wisata(
                    id = data.id,
                    nama = etNama.text.toString(),
                    deskripsi = etDeskripsi.text.toString(),
                    image = etImage.text.toString(),
                    lat = etLat.text.toString().toDoubleOrNull() ?: 0.0,
                    lng = etLng.text.toString().toDoubleOrNull() ?: 0.0
                )

                val result = db.wisataDao().update(updated)

                if (result == 0) {
                    Toast.makeText(context, "Update gagal ❌", Toast.LENGTH_SHORT).show()
                } else {
                    val index = list.indexOf(data)
                    if (index != -1) {
                        list[index] = updated
                    }

                    filter("")
                    Toast.makeText(context, "Data diupdate 🔥", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}