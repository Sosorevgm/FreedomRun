package com.freedomrun.finish.running.model.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.freedomrun.R
import com.freedomrun.finish.running.model.MyPlaces
import com.freedomrun.finish.running.model.Results
import com.freedomrun.finish.running.model.api.APIService
import com.freedomrun.finish.running.model.api.Retrofitmpl
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_maps.bottom_navigation
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.StringBuilder

private const val MY_PERMISSION_CODE = 1000
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,com.google.android.gms.location.LocationListener {


    private lateinit var mMap: GoogleMap
    private lateinit var mGoogleApiClient: GoogleApiClient

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var mLastLocation: Location
    private lateinit var mMarker: Marker //= Marker()
    private lateinit var mLocationRequest: LocationRequest
//    private lateinit var mService: IGoogleAPIService
    private lateinit var mServiceKot: APIService
    private val retrofitmpl: Retrofitmpl = Retrofitmpl()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.d(this.toString(),"CoroutineException: $exception")
    }

    private var scope = CoroutineScope(
        SupervisorJob() +
                Dispatchers.IO +
                exceptionHandler
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
//       mService = Common.getGoogleAPIService()
//        mServiceKot = CommonKot.getAPIService()
//        mService = CommonKot.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission()
        }
//        bottom_navigation.setOnNavigationItemReselectedListener {
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_hospital -> {
                    nearByPlace("hospital")
                    true
                }
                R.id.action_market -> {
                    nearByPlace("market")
                    true
                }
                R.id.action_restaurant -> {
                    nearByPlace("restaurant")
                    true
                }
                R.id.action_school -> {
                    nearByPlace("school")
                    true
                }
                else -> true
            }
        }
    }

    private fun nearByPlace(placeType : String){
//        scope.launch {
            mMap.clear()
            val url: String = getUrl(latitude, longitude, placeType)

            retrofitmpl.getRetrofit()
               .getNearByPlaces(url)
//                mService.getNearByPlaces(url)
                .enqueue(object :
                    Callback<MyPlaces> {
                    override fun onResponse(
                        call: Call<MyPlaces>,
                        response: Response<MyPlaces>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            Log.d("TAG", "Зашел")
 //                           for (i in 0 ..(response.body()?.results?.size?.minus(1) ?: 0)) {
                            for (i in 0 until (response.body()?.results?.size ?: 0)) {
                                val markerOptions = MarkerOptions()
                                val googlePlace: Results = response.body()!!.results[i]
                                val lat = googlePlace.geometry.location.lat.toDouble()
                                val lng = googlePlace.geometry.location.lng.toDouble()
                                val placeName = googlePlace.name
                                var vicinity = googlePlace.vicinity
                                val latLng = LatLng(lat, lng)
                                markerOptions
                                    .position(latLng)
                                    .title(placeName)
                                if (placeName == "hospital")
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_hospital))
                                else if (placeName == "market")
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_shop))
                                else if (placeName == "school")
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_school))
                                else if (placeName == "restaurant")
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_restaurant))
                                else markerOptions.icon(
                                    BitmapDescriptorFactory.defaultMarker(
                                        BitmapDescriptorFactory.HUE_RED
                                    )
                                )
//                                mMap.addMarker(markerOptions)
//                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
//                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f))
                                mMarker = mMap.addMarker(markerOptions)
                                mMap.addMarker(markerOptions)
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11f))

                            }
                        } else {
                            Toast.makeText(this@MapsActivity, "Данных нет", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<MyPlaces>, t: Throwable) {

                    }
                })
//       }
    }

    private fun getUrl(latitude: Double, longitude: Double, placeType: String):String {
       val googlePlaceUrl = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
        googlePlaceUrl.append("location=$latitude,$longitude")
        googlePlaceUrl.append("&radius="+10000)
        googlePlaceUrl.append("&type=$placeType")
        googlePlaceUrl.append("&sensor=true")
        googlePlaceUrl.append("&key="+resources.getString(R.string.browser_key))
        Log.d("getUrl",googlePlaceUrl.toString())
        return googlePlaceUrl.toString()
    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_CODE)
            } else
            {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_CODE)
                return false
            }
        }
        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.isMyLocationEnabled = true;
            }
        }
        else {
            buildGoogleApiClient();
            mMap.isMyLocationEnabled = true;
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode)
        {
            MY_PERMISSION_CODE ->
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
              if  (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    mMap.isMyLocationEnabled

                }
            }
            else -> Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildGoogleApiClient(){
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
        mGoogleApiClient.connect()
    }

    override fun onConnected(p0: Bundle?) {
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)

        }
    }

    override fun onConnectionSuspended(p0: Int) {
        mGoogleApiClient.context
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    override fun onLocationChanged(p0: Location) {
//    override fun onLocationChanged(p0: Location) {
        mLastLocation = p0
//        mMarker.remove()
        latitude = p0.latitude
        longitude = p0.longitude

        val latLng = LatLng(latitude,longitude)
        val markerOptions =
            MarkerOptions()
                .position(latLng)
                .title("Я ищу способ взять текущую локацию")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        mMarker = mMap.addMarker(markerOptions)
        mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f))

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this)

    }
}
