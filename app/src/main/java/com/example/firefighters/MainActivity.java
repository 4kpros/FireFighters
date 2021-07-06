package com.example.firefighters;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.models.UserModel;
import com.example.firefighters.service.NotificationService;
import com.example.firefighters.tools.ConstantsValues;
import com.example.firefighters.tools.FirebaseManager;
import com.example.firefighters.ui.main.MainFragment;
import com.example.firefighters.viewmodels.EmergencyViewModel;
import com.example.firefighters.viewmodels.UserViewModel;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private EmergencyViewModel emergencyViewModel;
    private NotificationManagerCompat notificationManagerCompat;
    private String CHANNEL_ID = "FIRE_FIGHTER_CHANNEL_1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize fresco
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        //Set theme
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String theme = sharedPref.getString(getString(R.string.save_theme), "");
        if (theme.equals("dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        loadGeneralInfo();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ConstantsValues.SMS_PERMISSION_CODE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permissions refused !", Toast.LENGTH_SHORT).show();
            } else {
                //Work
            }
        }
        if (requestCode == ConstantsValues.CALL_PERMISSION_CODE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Call permissions refused !", Toast.LENGTH_SHORT).show();
            } else {
                //Work
            }
        }
        if (requestCode == ConstantsValues.SMS_PERMISSION_CODE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Sms permissions refused !", Toast.LENGTH_SHORT).show();
            } else {
                //Work
            }
        }
        if (requestCode == ConstantsValues.AUDIO_RECORD_PERMISSION_CODE){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Record audio permissions refused !", Toast.LENGTH_SHORT).show();
            } else {
                //Work
            }
        }
        if (requestCode == ConstantsValues.CAMERA_PERMISSION_CODE){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permissions refused !", Toast.LENGTH_SHORT).show();
            } else {
                //Work
            }
        }
    }

    private void loadGeneralInfo(){
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.init();
        emergencyViewModel = new ViewModelProvider(this).get(EmergencyViewModel.class);
        emergencyViewModel.init();
        if (FirebaseManager.getInstance().getCurrentAuthUser() == null){
            loadViews();
        }else {
            userViewModel.loadUserModel(FirebaseManager.getInstance().getCurrentAuthUser().getEmail()).observe(this, new Observer<UserModel>() {
                @Override
                public void onChanged(UserModel userModel) {
                    if (userModel != null) {
                        ConstantsValues.setIsFirefighter(userModel.isFireFighter());
                        ConstantsValues.setIsChief(userModel.isChief());
                        ConstantsValues.setUnit(userModel.getUnit());
                    }
                    loadViews();
//                    startService(new Intent(getApplication(), NotificationService.class));
                    startListener();
                }
            });
        }
    }

    private void startListener() {
        emergencyViewModel.getEmergenciesQuerySnapshot(null, null).observe(this, new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null){
                    if (queryDocumentSnapshots.getDocumentChanges().size() > 0){
                        for (DocumentChange document:queryDocumentSnapshots.getDocumentChanges()) {
                            EmergencyModel emergencyModel = document.getDocument().toObject(EmergencyModel.class);
                            notifyUser(emergencyModel);
                        }
                    }
                }
            }
        });
    }

    private void notifyUser(EmergencyModel emergencyModel) {
        notificationManagerCompat = NotificationManagerCompat.from(this);
        String tempMessage = "EM "+emergencyModel.getId();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Nouveau message de sensibilisation")
                .setContentText(tempMessage)
                .setSmallIcon(R.drawable.ic_baseline_fireplace_24)
                .setChannelId(CHANNEL_ID)
                .setAutoCancel(true);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT > 26) {
            NotificationChannel channel = null;
            channel = new NotificationChannel(CHANNEL_ID, "covid_app", NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
        }

        manager.notify(0, builder.build());
    }

    private void loadViews(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.anim_scale_in, R.anim.anim_scale_in);
        ft.replace(R.id.main_frame_layout, new MainFragment()).addToBackStack(null);
        ft.commit();
    }

}