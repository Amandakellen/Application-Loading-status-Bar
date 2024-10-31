package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.databinding.ActivityMainBinding
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var loadingButton: LoadingButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        loadingButton = binding.contetMain.customButton
        setContentView(binding.root)
        onSelectedRadionButton()
        setSupportActionBar(binding.toolbar)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))


        loadingButton.setOnClickListener {
            checkRadioButton()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun checkRadioButton() {
        val selectedOptionId = binding.contetMain.radioGroup.checkedRadioButtonId
        if (selectedOptionId == -1) {
            Toast.makeText(this, this.getString(R.string.empty_selected), Toast.LENGTH_SHORT)
                .show()
        } else {
            val url = when (selectedOptionId) {
                R.id.radioButtonGlide -> GLIDE_URL
                R.id.radioButtonUdacity -> UDACITY_URL
                R.id.radioButtonRetrofit -> RETROFIT_URL
                else -> URL
            }
            Toast.makeText(this, "deu bom eim", Toast.LENGTH_SHORT).show()
            loadingButton.setOnLoadingButtonClick()
            download(url)
        }
    }

    private fun onSelectedRadionButton() {
        binding.contetMain.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = findViewById<RadioButton>(checkedId)
            val selectedText = selectedRadioButton?.text.toString()

            Toast.makeText(this, "Opção selecionada: $selectedText", Toast.LENGTH_SHORT).show()

        }
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL = "https://github.com/bumptech/glide"
        private const val RETROFIT_URL = "https://github.com/square/retrofit"
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val CHANNEL_ID = "channelId"
    }
}