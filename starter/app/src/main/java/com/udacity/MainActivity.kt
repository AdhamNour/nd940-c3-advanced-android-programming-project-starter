package com.udacity

import android.Manifest
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private val DOWNLOAD_COMPLETE_NOTIFICATION_ID: Int =0
    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createNotificationChannel()

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
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Adham", "Going to download")
                    custom_button.buttonState = ButtonState.Loading
                    download()
                } else {
                    Log.d("Adham", "Requesting")

                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PermissionInfo.PROTECTION_DANGEROUS);
                }

            } else {
                Toast.makeText(this, "Please Check a library to download", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if(id ==downloadID){
                custom_button.buttonState=ButtonState.Completed
                notifyDownloadComplete()
            }
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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "AN Downloader",
                NotificationManager.IMPORTANCE_HIGH).apply {
                setShowBadge(false) }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download complete!"

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    private fun notifyDownloadComplete(){
        notificationManager = ContextCompat.getSystemService(this,NotificationManager::class.java) as NotificationManager

        val intent = Intent(this,DetailActivity::class.java)
        intent.putExtra("downloadID",downloadID)
        pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        } as PendingIntent
        action = NotificationCompat.Action(R.drawable.ic_baseline_cloud_download_24, getString(R.string.notification_button), pendingIntent)
        val contentIntent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
        val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            DOWNLOAD_COMPLETE_NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
            .setContentTitle("Your Download is Completed")
            .setContentText("Your Download from ${URL} is completed successfully.")
            .setContentIntent(contentPendingIntent)
            .addAction(action)
            .setPriority(NotificationCompat.PRIORITY_HIGH).build()
        notificationManager.notify(DOWNLOAD_COMPLETE_NOTIFICATION_ID, notification)


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
