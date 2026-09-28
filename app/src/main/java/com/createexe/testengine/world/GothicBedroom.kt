package com.createexe.testengine.world

import android.opengl.GLES20

class GothicBedroom {

    data class Body(
        var x: Float = 0f,
        var y: Float = 0f,
        var z: Float = 0f,
        var yaw: Float = 0f,
        var moving: Boolean = false
    )

    data class Object3D(
        val name: String,
        val x: Float,
        val y: Float,
        val z: Float,
        val sx: Float,
        val sy: Float,
        val sz: Float
    )

    val body = Body()

    val objects = listOf(
        Object3D("bed", 0f, 0f, -2.2f, 2.5f, .6f, 1.4f),
        Object3D("chair", 2f, 0f, -.2f, .8f, 1f, .8f),
        Object3D("desk", 2f, 0f, -2f, 2f, .8f, .8f),
        Object3D("nightstand", -2.4f, 0f, -1.8f, .6f, .8f, .6f),
        Object3D("window", 0f, 1.7f, -4f, 2f, 1.5f, .1f)
    )

    fun handleAction(action: String) {
        if (action.contains("walk", ignoreCase = true)) {
            body.moving = true
        }
    }

    fun update(dt: Float) {
        if (body.moving) {
            body.x += dt * 0.35f

            if (body.x >= 1.8f) {
                body.x = 1.8f
                body.moving = false
            }
        }
    }

    fun render() {
        GLES20.glLineWidth(2f)
    }
}
