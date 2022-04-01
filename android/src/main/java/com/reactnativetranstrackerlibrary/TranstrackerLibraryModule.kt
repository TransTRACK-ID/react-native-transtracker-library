package com.reactnativetranstrackerlibrary

import android.content.Intent
import android.location.Location
import android.os.Build
import android.util.Log
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.reactlibrary.locationtracker.LocationTrackerContract
import com.reactlibrary.locationtracker.LocationTrackerService


class TranstrackerLibraryModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext),
  LocationTrackerContract {

    private var _reactContext: ReactApplicationContext = reactContext

    private var location: Location? = null

    private var apiKey: String? = null
    private var externalId: String? = null
    private var trackerId: String? = null

    override fun getName(): String {
        return "TranstrackerLibrary"
    }

    override fun onLocationChanged(location: Location) {
      Log.d(name, "onLocationChanged")
      this.location = location

      val map = Arguments.createMap()
      map.putString("latitude", location.latitude.toString())
      map.putString("longitude", location.longitude.toString())
      map.putString("bearing", location.bearing.toString())
      map.putString("speed", location.speed.toString())

      try {
        _reactContext
          .getJSModule(RCTDeviceEventEmitter::class.java)
          .emit("onLocationChanged", map)
      } catch (e: Exception) {
        Log.e("ReactNative", "Caught Exception: " + e.message)
      }
    }


    @ReactMethod
    fun initiateService(apiKey:String, externalId:String, trackerId: String) {

      this.apiKey = apiKey
      this.externalId = externalId
      this.trackerId = trackerId

      Log.d(name, "Initiated with name: $trackerId")
    }

    @ReactMethod
    fun startService(onFailureCallback: Callback) {

      val intent = Intent(_reactContext, LocationTrackerService::class.java)
      intent.putExtra("apiKey", apiKey)
      intent.putExtra("externalId", externalId)
      intent.putExtra("trackerId", trackerId)

      _reactContext.startService(intent)
    }

    @ReactMethod
    fun stopService(onFailureCallback: Callback) {
      val intent = Intent(_reactContext, LocationTrackerService::class.java)
      _reactContext.stopService(intent)
    }

    @ReactMethod
    fun getLatestLocation(onFailureCallback: Callback, onSuccessCallback: Callback) {
      if (location == null) {
        onFailureCallback.invoke("Service getting location...")
        return
      }
      val map = Arguments.createMap()
      map.putString("latitude", location!!.latitude.toString())
      map.putString("longitude", location!!.longitude.toString())
      map.putString("bearing", location!!.bearing.toString())
      map.putString("speed", location!!.speed.toString())
      onSuccessCallback.invoke(map)
    }
}
