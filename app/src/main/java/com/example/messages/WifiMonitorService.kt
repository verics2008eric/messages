package com.example.messages

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.util.Log

class WifiMonitorService : Service() {

    private lateinit var wifiReceiver: WifiReceiver

    override fun onCreate() {
        super.onCreate()

        // Create notification channel for foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "wifi_monitor_channel",
                "WiFi Monitor",
                NotificationManager.IMPORTANCE_LOW
            )
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }

        val notification: Notification = Notification.Builder(this, "wifi_monitor_channel")
            .setContentTitle("WiFi Monitor Running")
            .setContentText("Monitoring WiFi state changes")
            .setSmallIcon(android.R.drawable.stat_sys_wifi)
            .build()

        startForeground(1, notification)

        // Register the receiver for both connect and disconnect events
        wifiReceiver = WifiReceiver()
        val filter = IntentFilter().apply {
            addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        }
        registerReceiver(wifiReceiver, filter)

        Log.d("WifiMonitorService", "Service created and receiver registered")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(wifiReceiver)
        Log.d("WifiMonitorService", "Receiver unregistered, service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
