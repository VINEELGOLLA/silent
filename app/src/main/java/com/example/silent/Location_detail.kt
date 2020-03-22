package com.example.silent

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.location_detail_fragment.*
import kotlinx.android.synthetic.main.location_detail_fragment.chipGroup


class Location_detail : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private var currentLatLng: LatLng? = null


    companion object {
        //fun newInstance() = Location_detail()
    }

    private lateinit var viewModel: LocationDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.location_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        chipGroup.isSingleSelection = true





        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            val chip: Chip = view.findViewById(checkedId)
            for (i in 0 until chipGroup.childCount) {
                chipGroup.getChildAt(i).isClickable = true
            }
            chip.isClickable = false
        }

        chipGroup_loc.isSingleSelection = true


        chipGroup_loc.setOnCheckedChangeListener { group, _ ->
            for (i in 0 until group.childCount) {
                val chip = group.getChildAt(i)
                chip.isClickable = chip.id != group.checkedChipId
            }

        }


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)




        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        pluscode.setOnClickListener {

            val snack = Snackbar.make(it,"open in Google maps",Snackbar.LENGTH_LONG)
            snack.setAction("Okay") {
                // executed when DISMISS is clicked
                println("Snackbar Set Action - OnClick2.")
                val gmmIntentUri: Uri = Uri.parse("http://plus.codes/8774+J6 Charlotte, NC")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)

            }
            snack.show()


        }

        viewModel = ViewModelProvider(this).get(LocationDetailViewModel::class.java)
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        println("onMapReady called")

        map = googleMap!!

        map.uiSettings.isScrollGesturesEnabled = false


        map.uiSettings.isMyLocationButtonEnabled = true


        setUpMap()
    }

    private fun setUpMap() {
        println("setUpMap called")



        map.isMyLocationEnabled = true
        map.mapType = GoogleMap.MAP_TYPE_NORMAL

        map.uiSettings.isMyLocationButtonEnabled = true


        fusedLocationClient.lastLocation.addOnSuccessListener {

            if (it != null){

                lastLocation = it
                println(it)
                currentLatLng = LatLng(it.latitude, it.longitude)
                //map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))


            }
        }
    }

}
