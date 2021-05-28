package com.example.firefighters.ui.home;

import androidx.lifecycle.ViewModelProvider;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Path;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firefighters.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class HomeFragment extends Fragment {

    private boolean sosLocationPermissions;

    private HomeViewModel mViewModel;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        context = root.getContext();
        activity = getActivity();
        initViews(root);
        checkInteractions(root);
        setAnimations(root);

        return root;
    }

    private void setAnimations(View root) {
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

    private void initViews(View view) {
        sosBackground = view.findViewById(R.id.relative_sos_background);

        //Buttons
        buttonSettings = view.findViewById(R.id.button_image_settings);
        buttonHelp = view.findViewById(R.id.button_image_help);
        buttonCallNow = view.findViewById(R.id.button_call_now);
        buttonSos = view.findViewById(R.id.button_text_sos);
        buttonShowMap = view.findViewById(R.id.button_show_map);
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
    private void showBottomSheetDialogCallNow(View view) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        bottomSheet.setContentView(R.layout.bottom_sheet_dialog_cal_now);
        bottomSheet.setCancelable(true);
        bottomSheet.setCanceledOnTouchOutside(true);
        bottomSheet.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        bottomSheet.show();
    }
    private void showBottomSheetDialogSos(View view) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        bottomSheet.setContentView(R.layout.bottom_sheet_dialog_sos);
        bottomSheet.setCancelable(false);
        bottomSheet.setCanceledOnTouchOutside(false);
        bottomSheet.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        bottomSheet.findViewById(R.id.button_cancel_sos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
            }
        });
        bottomSheet.findViewById(R.id.button_get_permissions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToGetSosPermissions(v, bottomSheet);
            }
        });
        checkPermissionsViewForSos(view, bottomSheet);
        bottomSheet.show();
    }

    private void checkPermissionsViewForSos(View view, BottomSheetDialog bottomSheet) {
        int startCount = 5;
        if(sosLocationPermissions){
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
                    tryToSendSOS();
                    bottomSheet.dismiss();
                }

            }.start();
        }else{
            bottomSheet.findViewById(R.id.linear_no_permissions).setVisibility(View.VISIBLE);
            bottomSheet.findViewById(R.id.linear_waiting).setVisibility(View.GONE);
        }
    }

    private void tryToSendSOS() {
        //This is where the true work will begin
        Toast.makeText(context, "SOS sended", Toast.LENGTH_SHORT).show();
    }

    private void tryToGetSosPermissions(View view, BottomSheetDialog bottomSheet) {
        sosLocationPermissions = true;
        checkPermissionsViewForSos(view, bottomSheet);
    }
}