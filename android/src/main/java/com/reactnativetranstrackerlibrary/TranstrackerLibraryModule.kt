package com.reactnativetranstrackerlibrary

import android.location.Location
import android.util.Log
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.reactlibrary.locationtracker.LocationTrackerContract
import com.reactlibrary.locationtracker.LocationTrackerService


class TranstrackerLibraryModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext),
  LocationTrackerContract {

    private var _reactContext: ReactApplicationContext = reactContext

    private var location: Location? = null
    private var locationTrackerService: LocationTrackerService? = null


    override fun getName(): String {
        return "TranstrackerLibrary"
    }

    @ReactMethod
    fun multiply(a: Int, b: Int, promise: Promise) {

      promise.resolve(a * b)

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
    fun initiateService(apiKey:String, externalId:String, imei: String) {
      if (locationTrackerService != null) {
        Log.d(name, "Already running")
        return
      }
      locationTrackerService = LocationTrackerService(_reactContext, apiKey, externalId, imei, this)
      Log.d(name, "Initiated with name: $imei")
    }

    @ReactMethod
    fun startService(onFailureCallback: Callback) {
      if (locationTrackerService == null) {
        onFailureCallback.invoke("Please initiate service first.")
        return
      }
      locationTrackerService!!.startLocationUpdates()
    }

    @ReactMethod
    fun stopService(onFailureCallback: Callback) {
      if (locationTrackerService == null) {
        onFailureCallback.invoke("Please initiate service first.")
        return
      }
      locationTrackerService!!.stopLocationUpdates()
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
