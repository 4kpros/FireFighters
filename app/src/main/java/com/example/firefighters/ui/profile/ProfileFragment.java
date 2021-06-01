package com.example.firefighters.ui.profile;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
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
import android.widget.Toast;

import com.example.firefighters.R;
import com.example.firefighters.models.UserAdminModel;
import com.example.firefighters.models.UserFireFighterModel;
import com.example.firefighters.models.UserModel;
import com.example.firefighters.viewmodels.EmergencyViewModel;
import com.example.firefighters.viewmodels.FireTruckViewModel;
import com.example.firefighters.viewmodels.UserViewModel;
import com.example.firefighters.viewmodels.WaterSourceViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class ProfileFragment extends Fragment {

    private Context context;

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
    private MaterialButton forgotPasswordButton;

    private CircularProgressIndicator circularProgressIndicator;

    //Card views
    private CardView workingOnCard;

    //Toggle buttons
    MaterialCheckBox checkBoxRemember;
    private MaterialButtonToggleGroup toggleButton;
    private MaterialButton emergencyPointButton;
    private MaterialButton waterPointButton;
    private MaterialButton fireStationPointButton;

    //Recyclers views
    private RecyclerView recyclerViewExpandMyPoint;

    MotionLayout pageProfileConnexion;
    MotionLayout pageProfileHome;

    //Fab buttons
    ExtendedFloatingActionButton floatingButtonAdd;
    FloatingActionButton floatingButtonButtonAddEmergency;
    FloatingActionButton floatingButtonAddWater;
    FloatingActionButton floatingButtonAddFireStation;
    MaterialTextView textAddEmergency;
    MaterialTextView textAddWater;
    MaterialTextView textAddFireStation;

    private UserViewModel userViewModel;
    private EmergencyViewModel emergencyViewModel;
    private WaterSourceViewModel waterSourceViewModel;
    private FireTruckViewModel fireTruckViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getContext();
        initViews(view);
        initViewModel();
        setViewStates();
        ObserveLiveData();
        return view;
    }

    private void ObserveLiveData() {
        userViewModel.getCurrentAuthUser().observe(requireActivity(), new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser == null){
                    goToSignInPage();
                }else {
                    //Update data
                }
            }
        });
        userViewModel.getUser().observe(requireActivity(), new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel userModel) {
                if (userModel == null){
                    goToSignInPage();
                }else {
                    //Update data
                }
            }
        });
        userViewModel.getFireFighter().observe(requireActivity(), new Observer<UserFireFighterModel>() {
            @Override
            public void onChanged(UserFireFighterModel userFireFighterModel) {
                if (userFireFighterModel == null){
                    goToSignInPage();
                }else {
                    //Update data
                }
            }
        });
        userViewModel.getAdmin().observe(requireActivity(), new Observer<UserAdminModel>() {
            @Override
            public void onChanged(UserAdminModel userAdminModel) {
                if (userAdminModel == null)
                    goToSignInPage();
                else {
                    //Update data
                }
            }
        });
        userViewModel.getIsLoadingUser().observe(requireActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!aBoolean){
                    hideLoading();
                    if(userViewModel.getCurrentAuthUser().getValue() != null){
                        goToProfileHomePage();
                    }
                }
            }
        });
    }

    private void goToProfileHomePage() {
        pageProfileHome.setVisibility(View.VISIBLE);
        pageProfileConnexion.setVisibility(View.GONE);
    }

    private void goToSignInPage() {
        pageProfileHome.setVisibility(View.GONE);
        pageProfileConnexion.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkInteractions(view);
    }

    private void initViewModel() {
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.init();
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
        String userName = Objects.requireNonNull(userNameSignUp.getText()).toString();
        String userMail = Objects.requireNonNull(mailSignUp.getText()).toString();
        String userPassword = Objects.requireNonNull(passwordSignUp2.getText()).toString();
        userViewModel.createNewUser(userName, userMail, userPassword);
    }

    private void tryToSignIn() {
        showLoading();
        String userMail = Objects.requireNonNull(mailSignIn.getText()).toString();
        String userPassword = Objects.requireNonNull(passwordSignIn.getText()).toString();
        userViewModel.signInUser(userMail, userPassword);
    }

    private void setViewStates(){
        //Set the correct page if the user is auth
//        FirebaseManager.

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
        forgotPasswordButton = root.findViewById(R.id.button_forgot_password);

        checkBoxRemember = root.findViewById(R.id.checkbox_remember_me);
        circularProgressIndicator = root.findViewById(R.id.progress_circular);

        //Card views
        workingOnCard = root.findViewById(R.id.card_working_on);

        //Motions layouts
        pageProfileConnexion = root.findViewById(R.id.motion_layout_profile);
        pageProfileHome = root.findViewById(R.id.coordinatorLayout);

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
        checkBoxRemember.setVisibility(View.INVISIBLE);
        forgotPasswordButton.setVisibility(View.INVISIBLE);
        goToSignUpButton.setVisibility(View.INVISIBLE);
        signInButton.setVisibility(View.INVISIBLE);

        signUpButton.setVisibility(View.INVISIBLE);
        goToSignInButton.setVisibility(View.INVISIBLE);

        circularProgressIndicator.show();
    }

    private void hideLoading(){
        checkBoxRemember.setVisibility(View.VISIBLE);
        forgotPasswordButton.setVisibility(View.VISIBLE);
        goToSignUpButton.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.VISIBLE);

        signUpButton.setVisibility(View.VISIBLE);
        goToSignInButton.setVisibility(View.VISIBLE);

        circularProgressIndicator.show();
    }
}