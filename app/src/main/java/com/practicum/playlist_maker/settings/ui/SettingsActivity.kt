package com.practicum.playlist_maker.settings.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.practicum.playlist_maker.databinding.ActivitySettingsBinding
import com.practicum.playlist_maker.settings.domain.api.SharingInteractor
import com.practicum.playlist_maker.settings.domain.api.SettingsThemeModeInteractor
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()
    private lateinit var bindingSettingsActivity: ActivitySettingsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingSettingsActivity = ActivitySettingsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(bindingSettingsActivity.root)
        ViewCompat.setOnApplyWindowInsetsListener(bindingSettingsActivity.root) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        //region ViewModel
        viewModel.observeSettingsState().observe(this){isDarkThemeOrNot->
            updateTheme(isDarkThemeOrNot)
        }
        //endregion
        updateSwitch(viewModel.getThemeState())
        //region Listeners
        bindingSettingsActivity.settingsMaterialToolbar.setNavigationOnClickListener {
            finish()
        }
        bindingSettingsActivity.shareTextView.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, viewModel.sharedLink())
            startActivity(shareIntent)
        }
        bindingSettingsActivity.supportTextView.setOnClickListener{
            val emailData = viewModel.sendEmail()
            val sendMessageIntent = Intent(Intent.ACTION_SENDTO)
            sendMessageIntent.data = "mailto:".toUri()
            sendMessageIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.email))
            sendMessageIntent.putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
            sendMessageIntent.putExtra(Intent.EXTRA_TEXT, emailData.emailText)
            startActivity(sendMessageIntent)
        }
        bindingSettingsActivity.agreementTextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, viewModel.openAgreement().toUri())
            startActivity(intent)
        }

        bindingSettingsActivity.darkThemeSwitchMaterial.setOnCheckedChangeListener { switcher, checked ->
            updateTheme(checked)
            viewModel.saveThemeValue(checked)
        }
        //endregion
    }
    fun updateSwitch(darkTheme: Boolean) {
        if(darkTheme) bindingSettingsActivity.darkThemeSwitchMaterial.isChecked=true
        else bindingSettingsActivity.darkThemeSwitchMaterial.isChecked=false
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