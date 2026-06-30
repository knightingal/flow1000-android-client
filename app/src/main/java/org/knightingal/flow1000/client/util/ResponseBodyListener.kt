package org.knightingal.flow1000.client.util

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Source
import okio.buffer
import org.knightingal.flow1000.client.myapplication.AboutActivity

class ResponseBodyListener(val origin: ResponseBody, listener: AboutActivity.DownloadCounterListener): ResponseBody() {
    class ByteCounter(val totalBytes: Long, val listener: AboutActivity.DownloadCounterListener) {
        var bytesReadSoFar: Long = 0
        fun update(bytesRead: Long) {
            bytesReadSoFar += bytesRead
            val progress = bytesReadSoFar * 100 / totalBytes
            println("download progress: $progress% ($bytesReadSoFar/$totalBytes)")
            listener.update(bytesReadSoFar, totalBytes)
        }
    }

    val byteCounter = ByteCounter(contentLength(), listener)

    override fun contentLength(): Long {
        return origin.contentLength() ?: 0
    }

    private var bufferedSource: BufferedSource? = null

    override fun contentType(): MediaType? {
        return origin.contentType()
    }

    override fun source(): BufferedSource {
        if (bufferedSource ==null) {
            bufferedSource = source(origin.source()).buffer()
        }
        return bufferedSource!!
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                println("bytesRead: $bytesRead")
                if (bytesRead >= 0) {
                    byteCounter.update(bytesRead)
                }
                return bytesRead
            }
        }
    }
}
