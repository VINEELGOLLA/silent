package com.example.silent.view

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.silent.Constants
import com.example.silent.R
import com.example.silent.db.Location
import com.example.silent.viewmodel.LocationDetailViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.location_detail_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class Location_detail : Fragment(R.layout.location_detail_fragment), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private lateinit var locationid: String

    // circle and marker for map
    private lateinit var circle: Circle
    private lateinit var myMarker: Marker

    private lateinit var location: Location


    companion object {
        //fun newInstance() = Location_detail()

        var navController: NavController? = null


    }

    private lateinit var viewModel: LocationDetailViewModel



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)



        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        chip7.isEnabled = false
        chip6.isEnabled = false
        chip8.isEnabled = false
        chip9.isEnabled = false

        silent1.isEnabled = false
        vibrate1.isEnabled = false
        normal1.isEnabled = false

        notify1.isEnabled = false





        chipGroup.isSingleSelection = true



        chipGroup.setOnCheckedChangeListener{_, checkedid ->
            if(::circle.isInitialized) {
                when (checkedid) {
                    chip6.id -> {
                        circle.radius = Constants.mile50
                    }
                    chip7.id -> {
                        circle.radius = Constants.mile100
                    }
                    chip8.id -> {
                        circle.radius = Constants.mile150
                    }
                    chip9.id -> {
                        circle.radius = Constants.mile200
                    }
                }
            }

            val chip: Chip = view.findViewById(checkedid)
                for (i in 0 until chipGroup.childCount) {
                    chipGroup.getChildAt(i).isClickable = true
                }
                        chip.isClickable = false
        }


        chipGroup_loc.isSingleSelection = true


        chipGroup_loc.setOnCheckedChangeListener { group, _ ->
            println("ringer list")
            //updateview()
            for (i in 0 until group.childCount) {
                val chip = group.getChildAt(i)
                chip.isClickable = chip.id != group.checkedChipId
            }

        }

        edit.setOnClickListener{
            edit.visibility = View.GONE
            gone.visibility = View.VISIBLE
            tick.visibility = View.VISIBLE

            chip7.isEnabled = true
            chip6.isEnabled = true
            chip8.isEnabled = true
            chip9.isEnabled = true


            silent1.isEnabled = true
            vibrate1.isEnabled = true
            normal1.isEnabled = true

            notify1.isEnabled = true

            back.visibility = View.GONE

            toolbartext.text = ""


        }

        gone.setOnClickListener{
            tick.visibility = View.GONE
            gone.visibility = View.GONE
            edit.visibility = View.VISIBLE
            setUpData()

            chip7.isEnabled = false
            chip6.isEnabled = false
            chip8.isEnabled = false
            chip9.isEnabled = false


            silent1.isEnabled = false
            vibrate1.isEnabled = false
            normal1.isEnabled = false

            notify1.isEnabled = false

            toolbartext.text = location.name

            back.visibility = View.VISIBLE


        }

        tick.setOnClickListener{
            tick.visibility = View.GONE
            gone.visibility = View.GONE
            edit.visibility = View.VISIBLE

            chip7.isEnabled = false
            chip6.isEnabled = false
            chip8.isEnabled = false
            chip9.isEnabled = false


            silent1.isEnabled = false
            vibrate1.isEnabled = false
            normal1.isEnabled = false

            notify1.isEnabled = false

            //toolbartext.visibility = View.VISIBLE

            back.visibility = View.VISIBLE

            updatedata()

        }


        status.setOnCheckedChangeListener { _, isChecked ->
            //println("switch called ksn$isChecked")
            //status.isChecked = !isChecked
            when(isChecked){
                false -> {viewModel.updateswitch(location.id,false) }
                true -> {viewModel.updateswitch(locationid,true) }
            }
        }

        back.setOnClickListener{
            //navController?.navigate(R.id.action_location_detail_to_user_HomePage)
            activity!!.onBackPressed()
        }


    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //locationid = arguments?.getString("location").toString()
        locationid = Location_detailArgs.fromBundle(this.arguments!!).location



        pluscode.setOnClickListener {

            val snack = Snackbar.make(it,"open in Google maps",Snackbar.LENGTH_LONG)
            snack.setActionTextColor(Color.WHITE)
            snack.setAction("Okay") {
                // executed when DISMISS is clicked
                //println("Snackbar Set Action - OnClick2.")
                val gmmIntentUri: Uri = Uri.parse("http://plus.codes/" +location.plusCode)
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)

            }
            snack.show()

        }

        viewModel = ViewModelProvider(this).get(LocationDetailViewModel::class.java)

        observegetlocation()

        delete.setOnClickListener {
            viewModel.deleteLocation(location)

            //navController?.navigate(R.id.action_location_detail_to_user_HomePage)
            //view?.findNavController()?.navigate(R.id.action_location_detail_to_user_HomePage)

            activity!!.onBackPressed()

        }
    }

    private fun observegetlocation(){
        viewModel.getlocation(locationid)?.observe(viewLifecycleOwner, Observer {
            if (it != null){
                location = it
                //println("lololo $location")
                setUpData()
            }
        })
    }

    private fun updatedata(){

        var radius = 50.0
        var mode = 0

        when (chipGroup.checkedChipId) {
            chip6.id -> {
                radius = Constants.mile50
            }
            chip7.id -> {
                radius = Constants.mile100
            }
            chip8.id -> {
                radius = Constants.mile150
            }
            chip9.id -> {
                radius = Constants.mile200
            }
        }

        when (chipGroup_loc.checkedChipId) {
            silent1.id -> {
                mode = 0
            }
            vibrate1.id -> {
                mode = 1
            }
            normal1.id -> {
                mode = 2
            }
        }

        val statusl: Boolean = when (status.isChecked) {
            true -> {
                true
            }
            false -> {
                false
            }
        }

        val notifyl: Boolean = when (notify1.isChecked) {
            true -> {
                true
            }
            false -> {
                false
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            context.let {
                val loc =Location(
                    location.id,
                    location.name,
                    location.address,
                    radius,
                    mode,
                    notifyl,
                    location.lat,
                    location.lng,
                    location.plusCode,
                    statusl
                )

                viewModel.updateLocation(loc)
            }
        }

    }

    private fun setUpData(){
        toolbartext.text = location.name
        name.text = location.name
        address.text = location.address
        if(location.plusCode != "null"){
            pluscode.text = location.plusCode
        }
        else{
            pluscode.visibility = View.GONE
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.lat,location.lng),14f))
        addMarker(LatLng(location.lat,location.lng),location.name)


        when (location.radius) {
            Constants.mile50 -> {
                chip6.isChecked = true
                addCircle(LatLng(location.lat,location.lng),
                    Constants.mile50
                )

            }
            Constants.mile100 -> {
                chip7.isChecked = true
                addCircle(LatLng(location.lat,location.lng),
                    Constants.mile100
                )

            }
            Constants.mile150 -> {
                chip8.isChecked = true
                addCircle(LatLng(location.lat,location.lng),
                    Constants.mile150
                )

            }
            Constants.mile200 -> {
                chip9.isChecked = true
                addCircle(LatLng(location.lat,location.lng),
                    Constants.mile200
                )

            }
        }

        when (location.mode) {
            0 -> {
                silent1.isChecked = true
            }
            1 -> {
                vibrate1.isChecked = true
            }
            2 -> {
                normal1.isChecked = true
            }
        }

        when (location.notify) {
            true -> {
                notify1.isChecked = true
            }
            false -> {
                notify1.isChecked = false
            }
        }

        when (location.status) {
            true -> {
                status.isChecked = true
            }
            false -> {
                status.isChecked = false
            }
        }

    }

    // adding marker
    private fun addMarker(l1: LatLng,name: String){
        myMarker = map.addMarker(
            MarkerOptions()
                .position(l1)
                .title(name)
                //.snippet(name)

        )
    }




    // adding radius around location place
    private fun addCircle(l1: LatLng, radius: Double){
        circle = map.addCircle(
            CircleOptions()
                .center(l1)
                .radius(radius)
                .fillColor(Color.parseColor(getString(R.string.CircleFillColor)))
                .strokeColor(Color.parseColor(getString(R.string.CircleStrokeColor)))
        )
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        //println("onMapReady called")

        map = googleMap!!

        map.uiSettings.isScrollGesturesEnabled = false


        map.uiSettings.isMyLocationButtonEnabled = true

        setUpMap()
    }

    private fun setUpMap() {
        //println("setUpMap called")

        map.isMyLocationEnabled = true
        map.mapType = GoogleMap.MAP_TYPE_NORMAL

        map.uiSettings.isMyLocationButtonEnabled = true

    }

    override fun onResume() {
        observegetlocation()
        super.onResume()
    }

    override fun onStop() {
        viewModel.getlocation(locationid)?.removeObservers(viewLifecycleOwner)
        super.onStop()
    }

    override fun onPause() {
        viewModel.getlocation(locationid)?.removeObservers(viewLifecycleOwner)
        super.onPause()
    }


    override fun onDestroy() {
        //viewModel.getlocation(locationid)?.removeObservers(viewLifecycleOwner)
        println("location destroyed")
        super.onDestroy()
    }
}
