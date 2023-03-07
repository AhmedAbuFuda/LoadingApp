package com.abufoda.loadingapp

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context,
                                         fileName: String, status: Boolean) {

    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(applicationContext, NOTIFICATION_ID,
        contentIntent, PendingIntent.FLAG_UPDATE_CURRENT )

    val detailIntent = Intent(applicationContext, DetailActivity::class.java)
    detailIntent.putExtra(DetailActivity.FILENAME_EXTRA, fileName)
    detailIntent.putExtra(DetailActivity.STATUS_EXTRA, status)

    val detailPendingIntent: PendingIntent? = TaskStackBuilder.create(applicationContext).run {
        addNextIntentWithParentStack(detailIntent)
        getPendingIntent(REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    val builder = NotificationCompat.Builder(applicationContext, applicationContext.getString(R.string.notification_channel_id))

        .setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)

        .addAction(R.drawable.ic_baseline_cloud_download_24, applicationContext.getString(R.string.status), detailPendingIntent)

        .setPriority(NotificationCompat.PRIORITY_HIGH)
    notify(NOTIFICATION_ID, builder.build())
}