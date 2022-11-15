package com.udacity

import android.app.DownloadManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        if (intent?.extras != null) {
            val downloadId = intent.getLongExtra("downloadID", -1)
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
            if (cursor.moveToFirst()) {
                Log.d("Adham", cursor.getColumnIndex(DownloadManager.COLUMN_STATUS).toString())
                file_name.text =
                    when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                        DownloadManager.STATUS_SUCCESSFUL -> "Download Finished Successfully"
                        DownloadManager.STATUS_FAILED -> "Download failed Successfully"
                        DownloadManager.STATUS_PAUSED -> "Download paused Successfully"
                        DownloadManager.STATUS_RUNNING -> "Download running Successfully"
                        DownloadManager.STATUS_PENDING -> "Download pending Successfully"

                        else -> "There is some technical issues"
                    }

            }


        }

    }

}
