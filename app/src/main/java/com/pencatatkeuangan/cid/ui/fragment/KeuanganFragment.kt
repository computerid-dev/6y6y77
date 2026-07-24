package com.pencatatkeuangan.cid.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pencatatkeuangan.cid.R
import com.pencatatkeuangan.cid.adapter.TransaksiAdapter
import com.pencatatkeuangan.cid.data.AppDatabase
import com.pencatatkeuangan.cid.data.JENIS_BELI
import com.pencatatkeuangan.cid.data.JENIS_JUAL
import com.pencatatkeuangan.cid.data.TransaksiEntity
import com.pencatatkeuangan.cid.databinding.FragmentKeuanganBinding
import com.pencatatkeuangan.cid.ui.KategoriActivity
import com.pencatatkeuangan.cid.ui.TambahTransaksiActivity
import com.pencatatkeuangan.cid.util.Formatter
import kotlinx.coroutines.launch

class KeuanganFragment : Fragment() {

    private var _binding: FragmentKeuanganBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase
    private lateinit var adapter: TransaksiAdapter

    private var semuaTransaksi: List<TransaksiEntity> = emptyList()
    private var kataKunci: String = ""
    private var filterJenis: String = "SEMUA"
    private var filterKategori: String = "SEMUA"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKeuanganBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getInstance(requireContext())

        adapter = TransaksiAdapter(emptyList()) { transaksi -> konfirmasiHapus(transaksi) }
        binding.rvKeuangan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvKeuangan.adapter = adapter

        binding.fabTambah.setOnClickListener {
            startActivity(Intent(requireContext(), TambahTransaksiActivity::class.java))
        }
        binding.btnKategoriShortcut.setOnClickListener {
            startActivity(Intent(requireContext(), KategoriActivity::class.java))
        }

        setupChipJenis()
        setupPencarian()
    }

    override fun onResume() {
        super.onResume()
        muatData()
    }

    private fun setupChipJenis() {
        binding.chipGroupJenis.setOnCheckedStateChangeListener { _, checkedIds ->
            filterJenis = when (checkedIds.firstOrNull()) {
                binding.chipJual.id -> JENIS_JUAL
                binding.chipBeli.id -> JENIS_BELI
                else -> "SEMUA"
            }
            terapkanFilter()
        }
    }

    private fun setupSpinnerKategori(daftarKategori: List<String>) {
        val opsi = mutableListOf(getString(R.string.filter_semua))
        opsi.addAll(daftarKategori)
        val ctx = requireContext()
        binding.spinnerKategori.adapter =
            ArrayAdapter(ctx, android.R.layout.simple_spinner_dropdown_item, opsi)
        binding.spinnerKategori.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterKategori = if (position == 0) "SEMUA" else opsi[position]
                terapkanFilter()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupPencarian() {
        binding.etCari.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                kataKunci = s?.toString()?.trim().orEmpty()
                terapkanFilter()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun muatData() {
        lifecycleScope.launch {
            semuaTransaksi = db.transaksiDao().getAll()
            val totalJual = db.transaksiDao().getTotalJual()
            val totalBeli = db.transaksiDao().getTotalBeli()

            if (_binding == null) return@launch
            binding.tvTotalJual.text = Formatter.rupiah(totalJual)
            binding.tvTotalBeli.text = Formatter.rupiah(totalBeli)
            binding.tvSaldo.text = Formatter.rupiah(totalJual - totalBeli)

            val daftarKategori = semuaTransaksi.map { it.kategori }.distinct().sorted()
            setupSpinnerKategori(daftarKategori)

            terapkanFilter()
        }
    }

    private fun terapkanFilter() {
        if (_binding == null) return
        var hasil = semuaTransaksi

        if (filterJenis != "SEMUA") {
            hasil = hasil.filter { it.jenis == filterJenis }
        }
        if (filterKategori != "SEMUA") {
            hasil = hasil.filter { it.kategori == filterKategori }
        }
        if (kataKunci.isNotEmpty()) {
            hasil = hasil.filter { it.nama.contains(kataKunci, ignoreCase = true) }
        }

        adapter.updateData(hasil)

        val totalFilter = hasil.sumOf { item ->
            if (item.jenis == JENIS_JUAL) item.harga else -item.harga
        }
        binding.tvTotalFilter.text = Formatter.rupiah(totalFilter)

        binding.emptyState.visibility = if (hasil.isEmpty()) View.VISIBLE else View.GONE
        binding.rvKeuangan.visibility = if (hasil.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun konfirmasiHapus(transaksi: TransaksiEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.hapus_transaksi_judul)
            .setMessage(R.string.hapus_transaksi_pesan)
            .setPositiveButton(R.string.hapus) { _, _ ->
                lifecycleScope.launch {
                    db.transaksiDao().delete(transaksi)
                    muatData()
                }
            }
            .setNegativeButton(R.string.batal, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
