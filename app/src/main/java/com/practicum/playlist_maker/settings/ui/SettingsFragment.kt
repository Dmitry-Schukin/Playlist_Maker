package com.practicum.playlist_maker.settings.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.practicum.playlist_maker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment: Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //region ViewModel
        viewModel.observeSettingsState().observe(viewLifecycleOwner){isDarkThemeOrNot->
            updateTheme(isDarkThemeOrNot)
        }
        //endregion

        updateSwitch(viewModel.getThemeState())

        //region Listeners
        binding.shareTextView.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, viewModel.sharedLink())
            startActivity(shareIntent)
        }
        binding.supportTextView.setOnClickListener{
            val emailData = viewModel.sendEmail()
            val sendMessageIntent = Intent(Intent.ACTION_SENDTO)
            sendMessageIntent.data = "mailto:".toUri()
            sendMessageIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.email))
            sendMessageIntent.putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
            sendMessageIntent.putExtra(Intent.EXTRA_TEXT, emailData.emailText)
            startActivity(sendMessageIntent)
        }
        binding.agreementTextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, viewModel.openAgreement().toUri())
            startActivity(intent)
        }

        binding.darkThemeSwitchMaterial.setOnCheckedChangeListener { switcher, checked ->
            updateTheme(checked)
            viewModel.saveThemeValue(checked)
        }
        //endregion

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun updateSwitch(darkTheme: Boolean) {
        if(darkTheme) binding.darkThemeSwitchMaterial.isChecked=true
        else binding.darkThemeSwitchMaterial.isChecked=false
    }
    fun updateTheme(isDark: Boolean){
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}