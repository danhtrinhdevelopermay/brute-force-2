package com.monitor.cpugpu

import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.annotation.RequiresApi
import java.io.BufferedReader
import java.io.InputStreamReader

@RequiresApi(Build.VERSION_CODES.Q)
class ThermalMonitor(private val context: Context) {
    
    private val powerManager: PowerManager by lazy {
        context.getSystemService(Context.POWER_SERVICE) as PowerManager
    }
    
    private var thermalListener: PowerManager.OnThermalStatusChangedListener? = null
    private var statusCallback: ((Int) -> Unit)? = null
    
    private var lastCpuTotal = 0L
    private var lastCpuIdle = 0L
    private var isFirstRead = true
    
    fun startMonitoring(onStatusChanged: (Int) -> Unit) {
        statusCallback = onStatusChanged
        thermalListener = PowerManager.OnThermalStatusChangedListener { status ->
            statusCallback?.invoke(status)
        }
        thermalListener?.let { powerManager.addThermalStatusListener(it) }
    }
    
    fun stopMonitoring() {
        thermalListener?.let { powerManager.removeThermalStatusListener(it) }
        thermalListener = null
        statusCallback = null
    }
    
    fun getCurrentThermalStatus(): Int {
        return powerManager.currentThermalStatus
    }
    
    fun getThermalHeadroom(forecastSeconds: Int = 10): Float {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            powerManager.getThermalHeadroom(forecastSeconds)
        } else {
            Float.NaN
        }
    }
    
    fun getThermalStatusString(status: Int): String {
        return when (status) {
            PowerManager.THERMAL_STATUS_NONE -> "Normal"
            PowerManager.THERMAL_STATUS_LIGHT -> "Light Throttling"
            PowerManager.THERMAL_STATUS_MODERATE -> "Moderate Throttling"
            PowerManager.THERMAL_STATUS_SEVERE -> "Severe Throttling"
            PowerManager.THERMAL_STATUS_CRITICAL -> "Critical"
            PowerManager.THERMAL_STATUS_EMERGENCY -> "Emergency"
            PowerManager.THERMAL_STATUS_SHUTDOWN -> "Shutdown Warning"
            else -> "Unknown"
        }
    }
    
    fun getCPUTemperature(): Float? {
        return try {
            val zones = listOf(
                "/sys/class/thermal/thermal_zone0/temp",
                "/sys/class/thermal/thermal_zone1/temp",
                "/sys/class/thermal/thermal_zone2/temp"
            )
            
            for (zone in zones) {
                try {
                    val process = Runtime.getRuntime().exec("cat $zone")
                    val reader = BufferedReader(InputStreamReader(process.inputStream))
                    val line = reader.readLine()
                    reader.close()
                    
                    if (line != null && line.isNotEmpty()) {
                        val temp = line.toFloat() / 1000.0f
                        if (temp > 0 && temp < 150) {
                            return temp
                        }
                    }
                } catch (e: Exception) {
                    continue
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }
    
    fun getGPUTemperature(): Float? {
        return try {
            val zones = listOf(
                "/sys/class/thermal/thermal_zone3/temp",
                "/sys/class/thermal/thermal_zone4/temp",
                "/sys/class/thermal/thermal_zone5/temp"
            )
            
            for (zone in zones) {
                try {
                    val process = Runtime.getRuntime().exec("cat $zone")
                    val reader = BufferedReader(InputStreamReader(process.inputStream))
                    val line = reader.readLine()
                    reader.close()
                    
                    if (line != null && line.isNotEmpty()) {
                        val temp = line.toFloat() / 1000.0f
                        if (temp > 0 && temp < 150) {
                            return temp
                        }
                    }
                } catch (e: Exception) {
                    continue
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }
    
    fun getBatteryTemperature(): Float? {
        return try {
            val process = Runtime.getRuntime().exec("cat /sys/class/power_supply/battery/temp")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val line = reader.readLine()
            reader.close()
            
            if (line != null && line.isNotEmpty()) {
                line.toFloat() / 10.0f
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    fun getCPUUsage(): Float {
        return try {
            val process = Runtime.getRuntime().exec("cat /proc/stat")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val line = reader.readLine()
            reader.close()
            
            if (line != null && line.startsWith("cpu ")) {
                val stats = line.substring(5).trim().split("\\s+".toRegex())
                
                if (stats.size >= 4) {
                    val user = stats[0].toLongOrNull() ?: 0L
                    val nice = stats[1].toLongOrNull() ?: 0L
                    val system = stats[2].toLongOrNull() ?: 0L
                    val idle = stats[3].toLongOrNull() ?: 0L
                    val iowait = if (stats.size > 4) stats[4].toLongOrNull() ?: 0L else 0L
                    val irq = if (stats.size > 5) stats[5].toLongOrNull() ?: 0L else 0L
                    val softirq = if (stats.size > 6) stats[6].toLongOrNull() ?: 0L else 0L
                    
                    val total = user + nice + system + idle + iowait + irq + softirq
                    
                    if (isFirstRead) {
                        lastCpuTotal = total
                        lastCpuIdle = idle
                        isFirstRead = false
                        return 0f
                    }
                    
                    val totalDiff = total - lastCpuTotal
                    val idleDiff = idle - lastCpuIdle
                    
                    lastCpuTotal = total
                    lastCpuIdle = idle
                    
                    if (totalDiff > 0) {
                        val usage = ((totalDiff - idleDiff).toFloat() / totalDiff.toFloat()) * 100f
                        return usage.coerceIn(0f, 100f)
                    }
                }
            }
            0f
        } catch (e: Exception) {
            0f
        }
    }
}
