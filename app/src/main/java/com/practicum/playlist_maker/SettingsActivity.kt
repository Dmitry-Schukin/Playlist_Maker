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

    private lateinit var nightModeSwitch: SwitchMaterial
    private lateinit var backClickEvent: MaterialToolbar
    private lateinit var shareApp: MaterialTextView
    private lateinit var sendMessage: MaterialTextView
    private lateinit var showAgreement: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_screen)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }


        backClickEvent = findViewById<MaterialToolbar>(R.id.settings_material_toolbar)
        shareApp = findViewById<MaterialTextView>(R.id.share_text_view)
        sendMessage = findViewById<MaterialTextView>(R.id.support_text_view)
        showAgreement = findViewById<MaterialTextView>(R.id.agreement_text_view)
        nightModeSwitch = findViewById<SwitchMaterial>(R.id.dark_theme_switch_material)

        backClickEvent.setNavigationOnClickListener {
            finish()
        }
        shareApp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.url_practicum_course))
            startActivity(shareIntent)
        }
        sendMessage.setOnClickListener{
            val sendMessageIntent = Intent(Intent.ACTION_SENDTO)
            sendMessageIntent.data = "mailto:".toUri()
            sendMessageIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_mail)))
            sendMessageIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject))
            sendMessageIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text))
            startActivity(sendMessageIntent)
        }
        showAgreement.setOnClickListener {
            val url = getString(R.string.url_practicum_offer).toUri()
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }
        updateSwitch((applicationContext as App).darkTheme)
        nightModeSwitch.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }

    }
    fun updateSwitch(darkTheme: Boolean) {
        if(darkTheme) nightModeSwitch.isChecked=true
        else nightModeSwitch.isChecked=false
    }
}