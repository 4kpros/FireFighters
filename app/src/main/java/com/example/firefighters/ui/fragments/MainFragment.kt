package com.example.firefighters.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.firefighters.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_main, container, false)

        //Get view id
        val navigation: BottomNavigationView =
            view.findViewById<View>(R.id.nav_view) as BottomNavigationView

        //Listen for navigation changes
        navigation.setOnItemSelectedListener {
            var result = false
            val fm: FragmentManager = requireActivity().supportFragmentManager
            val ft: FragmentTransaction = fm.beginTransaction()
            when (it.itemId) {
                R.id.navigation_home -> {
                    ft.replace(R.id.nav_host_fragment, HomeFragment()).commit()
                    result = true
                }
                R.id.navigation_profile -> {
                    ft.replace(R.id.nav_host_fragment, ProfileFragment()).commit()
                    result = true
                }
                R.id.navigation_emergency -> {
                    ft.replace(R.id.nav_host_fragment, EmergencyFragment())
                        .commit()
                    result = true
                }
            }
            result
        }
        return view
    }
}