package com.udacity

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.udacity.NotificationHelper.showDownloadNotification
import com.udacity.databinding.ActivityMainBinding
import kotlin.properties.Delegates

private const val SUCCESSFUL = "Successfull"
private const val FAILED = "Failed"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var loadingButton: LoadingButton

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted != true) {
            Toast.makeText(this, "Permissão de notificação não concedida", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        loadingButton = binding.contetMain.customButton
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))


        loadingButton.setOnClickListener {
            requestNotificationPermission {
                requestPermissionLauncher
            }
            checkRadioButton()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val selectedOptionId = binding.contetMain.radioGroup.checkedRadioButtonId
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val query = DownloadManager.Query().setFilterById(downloadID)
                val cursor = downloadManager.query(query)

                if (cursor != null && cursor.moveToFirst()) {
                    val status =
                        cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                    val fileName = when (selectedOptionId) {
                        R.id.radioButtonGlide -> getString(R.string.glide_radion_button)
                        R.id.radioButtonUdacity -> getString(R.string.load_app_radion_button)
                        R.id.radioButtonRetrofit -> getString(R.string.retrofit_app_radion_button)
                        else -> URL
                    }

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        showDownloadNotification(this@MainActivity, fileName, SUCCESSFUL)
                        loadingButton.downloadComplete()

                    } else {
                        showDownloadNotification(this@MainActivity, fileName, FAILED)
                        loadingButton.downloadComplete()
                    }
                }
                cursor?.close()
            }
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

            loadingButton.setOnLoadingButtonClick()
            download(url)
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


    private fun requestNotificationPermission(onGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ActivityCompat.checkSelfPermission(
                    this, POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    onGranted()
                }

                else -> {
                    requestPermissionLauncher.launch(POST_NOTIFICATIONS)
                }
            }
        } else {
            onGranted()
        }
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL = "https://github.com/bumptech/glide"
        private const val RETROFIT_URL = "https://github.com/square/retrofit"
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
    }
}