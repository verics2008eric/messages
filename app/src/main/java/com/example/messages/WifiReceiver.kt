package com.example.messages

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.telephony.SmsManager
import android.util.Log

class WifiReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = context.getSharedPreferences("wifi_prefs", Context.MODE_PRIVATE)
        val lastConnected = prefs.getBoolean("lastConnected", false)

        val isConnected = isWifiConnected(context)

        Log.d("WifiReceiver", "onReceive: lastConnected=$lastConnected, isConnected=$isConnected, action=${intent.action}")

        if (lastConnected && !isConnected) {
            // Wi-Fi was connected before, now disconnected → send SMS
            sendSms("+911234567890", "WiFi disconnected!", context)
        }

        // Save current state for next time
        prefs.edit().putBoolean("lastConnected", isConnected).apply()
    }

    private fun isWifiConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    private fun sendSms(phoneNumber: String, message: String, context: Context) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Log.d("WifiReceiver", "SMS sent to $phoneNumber")
        } catch (e: Exception) {
            Log.e("WifiReceiver", "Failed to send SMS: ${e.message}")
        }
    }
}
