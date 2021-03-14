package com.freedomrun.features.main

import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.freedomrun.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MapsFragment : Fragment(), OnMapReadyCallback {

    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()

    private lateinit var mLastLocation: Location
    private var mMarker: Marker? = null

    //Location
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    private lateinit var mMap: GoogleMap

    companion object {
        private const val MY_PERMISSION_CODE: Int = 1000
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocatianPermission()) {
                buildLocationRequest()
                buildLocationCallback()

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                    requireActivity()
                )
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )
            }
        }else {
            buildLocationRequest()
            buildLocationCallback()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                requireActivity()
            )
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        }
    }

    private fun buildLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                mLastLocation = p0!!.locations.get(p0!!.locations.size - 1) //Get last location

                if(mMarker != null) {mMarker!!.remove()}
                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude
                val latLng = LatLng(latitude, longitude)
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("Your position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                mMarker = mMap!!.addMarker(markerOptions)

                //Move Camera
                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap!!.animateCamera(CameraUpdateFactory.zoomTo(11f))
            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    private fun checkLocatianPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ))
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSION_CODE
                )
            else
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSION_CODE
                )
            return false
        }
        else
            return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode)
        {
            MY_PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            requireActivity(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                        if (checkLocatianPermission()) {
                            buildLocationRequest()
                            buildLocationCallback()

                            fusedLocationProviderClient =
                                LocationServices.getFusedLocationProviderClient(
                                    requireActivity()
                                )
                            fusedLocationProviderClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.myLooper()
                            )
                            mMap!!.isMyLocationEnabled = true
                        }
                } else {
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //Init Google play services
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
            {
                mMap!!.isMyLocationEnabled=true
            }
        }
        else
            mMap!!.isMyLocationEnabled=true

        mMap.uiSettings.isZoomControlsEnabled=true
    }

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {
        val strOrigin = "origin=" + origin.latitude + "," + origin.longitude
        val strDestination = "destination=" + dest.latitude + "," + dest.longitude
        val sensor = "sensor=false"
        val mode = "mode=walking"

        val parameters = "$strOrigin&$strDestination&$sensor&$mode"

        val output = "json"

        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }

    @Throws(IOException::class)
    fun downloadUrl(strUrl: String): String? {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()
            iStream = urlConnection.inputStream
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String? = ""
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            data = sb.toString()
            br.close()
        } catch (e: Exception) {
            Log.d("Exception", e.toString())
        } finally {
            iStream?.close()
            urlConnection?.disconnect()
        }
        return data
    }
}