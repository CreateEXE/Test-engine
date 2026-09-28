package com.createexe.testengine.engine

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.createexe.testengine.world.GothicBedroom
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class EngineView(context: Context) : GLSurfaceView(context) {

    private val renderer = Renderer()

    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    fun enqueueAction(action: String) {
        queueEvent {
            renderer.world.handleAction(action)
        }
    }

    private class Renderer : GLSurfaceView.Renderer {

        val world = GothicBedroom()

        override fun onSurfaceCreated(
            gl: GL10?,
            config: EGLConfig?
        ) {
            GLES20.glClearColor(0.025f, 0.018f, 0.035f, 1f)
            GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        }

        override fun onSurfaceChanged(
            gl: GL10?,
            width: Int,
            height: Int
        ) {
            GLES20.glViewport(0, 0, width, height)
        }

        override fun onDrawFrame(gl: GL10?) {
            GLES20.glClear(
                GLES20.GL_COLOR_BUFFER_BIT or
                GLES20.GL_DEPTH_BUFFER_BIT
            )

            world.update(1f / 60f)
            world.render()
        }
    }
}
