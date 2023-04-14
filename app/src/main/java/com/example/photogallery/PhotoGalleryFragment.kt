package com.example.photogallery

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG = "PhotoGalleryFragment"
class PhotoGalleryFragment : Fragment() {

    private lateinit var photoRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val flickrLIveData: LiveData<List<GalleryItem>> = FlickrRepository().fetchPhotos()
        flickrLIveData.observe(
            this
        ) { responseString ->
            Log.d(TAG, "Response received: $responseString")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)

        photoRecyclerView = view.findViewById(R.id.photo_recycle_view)
        photoRecyclerView.layoutManager = GridLayoutManager(context,3)

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = PhotoGalleryFragment()
    }
}