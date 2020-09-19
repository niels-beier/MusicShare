package de.shuzo.musicshare

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.Payload
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ShareFile(private val context: Context, private val pathname: String) : File(pathname) {

    private val tag = "MusicShare/ShareFile"

    private val convertService = ConvertService()

    fun sendFile(endpointId: String) {
        val fileToSend = File(pathname)

        val filePayload = Payload.fromFile(fileToSend)
        Nearby.getConnectionsClient(context).sendPayload(endpointId, filePayload)
        val bytesPayload = Payload.fromBytes(convertService.longToBytes(Calendar.getInstance().timeInMillis))
        Nearby.getConnectionsClient(context).sendPayload(endpointId, bytesPayload)
    }
}