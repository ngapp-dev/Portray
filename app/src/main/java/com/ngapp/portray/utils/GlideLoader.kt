package com.ngapp.portray.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.bumptech.glide.request.target.Target

fun ImageView.load(
    url: String,
    placeholder: Int? = null,
    loadOnlyFromCache: Boolean = false,
    onLoadingFinished: () -> Unit = {}
) {
    val listener = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            onLoadingFinished()
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            onLoadingFinished()
            return false
        }
    }
    val requestOptions = RequestOptions.placeholderOf(placeholder ?: 0)
        .dontTransform()
        .onlyRetrieveFromCache(loadOnlyFromCache)

    Glide.with(this)
        .load(url)
        .placeholder(placeholder ?: 0)
//        .apply(requestOptions)
        .listener(listener)
        .into(this)
}