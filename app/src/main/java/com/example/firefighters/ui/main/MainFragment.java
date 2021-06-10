package com.example.firefighters.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.firefighters.R;
import com.example.firefighters.ui.emergency.EmergencyFragment;
import com.example.firefighters.ui.home.HomeFragment;
import com.example.firefighters.ui.mapview.MapViewFragment;
import com.example.firefighters.ui.profile.ProfileFragment;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

public class MainFragment extends Fragment {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if(item.getItemId() == R.id.navigation_home) {
                fragmentTransaction.replace(R.id.nav_host_fragment, new HomeFragment()).commit();
                return true;
            }else if (item.getItemId() == R.id.navigation_profile){
                fragmentTransaction.replace(R.id.nav_host_fragment, new ProfileFragment()).commit();
                return true;
            }else if (item.getItemId() == R.id.navigation_emergency){
                fragmentTransaction.replace(R.id.nav_host_fragment, new EmergencyFragment()).commit();
                return true;
            }
            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        BottomNavigationView navigation = (BottomNavigationView) view.findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        BottomNavigationView navView = view.findViewById(R.id.nav_view);
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_emergency, R.id.navigation_profile)
//                .build();
//        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
////        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);
        return view;
    }
}