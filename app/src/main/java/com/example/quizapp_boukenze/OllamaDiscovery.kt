package com.example.quizapp_boukenze

import android.content.Context
import android.net.ConnectivityManager
import kotlinx.coroutines.*
import java.net.InetSocketAddress
import java.net.Socket
import java.net.Inet4Address

object OllamaDiscovery {
    private const val SCAN_TIMEOUT = 200 // ms

    suspend fun findOllamaServer(context: Context): String? = withContext(Dispatchers.IO) {
        val subnet = getSubnet(context) ?: return@withContext null
        
        val jobs = (1..254).map { i ->
            async {
                val testIp = "$subnet.$i"
                if (isPortOpen(testIp, AppConfig.ollamaPort())) testIp else null
            }
        }
        
        jobs.awaitAll().filterNotNull().firstOrNull()
    }

    private fun isPortOpen(ip: String, port: Int): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(ip, port), SCAN_TIMEOUT)
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun getSubnet(context: Context): String? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetwork ?: return null
        val lp = cm.getLinkProperties(activeNetwork) ?: return null
        
        for (address in lp.linkAddresses) {
            val addr = address.address
            if (addr is Inet4Address && !addr.isLoopbackAddress) {
                val ip = addr.hostAddress
                if (ip != null && (ip.startsWith("192.168.") || ip.startsWith("10.") || ip.startsWith("172."))) {
                    return ip.substringBeforeLast(".")
                }
            }
        }
        return null
    }
}
