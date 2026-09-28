package com.createexe.testengine

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.createexe.testengine.engine.EngineView
import com.createexe.testengine.vrm.VrmInspector

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF100C14.toInt())
        }

        val engine = EngineView(this)

        root.addView(
            engine,
            LinearLayout.LayoutParams(-1, 0, 1f)
        )

        val controls = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
            setPadding(12, 8, 12, 8)
        }

        val status = TextView(this).apply {
            text = "CHIMERA ENGINE"
            textSize = 14f
            setTextColor(0xFFE8DFF0.toInt())
        }

        val walk = Button(this).apply {
            text = "TEST WALK"
            setOnClickListener {
                engine.enqueueAction("Walks towards user")
            }
        }

        val load = Button(this).apply {
            text = "LOAD VRM"
            setOnClickListener {
                VrmInspector.pick(this@MainActivity)
            }
        }

        controls.addView(status, LinearLayout.LayoutParams(0, -2, 1f))
        controls.addView(walk)
        controls.addView(load)

        root.addView(
            controls,
            LinearLayout.LayoutParams(-1, -2)
        )

        setContentView(root)
    }
}
