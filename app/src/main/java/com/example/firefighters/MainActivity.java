package com.example.firefighters;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.firefighters.tools.ConstantsValues;
import com.example.firefighters.ui.home.HomeFragment;
import com.example.firefighters.ui.main.MainFragment;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity implements HomeFragment.LoadPermissions {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize fresco
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.anim_scale_in, R.anim.anim_scale_in);
        ft.replace(R.id.main_frame_layout, new MainFragment()).addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ConstantsValues.SMS_PERMISSION_CODE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permissions refused", Toast.LENGTH_SHORT).show();
            } else {
                //Work
            }
        }
        if (requestCode == ConstantsValues.CALL_PERMISSION_CODE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Call permissions refused", Toast.LENGTH_SHORT).show();
            } else {
                //Work
            }
        }
        if (requestCode == ConstantsValues.SMS_PERMISSION_CODE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Sms permissions refused", Toast.LENGTH_SHORT).show();
            } else {
                //Work
            }
        }
    }

    @Override
    public void loadLocationPermissions() {

    }

    @Override
    public void loadCallPermissions() {

    }
}