package de.shuzo.musicshare

import android.app.AlertDialog
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import kotlinx.android.synthetic.main.activity_main.*

class ReceiveConnectionLifecycleCallback(private val context: Context) : ConnectionLifecycleCallback() {

    private val tag = "MusicShare/ReceiveConnectionLifecycleListener"

    private val receivePayloadCallback = ReceivePayloadCallback(context)

    override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
        Log.d(tag, "onConnectionInitiated: accepting connection")
        val alertDialog = AlertDialog.Builder(context)
        alertDialog
            .setTitle(R.string.accept_connection_title.toString() + " " + connectionInfo.endpointName)
            .setMessage(R.string.request_connection_message.toString() + " " + connectionInfo.authenticationToken)
            .setPositiveButton(
                R.string.accept_connection
            ) { _: DialogInterface?, _: Int ->
                Nearby.getConnectionsClient(context).acceptConnection(
                    endpointId,
                    receivePayloadCallback
                )
            }
            .setNegativeButton(
                android.R.string.cancel
            ) { _: DialogInterface?, _: Int ->
                Nearby.getConnectionsClient(context).rejectConnection(endpointId)
            }
            .setIcon(android.R.drawable.ic_dialog_info)
            .show()
    }

    override fun onConnectionResult(endpointId: String, connectionResolution: ConnectionResolution) {
        when (connectionResolution.status.statusCode) {
            ConnectionsStatusCodes.STATUS_OK -> {
                Log.d(tag, "onConnectionResult: status ok")
                Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
            }
            ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                Log.d(tag, "onConnectionResult: connection rejected")
                Toast.makeText(
                    context,
                    "Connection rejected",
                    Toast.LENGTH_SHORT
                ).show()
            }
            ConnectionsStatusCodes.STATUS_ERROR -> {
                Log.d(tag, "onConnectionResult: error")
                Log.d(tag, "onConnectionResult: " + connectionResolution.status)
                Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onDisconnected(endpointId: String) {
        Log.d(tag, "Device disconnected")
    }
}