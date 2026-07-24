package com.pencatatkeuangan.cid

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.pencatatkeuangan.cid.data.AppDatabase
import com.pencatatkeuangan.cid.data.KategoriEntity
import com.pencatatkeuangan.cid.util.DefaultData
import com.pencatatkeuangan.cid.util.PrefManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class PencatatApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val pref = PrefManager(this)
        AppCompatDelegate.setDefaultNightMode(
            if (pref.darkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        seedKategoriDefault()
    }

    /**
     * Sengaja pakai runBlocking (bukan coroutine async biasa) supaya proses
     * pengisian kategori bawaan ini PASTI selesai sebelum activity manapun
     * dibuka. Datanya kecil (puluhan baris) jadi prosesnya cepat sekali,
     * tidak akan bikin aplikasi terasa lambat saat pertama kali dibuka.
     */
    private fun seedKategoriDefault() = runBlocking(Dispatchers.IO) {
        val db = AppDatabase.getInstance(this@PencatatApp)
        val kategoriSaatIni = db.kategoriDao().getAll()
        if (kategoriSaatIni.isEmpty()) {
            val daftar = DefaultData.DAFTAR_KATEGORI.map { KategoriEntity(nama = it) }
            db.kategoriDao().insertAll(daftar)
        }
    }
}
