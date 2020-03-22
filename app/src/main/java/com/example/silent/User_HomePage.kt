package com.example.silent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.user__home_page_fragment.*


class User_HomePage : Fragment() {

    companion object {
        fun newInstance() = User_HomePage()
        var navController: NavController? = null
    }

    private lateinit var viewModel: UserHomePageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user__home_page_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        fab.setOnClickListener {
            navController!!.navigate(R.id.action_user_HomePage_to_mapFragment)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UserHomePageViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
