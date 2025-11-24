package com.monitor.cpugpu

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import androidx.appcompat.app.AppCompatActivity
import com.monitor.cpugpu.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var thermalMonitor: ThermalMonitor
    private val handler = Handler(Looper.getMainLooper())
    private var isMonitoring = false
    
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateMonitoringData()
            if (isMonitoring) {
                handler.postDelayed(this, 1000)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            thermalMonitor = ThermalMonitor(this)
            setupThermalMonitoring()
            startMonitoring()
        } else {
            binding.thermalStatusText.text = "Requires Android 10+"
            binding.cpuUsageText.text = "N/A"
        }
    }
    
    private fun setupThermalMonitoring() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            thermalMonitor.startMonitoring { status ->
                runOnUiThread {
                    updateThermalStatus(status)
                }
            }
        }
    }
    
    private fun startMonitoring() {
        isMonitoring = true
        handler.post(updateRunnable)
    }
    
    private fun stopMonitoring() {
        isMonitoring = false
        handler.removeCallbacks(updateRunnable)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            thermalMonitor.stopMonitoring()
        }
    }
    
    private fun updateMonitoringData() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
        
        val cpuUsage = thermalMonitor.getCPUUsage()
        binding.cpuUsageText.text = String.format("%.1f%%", cpuUsage)
        
        val cpuTemp = thermalMonitor.getCPUTemperature()
        if (cpuTemp != null) {
            binding.cpuTempText.text = String.format("Temperature: %.1f°C", cpuTemp)
            updateCPUTempColor(cpuTemp)
        } else {
            binding.cpuTempText.text = "Temperature: N/A"
        }
        
        val gpuTemp = thermalMonitor.getGPUTemperature()
        if (gpuTemp != null) {
            binding.gpuTempText.text = String.format("Temperature: %.1f°C", gpuTemp)
            updateGPUTempColor(gpuTemp)
        } else {
            binding.gpuTempText.text = "Temperature: N/A"
        }
        
        val batteryTemp = thermalMonitor.getBatteryTemperature()
        if (batteryTemp != null) {
            binding.batteryTempText.text = String.format("Battery: %.1f°C", batteryTemp)
        } else {
            binding.batteryTempText.text = "Battery: N/A"
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val headroom = thermalMonitor.getThermalHeadroom()
            if (!headroom.isNaN()) {
                val percentage = (1.0f - headroom) * 100
                binding.thermalHeadroomText.text = String.format("Thermal Load: %.1f%%", percentage)
            } else {
                binding.thermalHeadroomText.text = "Headroom: N/A"
            }
        } else {
            binding.thermalHeadroomText.text = "Headroom: Requires Android 11+"
        }
        
        val thermalStatus = thermalMonitor.getCurrentThermalStatus()
        updateThermalStatus(thermalStatus)
    }
    
    private fun updateThermalStatus(status: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
        
        val statusText = thermalMonitor.getThermalStatusString(status)
        binding.thermalStatusText.text = statusText
        
        val color = when (status) {
            PowerManager.THERMAL_STATUS_NONE -> Color.parseColor("#4CAF50")
            PowerManager.THERMAL_STATUS_LIGHT -> Color.parseColor("#8BC34A")
            PowerManager.THERMAL_STATUS_MODERATE -> Color.parseColor("#FFC107")
            PowerManager.THERMAL_STATUS_SEVERE -> Color.parseColor("#FF9800")
            PowerManager.THERMAL_STATUS_CRITICAL -> Color.parseColor("#FF5722")
            PowerManager.THERMAL_STATUS_EMERGENCY -> Color.parseColor("#F44336")
            PowerManager.THERMAL_STATUS_SHUTDOWN -> Color.parseColor("#B71C1C")
            else -> Color.parseColor("#9E9E9E")
        }
        binding.thermalStatusText.setTextColor(color)
    }
    
    private fun updateCPUTempColor(temp: Float) {
        val color = when {
            temp < 50 -> Color.parseColor("#4CAF50")
            temp < 60 -> Color.parseColor("#8BC34A")
            temp < 70 -> Color.parseColor("#FFC107")
            temp < 80 -> Color.parseColor("#FF9800")
            else -> Color.parseColor("#F44336")
        }
        binding.cpuTempText.setTextColor(color)
    }
    
    private fun updateGPUTempColor(temp: Float) {
        val color = when {
            temp < 50 -> Color.parseColor("#4CAF50")
            temp < 60 -> Color.parseColor("#8BC34A")
            temp < 70 -> Color.parseColor("#FFC107")
            temp < 80 -> Color.parseColor("#FF9800")
            else -> Color.parseColor("#F44336")
        }
        binding.gpuTempText.setTextColor(color)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopMonitoring()
    }
    
    override fun onPause() {
        super.onPause()
        stopMonitoring()
    }
    
    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startMonitoring()
        }
    }
}
