package de.shuzo.musicshare

class ConvertService {
    fun longToBytes(l: Long): ByteArray {
        return l.toString().toByteArray(Charsets.UTF_8)
    }

    fun bytesToLong(b: ByteArray): Long {
        return String(b, Charsets.UTF_8).toLong()
    }
}