package com.idbsoftek.vms.setup.push_notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.MainActivity
import com.idbsoftek.vms.util.PrefUtil

class NotificationService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.from!!)

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {

            Log.d(TAG, "Message data payload: " + remoteMessage.data)

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob()
            } else {
                // Handle message within 10 seconds
                handleNow()
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            if (remoteMessage.notification!!.body != null) {
                Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body!!)
                sendNotification(
                    remoteMessage.notification!!.body!!,
                    remoteMessage.notification!!.title!!
                )
            }

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        val prefUtil = PrefUtil(this)
        Log.e(TAG, "Refreshed token: " + token)

        prefUtil.saveFcmKey(token)
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private fun scheduleJob() {
        // [START dispatch_job]
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .build()
        WorkManager.getInstance().beginWith(work).enqueue()
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    /**
     * Persist token to third-party servers.
     *
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String, title: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = "ID-Fleetek Channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setColorized(true)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setSound(defaultSoundUri)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "ID-Fleetek",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NotificationID.id, notificationBuilder.build())

        Log.d(TAG, "Shown Notification")
    }

    companion object {

        private val TAG = "MyFirebaseMsgService"
    }


    //    ------------------------------------

    /* private static final String TAG = "NOTIFICATION";
    NotificationHelper notificationHelper;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        Log.e(TAG, "Message data payload: " + remoteMessage.getData().toString());

        notificationHelper = new NotificationHelper(this);

        showNotification(remoteMessage);
    }

    private void showNotification(RemoteMessage remoteMessage) {
        NotificationChannel notificationChannel = null;

        String image = remoteMessage.getData().get("image");
        String title = remoteMessage.getData().get("title");
        String msg = remoteMessage.getData().get("message");
        String action = remoteMessage.getData().get("action");
        String actionDestination = remoteMessage.getData().get("action_destination");


        assert image != null;
        if (image.equals("")) {

            assert action != null;
            if (action.equalsIgnoreCase("activity")) {
                if (Build.VERSION.SDK_INT >= 26) {
                    notificationChannel = notificationHelper.getNotificationChannel(NotificationHelper.NOTIFICATION_WITH_ACTION_CHANNEL,
                            "Claims", NotificationManager.IMPORTANCE_HIGH, "Claim Feeback");
                }

                //SHOW CUSTOM NOTIFICATION
*//*                notificationHelper.showCustomClaimApprovalNotification(title,
                        msg, notificationChannel);*//*
            }
            else {
                if (Build.VERSION.SDK_INT >= 26) {
                    notificationChannel = notificationHelper.getNotificationChannel(NotificationHelper.NOTIFICATION_CHANNEL2,
                            "Notifications", NotificationManager.IMPORTANCE_DEFAULT, "Sangeetha Care Notification");
                }
                notificationHelper.createNotification(title, msg, notificationChannel);
            }

        } else {
            if (Build.VERSION.SDK_INT >= 26) {
                notificationChannel = notificationHelper.getNotificationChannel(NotificationHelper.NOTIFICATION_CHANNEL1,
                        "Cashback Redemption", NotificationManager.IMPORTANCE_HIGH, "Wallet redemption notification");
            }
            notificationHelper.createPictureNotification(title, msg, image, notificationChannel);
        }
    }*/

}

