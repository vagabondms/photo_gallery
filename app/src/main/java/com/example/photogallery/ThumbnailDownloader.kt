package com.example.photogallery

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

private const val TAG = "ThumbnailDownloader"
private const val MESSAGE_DOWNLOAD = 0

// in keyword: Should only be consumed. not returned
class ThumbnailDownloader<in T>(
    private val responseHandler: Handler,
    private  val onThumbnailDownloaded : (T, Bitmap) -> Unit
) : HandlerThread(TAG) {

    private var hasQuit = false
    private lateinit var requestHandler: Handler
    private val requestMap = ConcurrentHashMap<T,String>()
    private val flickrRepository = FlickrRepository()

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    val fragmentLifecycleEventObserver : LifecycleEventObserver =
        LifecycleEventObserver { source, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    Log.i(TAG, "Starting background thread")
                    start()
                    looper
                }
                Lifecycle.Event.ON_DESTROY -> {
                    Log.i(TAG, "Destroying background thread")
                    quit()
                }
                else -> Unit
            }
        }

    val viewLifecycleEventObserver : LifecycleEventObserver =
        LifecycleEventObserver { source, event ->
            when(event){
                Lifecycle.Event.ON_DESTROY ->{
                    Log.i(TAG, "Clearing all requests from queue")
                    requestHandler.removeMessages(MESSAGE_DOWNLOAD)
                    requestMap.clear()
                }
                else -> Unit
            }
        }

    override fun onLooperPrepared() {
        requestHandler = object : Handler(looper){
            override fun handleMessage(msg: Message) {
                if(msg.what == MESSAGE_DOWNLOAD){
                    val target = msg.obj as T
                    Log.i(TAG, "Got a request for URL: ${requestMap[target]}")
                    handleRequest(target)
                }
            }
        }
    }

    private fun handleRequest(target: T) {
        val url = requestMap[target] ?: return
        val bitmap = flickrRepository.fetchPhoto(url) ?: return

        responseHandler.post(Runnable {
            if(requestMap[target] != null || hasQuit){
                return@Runnable
            }
            requestMap.remove(target)
            onThumbnailDownloaded(target,bitmap)
        })
    }


    fun queueThumbnail(target: T, url: String) {
        Log.i(TAG, "Got a URL: $url")
        requestMap[target] = url
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget()
    }

}