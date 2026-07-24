package com.pencatatkeuangan.cid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pencatatkeuangan.cid.R
import com.pencatatkeuangan.cid.data.JENIS_JUAL
import com.pencatatkeuangan.cid.data.TransaksiEntity
import com.pencatatkeuangan.cid.databinding.ItemTransaksiBinding
import com.pencatatkeuangan.cid.util.Formatter

class TransaksiAdapter(
    private var items: List<TransaksiEntity>,
    private val onHapus: (TransaksiEntity) -> Unit
) : RecyclerView.Adapter<TransaksiAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTransaksiBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransaksiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val ctx = holder.binding.root.context

        holder.binding.tvNama.text = item.nama
        holder.binding.tvKategoriTanggal.text =
            "${item.kategori} • ${Formatter.tanggalTampil(item.tanggal)}"

        if (item.jenis == JENIS_JUAL) {
            holder.binding.tvLabelAksi.text = ctx.getString(R.string.menjual) + ": " +
                "Mendapatkan ${Formatter.rupiah(item.harga)}"
            holder.binding.tvLabelAksi.setTextColor(ctx.getColor(R.color.income_green))
        } else {
            holder.binding.tvLabelAksi.text = ctx.getString(R.string.membeli) + ": " +
                "Pengeluaran ${Formatter.rupiah(item.harga)}"
            holder.binding.tvLabelAksi.setTextColor(ctx.getColor(R.color.expense_red))
        }

        holder.binding.btnHapus.setOnClickListener { onHapus(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<TransaksiEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}
