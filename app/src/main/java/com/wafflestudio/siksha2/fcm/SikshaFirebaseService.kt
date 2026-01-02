package com.wafflestudio.siksha2.fcm

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.preferences.SikshaPrefObjects
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SikshaFirebaseService : FirebaseMessagingService() {

    @Inject lateinit var prefs: SikshaPrefObjects

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New FCM token generated: $token")
        prefs.fcmToken.setValue(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "notification=${remoteMessage.notification}")
        Log.d("FCM", "data=${remoteMessage.data}")

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "식샤"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: ""

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager

        val builder = NotificationCompat.Builder(this, "siksha_channel")
            // .setSmallIcon(R.drawable.siksha_rice_bowl)
            .setSmallIcon(R.drawable.ic_notification)
            // .setLargeIcon(createNotificationLargeIcon())
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)

        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            builder.build()
        )
    }

    private fun createNotificationLargeIcon(): Bitmap {
        val size = 39
        val radius = 8.5f

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paintBg = Paint().apply {
            color = resources.getColor(R.color.orange_500, null)
            isAntiAlias = true
        }

        val rect = RectF(0f, 0f, size.toFloat(), size.toFloat())
        canvas.drawRoundRect(rect, radius, radius, paintBg)

        val riceBmp = BitmapFactory.decodeResource(resources, R.drawable.siksha_rice_bowl)

        val iconSize = (size * 0.55).toInt()
        val left = (size - iconSize) / 2
        val top = (size - iconSize) / 2
        val resized = Bitmap.createScaledBitmap(riceBmp, iconSize, iconSize, true)

        canvas.drawBitmap(resized, left.toFloat(), top.toFloat(), null)

        return bitmap
    }
}
