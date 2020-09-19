package de.shuzo.musicshare

import android.content.Context
import android.content.Intent.getIntent
import android.net.Uri
import android.os.Build
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.Calendar.getInstance
import kotlin.concurrent.schedule


class ReceivePayloadCallback(private val context: Context) : PayloadCallback() {

    private val tag = "MusicShare/ReceivePayloadsListener"

    private lateinit var payloadFile: File
    private var latency: Long? = null
    private val convertService = ConvertService()
    private val musicPlayer = MusicPlayer(context)

    override fun onPayloadReceived(endpointId: String, payload: Payload) {
        if (context is MainActivity) {
            Log.d(tag, "onPayloadReceived: received payload")
            when (payload.type) {
                Payload.Type.FILE -> {
                    payloadFile = payload.asFile()!!.asJavaFile()!!
                    Log.d(tag, "onPayloadReceived: " + payloadFile.absolutePath)
                }
                Payload.Type.BYTES -> {
                    if (convertService.bytesToLong(payload.asBytes()!!) == 1L) {
                        musicPlayer.startPlaying(context.openable)
                    } else {
                        latency =
                            getInstance().timeInMillis - convertService.bytesToLong(payload.asBytes()!!)
                        Log.d(tag, latency.toString())
                        Log.d(tag, getInstance().timeInMillis.toString())
                        Log.d(tag, convertService.bytesToLong(payload.asBytes()!!).toString())
                    }
                }
            }
        }
    }

    override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
        if (context is MainActivity) {
            if (!context.sent && update.status == PayloadTransferUpdate.Status.SUCCESS) {
                val destination = File(payloadFile, "payload.mp3")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Log.d(tag, "onPayloadTransferUpdate: copyStream")
                    val uri = Uri.parse(destination.absolutePath)
                    try {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        inputStream!!.copyTo(FileOutputStream(File(uri.toString(), "payload.mp3")))
                    } catch (e: IOException) {
                        Log.e(tag, "moveTo: $e")
                    } finally {
                        context.contentResolver.delete(uri, null, null)
                    }
                } else {
                    payloadFile.renameTo(destination)
                    Log.d(tag, "onPayloadTransferUpdate: rename")
                }
                if (latency != null) {
                    val startBytes = Payload.fromBytes(convertService.longToBytes(1))
                    Nearby.getConnectionsClient(context)
                        .sendPayload(context.endpointId, startBytes)
                    // play music after delay
                    //Timer("delayTimer", false).schedule(latency!!) {
                        musicPlayer.startPlaying(Uri.parse("/storage/emulated/0/Download/Nearby/payload.mp3"))
                    //}
                }
            }
        }
    }
}