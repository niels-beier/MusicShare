package de.shuzo.musicshare

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.Strategy

class AdvertisingService(private val context: Context) {

    private val tag = "MusicShare/AdvertisingService"

    private val receiveConnectionLifecycleCallback = ReceiveConnectionLifecycleCallback(context)

    fun startAdvertising(strategy: Strategy) {
        if (context is MainActivity) {
            val advertisingOptions = AdvertisingOptions.Builder().setStrategy(strategy).build()

            Nearby.getConnectionsClient(context)
                .startAdvertising(
                    context.getUserName(),
                    "a",
                    receiveConnectionLifecycleCallback,
                    advertisingOptions
                )
                .addOnSuccessListener {
                    Toast.makeText(context, R.string.advertising_success, Toast.LENGTH_SHORT)
                        .show()
                    Log.d(tag, "startAdvertising: successful")
                }
                .addOnFailureListener {
                    Toast.makeText(context, R.string.advertising_fail, Toast.LENGTH_SHORT)
                        .show()
                    Log.d(tag, "startAdvertising: failed")
                }
        }
    }
}