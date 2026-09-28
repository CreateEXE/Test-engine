package com.createexe.testengine

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.createexe.testengine.engine.EngineView
import com.google.android.filament.utils.Utils

class MainActivity : Activity() {

    private lateinit var engine: EngineView
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        engine = EngineView(this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF100C14.toInt())
        }

        root.addView(
            engine,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val controls = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
            setPadding(8, 6, 8, 6)
            setBackgroundColor(0xFF17121C.toInt())
        }

        status = TextView(this).apply {
            text = "CHIMERA ENGINE — READY"
            textSize = 13f
            setTextColor(0xFFE8DFF0.toInt())
            setPadding(8, 0, 8, 0)
        }

        val walk = Button(this).apply {
            text = "TEST WALK"
            setOnClickListener {
                engine.enqueueAction("Walks towards user")
                status.text = "CHIMERA ENGINE — WALK"
            }
        }

        val load = Button(this).apply {
            text = "LOAD VRM"
            setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                }
                startActivityForResult(intent, REQUEST_VRM)
            }
        }

        controls.addView(
            status,
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        controls.addView(walk)
        controls.addView(load)

        root.addView(
            controls,
            LinearLayout.LayoutParams(-1, -2)
        )

        setContentView(root)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != REQUEST_VRM ||
            resultCode != RESULT_OK ||
            data?.data == null
        ) {
            return
        }

        try {
            val bytes = contentResolver
                .openInputStream(data.data!!)
                ?.use { it.readBytes() }

            if (bytes == null || bytes.isEmpty()) {
                status.text = "VRM LOAD FAILED — EMPTY FILE"
                return
            }

            status.text = "LOADING VRM..."

            engine.loadVrm(bytes) { message ->
                runOnUiThread {
                    status.text = message
                }
            }

        } catch (e: Exception) {
            status.text =
                "VRM LOAD FAILED — ${e.message ?: "unknown error"}"
        }
    }

    override fun onResume() {
        super.onResume()
        engine.resume()
    }

    override fun onPause() {
        engine.pause()
        super.onPause()
    }

    override fun onDestroy() {
        engine.shutdown()
        super.onDestroy()
    }

    companion object {
        private const val REQUEST_VRM = 9001

        init {
            Utils.init()
        }
    }
}
