package com.createexe.testengine.vrm

import android.app.Activity
import android.content.Intent
import java.nio.ByteBuffer
import java.nio.ByteOrder

object VrmInspector {

    fun pick(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        }

        activity.startActivityForResult(intent, 9001)
    }

    fun inspect(bytes: ByteArray): Result {
        if (bytes.size < 12) {
            return Result(false, "File too small")
        }

        val header = ByteBuffer.wrap(bytes)
            .order(ByteOrder.LITTLE_ENDIAN)

        if (header.int != 0x46546C67) {
            return Result(false, "Not a GLB container")
        }

        val version = header.int
        header.int

        if (version != 2) {
            return Result(false, "Unsupported GLB version $version")
        }

        var offset = 12

        while (offset + 8 <= bytes.size) {

            val chunkLength =
                ByteBuffer.wrap(bytes, offset, 4)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .int

            val chunkType =
                ByteBuffer.wrap(bytes, offset + 4, 4)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .int

            if (chunkLength < 0 ||
                offset + 8 + chunkLength > bytes.size) {
                break
            }

            if (chunkType == 0x4E4F534A) {
                val json = String(
                    bytes,
                    offset + 8,
                    chunkLength
                )

                val isVrm =
                    json.contains("\"VRM\"") ||
                    json.contains("\"VRMC\"") ||
                    json.contains("VRM")

                return Result(
                    isVrm,
                    if (isVrm)
                        "VRM metadata detected"
                    else
                        "Valid GLB, but no VRM extension detected"
                )
            }

            offset += 8 + chunkLength
        }

        return Result(
            false,
            "Valid GLB, but JSON metadata was not found"
        )
    }

    data class Result(
        val isVrm: Boolean,
        val message: String
    )
}
