package de.shuzo.musicshare

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.Strategy

class DiscoveryService(private val context: Context) {

    private val tag = "MusicShare/DiscoveryService"

    private val receiveEndpointDiscoveryCallback = ReceiveEndpointDiscoveryCallback(context)

    fun startDiscovery(strategy: Strategy) {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(strategy).build()
        Nearby.getConnectionsClient(context)
            .startDiscovery("a", receiveEndpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener {
                Log.d(tag, "startDiscovery: successful")
                Toast.makeText(context, R.string.discovery_success, Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener {
                Log.d(tag, "startDiscovery: failed")
                Toast.makeText(context, R.string.discovery_failed, Toast.LENGTH_SHORT)
                    .show()
            }
    }
}