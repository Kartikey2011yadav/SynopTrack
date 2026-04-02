package com.example.synoptrack.core.utils

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.example.synoptrack.R
import com.google.android.gms.maps.model.MapStyleOptions

object MapStyleManager {
    private const val TAG = "MapStyleManager"

    fun getMapStyle(context: Context, isDarkTheme: Boolean): MapStyleOptions? {
        return try {
            val styleResId = if (isDarkTheme) {
                R.raw.map_style_dark
            } else {
                R.raw.map_style_light
            }
            MapStyleOptions.loadRawResourceStyle(context, styleResId)
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
            null
        }
    }
}
