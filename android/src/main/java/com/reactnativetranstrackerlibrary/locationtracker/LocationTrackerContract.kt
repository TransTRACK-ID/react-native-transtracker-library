package com.reactlibrary.locationtracker

import android.location.Location

interface LocationTrackerContract {
    fun onLocationChanged(location: Location)
}