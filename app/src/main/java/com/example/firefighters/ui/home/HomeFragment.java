package com.example.firefighters.ui.home;

import android.Manifest;
import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firefighters.R;
import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.tools.ConstantsValues;
import com.example.firefighters.tools.PermissionsManager;
import com.example.firefighters.ui.bottomsheetfragment.mapview.MapViewFragment;
import com.example.firefighters.ui.bottomsheetfragment.mapview.TestActivity;
import com.example.firefighters.viewmodels.EmergencyViewModel;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class HomeFragment extends Fragment {
    private final int phoneNumber = 000;
    EmergencyViewModel emergencyViewModel;
    CountDownTimer countDownTimerCall;
    CountDownTimer countDownTimerSos;
    //Relative layouts
    private LinearLayout sosBackground;
    //Image view
    private ImageView buttonSettings;
    private ImageView buttonHelp;
    //MaterialButton
    private MaterialButton buttonCallNow;
    private MaterialButton buttonShowMap;
    //Text View
    private TextView buttonSos;
    private Context context;
    private AppCompatActivity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = view.getContext();
        activity = (AppCompatActivity) getActivity();
        initViews(view);
        initViewModels(view);
        setAnimations();
        observeLiveData();
        return view;
    }

    private void observeLiveData() {
//        tryToGetCallPermissions(view, bottomSheet);
    }

    private void initViewModels(View view) {
        emergencyViewModel = new ViewModelProvider(requireActivity()).get(EmergencyViewModel.class);
        emergencyViewModel.init();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkInteractions(view);
    }

    private void setAnimations() {
        final int RED = 0xffF04545;
        final int TRANSPARENT = 0xffFFFFFF;
        ObjectAnimator colorAnim = ObjectAnimator.ofFloat(sosBackground, "alpha", 0, 1);
        colorAnim.setDuration(2000);
        colorAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        colorAnim.setEvaluator(new FloatEvaluator());
        colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
    }

    private void checkInteractions(View root) {
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialogSettings(v);
            }
        });
        buttonHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialogHelp(v);
            }
        });
        buttonCallNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialogCallNow(v);
            }
        });
        buttonSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialogSos(v);
            }
        });
        buttonShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMapView(v);
            }
        });
    }

    private void showMapView(View v) {
//        Intent myIntent = new Intent(activity, TestActivity.class);
//        myIntent.putExtra("key", "ras"); //Optional parameters
//        activity.startActivity(myIntent);
        MapViewFragment mapViewFragment = MapViewFragment.newInstance(context);
        mapViewFragment.show(activity.getSupportFragmentManager(), ConstantsValues.MAP_VIEW_TAG);
    }

    private void showBottomSheetDialogSettings(View view) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        bottomSheet.setContentView(R.layout.bottom_sheet_dialog_settings);
        bottomSheet.setCancelable(true);
        bottomSheet.setCanceledOnTouchOutside(true);
        bottomSheet.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        bottomSheet.show();
    }

    private void showBottomSheetDialogHelp(View view) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        bottomSheet.setContentView(R.layout.bottom_sheet_dialog_help);
        bottomSheet.setCancelable(true);
        bottomSheet.setCanceledOnTouchOutside(true);
        bottomSheet.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        bottomSheet.show();
    }

    //Show bottom sheet dialog send sos
    private void showBottomSheetDialogSos(View view) {
        BottomSheetDialog bottomSheetSos = new BottomSheetDialog(context);
        bottomSheetSos.setContentView(R.layout.bottom_sheet_dialog_sos);
        bottomSheetSos.setCancelable(false);
        bottomSheetSos.setCanceledOnTouchOutside(false);
        bottomSheetSos.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        bottomSheetSos.findViewById(R.id.button_add_media_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetFragmentAddMedia(v, bottomSheetSos);
            }
        });
        bottomSheetSos.findViewById(R.id.button_cancel_sos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetSos.dismiss();
                if (countDownTimerSos != null)
                    countDownTimerSos.cancel();
            }
        });
        bottomSheetSos.findViewById(R.id.button_get_permissions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetSos != null)
                    bottomSheetSos.dismiss();
                PermissionsManager.getInstance().requestLocationPermission(activity);
            }
        });
        tryToGetSosPermissions(view, bottomSheetSos);
        bottomSheetSos.show();
    }

    private void showBottomSheetFragmentAddMedia(View v, BottomSheetDialog bottomSheetSos) {
//        if (countDownTimerSos != null)
//            countDownTimerSos.cancel();
//        BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetDialogFragment();
//        bottomSheetDialogFragment.sho
    }

    private void tryToGetSosPermissions(View view, BottomSheetDialog bottomSheet) {
        int startCount = 5;
        if (PermissionsManager.getInstance().isLocationPermissions(activity)) {
            bottomSheet.findViewById(R.id.linear_no_permissions).setVisibility(View.GONE);
            bottomSheet.findViewById(R.id.linear_waiting).setVisibility(View.VISIBLE);
            MaterialTextView text = (MaterialTextView) bottomSheet.findViewById(R.id.text_progression);
            countDownTimerSos = new CountDownTimer(6000, 1000) {
                public void onTick(long millisUntilFinished) {
                    text.setText("" + millisUntilFinished / 1000);
                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {
                    text.setText("");
                    sendSOS();
                    bottomSheet.dismiss();
                }

            };
            countDownTimerSos.start();
        } else {
            bottomSheet.findViewById(R.id.linear_no_permissions).setVisibility(View.VISIBLE);
            bottomSheet.findViewById(R.id.linear_waiting).setVisibility(View.GONE);
        }
    }

    private void sendSOS() {
        getLocation();
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(context)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(context)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            EmergencyModel emergencyModel = new EmergencyModel();
                            emergencyModel.setLongitude(locationResult.getLocations().get(locationResult.getLocations().size() - 1).getLongitude());
                            emergencyModel.setLatitude(locationResult.getLocations().get(locationResult.getLocations().size() - 1).getLatitude());
                            emergencyViewModel.uploadEmergency(emergencyModel, activity);
                        } else {
                            Toast.makeText(context, "Not sent", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLocationAvailability(LocationAvailability locationAvailability) {
                        super.onLocationAvailability(locationAvailability);
                    }
                }, Looper.getMainLooper());
    }

    //Show bottom sheet dialog for call
    private void showBottomSheetDialogCallNow(View view) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        bottomSheet.setContentView(R.layout.bottom_sheet_dialog_cal_now);
        bottomSheet.setCancelable(false);
        bottomSheet.setCanceledOnTouchOutside(false);
        bottomSheet.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        bottomSheet.findViewById(R.id.button_cancel_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
                if (countDownTimerCall != null)
                    countDownTimerCall.cancel();
            }
        });
        bottomSheet.findViewById(R.id.button_get_permissions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheet != null)
                    bottomSheet.dismiss();
                PermissionsManager.getInstance().requestCallPermission(activity);
            }
        });
        tryToGetCallPermissions(view, bottomSheet);
        bottomSheet.show();
    }

    private void tryToGetCallPermissions(View view, BottomSheetDialog bottomSheet) {
        int startCount = 5;
        if (PermissionsManager.getInstance().isCallPermissions(activity)) {
            bottomSheet.findViewById(R.id.linear_no_permissions).setVisibility(View.GONE);
            bottomSheet.findViewById(R.id.linear_waiting).setVisibility(View.VISIBLE);
            MaterialTextView text = (MaterialTextView) bottomSheet.findViewById(R.id.text_progression);
            countDownTimerCall = new CountDownTimer(6000, 1000) {
                public void onTick(long millisUntilFinished) {
                    text.setText("" + millisUntilFinished / 1000);
                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {
                    text.setText("");
                    callNow();
                    bottomSheet.dismiss();
                }

            };
            countDownTimerCall.start();
        } else {
            bottomSheet.findViewById(R.id.linear_no_permissions).setVisibility(View.VISIBLE);
            bottomSheet.findViewById(R.id.linear_waiting).setVisibility(View.GONE);
        }
    }

    private void callNow() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, ConstantsValues.CALL_PERMISSION_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        }
    }

    private void initViews(View view) {
        sosBackground = view.findViewById(R.id.relative_sos_background);

        //Buttons
        buttonSettings = view.findViewById(R.id.button_image_settings);
        buttonHelp = view.findViewById(R.id.button_image_help);
        buttonCallNow = view.findViewById(R.id.button_call_now);
        buttonSos = view.findViewById(R.id.button_text_sos);
        buttonShowMap = view.findViewById(R.id.button_show_map);
    }

    public interface LoadPermissions {
        void loadLocationPermissions();

        void loadCallPermissions();
    }
}