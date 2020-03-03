package com.idbsoftek.vms.setup.push_notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.dashboard.VMSDashboardActivity
import java.net.HttpURLConnection
import java.net.URL

class NotificationHelper(private val context: Context) {
    private val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun getNotificationChannel(channelID: String, channelName: String,
                               importance: Int, description: String): NotificationChannel? {
        return if (Build.VERSION.SDK_INT >= 26) {
            val notificationChannel = NotificationChannel(channelID,
                channelName, importance)
            notificationChannel.description = description

            notificationChannel
        } else
            null
    }

    private fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)

        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            null
        }

    }

    fun createNotification(title: String, msg: String,
                           notificationChannel: NotificationChannel) {
        val builder: NotificationCompat.Builder
        if (Build.VERSION.SDK_INT >= 26) {
            builder = NotificationCompat.Builder(context, notificationChannel.id)
        } else
            builder = NotificationCompat.Builder(context)

        val resultIntent = Intent(context, VMSDashboardActivity::class.java)
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        val stackBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            TaskStackBuilder.create(context)
        } else {
            TODO("VERSION.SDK_INT < JELLY_BEAN")
        }
        stackBuilder.addNextIntentWithParentStack(resultIntent)
        // Get the PendingIntent containing the entire back stack
        val resultPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            TODO("VERSION.SDK_INT < JELLY_BEAN")
        }

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(msg))

                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.priority = Notification.PRIORITY_DEFAULT
        }

        if (Build.VERSION.SDK_INT >= 26) {
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(1, builder.build())
    }

    fun createPictureNotification(title: String, msg: String, imageURL: String,
                                  notificationChannel: NotificationChannel) {

        val builder: NotificationCompat.Builder = if (Build.VERSION.SDK_INT >= 26) {
            NotificationCompat.Builder(context, notificationChannel.id)
        } else
            NotificationCompat.Builder(context)

        val picture = getBitmapFromUrl(imageURL)
        val largeExpandedAvatar = getBitmapFromUrl(imageURL)

        val style = NotificationCompat.BigPictureStyle(builder)

        style.bigPicture(picture)
                .bigLargeIcon(largeExpandedAvatar)

        // Create an Intent for the activity you want to start
        val resultIntent = Intent(context, VMSDashboardActivity::class.java)
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        val stackBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            TaskStackBuilder.create(context)
        } else {
            TODO("VERSION.SDK_INT < JELLY_BEAN")
        }
        stackBuilder.addNextIntentWithParentStack(resultIntent)
        // Get the PendingIntent containing the entire back stack
        val resultPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            TODO("VERSION.SDK_INT < JELLY_BEAN")
        }

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(style)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.priority = Notification.PRIORITY_MAX
        }

        if (Build.VERSION.SDK_INT >= 26) {
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(NotificationID.id, builder.build())
    }

    companion object {
        val NOTIFICATION_CHANNEL1 = "BANNER_NOTIFICATION"
        val NOTIFICATION_CHANNEL2 = "TEXT_NOTIFICATION"
        val NOTIFICATION_WITH_ACTION_CHANNEL = "ACTION_NOTIFICATION"
    }
}

