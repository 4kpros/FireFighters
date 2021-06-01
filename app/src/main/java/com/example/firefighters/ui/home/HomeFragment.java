package com.example.firefighters.ui.home;

import android.Manifest;
import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.firefighters.R;
import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.tools.ConstantsValues;
import com.example.firefighters.tools.PermissionsManager;
import com.example.firefighters.viewmodels.EmergencyViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

public class HomeFragment extends Fragment {
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
    private Activity activity;

    EmergencyViewModel emergencyViewModel;
    private final int phoneNumber = 000;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = view.getContext();
        activity = getActivity();
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
        //
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
        bottomSheetSos.findViewById(R.id.button_cancel_sos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetSos.dismiss();
            }
        });
        bottomSheetSos.findViewById(R.id.button_get_permissions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionsManager.getInstance().requestLocationPermission(activity);
            }
        });
        tryToGetSosPermissions(view, bottomSheetSos);
        bottomSheetSos.show();
    }
    private void tryToGetSosPermissions(View view, BottomSheetDialog bottomSheet) { int startCount = 5;
        if(PermissionsManager.getInstance().isLocationPermissions(activity)){
            bottomSheet.findViewById(R.id.linear_no_permissions).setVisibility(View.GONE);
            bottomSheet.findViewById(R.id.linear_waiting).setVisibility(View.VISIBLE);
            MaterialTextView text = (MaterialTextView) bottomSheet.findViewById(R.id.text_progression);
            new CountDownTimer(6000, 1000) {
                public void onTick(long millisUntilFinished) {
                    text.setText("" + millisUntilFinished / 1000);
                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {
                    text.setText("");
                    sendSOS();
                    bottomSheet.dismiss();
                }

            }.start();
        }else{
            bottomSheet.findViewById(R.id.linear_no_permissions).setVisibility(View.VISIBLE);
            bottomSheet.findViewById(R.id.linear_waiting).setVisibility(View.GONE);
        }
    }
    private void sendSOS() {
        EmergencyModel emergencyModel = new EmergencyModel();
        emergencyModel.setLongitude(1);
        emergencyModel.setLatitude(1);
        emergencyModel.setSenderId(1);
        emergencyViewModel.uploadEmergency(emergencyModel, activity);
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
            }
        });
        bottomSheet.findViewById(R.id.button_get_permissions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionsManager.getInstance().requestCallPermission(activity);
            }
        });
        tryToGetCallPermissions(view, bottomSheet);
        bottomSheet.show();
    }
    private void tryToGetCallPermissions(View view, BottomSheetDialog bottomSheet) {
        int startCount = 5;
        if(PermissionsManager.getInstance().isCallPermissions(activity)){
            bottomSheet.findViewById(R.id.linear_no_permissions).setVisibility(View.GONE);
            bottomSheet.findViewById(R.id.linear_waiting).setVisibility(View.VISIBLE);
            MaterialTextView text = (MaterialTextView) bottomSheet.findViewById(R.id.text_progression);
            new CountDownTimer(6000, 1000) {
                public void onTick(long millisUntilFinished) {
                    text.setText("" + millisUntilFinished / 1000);
                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {
                    text.setText("");
                    callNow();
                    bottomSheet.dismiss();
                }

            }.start();
        }else{
            bottomSheet.findViewById(R.id.linear_no_permissions).setVisibility(View.VISIBLE);
            bottomSheet.findViewById(R.id.linear_waiting).setVisibility(View.GONE);
        }
    }
    private void callNow() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, ConstantsValues.CALL_PERMISSION_CODE);
        }else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:"+phoneNumber));
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

    public interface LoadPermissions{
        void loadLocationPermissions();
        void loadCallPermissions();
    }
}