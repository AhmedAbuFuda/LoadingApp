package com.abufoda.loadingapp

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.abufoda.loadingapp.databinding.ActivityMainBinding
import com.abufoda.loadingapp.databinding.ContentMainBinding

class MainActivity : AppCompatActivity() {
    private var downloadID: Long = 0

    private lateinit var binding: ActivityMainBinding
    private lateinit var contentBinding: ContentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        contentBinding = binding.content
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        contentBinding.customButton.setOnClickListener {
            if(contentBinding.radioButton.checkedRadioButtonId > 0)
                download()
            else
                Toast.makeText(applicationContext, getText(R.string.select_download), Toast.LENGTH_SHORT).show()
        }

        createChannel(getString(R.string.notification_channel_id), getString(R.string.notification_channel_name))
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {

                val query = DownloadManager.Query()
                query.setFilterById(id)
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor = downloadManager.query(query)

                if (cursor.moveToFirst()) {

                    val statusColumn = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)

                    when (cursor.getInt(statusColumn)) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
                            notificationManager.sendNotification(applicationContext.getText(R.string.download_ready).toString(), applicationContext,
                                findViewById<RadioButton>(contentBinding.radioButton.checkedRadioButtonId).text.toString(), true)
                        }
                        DownloadManager.STATUS_FAILED -> {
                            val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
                            notificationManager.sendNotification(applicationContext.getText(R.string.download_ready).toString(), applicationContext,
                                findViewById<RadioButton>(contentBinding.radioButton.checkedRadioButtonId).text.toString(), false)
                        }
                    }
                }
            }
        }
    }

    private fun download() {
        val requestUrl = Uri.parse(
            when(contentBinding.radioButton.checkedRadioButtonId) {
                R.id.radio_glide -> URL_GLIDE
                R.id.radio_nd940 -> URL_ND940
                else -> URL_RETROFIT
            })
        val request =
            DownloadManager.Request(requestUrl)
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)
    }

    companion object {
        private const val URL_ND940 = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
        private const val URL_GLIDE = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val URL_RETROFIT = "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_HIGH)
                .apply {
                    setShowBadge(false)
                }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.download_notification_channel_description)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}


