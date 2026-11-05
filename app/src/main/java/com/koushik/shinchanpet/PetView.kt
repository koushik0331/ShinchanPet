package com.koushik.shinchanpet

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.View
import android.os.Handler
import android.os.Looper
import kotlin.random.Random

class PetView(context: Context) : View(context) {
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val handler = Handler(Looper.getMainLooper())
    private var frame = 0f
    private var action = 0 // 0 idle, 1 happy, 2 sleepy, 3 surprised
    private var battery = 100
    private var bubble = ""
    private var bubbleUntil = 0L
    private val lines = listOf("Hehe! Kya kar rahe ho?", "Oho! Look at me!", "I'm the boss!", "Bhookh lagi hai!", "Aaj mast chill karte hain!", "Don't disturb my nap!", "Nohara power!", "Mujhe chocolate chahiye!")
    private val tick = object : Runnable { override fun run() { frame += 0.13f; if (System.currentTimeMillis() > bubbleUntil) bubble = ""; invalidate(); handler.postDelayed(this, 45) } }
    init { setLayerType(View.LAYER_TYPE_SOFTWARE, null); handler.post(tick) }
    fun setBattery(value: Int) { battery = value; if (value <= 15) say("Battery low? Main bhi sleepy hoon!") }
    fun playTap() {
        action = (action + 1) % 4
        say(when (action) { 1 -> "Hehehe! Tickles!"; 2 -> "Five more minutes..."; 3 -> "EH?! Kya hua?!"; else -> lines[Random.nextInt(lines.size)] })
        invalidate()
    }
    private fun say(s: String) { bubble = s; bubbleUntil = System.currentTimeMillis() + 2600; invalidate() }
    override fun onDraw(c: Canvas) {
        super.onDraw(c)
        // The view is a 48x56dp touch target, while the visible character is 32x40dp.
        // Center the tiny character inside the larger invisible hit area.
        val w = dp(32f); val h = dp(40f)
        c.save()
        c.translate((width - w) / 2f, (height - h) / 2f)
        val cx = w * .50f; val bob = kotlin.math.sin(frame.toDouble()).toFloat() * dp(0.65f)
        // Soft shadow
        p.color = 0x44303040; c.drawOval(cx-w*.25f, h*.91f, cx+w*.25f, h*.96f, p)
        c.save(); c.translate(0f, bob)
        // Legs and yellow shorts
        p.color = Color.rgb(255, 207, 42); c.drawRoundRect(RectF(cx-w*.22f,h*.73f,cx+w*.22f,h*.88f), w*.07f,w*.07f,p)
        p.color = Color.rgb(247, 218, 176); c.drawRoundRect(RectF(cx-w*.19f,h*.84f,cx-w*.015f,h*.94f), w*.045f,w*.045f,p); c.drawRoundRect(RectF(cx+w*.015f,h*.84f,cx+w*.19f,h*.94f), w*.045f,w*.045f,p)
        // Red shirt
        p.color = Color.rgb(224, 55, 55); c.drawRoundRect(RectF(cx-w*.27f,h*.55f,cx+w*.27f,h*.79f), w*.08f,w*.08f,p)
        // Arms
        p.color = Color.rgb(247,218,176); c.drawRoundRect(RectF(cx-w*.34f,h*.57f,cx-w*.24f,h*.73f),w*.05f,w*.05f,p); c.drawRoundRect(RectF(cx+w*.24f,h*.57f,cx+w*.34f,h*.73f),w*.05f,w*.05f,p)
        // Head / ears
        p.color = Color.rgb(255, 222, 184); c.drawCircle(cx-w*.245f,h*.39f,w*.085f,p); c.drawCircle(cx+w*.245f,h*.39f,w*.085f,p)
        c.drawOval(RectF(cx-w*.29f,h*.17f,cx+w*.29f,h*.61f),p)
        // Hair cap
        p.color = Color.rgb(25,22,20); c.drawOval(RectF(cx-w*.29f,h*.145f,cx+w*.29f,h*.40f),p)
        // forehead skin overlays to create hairline scallops
        p.color = Color.rgb(255,222,184); c.drawOval(RectF(cx-w*.245f,h*.27f,cx+w*.245f,h*.60f),p)
        p.color = Color.rgb(25,22,20)
        val hair = Path().apply { moveTo(cx-w*.27f,h*.34f); cubicTo(cx-w*.23f,h*.18f,cx-w*.14f,h*.24f,cx-w*.12f,h*.29f); cubicTo(cx-w*.07f,h*.16f,cx+w*.02f,h*.22f,cx+w*.025f,h*.29f); cubicTo(cx+w*.10f,h*.18f,cx+w*.20f,h*.23f,cx+w*.25f,h*.34f); lineTo(cx+w*.27f,h*.38f); lineTo(cx-w*.27f,h*.38f); close() }; c.drawPath(hair,p)
        // Eyes, blink every so often
        val blink = (frame.toInt() % 95) in 0..2
        p.color = Color.BLACK
        if (action == 2 || blink) { p.strokeWidth=w*.018f; p.style=Paint.Style.STROKE; c.drawLine(cx-w*.14f,h*.405f,cx-w*.055f,h*.405f,p); c.drawLine(cx+w*.055f,h*.405f,cx+w*.14f,h*.405f,p); p.style=Paint.Style.FILL }
        else { c.drawOval(RectF(cx-w*.145f,h*.36f,cx-w*.055f,h*.455f),p); c.drawOval(RectF(cx+w*.055f,h*.36f,cx+w*.145f,h*.455f),p); p.color=Color.WHITE; c.drawCircle(cx-w*.09f,h*.38f,w*.018f,p); c.drawCircle(cx+w*.11f,h*.38f,w*.018f,p) }
        // Eyebrows
        p.color=Color.BLACK; p.strokeWidth=w*.022f; p.strokeCap=Paint.Cap.ROUND; p.style=Paint.Style.STROKE
        c.drawLine(cx-w*.17f,h*.335f,cx-w*.055f,h*.32f,p); c.drawLine(cx+w*.055f,h*.32f,cx+w*.17f,h*.335f,p); p.style=Paint.Style.FILL
        // Cheeks
        p.color=0x55F06D70; c.drawOval(RectF(cx-w*.22f,h*.46f,cx-w*.12f,h*.50f),p); c.drawOval(RectF(cx+w*.12f,h*.46f,cx+w*.22f,h*.50f),p)
        // Mouth changes with mood
        p.color=Color.rgb(115,35,38)
        if (action == 1 || action == 3) c.drawOval(RectF(cx-w*.055f,h*.47f,cx+w*.055f,h*.54f),p) else c.drawOval(RectF(cx-w*.035f,h*.49f,cx+w*.035f,h*.515f),p)
        // Shirt collar
        p.color=Color.WHITE; val collar=Path().apply { moveTo(cx-w*.06f,h*.56f); lineTo(cx,h*.62f); lineTo(cx+w*.06f,h*.56f); lineTo(cx,h*.575f); close() }; c.drawPath(collar,p)
        c.restore()
        // Battery sparkle / indicator
        // Keep the character silhouette clean at fly-sized dimensions.
        c.restore()
    }
    private fun dp(value: Float): Float = value * resources.displayMetrics.density
    private fun drawBubble(c: Canvas, text: String, w: Float, h: Float) {
        p.color=0xEEFFFFFF.toInt(); c.drawRoundRect(RectF(w*.02f,h*.015f,w*.98f,h*.17f),w*.06f,w*.06f,p)
        p.color=Color.DKGRAY; p.textSize=w*.075f; p.textAlign=Paint.Align.CENTER
        val maxChars=23; val shown=if(text.length>maxChars) text.take(maxChars-1)+"…" else text
        c.drawText(shown,w*.5f,h*.115f,p); p.textAlign=Paint.Align.LEFT
    }
    override fun onDetachedFromWindow() { handler.removeCallbacks(tick); super.onDetachedFromWindow() }
}
