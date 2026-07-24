package com.pencatatkeuangan.cid.ui.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.pencatatkeuangan.cid.R
import com.pencatatkeuangan.cid.databinding.FragmentOpsiDeveloperBinding

class OpsiDeveloperFragment : Fragment() {

    private var _binding: FragmentOpsiDeveloperBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val URL_GITHUB = "https://github.com/computerid-dev"
        private const val URL_STORE = "https://valora-store.vercel.app"
        private const val URL_WHATSAPP = "https://whatsapp.com/channel/0029VbDsVxHKQuJNCkZNK82S"
        private const val EMAIL_TUJUAN = "nugrohokelyn@gmail.com"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOpsiDeveloperBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Baris kredit tim (tidak bisa di-tap)
        binding.rowOtak.ivIcon.setImageResource(R.drawable.ic_code)
        binding.rowOtak.tvLabel.text = getString(R.string.label_otak_pembuat)
        binding.rowOtak.tvValue.text = getString(R.string.value_otak_pembuat)

        binding.rowDesain.ivIcon.setImageResource(R.drawable.ic_info)
        binding.rowDesain.tvLabel.text = getString(R.string.label_desain)
        binding.rowDesain.tvValue.text = getString(R.string.value_desain)

        binding.rowBuilding.ivIcon.setImageResource(R.drawable.ic_settings)
        binding.rowBuilding.tvLabel.text = getString(R.string.label_building)
        binding.rowBuilding.tvValue.text = getString(R.string.value_building)

        // Baris kontak (bisa di-tap)
        binding.rowEmail.ivIcon.setImageResource(R.drawable.ic_mail)
        binding.rowEmail.tvLabel.text = getString(R.string.label_email)
        binding.rowEmail.tvValue.text = getString(R.string.value_email)
        binding.rowEmail.ivTrailing.visibility = View.VISIBLE
        binding.rowEmail.root.setOnClickListener { bukaEmail() }

        binding.rowGithub.ivIcon.setImageResource(R.drawable.ic_code)
        binding.rowGithub.tvLabel.text = getString(R.string.label_github)
        binding.rowGithub.tvValue.text = getString(R.string.value_github)
        binding.rowGithub.ivTrailing.visibility = View.VISIBLE
        binding.rowGithub.root.setOnClickListener { bukaTautan(URL_GITHUB) }

        binding.rowStore.ivIcon.setImageResource(R.drawable.ic_folder)
        binding.rowStore.tvLabel.text = getString(R.string.label_store)
        binding.rowStore.tvValue.text = getString(R.string.value_store) + " • " + getString(R.string.store_subtitle)
        binding.rowStore.ivTrailing.visibility = View.VISIBLE
        binding.rowStore.root.setOnClickListener { bukaTautan(URL_STORE) }

        binding.rowWhatsapp.ivIcon.setImageResource(R.drawable.ic_info)
        binding.rowWhatsapp.tvLabel.text = getString(R.string.label_whatsapp)
        binding.rowWhatsapp.tvValue.text = getString(R.string.value_whatsapp)
        binding.rowWhatsapp.ivTrailing.visibility = View.VISIBLE
        binding.rowWhatsapp.root.setOnClickListener { bukaTautan(URL_WHATSAPP) }
    }

    private fun bukaTautan(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), R.string.tidak_ada_aplikasi, Toast.LENGTH_SHORT).show()
        }
    }

    private fun bukaEmail() {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$EMAIL_TUJUAN")
            }
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), R.string.tidak_ada_aplikasi, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
