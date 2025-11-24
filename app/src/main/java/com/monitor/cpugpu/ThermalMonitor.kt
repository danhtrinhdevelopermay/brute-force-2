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
            val process = Runtime.getRuntime().exec("top -n 1")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            var cpuUsage = 0f
            
            while (reader.readLine().also { line = it } != null) {
                if (line?.contains("CPU:") == true || line?.contains("cpu") == true) {
                    val parts = line!!.split("\\s+".toRegex())
                    for (i in parts.indices) {
                        if (parts[i].endsWith("%")) {
                            try {
                                cpuUsage = parts[i].replace("%", "").toFloat()
                                break
                            } catch (e: Exception) {
                                continue
                            }
                        }
                    }
                    break
                }
            }
            reader.close()
            cpuUsage
        } catch (e: Exception) {
            0f
        }
    }
}
