package com.practicum.playlist_maker

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_screen)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }
        val backClickEvent = findViewById<MaterialToolbar>(R.id.settings_material_toolbar)
        backClickEvent.setNavigationOnClickListener {
            finish()
        }
        val shareApp = findViewById<MaterialTextView>(R.id.share_text_view)
        shareApp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.url_practicum_course))
            startActivity(shareIntent)
        }

        val sendMessage = findViewById<MaterialTextView>(R.id.support_text_view)
        sendMessage.setOnClickListener{
            val sendMessageIntent = Intent(Intent.ACTION_SENDTO)
            sendMessageIntent.data = "mailto:".toUri()
            sendMessageIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_mail)))
            sendMessageIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject))
            sendMessageIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text))
            startActivity(sendMessageIntent)
        }

        val showAgreement = findViewById<MaterialTextView>(R.id.agreement_text_view)
        showAgreement.setOnClickListener {
            val url = getString(R.string.url_practicum_offer).toUri()
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }

        val nightModeSwitch = findViewById<SwitchMaterial>(R.id.dark_theme_switch_material)
        nightModeSwitch.setOnClickListener{
            if(nightModeSwitch.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}