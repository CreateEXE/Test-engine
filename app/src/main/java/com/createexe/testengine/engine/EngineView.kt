package com.createexe.testengine.engine

import android.content.Context
import android.view.Choreographer
import android.view.SurfaceView
import android.widget.FrameLayout
import com.createexe.testengine.world.GothicBedroom
import com.google.android.filament.View
import com.google.android.filament.utils.ModelViewer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class EngineView(context: Context) : FrameLayout(context) {

    private val surfaceView = SurfaceView(context)
    private val modelViewer = ModelViewer(surfaceView)
    private val choreographer = Choreographer.getInstance()

    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            choreographer.postFrameCallback(this)

            modelViewer.animator?.let { animator ->
                if (animator.animationCount > 0) {
                    val elapsed =
                        (frameTimeNanos - animationStartNanos)
                            .toDouble() / 1_000_000_000.0

                    animator.applyAnimation(
                        0,
                        elapsed.toFloat()
                    )

                    animator.updateBoneMatrices()
                }
            }

            modelViewer.render(frameTimeNanos)
        }
    }

    private var animationStartNanos = System.nanoTime()

    private val world = GothicBedroom()

    init {
        addView(
            surfaceView,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )

        surfaceView.setOnTouchListener { _, event ->
            modelViewer.onTouchEvent(event)
            true
        }

        // Keep the initial scene visible instead of an empty black GL surface.
        modelViewer.view.renderQuality =
            modelViewer.view.renderQuality.apply {
                hdrColorBuffer = View.QualityLevel.MEDIUM
            }

        modelViewer.view.antiAliasing = View.AntiAliasing.FXAA

        // Disable expensive effects on the phone for now.
        modelViewer.view.dynamicResolutionOptions =
            modelViewer.view.dynamicResolutionOptions.apply {
                enabled = false
            }

        modelViewer.renderer.clearOptions =
            modelViewer.renderer.clearOptions.apply {
                clear = true
                clearColor = floatArrayOf(
                    0.035f,
                    0.025f,
                    0.045f,
                    1f
                )
            }
    }

    fun loadVrm(
        bytes: ByteArray,
        callback: (String) -> Unit
    ) {
        if (bytes.size < 12) {
            callback("VRM LOAD FAILED — FILE TOO SMALL")
            return
        }

        // VRM 0.x and VRM 1.x are GLB/glTF containers.
        // Filament's glTF loader handles the actual geometry,
        // textures, skinning and animations.
        val magic =
            (bytes[0].toInt() and 0xff) or
            ((bytes[1].toInt() and 0xff) shl 8) or
            ((bytes[2].toInt() and 0xff) shl 16) or
            ((bytes[3].toInt() and 0xff) shl 24)

        if (magic != 0x46546C67) {
            callback("VRM LOAD FAILED — NOT A GLB/VRM FILE")
            return
        }

        post {
            try {
                modelViewer.destroyModel()

                val buffer = ByteBuffer
                    .wrap(bytes)
                    .order(ByteOrder.nativeOrder())

                modelViewer.loadModelGlb(buffer)

                modelViewer.transformToUnitCube()

                animationStartNanos = System.nanoTime()

                callback("VRM LOADED")
            } catch (e: Exception) {
                callback(
                    "VRM LOAD FAILED — ${e.message ?: "unknown error"}"
                )
            }
        }
    }

    fun enqueueAction(action: String) {
        if (action.contains("walk", ignoreCase = true)) {
            world.handleAction(action)

            modelViewer.animator?.let {
                if (it.animationCount > 0) {
                    animationStartNanos = System.nanoTime()
                }
            }
        }
    }

    fun resume() {
        choreographer.removeFrameCallback(frameCallback)
        choreographer.postFrameCallback(frameCallback)
    }

    fun pause() {
        choreographer.removeFrameCallback(frameCallback)
    }

    fun shutdown() {
        choreographer.removeFrameCallback(frameCallback)
        modelViewer.destroyModel()
    }
}
