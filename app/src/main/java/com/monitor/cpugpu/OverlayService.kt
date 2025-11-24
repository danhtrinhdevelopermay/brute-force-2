package com.monitor.cpugpu

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.Q)
class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null
    private lateinit var thermalMonitor: ThermalMonitor
    private val handler = Handler(Looper.getMainLooper())
    
    private lateinit var cpuUsageText: TextView
    private lateinit var cpuTempText: TextView
    private lateinit var gpuTempText: TextView
    private lateinit var fpsText: TextView
    
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    private val updateRunnable = object : Runnable {
        override fun run() {
            updateOverlayData()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        thermalMonitor = ThermalMonitor(this)
        
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        
        createOverlayView()
        thermalMonitor.startMonitoring { }
        handler.post(updateRunnable)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "CPU/GPU Monitor Overlay",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows overlay monitoring stats"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("CPU/GPU Monitor")
                .setContentText("Overlay is active")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build()
        } else {
            Notification.Builder(this)
                .setContentTitle("CPU/GPU Monitor")
                .setContentText("Overlay is active")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build()
        }
    }

    private fun createOverlayView() {
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.x = 100
        layoutParams.y = 100

        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        overlayView = inflater.inflate(R.layout.overlay_layout, null)

        cpuUsageText = overlayView!!.findViewById(R.id.overlayCpuUsage)
        cpuTempText = overlayView!!.findViewById(R.id.overlayCpuTemp)
        gpuTempText = overlayView!!.findViewById(R.id.overlayGpuTemp)
        fpsText = overlayView!!.findViewById(R.id.overlayFps)

        overlayView!!.findViewById<View>(R.id.overlayClose).setOnClickListener {
            stopSelf()
        }

        overlayView!!.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = layoutParams.x
                    initialY = layoutParams.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    layoutParams.x = initialX + (event.rawX - initialTouchX).toInt()
                    layoutParams.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(overlayView, layoutParams)
                    true
                }
                else -> false
            }
        }

        windowManager.addView(overlayView, layoutParams)
    }

    private fun updateOverlayData() {
        val cpuUsage = thermalMonitor.getCPUUsage()
        cpuUsageText.text = String.format("CPU: %.1f%%", cpuUsage)
        cpuUsageText.setTextColor(getCpuUsageColor(cpuUsage))

        val cpuTemp = thermalMonitor.getCPUTemperature()
        if (cpuTemp != null) {
            cpuTempText.text = String.format("Temp: %.1f°C", cpuTemp)
            cpuTempText.setTextColor(getTempColor(cpuTemp))
        } else {
            cpuTempText.text = "Temp: --°C"
        }

        val gpuTemp = thermalMonitor.getGPUTemperature()
        if (gpuTemp != null) {
            gpuTempText.text = String.format("GPU: %.1f°C", gpuTemp)
            gpuTempText.setTextColor(getTempColor(gpuTemp))
        } else {
            gpuTempText.text = "GPU: --°C"
        }

        val fps = thermalMonitor.getFPS()
        fpsText.text = String.format("FPS: %.0f", fps)
        fpsText.setTextColor(getFpsColor(fps))
    }

    private fun getCpuUsageColor(usage: Float): Int {
        return when {
            usage < 30 -> Color.parseColor("#4CAF50")
            usage < 60 -> Color.parseColor("#FFC107")
            usage < 80 -> Color.parseColor("#FF9800")
            else -> Color.parseColor("#F44336")
        }
    }

    private fun getTempColor(temp: Float): Int {
        return when {
            temp < 50 -> Color.parseColor("#4CAF50")
            temp < 60 -> Color.parseColor("#8BC34A")
            temp < 70 -> Color.parseColor("#FFC107")
            temp < 80 -> Color.parseColor("#FF9800")
            else -> Color.parseColor("#F44336")
        }
    }

    private fun getFpsColor(fps: Float): Int {
        return when {
            fps >= 55 -> Color.parseColor("#4CAF50")
            fps >= 45 -> Color.parseColor("#8BC34A")
            fps >= 30 -> Color.parseColor("#FFC107")
            fps >= 20 -> Color.parseColor("#FF9800")
            else -> Color.parseColor("#F44336")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
        thermalMonitor.stopMonitoring()
        overlayView?.let { windowManager.removeView(it) }
        
        sendBroadcast(Intent(ACTION_OVERLAY_STOPPED))
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "overlay_channel"
        private const val NOTIFICATION_ID = 1
        const val ACTION_OVERLAY_STOPPED = "com.monitor.cpugpu.OVERLAY_STOPPED"
    }
}
