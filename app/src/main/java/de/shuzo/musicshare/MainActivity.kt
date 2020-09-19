package de.shuzo.musicshare

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Strategy

class MainActivity : AppCompatActivity() {

    // TAG for logs
    private val tag = "MusicShare"

    // all required permissions
    private val requiredPermissions = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    // request code for required permissions
    private val requestCodeRequiredPermissions = 1

    private val strategy = Strategy.P2P_STAR

    lateinit var connectionsClient: ConnectionsClient

    lateinit var adapter: ArrayAdapter<Any>

    lateinit var songInfo: Array<SongInfo?>

    lateinit var endpointId: String

    lateinit var openable: Uri

    lateinit var filepath: Uri

    private val advertisingService = AdvertisingService(this)

    private val discoveryService = DiscoveryService(this)

    private lateinit var shareFile: ShareFile

    private val musicPlayer = MusicPlayer(this)

    var sent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connectionsClient = Nearby.getConnectionsClient(applicationContext)
        adapter = ArrayAdapter<Any>(applicationContext, android.R.layout.simple_list_item_1)
        val listMusicService = ListMusicService(this, adapter)

        val musicList = findViewById<ListView>(R.id.musicList)
        val connectButton = findViewById<Button>(R.id.connectButton)
        val searchButton = findViewById<Button>(R.id.searchButton)
        val pauseButton = findViewById<ImageButton>(R.id.pauseButton)
        val stopButton = findViewById<ImageButton>(R.id.stopButton)

        musicList.adapter = adapter

        musicList.setOnItemClickListener { _, _, position, _ ->
            filepath = Uri.parse(songInfo[position]!!.filepath)
            openable = Uri.parse(
                MediaStore.Audio.Media.getContentUri("external")
                    .toString() + "/" + songInfo[position]!!.contentId
            )
            shareFile = ShareFile(this, filepath.toString())
            shareFile.sendFile(endpointId)
            sent = true
        }

        connectButton.setOnClickListener {
            advertisingService.startAdvertising(strategy, getUserName())
            discoveryService.startDiscovery(strategy)
        }

        searchButton.setOnClickListener {
            val cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    "_id",
                    "_data",
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DURATION
                ),
                "is_music == 1",
                null, null
            )

            songInfo = arrayOfNulls(cursor!!.count)

            for (index in songInfo.indices) {
                songInfo[index] = SongInfo()
                Log.d(tag, "searchButton: $index")
            }

            adapter = listMusicService.listMusic(cursor)
            adapter.notifyDataSetChanged()
        }

        pauseButton.setOnClickListener {
            if (musicPlayer.isPlaying) {
                musicPlayer.pause()
            } else {
                musicPlayer.start()
            }
        }

        stopButton.setOnClickListener {
            musicPlayer.stop()
        }
    }

    override fun onStart() {
        super.onStart()

        Log.d(tag, "onStart: manufacturer: " + Build.MANUFACTURER)
        Log.d(tag, "onStart: model: " + Build.MODEL)
        Log.d(tag, "onStart: os version: " + Build.VERSION.BASE_OS)

        if (!hasPermissions(this, *requiredPermissions)) {
            requestPermissions(requiredPermissions, requestCodeRequiredPermissions)
        }

        getUserName()
    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun getUserName(): String {
        // use BluetoothAdapter to get device name given by the user in system settings
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        Log.d(tag, "getUserName: " + bluetoothAdapter.name)
        return bluetoothAdapter.name
    }
}