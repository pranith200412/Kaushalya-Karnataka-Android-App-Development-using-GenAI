package com.kaushalya.karnataka.util

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.imageview.ShapeableImageView
import java.io.File

/**
 * Common helper to bind a "circle avatar with initials fallback" — used by every
 * card and profile screen. Centralised so contrast logic lives in one place.
 */
object AvatarBinder {

    fun bind(
        container: FrameLayout,
        initialsView: TextView,
        imageView: ShapeableImageView,
        name: String,
        avatarPath: String?,
    ) {
        // mutate() forces a fresh drawable state so tint changes on a recycled view
        // don't leak back through the shared resource cache to every other avatar.
        val drawable = container.background?.mutate()
        val tint = ContextCompat.getColor(container.context, Format.avatarColor(name))
        when (drawable) {
            is GradientDrawable -> drawable.setColor(tint)
            null -> Unit
            else -> {
                drawable.colorFilter =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        BlendModeColorFilter(tint, BlendMode.SRC_IN)
                    else
                        PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
            }
        }
        container.background = drawable

        if (avatarPath.isNullOrBlank()) {
            imageView.visibility = ShapeableImageView.GONE
            initialsView.visibility = TextView.VISIBLE
            initialsView.text = Format.initials(name)
        } else {
            imageView.visibility = ShapeableImageView.VISIBLE
            initialsView.visibility = TextView.GONE
            val data = if (avatarPath.startsWith("/")) File(avatarPath) else Uri.parse(avatarPath)
            imageView.load(data) {
                crossfade(true)
                transformations(CircleCropTransformation())
            }
        }
    }
}
