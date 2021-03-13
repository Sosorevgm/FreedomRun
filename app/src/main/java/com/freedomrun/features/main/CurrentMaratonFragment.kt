package com.freedomrun.features.main

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.freedomrun.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CurrentMaratonFragment : Fragment(), OnMapReadyCallback {

    lateinit var client: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_maraton, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val mapFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        client = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        val map = googleMap

        if(checkLocationPermission()) {
            client.lastLocation.addOnCompleteListener {
                val latitude = it.result?.latitude
                val longitude = it.result?.longitude

                val pos = LatLng(latitude!!, longitude!!)

                map?.addMarker(MarkerOptions().position(pos).title("My name"))

                Toast.makeText(requireContext(), "My Location: " + latitude.toString() + ", " + longitude.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        var state = false
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if( (requireActivity().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && (requireActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)) == PackageManager.PERMISSION_GRANTED) {
                    state = true
            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                 android.Manifest.permission.ACCESS_FINE_LOCATION), 1000)
            }
        }else {
            state = true
        }
        return state
    }
}