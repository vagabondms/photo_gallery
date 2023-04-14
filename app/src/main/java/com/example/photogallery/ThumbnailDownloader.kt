package com.example.photogallery

import android.os.HandlerThread
import android.util.Log
import androidx.lifecycle.*

private const val TAG = "ThumbnailDownloader"

// in keyword: Should only be consumed. not returned
class ThumbnailDownloader<in T> : HandlerThread(TAG), LifecycleEventObserver {

    private var hasQuit = false

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when(event){
            Lifecycle.Event.ON_CREATE -> Log.i(TAG, "Starting background thread")
            Lifecycle.Event.ON_DESTROY -> Log.i(TAG, "Destroying background thread")
            else -> Unit
        }
    }

    fun queueThumbnail(target: T, url: String){
        Log.i(TAG, "Got a URL: $url")
    }

}