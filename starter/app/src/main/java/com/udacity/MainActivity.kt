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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))


        radioGroup.setOnCheckedChangeListener { _, i ->
            if (i == R.id.radio_load_app) {
                URL = UDACITY_URI
            }
            if (i == R.id.radio_glide) {
                URL = GLIDE_URI
            }


            if (i == R.id.radio_retro) {
                URL = RETROFIT_URI
            }


        }

        custom_button.setOnClickListener {
            if (radioGroup.checkedRadioButtonId != -1) {
                download()
                Toast.makeText(this, URL, Toast.LENGTH_SHORT).show()
                radioGroup.clearCheck()
            } else {
                Toast.makeText(this, "Please Check a library to download", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
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

        private const val GLIDE_URI = "https://github.com/bumptech/glide/archive/master.zip"
        private const val UDACITY_URI =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URI = "https://github.com/square/retrofit/archive/master.zip"

        private var URL = UDACITY_URI

        private const val CHANNEL_ID = "channelId"
    }

}
