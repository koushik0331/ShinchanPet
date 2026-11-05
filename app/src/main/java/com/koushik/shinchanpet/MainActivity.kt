package com.koushik.shinchanpet

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var status: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 36, 28, 36)
            setBackgroundColor(0xFF171923.toInt())
        }
        val title = TextView(this).apply { text = "\uD83D\uDC4B Shinchan Pet"; textSize = 30f; setTextColor(0xFFFFD54F.toInt()); gravity = Gravity.CENTER }
        val intro = TextView(this).apply {
            text = "Your tiny home-screen buddy\n\nDrag him around. Tap him to play.\nHe blinks, naps, laughs and reacts to your battery." 
            textSize = 16f; setTextColor(0xFFE8E8EE.toInt()); gravity = Gravity.CENTER; setPadding(0, 22, 0, 24)
        }
        status = TextView(this).apply { textSize = 14f; setTextColor(0xFFB8C0D8.toInt()); gravity = Gravity.CENTER; setPadding(0, 8, 0, 18) }
        val start = Button(this).apply { text = "LET SHINCHAN OUT  \uD83C\uDF89"; textSize = 16f; setOnClickListener { enablePet() } }
        val stop = Button(this).apply { text = "Put Shinchan away"; setOnClickListener { stopService(Intent(this@MainActivity, PetService::class.java)); Toast.makeText(this@MainActivity, "Shinchan went home!", Toast.LENGTH_SHORT).show(); refresh() } }
        val hint = TextView(this).apply { text = "Tip: allow ‘Display over other apps’ when Android asks. The pet can be moved by dragging."; textSize = 13f; setTextColor(0xFF9AA3B8.toInt()); gravity = Gravity.CENTER; setPadding(8, 20, 8, 0) }
        root.addView(title); root.addView(intro); root.addView(status)
        root.addView(start, LinearLayout.LayoutParams(-1, -2)); root.addView(stop, LinearLayout.LayoutParams(-1, -2)); root.addView(hint)
        setContentView(root); refresh()
    }
    private fun enablePet() {
        if (!Settings.canDrawOverlays(this)) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            Toast.makeText(this, "Allow ‘Display over other apps’, then return and tap the button again.", Toast.LENGTH_LONG).show()
            return
        }
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }
        val i = Intent(this, PetService::class.java)
        if (Build.VERSION.SDK_INT >= 26) startForegroundService(i) else startService(i)
        Toast.makeText(this, "Shinchan is home!", Toast.LENGTH_SHORT).show(); refresh()
    }
    private fun refresh() { status.text = if (Settings.canDrawOverlays(this)) "Overlay permission: ready" else "Overlay permission: not enabled yet" }
    override fun onResume() { super.onResume(); if (::status.isInitialized) refresh() }
}
