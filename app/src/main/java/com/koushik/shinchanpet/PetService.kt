package com.koushik.shinchanpet

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
import kotlin.math.abs

class PetService : Service() {
    private lateinit var wm: WindowManager
    private var pet: PetView? = null
    private lateinit var params: WindowManager.LayoutParams
    private var downX = 0f; private var downY = 0f; private var startX = 0; private var startY = 0
    private var moved = false
    private var battery = 100
    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
            if (level >= 0 && scale > 0) { battery = level * 100 / scale; pet?.setBattery(battery) }
        }
    }
    override fun onCreate() {
        super.onCreate(); createChannel(); startForeground(41, notification())
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        pet = PetView(this).also { view ->
            // 48x56dp invisible touch target; the visible Shinchan is drawn at exactly 32x40dp.
            params = WindowManager.LayoutParams(dp(48), dp(56),
                if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT).apply { gravity = Gravity.TOP or Gravity.START; x = dp(18); y = dp(190) }
            view.setOnTouchListener { _, event -> handleTouch(event) }
            try { wm.addView(view, params) } catch (_: Exception) { stopSelf() }
        }
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }
    private fun handleTouch(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> { downX = e.rawX; downY = e.rawY; startX = params.x; startY = params.y; moved = false; return true }
            MotionEvent.ACTION_MOVE -> {
                val dx = (e.rawX - downX).toInt(); val dy = (e.rawY - downY).toInt()
                if (abs(dx) + abs(dy) > dp(5)) moved = true
                params.x = startX + dx; params.y = startY + dy
                try { pet?.let { wm.updateViewLayout(it, params) } } catch (_: Exception) {}
                return true
            }
            MotionEvent.ACTION_UP -> { if (!moved) pet?.playTap(); return true }
        }
        return true
    }
    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(NotificationChannel("shinchan_pet", "Shinchan Pet", NotificationManager.IMPORTANCE_MIN))
        }
    }
    private fun notification(): Notification {
        val builder = if (Build.VERSION.SDK_INT >= 26) Notification.Builder(this, "shinchan_pet") else Notification.Builder(this)
        return builder.setSmallIcon(android.R.drawable.ic_menu_view).setContentTitle("Shinchan is hanging out")
            .setContentText("Tap the little guy on your screen to play").setOngoing(true).build()
    }
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY
    override fun onDestroy() {
        try { unregisterReceiver(batteryReceiver) } catch (_: Exception) {}
        try { pet?.let { wm.removeView(it) } } catch (_: Exception) {}
        pet = null; super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? = null
}
