package com.kaushalya.karnataka.util

import android.content.Context
import com.kaushalya.karnataka.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

object Format {

    fun price(context: Context, value: Int, isStartingAt: Boolean): String =
        if (isStartingAt) context.getString(R.string.starting_at, value)
        else context.getString(R.string.price_fixed, value)

    fun rating(value: Float): String = String.format(Locale.US, "%.1f", value)

    fun reviews(context: Context, count: Int): String =
        if (count == 0) context.getString(R.string.reviews_count_zero)
        else context.resources.getQuantityString(R.plurals.reviews_count, count, count)

    fun services(context: Context, count: Int): String =
        context.resources.getQuantityString(R.plurals.services_count, count, count)

    fun initials(name: String): String {
        val parts = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        return when (parts.size) {
            0 -> "?"
            1 -> parts[0].firstOrNull()?.uppercase(Locale.getDefault()) ?: "?"
            else -> "${parts.first().firstOrNull()?.uppercase(Locale.getDefault()) ?: ""}${parts.last().firstOrNull()?.uppercase(Locale.getDefault()) ?: ""}"
        }
    }

    private val timeFormat = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)
    fun timestamp(epochMs: Long): String = timeFormat.format(Date(epochMs))

    fun avatarColor(name: String): Int {
        val key = name.firstOrNull()?.uppercaseChar() ?: 'A'
        // Group letters into 5 buckets so we have a stable palette mapping.
        val bucket = max(0, key.code - 'A'.code) % 5
        return AVATAR_COLOR_RES[bucket]
    }

    private val AVATAR_COLOR_RES = intArrayOf(
        R.color.avatar_a,
        R.color.avatar_b,
        R.color.avatar_c,
        R.color.avatar_d,
        R.color.avatar_e,
    )
}
