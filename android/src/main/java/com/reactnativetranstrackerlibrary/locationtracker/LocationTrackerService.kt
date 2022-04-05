package com.reactlibrary.locationtracker

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import java.io.UnsupportedEncodingException
import java.lang.System.currentTimeMillis


class LocationTrackerService : Service() {

    private var tag = "LocationService"
    private val apiMirror = "https://transtracker.transtrack.id/api/send-telematic"

    private var isGrantedLocation: Boolean = false
    private var isServiceStarted: Boolean = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var requestQueue: RequestQueue

    private lateinit var context: Context
    private lateinit var apiKey: String
    private lateinit var externalId: String
    private lateinit var trackerId: String

    val NOTIFICATION_CHANNEL_ID = "com.reactnativetranstrackerlibrary.locationtracker"


  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    super.onStartCommand(intent, flags, startId)

    val extras = intent!!.extras

    if(extras != null){
      this.apiKey = extras.getString("apiKey", "")
      this.externalId =  extras.getString("externalId", "")
      this.trackerId = extras.getString("trackerId", "")
    }

    if(apiKey.isNotEmpty() || externalId.isNotEmpty() || trackerId.isNotEmpty()) {
      startLocationUpdates()
    }

    return (START_NOT_STICKY)
  }

  override fun onCreate() {
    super.onCreate()

    initialService()
  }

  override fun onDestroy() {
    super.onDestroy()

    stopLocationUpdates()
  }

    private fun initialService() {
      context = applicationContext

      fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
      requestQueue = Volley.newRequestQueue(context)

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
        Log.d(tag, "Location requested")
      }
    }

    @SuppressLint("MissingPermission")
     fun startLocationUpdates() {
         if(isGrantedLocation) {
            if (isServiceStarted){
              return;
            }

           createNotificationChanel()

           locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                  super.onLocationResult(locationResult)

                  val lastLocation = locationResult.lastLocation

                  val apiWithParams =
                    "$apiMirror?altitude=${lastLocation.altitude}" +
                      "&odometer=" +
                      "&bearing=${lastLocation.bearing}" +
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
                      headers["X-Api-Key"] = apiKey
                      headers["X-External-Id"] = externalId;
                      headers["X-Tracker-Id"] = trackerId;
                      return headers
                    }
                  };

                  stringRequest.retryPolicy = DefaultRetryPolicy(
                    5000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                  )

                  requestQueue.add(stringRequest)
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
                Looper.getMainLooper()
            )

            isServiceStarted = true;
            Log.d(tag, "Service started")
         }
     }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        isServiceStarted = false;

        stopForeground(true)
        Log.d(tag, "Service stoped")
    }

    private fun createNotificationChanel() {
      val channelName = "Background Service"
      val notificationBuilder =
        NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
          NOTIFICATION_CHANNEL_ID,
          channelName,
          NotificationManager.IMPORTANCE_NONE
        )
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager =
          (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(notificationChannel)

        val notification: Notification = notificationBuilder.setOngoing(true)
          .setContentTitle("Location Tracked")
          .setPriority(NotificationManager.IMPORTANCE_MIN)
          .setCategory(Notification.CATEGORY_SERVICE)
          .build()

        startForeground(2, notification)
      }else{
        val notification: Notification = notificationBuilder.setOngoing(true)
          .setContentTitle("Location Tracked")
          .build()

        startForeground(2, notification)

      }
      Log.d(tag, "Notification Setup for ${Build.VERSION.SDK_INT}")
    }

  override fun onBind(p0: Intent?): IBinder? {
    return null
  }

}
