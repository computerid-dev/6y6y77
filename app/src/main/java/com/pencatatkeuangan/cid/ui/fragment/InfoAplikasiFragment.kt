package com.pencatatkeuangan.cid.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pencatatkeuangan.cid.BuildConfig
import com.pencatatkeuangan.cid.R
import com.pencatatkeuangan.cid.databinding.FragmentInfoAplikasiBinding
import com.pencatatkeuangan.cid.databinding.ItemFeatureBinding

class InfoAplikasiFragment : Fragment() {

    private var _binding: FragmentInfoAplikasiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoAplikasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvVersiValue.text = BuildConfig.VERSION_NAME
        binding.tvPackageValue.text = BuildConfig.APPLICATION_ID
        binding.tvVersiInfo.text = getString(R.string.versi_aplikasi)

        val daftarFitur = listOf(
            R.string.fitur_1, R.string.fitur_2, R.string.fitur_3, R.string.fitur_4,
            R.string.fitur_5, R.string.fitur_6, R.string.fitur_7, R.string.fitur_8,
            R.string.fitur_9, R.string.fitur_10
        )

        binding.containerFitur.removeAllViews()
        daftarFitur.forEach { resId ->
            val itemBinding = ItemFeatureBinding.inflate(
                LayoutInflater.from(requireContext()), binding.containerFitur, false
            )
            itemBinding.tvFeatureText.text = getString(resId)
            binding.containerFitur.addView(itemBinding.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
