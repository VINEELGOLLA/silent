package com.example.silent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.silent.adapters.LocationAdapter
import com.example.silent.db.Location
import kotlinx.android.synthetic.main.user__home_page_fragment.*



class User_HomePage : Fragment(R.layout.user__home_page_fragment), LocationAdapter.ClickListener {

    private lateinit var recyclerView: RecyclerView


    companion object {
        //fun newInstance() = User_HomePage()
        var navController: NavController? = null
    }

    private lateinit var viewModel: UserHomePageViewModel



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("user onactivityview created called")


        navController = Navigation.findNavController(view)

        recyclerView = view.findViewById(R.id.recyclerview)


        fab.setOnClickListener {
            navController!!.navigate(R.id.action_user_HomePage_to_mapFragment)
        }

        /*CoroutineScope(Dispatchers.IO).launch {
            context.let {
                var lol = LocationDatabase(it!!).locationDao().getAllLocations()

                println("downloaded loc")
                println(lol)

                withContext(Dispatchers.Main) {
                    recyclerView.layoutManager = LinearLayoutManager(activity)
                    recyclerView.adapter = LocationAdapter(lol, this@User_HomePage)
                }

            }
        }*/

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        println("user onactivity created called")

        viewModel = ViewModelProvider(this).get(UserHomePageViewModel::class.java)


        viewModel.getLocations()?.observe(viewLifecycleOwner, Observer {
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.adapter = LocationAdapter(it, this@User_HomePage)
        })
    }

    override fun onPause() {
        super.onPause()
        println("onpause called")
    }

    override fun itemclicked(task: Location) {
        println(task)
        //viewModel.deleteLocation(task)
        /*val bundle = bundleOf("location" to task.id)
        view?.findNavController()?.navigate(R.id.action_user_HomePage_to_location_detail, bundle)
*/
        val action = User_HomePageDirections.actionUserHomePageToLocationDetail(task.id)
        view?.findNavController()?.navigate(action)


    }

}
