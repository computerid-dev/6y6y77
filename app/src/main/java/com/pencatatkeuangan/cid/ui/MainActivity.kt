package com.pencatatkeuangan.cid.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.pencatatkeuangan.cid.R
import com.pencatatkeuangan.cid.databinding.ActivityMainBinding
import com.pencatatkeuangan.cid.ui.fragment.InfoAplikasiFragment
import com.pencatatkeuangan.cid.ui.fragment.KeuanganFragment
import com.pencatatkeuangan.cid.ui.fragment.OpsiDeveloperFragment
import com.pencatatkeuangan.cid.ui.fragment.PengaturanFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val fragKeuangan by lazy { KeuanganFragment() }
    private val fragPengaturan by lazy { PengaturanFragment() }
    private val fragDeveloper by lazy { OpsiDeveloperFragment() }
    private val fragInfo by lazy { InfoAplikasiFragment() }
    private var fragmentAktif: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            tampilkanFragment(fragKeuangan)
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_keuangan -> tampilkanFragment(fragKeuangan)
                R.id.nav_pengaturan -> tampilkanFragment(fragPengaturan)
                R.id.nav_developer -> tampilkanFragment(fragDeveloper)
                R.id.nav_info -> tampilkanFragment(fragInfo)
            }
            true
        }
    }

    private fun tampilkanFragment(target: Fragment) {
        if (fragmentAktif === target) return

        val transaction = supportFragmentManager.beginTransaction()

        if (!target.isAdded) {
            transaction.add(R.id.fragmentContainer, target)
        }
        fragmentAktif?.let { transaction.hide(it) }
        transaction.show(target)
        transaction.commit()

        fragmentAktif = target
    }
}
