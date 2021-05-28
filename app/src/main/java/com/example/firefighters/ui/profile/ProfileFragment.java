package com.example.firefighters.ui.profile;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.firefighters.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;


public class ProfileFragment extends Fragment {

    private Context context;
    private ProfileViewModel profileViewModel;

    //Boolean
    private boolean isMyPointsPanel;
    private boolean isWorkingOnPanel;
    private boolean isAllFabVisible;

    //Edit text
    private TextInputEditText mailSignIn;
    private TextInputEditText passwordSignIn;
    private TextInputEditText userNameSignUp;
    private TextInputEditText mailSignUp;
    private TextInputEditText passwordSignUp1;
    private TextInputEditText passwordSignUp2;

    //Buttons
    private MaterialButton signInButton;
    private MaterialButton signUpButton;
    private MaterialButton goToSignInButton;
    private MaterialButton goToSignUpButton;
    private MaterialButton workingOnButton;
    private MaterialButton myPointsButton;

    //Card views
    private CardView workingOnCard;

    //Toggle buttons
    private MaterialButtonToggleGroup toggleButton;
    private MaterialButton emergencyPointButton;
    private MaterialButton waterPointButton;
    private MaterialButton fireStationPointButton;

    //Recyclers views
    private RecyclerView recyclerViewExpandMyPoint;

    //Fab buttons
    ExtendedFloatingActionButton floatingButtonAdd;
    FloatingActionButton floatingButtonButtonAddEmergency;
    FloatingActionButton floatingButtonAddWater;
    FloatingActionButton floatingButtonAddFireStation;
    MaterialTextView textAddEmergency;
    MaterialTextView textAddWater;
    MaterialTextView textAddFireStation;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        context = getContext();
        initViews(root);
        setViewStates();
        checkInteractions(root);

        return root;
    }

    private void checkInteractions(View root) {
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToSignIn();
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToSignUp();
            }
        });
        workingOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWorkingOnPanel(v);
            }
        });
        myPointsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyPointsPanel(v);
            }
        });
        toggleButton.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                int tempId = group.getCheckedButtonId();
                if(tempId == emergencyPointButton.getId()){
                    loadEmergencyRecycler();
                }else if (tempId == waterPointButton.getId()){
                    loadWaterRecycler();
                }else if (tempId == fireStationPointButton.getId()){
                    loadFireStationRecycler();
                }
            }
        });
        //Get action for fab action button
        floatingButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAllFabVisible){
                    hideAllFab();
                }else {
                    showAllFab();
                }
            }
        });
        floatingButtonButtonAddEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddEmergencyPoint(v);
            }
        });
        floatingButtonAddWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddWaterPoint(v);
            }
        });
        floatingButtonAddFireStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddFireStationPoint(v);
            }
        });
    }

    private void showDialogAddFireStationPoint(View v) {
        hideAllFab();
        Toast.makeText(context, "Fire station", Toast.LENGTH_SHORT).show();
    }

    private void showDialogAddWaterPoint(View v) {
        hideAllFab();
        Toast.makeText(context, "Water Point", Toast.LENGTH_SHORT).show();
    }

    private void showDialogAddEmergencyPoint(View v) {
        hideAllFab();
        Toast.makeText(context, "Emergency Point", Toast.LENGTH_SHORT).show();
    }

    private void loadFireStationRecycler() {
        //
    }

    private void loadWaterRecycler() {
        //
    }

    private void loadEmergencyRecycler() {
        //
    }

    private void tryToSignUp() {
        showLoading();
    }

    private void tryToSignIn() {
        showLoading();
    }

    private void setViewStates(){
        if(isWorkingOnPanel){
            workingOnCard.setVisibility(View.VISIBLE);
        }else {
            workingOnCard.setVisibility(View.GONE);
        }
        if (isMyPointsPanel){
            toggleButton.setVisibility(View.VISIBLE);
            recyclerViewExpandMyPoint.setVisibility(View.VISIBLE);
        }else {
            toggleButton.setVisibility(View.GONE);
            recyclerViewExpandMyPoint.setVisibility(View.GONE);
        }

        //Set for action floating buttons
        if(isAllFabVisible){
            textAddEmergency.setVisibility(View.VISIBLE);
            textAddFireStation.setVisibility(View.VISIBLE);
            textAddWater.setVisibility(View.VISIBLE);
            floatingButtonButtonAddEmergency.setVisibility(View.VISIBLE);
            floatingButtonAddWater.setVisibility(View.VISIBLE);
            floatingButtonAddFireStation.setVisibility(View.VISIBLE);
            floatingButtonAdd.extend();
        }else{
            textAddEmergency.setVisibility(View.GONE);
            textAddFireStation.setVisibility(View.GONE);
            textAddWater.setVisibility(View.GONE);
            floatingButtonButtonAddEmergency.setVisibility(View.GONE);
            floatingButtonAddWater.setVisibility(View.GONE);
            floatingButtonAddFireStation.setVisibility(View.GONE);
            floatingButtonAdd.shrink();
        }
    }

    private void hideAllFab() {
        textAddEmergency.setVisibility(View.GONE);
        textAddFireStation.setVisibility(View.GONE);
        textAddWater.setVisibility(View.GONE);
        floatingButtonButtonAddEmergency.hide();
        floatingButtonAddWater.hide();
        floatingButtonAddFireStation.hide();
        isAllFabVisible = false;
        floatingButtonAdd.shrink();
    }

    private void showAllFab() {
        textAddEmergency.setVisibility(View.VISIBLE);
        textAddFireStation.setVisibility(View.VISIBLE);
        textAddWater.setVisibility(View.VISIBLE);
        floatingButtonButtonAddEmergency.show();
        floatingButtonAddWater.show();
        floatingButtonAddFireStation.show();
        isAllFabVisible = true;
        floatingButtonAdd.extend();
    }

    private void getWorkingOnPanel(View v) {
        if (isWorkingOnPanel){
            isWorkingOnPanel = false;
            //Now hide working on panel
            workingOnCard.setAlpha(1);
            workingOnCard.setVisibility(View.VISIBLE);
            workingOnCard.animate()
                    .alpha(0)
                    .setDuration(100)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            workingOnCard.setVisibility(View.GONE);
                        }
                    });
        }else{
            isWorkingOnPanel = true;
            //Now show working on panel
            workingOnCard.setAlpha(0);
            workingOnCard.setVisibility(View.VISIBLE);
            workingOnCard.animate()
                    .alpha(1)
                    .setDuration(100)
                    .setListener(null);
        }
    }

    private void getMyPointsPanel(View v) {
        if (isMyPointsPanel){
            isMyPointsPanel = false;
            //Now hide my points panel
            toggleButton.setAlpha(1);
            toggleButton.setVisibility(View.VISIBLE);
            toggleButton.animate()
                    .alpha(0)
                    .setDuration(100)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            toggleButton.setVisibility(View.GONE);
                        }
                    });
            recyclerViewExpandMyPoint.animate()
                    .alpha(0)
                    .setDuration(100)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            recyclerViewExpandMyPoint.setVisibility(View.GONE);
                        }
                    });
        }else{
            isMyPointsPanel = true;
            //Now show my points panel
            toggleButton.setAlpha(0);
            toggleButton.setVisibility(View.VISIBLE);
            toggleButton.animate()
                    .alpha(1)
                    .setDuration(100)
                    .setListener(null);
            recyclerViewExpandMyPoint.setAlpha(0);
            recyclerViewExpandMyPoint.setVisibility(View.VISIBLE);
            recyclerViewExpandMyPoint.animate()
                    .alpha(1)
                    .setDuration(100)
                    .setListener(null);
        }
    }

    private void initViews(View root) {
        //Edit texts
        mailSignIn = root.findViewById(R.id.text_input_sign_in_mail);
        passwordSignIn = root.findViewById(R.id.text_input_sign_in_password);
        userNameSignUp = root.findViewById(R.id.text_input_sign_up_user_name);
        mailSignUp = root.findViewById(R.id.text_input_sign_up_mail);
        passwordSignUp1 = root.findViewById(R.id.text_input_sign_up_password);
        passwordSignUp2 = root.findViewById(R.id.text_input_sign_up_password_repeat);

        //Buttons
        signInButton = root.findViewById(R.id.button_sign_in);
        signUpButton = root.findViewById(R.id.button_sign_up);
        goToSignInButton = root.findViewById(R.id.button_go_to_sign_in);
        goToSignUpButton = root.findViewById(R.id.button_go_to_sign_up);
        workingOnButton = root.findViewById(R.id.button_working_on);
        myPointsButton = root.findViewById(R.id.button_my_points);

        //Card views
        workingOnCard = root.findViewById(R.id.card_working_on);

        //Card views
        recyclerViewExpandMyPoint = root.findViewById(R.id.recycler_expand_my_points);

        //Toggle Groups
        toggleButton = root.findViewById(R.id.toggle_button);
        emergencyPointButton = root.findViewById(R.id.button_emergency_points);
        waterPointButton = root.findViewById(R.id.button_water_points);
        fireStationPointButton = root.findViewById(R.id.button_station_points);

        //Floating buttons
        floatingButtonAdd = root.findViewById(R.id.floating_action_button_add);
        floatingButtonButtonAddEmergency = root.findViewById(R.id.floating_action_button_emergency);
        floatingButtonAddWater = root.findViewById(R.id.floating_action_button_water);
        floatingButtonAddFireStation = root.findViewById(R.id.floating_action_button_fire_station);
        textAddEmergency = root.findViewById(R.id.add_emergency_action_text);
        textAddWater = root.findViewById(R.id.add_water_action_text);
        textAddFireStation = root.findViewById(R.id.add_fire_station_action_text);
    }

    private void showLoading(){
        //
    }

    private void hideLoading(){
        //
    }
}