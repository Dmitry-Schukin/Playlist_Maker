package com.practicum.playlist_maker.settings.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlist_maker.App
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
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

        viewModel= ViewModelProvider(
            this,
            SettingsViewModel.Companion.getFactory()
        )
            .get(SettingsViewModel::class.java)

        viewModel.observeSettingsState().observe(this){isDarkThemeOrNot->
            (applicationContext as App).switchTheme(isDarkThemeOrNot)

        }
        viewModel.observeShared().observe(this){text->
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, text)
            startActivity(shareIntent)
        }
        viewModel.observeEmail().observe(this){email->
            val sendMessageIntent = Intent(Intent.ACTION_SENDTO)
            sendMessageIntent.data = "mailto:".toUri()
            sendMessageIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            sendMessageIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject))
            sendMessageIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text))
            startActivity(sendMessageIntent)
        }
        viewModel.observeAgreement().observe(this){url->
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        }
        //endregion

        //region Listeners
        bindingSettingsActivity.settingsMaterialToolbar.setNavigationOnClickListener {
            finish()
        }
        bindingSettingsActivity.shareTextView.setOnClickListener {
            viewModel.sharedLink()
        }
        bindingSettingsActivity.supportTextView.setOnClickListener{
            viewModel.sendEmail()
        }
        bindingSettingsActivity.agreementTextView.setOnClickListener {
            viewModel.openAgreement()
        }
        updateSwitch((applicationContext as App).darkTheme)
        bindingSettingsActivity.darkThemeSwitchMaterial.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            viewModel.saveThemeValue(checked)
        }
        //endregion
    }
    fun updateSwitch(darkTheme: Boolean) {
        if(darkTheme) bindingSettingsActivity.darkThemeSwitchMaterial.isChecked=true
        else bindingSettingsActivity.darkThemeSwitchMaterial.isChecked=false
    }
}