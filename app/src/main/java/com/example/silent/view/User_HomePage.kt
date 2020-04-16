package com.example.silent.view

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.View.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.silent.ConnectionDetector
import com.example.silent.R
import com.example.silent.SwipetoDelete
import com.example.silent.adapters.LocationAdapter
import com.example.silent.db.Location
import com.example.silent.viewmodel.UserHomePageViewModel
import kotlinx.android.synthetic.main.user__home_page_fragment.*

class User_HomePage : Fragment(R.layout.user__home_page_fragment) {

    private var am: AudioManager? = null

    private var notificationManager: NotificationManager? = null



    private lateinit var cd: ConnectionDetector

    companion object {
        //fun newInstance() = User_HomePage()
        var navController: NavController? = null
        private const val ON_DO_NOT_DISTURB_CALLBACK_CODE = 5
    }

    private lateinit var viewModel: UserHomePageViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //println("user onactivityview created called")

        navController = Navigation.findNavController(view)


        cd = ConnectionDetector()

        fab.setOnClickListener {
            if(cd.isConnectingToInternet(requireContext()) && notificationManager?.isNotificationPolicyAccessGranted!!){
                view.findNavController().navigate(R.id.action_user_HomePage_to_mapFragment)
            }else{
                if(!notificationManager?.isNotificationPolicyAccessGranted!!)
                {
                    Toast.makeText(requireContext(),"Enable access to continue",Toast.LENGTH_LONG).show()
                }
                else
                {
                    Toast.makeText(requireContext(),"No Internet Connection",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        println("user onactivity created called user")

        am = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager?


        viewModel = ViewModelProvider(this).get(UserHomePageViewModel::class.java)

        recyclerview.layoutManager = LinearLayoutManager(this.context)
        recyclerview.setHasFixedSize(true)
        recyclerview.adapter = LocationAdapter(mutableListOf()) { location, s ->itemclicked(location,s) }

        observegetlocations()


        requestForDoNotDisturbPermissionOrSetDoNotDisturbForApi23AndUp()

        enable.setOnClickListener{
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivityForResult(intent,
                ON_DO_NOT_DISTURB_CALLBACK_CODE
            )
        }


    }

    private fun observegetlocations(){
        viewModel.getLocations()?.observe(this.viewLifecycleOwner, Observer {
            if(it.isNotEmpty()) {
                nolocations.visibility = INVISIBLE
                image.visibility = INVISIBLE
                nolocationview.visibility = INVISIBLE
                /*         recyclerview.layoutManager = LinearLayoutManager(this.context)
                         recyclerview.setHasFixedSize(true)
                         recyclerview.adapter = LocationAdapter(it as MutableList<Location>) { location, s ->itemclicked(location,s) }*/
                (recyclerview.adapter as LocationAdapter).addData(it as MutableList<Location>)
                //println("vineel sjlolololo")
            }else{
                //println("vineel sjwowowo")
                nolocations.visibility = VISIBLE
                image.visibility = VISIBLE
                nolocationview.visibility = VISIBLE
            }
            val itemTouchHelper = ItemTouchHelper(SwipetoDelete(recyclerview.adapter as LocationAdapter))
            itemTouchHelper.attachToRecyclerView(recyclerview);
            //ajsndkajsndas
        })
    }

    // Recyclerview onclick callback
    private fun itemclicked(task: Location, lol: String) {
        if (lol == "item") {
            val action =
                User_HomePageDirections.actionUserHomePageToLocationDetail(
                    task.id
                )
            view?.findNavController()?.navigate(action)
        }
        else if (lol == "switch" && task.status){
            viewModel.updateswitch(task.id,false)
        }
        else if (lol == "switch" && !task.status) {
            viewModel.updateswitch(task.id, true)
        }
       /* else if(lol == "swipedelete"){
            //println("data lol" + task)
            //viewModel.deleteLocation(task)
        }*/
        else if (lol == "swipeadd"){
            viewModel.addlocation(task)
        }
    }

    private fun requestForDoNotDisturbPermissionOrSetDoNotDisturbForApi23AndUp() {
        notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if(notificationManager?.isNotificationPolicyAccessGranted!!){
            permissionalert.visibility = GONE

        }else{
            permissionalert.visibility = VISIBLE
            permission.text = getString(R.string.Enable_Notification_Policy)
        }
    }

    override fun onResume() {
        super.onResume()
        observegetlocations()
        //println("user onactivity created called user resume")

        requestForDoNotDisturbPermissionOrSetDoNotDisturbForApi23AndUp()
    }

    override fun onPause() {
        println("user onactivity created called user pause")

        viewModel.getLocations()?.removeObservers(this.viewLifecycleOwner)
        super.onPause()
    }


    override fun onDestroy() {
        //viewModel.getLocations()?.removeObservers(this.viewLifecycleOwner)
        //recyclerview.adapter = null
        //recyclerview.layoutManager = null
        println("user home page over")
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ON_DO_NOT_DISTURB_CALLBACK_CODE) {
            this.requestForDoNotDisturbPermissionOrSetDoNotDisturbForApi23AndUp()
        }
    }
}