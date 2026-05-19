package com.kaushalya.karnataka.util

import android.content.Context

/**
 * The app has no real auth — to demo "I am this worker" without backend login,
 * we store the currently-active worker's row ID in SharedPreferences.
 */
object Prefs {

    private const val FILE = "kaushalya_prefs"
    private const val KEY_CURRENT_WORKER = "current_worker_id"

    fun currentWorkerId(context: Context): Long =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getLong(KEY_CURRENT_WORKER, 0L)

    fun setCurrentWorkerId(context: Context, id: Long) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_CURRENT_WORKER, id)
            .apply()
    }

    fun clearCurrentWorker(context: Context) = setCurrentWorkerId(context, 0L)
}
