package de.shuzo.musicshare

import android.content.Context
import android.database.Cursor
import android.util.Log
import android.widget.ArrayAdapter
import java.io.File
import kotlin.math.roundToInt

class ListMusicService(val context: Context, private var adapter: ArrayAdapter<Any>) {

    private val tag = "MusicShare/ListMusicService"

    fun listMusic(cursor: Cursor): ArrayAdapter<Any> {
        if (context is MainActivity) {
            adapter.clear()

            val songInfo = arrayOfNulls<SongInfo>(cursor.count)

            var duration: Double

            var index = 0

            while (cursor.moveToNext()) {
                duration = ((cursor.getDouble(4) / 600).roundToInt() / 100).toDouble()

                adapter.add(cursor.getString(3) + ": " + cursor.getString(2) + " (" + duration + ")")

                val filepath = File(cursor.getString(1))

                Log.d(tag, "searchMusic: writing songInfo")

                songInfo[index] = SongInfo()

                songInfo[index]!!.title = filepath.name
                songInfo[index]!!.filepath = filepath.absolutePath
                context.songInfo[index]!!.filepath = filepath.absolutePath
                songInfo[index]!!.contentId = cursor.getString(0)
                context.songInfo[index]!!.contentId = cursor.getString(0)

                index++
            }
        }

        return adapter
    }
}