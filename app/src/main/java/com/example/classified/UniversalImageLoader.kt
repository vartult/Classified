package com.example.classified

import android.R
import android.content.Context
import android.widget.ImageView

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer
import com.nostra13.universalimageloader.core.assist.QueueProcessingType
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator





class UniversalImageLoader(private val mContext: Context) {

    val config: ImageLoaderConfiguration
        get() {
            val defaultOptions = DisplayImageOptions.Builder()
                .showImageOnLoading(defaultImage)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .considerExifParams(true)
                .cacheOnDisk(true).cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(FadeInBitmapDisplayer(300)).build()

            return ImageLoaderConfiguration.Builder(mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024).build()
        }

    companion object {

        private val defaultImage = R.drawable.ic_menu_report_image

        /**
         * this method can be sued to set images that are static. It can't be used if the images
         * are being changed in the Fragment/Activity - OR if they are being set in a list or
         * a grid
         * @param imgURL
         * @param image
         */
        fun setImage(imgURL: String, image: ImageView) {

            val imageLoader = ImageLoader.getInstance()
            imageLoader.displayImage(imgURL, image)
        }

        fun initImageLoader(context: Context) {

            // This configuration tuning is custom. You can tune every option, you may tune some of them,
            // or you can create default configuration by the
            //  ImageLoaderConfiguration.createDefault(this);
            // method.
            //
            val config = ImageLoaderConfiguration.Builder(context)
            config.threadPriority(Thread.NORM_PRIORITY - 2)
            config.denyCacheImageMultipleSizesInMemory()
            config.diskCacheFileNameGenerator(Md5FileNameGenerator())
            config.diskCacheSize(50 * 1024 * 1024) // 50 MiB
            config.tasksProcessingOrder(QueueProcessingType.LIFO)
            config.writeDebugLogs() // Remove for release app

            // Initialize ImageLoader with configuration.
            ImageLoader.getInstance().init(config.build())
        }
    }
}