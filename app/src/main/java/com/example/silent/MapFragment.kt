package com.example.silent

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity.RESULT_ERROR
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlinx.android.synthetic.main.bottom_sheet.view.add
import kotlinx.android.synthetic.main.bottom_sheet.view.address
import kotlinx.android.synthetic.main.bottom_sheet.view.chip6
import kotlinx.android.synthetic.main.bottom_sheet.view.chipGroup
import kotlinx.android.synthetic.main.bottom_sheet.view.chipGroup2
import kotlinx.android.synthetic.main.bottom_sheet.view.close
import kotlinx.android.synthetic.main.bottom_sheet.view.name
import kotlinx.android.synthetic.main.map_fragment.*


class MapFragment : Fragment(), OnMapReadyCallback {

    private var stat: Boolean = true

    // projection screen
    private lateinit var projection: Projection
    private  var markerPosition: LatLng? = null
    private  lateinit var markerPoint: Point
    private lateinit var targetPoint: Point
    private lateinit var targetPosition: LatLng



    // google places
    private lateinit var place: Place

    //map
    private lateinit var map: GoogleMap


    // bottom sheet
    private lateinit var sheetBehavior: BottomSheetBehavior<View>
    private lateinit var bsv: View

    // current user location
    private var currentLatLng: LatLng? = null

    // user search location
    private var searchLatLng: LatLng? = null

    // circle and marker for map
    private lateinit var circle: Circle
    private lateinit var myMarker: Marker

    // viewmodel
    private lateinit var viewModel: MapViewModel


    companion object {
        //fun newInstance() = MapFragment()
        private const val AUTOCOMPLETE_REQUEST_CODE = 2

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.map_fragment, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)



        bsv = view.findViewById(R.id.bottom_sheet)


        //add button
        bsv.add.setOnClickListener {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)
        }

        // cancel button
        bsv.close.setOnClickListener {

            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            sheetBehavior.setPeekHeight(0,true)

            map.clear()

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))

            searchLatLng = null

            viewModel.add()
        }

        // mode selection call back
        bsv.chipGroup2.setOnCheckedChangeListener { group, _ ->
            for (i in 0 until group.childCount) {
                val chip = group.getChildAt(i)
                chip.isClickable = chip.id != group.checkedChipId
            }
        }


        // radius selection callback
        bsv.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
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

            val chip: Chip = bsv.findViewById(checkedId)
            for (i in 0 until chipGroup.childCount) {
                chipGroup.getChildAt(i).isClickable = true
            }
            chip.isClickable = false
        }



        // bottomsheet setup
        sheetBehavior = BottomSheetBehavior.from(bsv)
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


        // search interface
        fab.setOnClickListener {
            if(searchLatLng != null){
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            loadPlacePicker()

        }

        // current location
        current.setOnClickListener{
            if(searchLatLng == null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
            }
            else{

                projectionscreen(6,view.height)

            }
        }

    }




    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // places initialization
        Places.initialize(context!!, Constants.api_key)


        // bottomsheet callback
        sheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(view: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)

                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {

                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        bsv.remove.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)

                        projectionscreen(2,view.height)

                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        bsv.remove.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }
                    BottomSheetBehavior.STATE_SETTLING -> {

                    }
                }
            }

        })

        // viewmodel
        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)



    }


    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap!!

        map.uiSettings.isMyLocationButtonEnabled = true

        setUpMap()
    }


    private fun setUpMap() {

        map.isMyLocationEnabled = true
        map.mapType = GoogleMap.MAP_TYPE_NORMAL

        map.uiSettings.isMyLocationButtonEnabled = true

        startLocationUpdates()

        // marker on click to show title of the marker
        map.setOnMarkerClickListener {
            myMarker.showInfoWindow()

            true
        }
    }

    private fun startLocationUpdates(){
        // observing changes to live location
        viewModel.getLocationData().observe(this, androidx.lifecycle.Observer {
            currentLatLng = LatLng(it.latitude, it.longitude)
            setonce()
        })
    }

    private fun stopLocationUpdates(){
        // removing location updates
        viewModel.remove()
    }



    private fun setonce() {
        if(stat) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            stat = false
        }
    }

    private fun loadPlacePicker() {

        val intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, Constants.fields
            )
            .build(context!!)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
}


    // callback after place selection
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == AUTOCOMPLETE_REQUEST_CODE){
            if (resultCode == RESULT_OK ){

                stopLocationUpdates()

                map.clear()
                // setting bottomsheet peek height
                sheetBehavior.setPeekHeight(Constants.peekheight,true)


                place = Autocomplete.getPlaceFromIntent(data!!)
                searchLatLng = place.latLng

                addMarker(place.latLng!!,place.name!!)

                addCircle(place.latLng!!)


                bottomsheetSetupAfterplacesearch(place.name.toString(),place.address.toString())
            }

            }
            else if (resultCode == RESULT_ERROR){
                val status: Status = Autocomplete.getStatusFromIntent(data!!)
                println("error$status")
            }
            else if (resultCode == RESULT_CANCELED) {
                println("user exit")
            }
        }

    // adding marker
    private fun addMarker(l1: LatLng,name: String){
        myMarker = map.addMarker(
            MarkerOptions()
                .position(l1)
                .title(name)
                .snippet(name)
        )
    }

    // adding radius around location place
    private fun addCircle(l1: LatLng){
        circle = map.addCircle(
            CircleOptions()
                .center(l1)
                .radius(Constants.mile50)
                .fillColor(Color.parseColor(getString(R.string.CircleFillColor)))
                .strokeColor(Color.parseColor(getString(R.string.CircleStrokeColor)))
        )
    }

    private fun bottomsheetSetupAfterplacesearch(name: String, address: String){
        bsv.chip6.isChecked = true
        bsv.silent.isChecked = true
        bsv.name.text = name
        bsv.address.text = address
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun projectionscreen(divide: Int , height: Int){
        projection = map.projection
        markerPosition = searchLatLng
        markerPoint = projection.toScreenLocation(markerPosition)
        targetPoint = Point(markerPoint.x, markerPoint.y - height / divide )
        targetPosition = projection.fromScreenLocation(targetPoint)

        map.animateCamera(CameraUpdateFactory.newLatLng(targetPosition), 1000, null)
    }

}
