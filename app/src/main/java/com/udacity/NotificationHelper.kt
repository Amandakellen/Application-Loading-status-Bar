package com.udacity

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {

    private const val CHANNEL_ID = "download_channel"
    private const val NOTIFICATION_ID = 1

    // Função para criar o canal de notificação (necessário para Android 8.0 e superior)
    fun createNotificationChannel(context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Download Notifications"
            val descriptionText = "Notification channel for download status"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Função para exibir a notificação
    fun showDownloadNotification(context: Context) {
        createNotificationChannel(context)

        // Intent para abrir uma Activity específica ao clicar no botão "Check the status"
        val intent = Intent(context, DetailActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construção da notificação
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.dowload_icon_round) // Ícone da notificação
            .setContentTitle("Udacity: Android Kotlin Nanodegree") // Título da notificação
            .setContentText("The Project 3 repository is downloaded") // Mensagem da notificação
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Prioridade
            .setContentIntent(pendingIntent) // Intent para a ação
            .setAutoCancel(true) // A notificação é cancelada ao clicar
            .addAction(
                R.mipmap.dowload_icon_round, // Ícone do botão de ação (substitua pelo ícone desejado)
                "Check the status", // Texto do botão
                pendingIntent // Ação para abrir a Activity ao clicar
            )


        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(NOTIFICATION_ID, builder.build())
        }
    }

}
