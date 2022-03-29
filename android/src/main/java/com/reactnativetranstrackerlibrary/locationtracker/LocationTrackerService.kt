package com.reactlibrary.locationtracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import java.io.UnsupportedEncodingException
import java.lang.System.currentTimeMillis


class LocationTrackerService(context: Context, apiKey: String, externalId: String, imei: String, contract: LocationTrackerContract){

    private var tag = "LocationService"
    private val apiMirror = "https://transtracker-test.transtrack.id/api/send-telematic"

    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationContract: LocationTrackerContract = contract

    private var isGrantedLocation: Boolean = false

    private lateinit var locationRequest: LocationRequest

    private var requestQueue: RequestQueue = Volley.newRequestQueue(context)
    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            val lastLocation = locationResult.lastLocation

            val apiWithParams =
                "$apiMirror?altitude=${lastLocation.altitude}" +
                        "&odometer=&" +
                        "bearing=${lastLocation.bearing}" +
                        "&lon=${lastLocation.longitude}" +
                        "&hdop=1" +
                        "&ignition=true" +
                        "&lat=${lastLocation.latitude}" +
                        "&speed=${lastLocation.speed}" +
                        "&timestamp=${currentTimeMillis()}"

            val stringRequest = object: StringRequest(
                Request.Method.POST, apiWithParams,
                { response ->
                    Log.v(tag, "Response: ${response.toString()}")
                },
                { e ->
                    if (e.networkResponse != null && e.networkResponse.data.isNotEmpty()) {
                        try {
                            val body = String(e.networkResponse.data)
                            Log.e(tag, body)
                        } catch (e: UnsupportedEncodingException) {
                            e.printStackTrace()
                        }
                    }else{
                        Log.e(tag, e.toString())
                    }
                },
            ) {
              override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                Log.d("tag", "API Key: $apiKey");
                Log.d("tag", "External ID: $externalId");
                Log.d("tag", "Tracker ID: $imei");

                headers["X-Api-Key"] = apiKey
                headers["X-External-Id"] = externalId;
                headers["X-Tracker-Id"] = imei;
                return headers
              }
            };




            stringRequest.retryPolicy = DefaultRetryPolicy(
                5000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )

            requestQueue.add(stringRequest)
            locationContract.onLocationChanged(lastLocation)
        }
    }

    init {

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )  {

            Log.e(tag, "Permission Access Fine Location not Granted")
        }else{
             isGrantedLocation = true
             locationRequest = LocationRequest.create().apply {
                 interval = 100
                 fastestInterval = 50
                 priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                 maxWaitTime= 100
             }
         }
    }

    @SuppressLint("MissingPermission")
     fun startLocationUpdates() {
         if(isGrantedLocation) {
             fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
                 Looper.getMainLooper()
             )
         }
     }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}
