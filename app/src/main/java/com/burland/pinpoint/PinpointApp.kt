package com.burland.pinpoint

import android.app.Application

class PinpointApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // No analytics, no logging, no crashlytics.
        // Pure silence.
    }
}
