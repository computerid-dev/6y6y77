package com.pencatatkeuangan.cid.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.pencatatkeuangan.cid.R
import com.pencatatkeuangan.cid.data.AppDatabase
import com.pencatatkeuangan.cid.data.KategoriEntity
import com.pencatatkeuangan.cid.data.TransaksiEntity
import com.pencatatkeuangan.cid.databinding.FragmentPengaturanBinding
import com.pencatatkeuangan.cid.util.PrefManager
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class PengaturanFragment : Fragment() {

    private var _binding: FragmentPengaturanBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase
    private lateinit var pref: PrefManager

    private val backupLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri -> if (uri != null) lakukanBackup(uri) }

    private val restoreLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> if (uri != null) lakukanRestore(uri) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPengaturanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getInstance(requireContext())
        pref = PrefManager(requireContext())

        binding.switchDarkMode.isChecked = pref.darkMode
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            pref.darkMode = isChecked
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        binding.btnBackup.setOnClickListener {
            backupLauncher.launch("backup_pencatat_keuangan.json")
        }
        binding.btnRestore.setOnClickListener {
            restoreLauncher.launch(arrayOf("application/json"))
        }
    }

    private fun lakukanBackup(uri: Uri) {
        lifecycleScope.launch {
            try {
                val transaksiList = db.transaksiDao().getAll()
                val kategoriList = db.kategoriDao().getAll()

                val jsonKategori = JSONArray()
                kategoriList.forEach { k ->
                    val obj = JSONObject()
                    obj.put("nama", k.nama)
                    jsonKategori.put(obj)
                }

                val jsonTransaksi = JSONArray()
                transaksiList.forEach { t ->
                    val obj = JSONObject()
                    obj.put("nama", t.nama)
                    obj.put("harga", t.harga)
                    obj.put("kategori", t.kategori)
                    obj.put("tanggal", t.tanggal)
                    obj.put("jenis", t.jenis)
                    jsonTransaksi.put(obj)
                }

                val root = JSONObject()
                root.put("kategori", jsonKategori)
                root.put("transaksi", jsonTransaksi)

                requireContext().contentResolver.openOutputStream(uri)?.use { output ->
                    OutputStreamWriter(output).use { writer ->
                        writer.write(root.toString(2))
                    }
                }

                Toast.makeText(requireContext(), R.string.pesan_backup_sukses, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), R.string.pesan_backup_gagal, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun lakukanRestore(uri: Uri) {
        lifecycleScope.launch {
            try {
                val teks = StringBuilder()
                requireContext().contentResolver.openInputStream(uri)?.use { input ->
                    BufferedReader(InputStreamReader(input)).use { reader ->
                        var line = reader.readLine()
                        while (line != null) {
                            teks.append(line)
                            line = reader.readLine()
                        }
                    }
                }

                val root = JSONObject(teks.toString())
                val jsonKategori = root.getJSONArray("kategori")
                val jsonTransaksi = root.getJSONArray("transaksi")

                val daftarKategori = mutableListOf<KategoriEntity>()
                for (i in 0 until jsonKategori.length()) {
                    val obj = jsonKategori.getJSONObject(i)
                    daftarKategori.add(KategoriEntity(nama = obj.getString("nama")))
                }

                val daftarTransaksi = mutableListOf<TransaksiEntity>()
                for (i in 0 until jsonTransaksi.length()) {
                    val obj = jsonTransaksi.getJSONObject(i)
                    daftarTransaksi.add(
                        TransaksiEntity(
                            nama = obj.getString("nama"),
                            harga = obj.getDouble("harga"),
                            kategori = obj.getString("kategori"),
                            tanggal = obj.getLong("tanggal"),
                            jenis = obj.getString("jenis")
                        )
                    )
                }

                db.kategoriDao().deleteAll()
                db.transaksiDao().deleteAll()
                db.kategoriDao().insertAll(daftarKategori)
                db.transaksiDao().insertAll(daftarTransaksi)

                Toast.makeText(requireContext(), R.string.pesan_restore_sukses, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), R.string.pesan_restore_gagal, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
