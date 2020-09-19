package de.shuzo.musicshare

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback

class ReceiveEndpointDiscoveryCallback(private val context: Context) : EndpointDiscoveryCallback() {

    private val tag = "MusicShare/ReceiveEndpointDiscoveryCallback"

    override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
        Log.d(tag, "onEndpointFound: endpoint found, connecting")

        if (context is MainActivity) {
            context.endpointId = endpointId

            val receiveConnectionLifecycleCallback = ReceiveConnectionLifecycleCallback(context)

            val connectionDialog = AlertDialog.Builder(context)
            connectionDialog
                .setTitle(R.string.request_connection_title)
                .setMessage(R.string.request_connection_message.toString() + info.endpointName)
                .setPositiveButton(
                    R.string.request_connection
                ) { _: DialogInterface?, _: Int ->
                    Nearby.getConnectionsClient(context).requestConnection(
                        context.getUserName(),
                        endpointId,
                        receiveConnectionLifecycleCallback
                    )
                        .addOnSuccessListener {
                            Log.d(tag, "onEndpointFound: success")
                        }
                        .addOnFailureListener {
                            Log.d(tag, "onEndpointFound: failed")
                        }
                }
                .setNegativeButton(
                    android.R.string.no
                ) { _: DialogInterface, _: Int ->
                    // do nothing
                }
                .setIcon(android.R.drawable.ic_dialog_info)
                .show()
        }
    }

    override fun onEndpointLost(endpointId: String) {
        Log.d(tag, "onEndpointLost: lost endpoint $endpointId")
    }
}