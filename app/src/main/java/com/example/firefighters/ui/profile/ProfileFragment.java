package com.example.firefighters.ui.profile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.firefighters.R;
import com.example.firefighters.adapters.MyEmergencyAdapter;
import com.example.firefighters.adapters.MyFireStationAdapter;
import com.example.firefighters.adapters.MyWaterPointAdapter;
import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.models.FireStationModel;
import com.example.firefighters.models.FireTruckModel;
import com.example.firefighters.models.WaterPointModel;
import com.example.firefighters.tools.ConstantsValues;
import com.example.firefighters.tools.FirebaseManager;
import com.example.firefighters.viewmodels.EmergencyViewModel;
import com.example.firefighters.viewmodels.FireStationViewModel;
import com.example.firefighters.viewmodels.FireTruckViewModel;
import com.example.firefighters.viewmodels.UserViewModel;
import com.example.firefighters.viewmodels.WaterPointViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ProfileFragment extends Fragment {

    //Toggle buttons
    MaterialCheckBox checkBoxRemember;
    MotionLayout pageProfileConnexion;
    MotionLayout pageProfileHome;
    //Fab buttons
    ExtendedFloatingActionButton floatingButtonAdd;
    FloatingActionButton floatingButtonAddFireFighter;
    FloatingActionButton floatingButtonAddFireStation;
    MaterialTextView textAddFireStation;
    MaterialTextView textAddFireFighter;
    private Context context;
    //Boolean
    private boolean isMyPointsPanel;
    private boolean isWorkingOnPanel;
    private boolean isAllFabVisible;
    //Text views
    MaterialTextView textProfileMail;
    MaterialTextView textProfileName;
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
    private MaterialButton signOutButton;
    private MaterialButton goToSignInButton;
    private MaterialButton goToSignUpButton;
    private MaterialButton workingOnButton;
    private MaterialButton myPointsButton;
    private MaterialButton forgotPasswordButton;
    private CircularProgressIndicator circularProgressIndicator;
    //Card views
    private CardView workingOnCard;
    private MaterialButtonToggleGroup toggleButton;
    private MaterialButton emergencyPointButton;
    private MaterialButton waterPointButton;
    private MaterialButton fireStationPointButton;
    //Recyclers views
    //Recyclers views
    private RecyclerView recyclerViewMyEmergencies;
    private RecyclerView recyclerViewMyWaterPoints;
    private RecyclerView recyclerViewMyFireStation;

    private UserViewModel userViewModel;
    private EmergencyViewModel emergencyViewModel;
    private WaterPointViewModel waterPointViewModel;
    private FireStationViewModel fireStationViewModel;
    private LinearLayout linearWorkingOn;
    private ConstraintLayout constraintFloatingAction;
    private LinearLayout linearMyPointExpand
            ;
    private MyEmergencyAdapter myEmergencyAdapter;
    private MyFireStationAdapter myFireStationAdapter;
    private MyWaterPointAdapter myWaterPointAdapter;

    private final int loadQte = 10;
    ArrayList<EmergencyModel> emergencies;
    ArrayList<WaterPointModel> waterPoints;
    ArrayList<FireStationModel> fireStations;

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
        //
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
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
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
                if (tempId == emergencyPointButton.getId()) {
                    loadEmergencyRecycler();
                } else if (tempId == waterPointButton.getId()) {
                    loadWaterRecycler();
                } else if (tempId == fireStationPointButton.getId()) {
                    loadFireStationRecycler();
                }
            }
        });
        //Get action for fab action button
        floatingButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllFabVisible) {
                    hideAllFab();
                } else {
                    showAllFab();
                }
            }
        });
        floatingButtonAddFireFighter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddFireFighter(v);
            }
        });
        floatingButtonAddFireStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddFireStationPoint(v);
            }
        });
    }

    private void showDialogAddFireFighter(View v) {
        hideAllFab();
        Toast.makeText(context, "new Fire fighter", Toast.LENGTH_SHORT).show();
    }

    private void showDialogAddFireStationPoint(View v) {
        hideAllFab();
        Toast.makeText(context, "Fire station", Toast.LENGTH_SHORT).show();
    }

    private void setupRecyclersViews() {
        emergencies = new ArrayList<>();
        waterPoints = new ArrayList<>();
        fireStations = new ArrayList<>();
        recyclerViewMyFireStation.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)){
                    loadMoreFireStations();
                }
            }
        });
        recyclerViewMyEmergencies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)){
                    loadMoreEmergencies();
                }
            }
        });
        recyclerViewMyWaterPoints.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)){
                    loadMoreWaterPoints();
                }
            }
        });
        GridLayoutManager layoutManager1 = new GridLayoutManager(requireContext(), 1, RecyclerView.VERTICAL, false);
        recyclerViewMyEmergencies.setLayoutManager(layoutManager1);
        recyclerViewMyEmergencies.setAdapter(myEmergencyAdapter);

        GridLayoutManager layoutManager2 = new GridLayoutManager(requireContext(), 1, RecyclerView.VERTICAL, false);
        recyclerViewMyWaterPoints.setLayoutManager(layoutManager2);
        recyclerViewMyWaterPoints.setAdapter(myWaterPointAdapter);

        GridLayoutManager layoutManager3 = new GridLayoutManager(requireContext(), 1, RecyclerView.VERTICAL, false);
        recyclerViewMyFireStation.setLayoutManager(layoutManager3);
        recyclerViewMyFireStation.setAdapter(myFireStationAdapter);
        reloadMoreEmergencies();
        reloadMoreWaterPoints();
        reloadMoreFireStations();
    }

    private void loadMoreEmergencies() {
        emergencyViewModel.getEmergenciesQuery(requireActivity()).observe(requireActivity(), new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document:queryDocumentSnapshots) {
                    emergencies.add(document.toObject(EmergencyModel.class));
                }
                myEmergencyAdapter.notifyItemRangeInserted(myEmergencyAdapter.getItemCount(), emergencies.size());
            }
        });
    }

    private void reloadMoreEmergencies() {
        emergencies.clear();
        emergencyViewModel.getEmergenciesQuery(requireActivity()).observe(requireActivity(), new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document:queryDocumentSnapshots) {
                    emergencies.add(document.toObject(EmergencyModel.class));
                }
                myFireStationAdapter.notifyItemRangeInserted(myFireStationAdapter.getItemCount(), fireStations.size());
            }
        });
    }

    private void loadMoreFireStations() {
        fireStationViewModel.getFireStationsQuery(requireActivity()).observe(requireActivity(), new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document:queryDocumentSnapshots) {
                    fireStations.add(document.toObject(FireStationModel.class));
                }
                myEmergencyAdapter.notifyDataSetChanged();
            }
        });
    }

    private void reloadMoreFireStations() {
        fireStations.clear();
        fireStationViewModel.getFireStationsQuery(requireActivity()).observe(requireActivity(), new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document:queryDocumentSnapshots) {
                    fireStations.add(document.toObject(FireStationModel.class));
                }
                myWaterPointAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadMoreWaterPoints() {
        waterPointViewModel.getWaterSourcesQuery(requireActivity()).observe(requireActivity(), new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document:queryDocumentSnapshots) {
                    waterPoints.add(document.toObject(WaterPointModel.class));
                }
                myFireStationAdapter.notifyItemRangeInserted(myFireStationAdapter.getItemCount(), fireStations.size());
            }
        });
    }

    private void reloadMoreWaterPoints() {
        waterPoints.clear();
        waterPointViewModel.getWaterSourcesQuery(requireActivity()).observe(requireActivity(), new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document:queryDocumentSnapshots) {
                    waterPoints.add(document.toObject(WaterPointModel.class));
                }
                myWaterPointAdapter.notifyItemRangeInserted(myWaterPointAdapter.getItemCount(), waterPoints.size());
            }
        });
    }

    private void loadWaterRecycler() {
        recyclerViewMyEmergencies.setVisibility(View.GONE);
        recyclerViewMyWaterPoints.setVisibility(View.VISIBLE);
        recyclerViewMyFireStation.setVisibility(View.GONE);
    }

    private void loadEmergencyRecycler() {
        recyclerViewMyEmergencies.setVisibility(View.VISIBLE);
        recyclerViewMyWaterPoints.setVisibility(View.GONE);
        recyclerViewMyFireStation.setVisibility(View.GONE);
    }

    private void loadFireStationRecycler() {
        recyclerViewMyEmergencies.setVisibility(View.GONE);
        recyclerViewMyWaterPoints.setVisibility(View.GONE);
        recyclerViewMyFireStation.setVisibility(View.VISIBLE);
    }

    private void tryToSignUp() {
        showLoading();
        String userName = Objects.requireNonNull(userNameSignUp.getText()).toString();
        String userMail = Objects.requireNonNull(mailSignUp.getText()).toString();
        String userPassword = Objects.requireNonNull(passwordSignUp2.getText()).toString();
        userViewModel.createNewUser(requireActivity(), userName, userMail, userPassword).observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer >= 1){
                    setViewStates();
                    Toast.makeText(context, "Registered !", Toast.LENGTH_SHORT).show();
                }else{
                    hideLoading();
                    Toast.makeText(context, "Error sign up !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void tryToSignIn() {
        showLoading();
        String userMail = Objects.requireNonNull(mailSignIn.getText()).toString();
        String userPassword = Objects.requireNonNull(passwordSignIn.getText()).toString();
        userViewModel.signInUser(requireActivity(), userMail, userPassword).observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer >= 1){
                    setViewStates();
                    Toast.makeText(context, "Logged !", Toast.LENGTH_SHORT).show();
                }else{
                    hideLoading();
                    Toast.makeText(context, "Error logging !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void signOut(){
        userViewModel.logOut().observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 1){
                    setViewStates();
                }
            }
        });
    }

    private void setViewStates() {
        //Set the correct page if the user is auth
        if (FirebaseManager.getInstance().getCurrentAuthUser() == null){
            //Set the connexion page
            goToSignInPage();
        }else{
            userViewModel.loadTypeUser().observe(requireActivity(), new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    if (s.equals(ConstantsValues.NORMAL_USER)){
                        //Go to normal user page
                        goToProfileHomePageDefault();
                    }else if(s.equals(ConstantsValues.FIRE_FIGHTER_USER)){
                        //Go to fire fighter page
                        goToProfileHomePageFireFighter();
                    }else if(s.equals(ConstantsValues.ADMIN_USER)){
                        //Go to admin page
                        goToProfileHomePageAdmin();
                    }else{
                        Toast.makeText(context, "null", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        int tempId = toggleButton.getCheckedButtonId();
        if (tempId == emergencyPointButton.getId()) {
            loadEmergencyRecycler();
        } else if (tempId == waterPointButton.getId()) {
            loadWaterRecycler();
        } else if (tempId == fireStationPointButton.getId()) {
            loadFireStationRecycler();
        }

        if (isWorkingOnPanel) {
            workingOnCard.setVisibility(View.VISIBLE);
        } else {
            workingOnCard.setVisibility(View.GONE);
        }
        if (isMyPointsPanel) {
            linearMyPointExpand.setVisibility(View.VISIBLE);
        } else {
            linearMyPointExpand.setVisibility(View.GONE);
        }

        //Set for action floating buttons
        if (isAllFabVisible) {
            textAddFireStation.setVisibility(View.VISIBLE);
            textAddFireFighter.setVisibility(View.VISIBLE);
            floatingButtonAddFireFighter.setVisibility(View.VISIBLE);
            floatingButtonAddFireStation.setVisibility(View.VISIBLE);
            floatingButtonAdd.extend();
        } else {
            textAddFireStation.setVisibility(View.GONE);
            textAddFireFighter.setVisibility(View.GONE);
            floatingButtonAddFireFighter.setVisibility(View.GONE);
            floatingButtonAddFireStation.setVisibility(View.GONE);
            floatingButtonAdd.shrink();
        }
    }

    private void goToProfileHomePageAdmin() {
        if(FirebaseManager.getInstance().getCurrentAuthUser().getDisplayName() == null){
            textProfileName.setText("<<Unknown name>>");
        }else {
            textProfileName.setText(FirebaseManager.getInstance().getCurrentAuthUser().getDisplayName());
        }
        textProfileMail.setText(FirebaseManager.getInstance().getCurrentAuthUser().getEmail());
        pageProfileHome.setVisibility(View.VISIBLE);
        pageProfileConnexion.setVisibility(View.GONE);
        linearWorkingOn.setVisibility(View.VISIBLE);
        constraintFloatingAction.setVisibility(View.VISIBLE);
    }
    private void goToProfileHomePageFireFighter() {
        if(FirebaseManager.getInstance().getCurrentAuthUser().getDisplayName() == null){
            textProfileName.setText("<<Unknown name>>");
        }else {
            textProfileName.setText(FirebaseManager.getInstance().getCurrentAuthUser().getDisplayName());
        }
        textProfileMail.setText(FirebaseManager.getInstance().getCurrentAuthUser().getEmail());
        pageProfileHome.setVisibility(View.VISIBLE);
        pageProfileConnexion.setVisibility(View.GONE);
        linearWorkingOn.setVisibility(View.VISIBLE);
        constraintFloatingAction.setVisibility(View.VISIBLE);
        floatingButtonAddFireFighter.setVisibility(View.GONE);
        textAddFireFighter.setVisibility(View.GONE);
    }
    private void goToProfileHomePageDefault() {
        if(FirebaseManager.getInstance().getCurrentAuthUser().getDisplayName() == null){
            textProfileName.setText("<<Unknown name>>");
        }else {
            textProfileName.setText(FirebaseManager.getInstance().getCurrentAuthUser().getDisplayName());
        }
        textProfileMail.setText(FirebaseManager.getInstance().getCurrentAuthUser().getEmail());
        pageProfileHome.setVisibility(View.VISIBLE);
        pageProfileConnexion.setVisibility(View.GONE);
        linearWorkingOn.setVisibility(View.GONE);
        constraintFloatingAction.setVisibility(View.GONE);
    }

    private void goToSignInPage() {
        textProfileName.setText("<<Unknown name>>");
        textProfileMail.setText("<<Unknown mail>>");
        pageProfileHome.setVisibility(View.GONE);
        pageProfileConnexion.setVisibility(View.VISIBLE);
        linearWorkingOn.setVisibility(View.GONE);
        constraintFloatingAction.setVisibility(View.GONE);
    }

    private void hideAllFab() {
        textAddFireStation.setVisibility(View.GONE);
        textAddFireFighter.setVisibility(View.GONE);
        floatingButtonAddFireFighter.hide();
        floatingButtonAddFireStation.hide();
        isAllFabVisible = false;
        floatingButtonAdd.shrink();
    }

    private void showAllFab() {
        textAddFireStation.setVisibility(View.VISIBLE);
        textAddFireFighter.setVisibility(View.VISIBLE);
        floatingButtonAddFireFighter.show();
        floatingButtonAddFireStation.show();
        isAllFabVisible = true;
        floatingButtonAdd.extend();
    }

    private void getWorkingOnPanel(View v) {
        if (isWorkingOnPanel) {
            isWorkingOnPanel = false;
            //Now hide working on panel
            workingOnCard.setVisibility(View.GONE);
            workingOnCard.setAlpha(1);
            workingOnCard.setVisibility(View.VISIBLE);
            workingOnCard.animate()
                    .alpha(0)
                    .setDuration(100)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                        }
                    });
        } else {
            isWorkingOnPanel = true;
            //Now show working on panel
            workingOnCard.setVisibility(View.VISIBLE);
            workingOnCard.setAlpha(0);
            workingOnCard.animate()
                    .alpha(1)
                    .setDuration(100)
                    .setListener(null);
        }
    }

    private void getMyPointsPanel(View v) {
        if (isMyPointsPanel) {
            isMyPointsPanel = false;
            //Now hide my points panel
            linearMyPointExpand.animate()
                    .alpha(0)
                    .setDuration(100)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            linearMyPointExpand.setVisibility(View.GONE);
                        }
                    });
        } else {
            isMyPointsPanel = true;
            //Now show my points panel
            linearMyPointExpand.setAlpha(1);
            linearMyPointExpand.setVisibility(View.VISIBLE);
        }
    }

    private void initViews(View root) {
        //Text views
        textProfileMail = root.findViewById(R.id.text_profile_mail);
        textProfileName = root.findViewById(R.id.text_profile_user_name);

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
        signOutButton = root.findViewById(R.id.button_log_out);
        goToSignInButton = root.findViewById(R.id.button_go_to_sign_in);
        goToSignUpButton = root.findViewById(R.id.button_go_to_sign_up);
        workingOnButton = root.findViewById(R.id.button_working_on);
        myPointsButton = root.findViewById(R.id.button_my_points);
        forgotPasswordButton = root.findViewById(R.id.button_forgot_password);

        checkBoxRemember = root.findViewById(R.id.checkbox_remember_me);
        circularProgressIndicator = root.findViewById(R.id.progress_circular_connexion);

        //Card views
        workingOnCard = root.findViewById(R.id.card_working_on);

        //Motions layouts
        pageProfileConnexion = root.findViewById(R.id.motion_layout_profile);
        pageProfileHome = root.findViewById(R.id.coordinatorLayout);

        //Recyclers
        recyclerViewMyEmergencies = root.findViewById(R.id.recycler_my_emergencies);
        recyclerViewMyWaterPoints = root.findViewById(R.id.recycler_my_water_points);
        recyclerViewMyFireStation = root.findViewById(R.id.recycler_my_fire_station);

        //Toggle Groups
        toggleButton = root.findViewById(R.id.toggle_button);
        emergencyPointButton = root.findViewById(R.id.button_emergency_points);
        waterPointButton = root.findViewById(R.id.button_water_points);
        fireStationPointButton = root.findViewById(R.id.button_station_points);

        //Floating buttons
        floatingButtonAdd = root.findViewById(R.id.floating_action_button_add);
        floatingButtonAddFireFighter = root.findViewById(R.id.floating_action_button_fire_fighter);
        floatingButtonAddFireStation = root.findViewById(R.id.floating_action_button_fire_station);
        textAddFireFighter = root.findViewById(R.id.add_fire_fighter_action_text);
        textAddFireStation = root.findViewById(R.id.add_fire_station_action_text);

        //LinearLayouts
        linearWorkingOn = root.findViewById(R.id.linear_working_on);
        linearMyPointExpand = root.findViewById(R.id.linear_my_points_expand);
        constraintFloatingAction = root.findViewById(R.id.constraint_floating_action_buttons);
    }

    private void showLoading() {
//        checkBoxRemember.setVisibility(View.INVISIBLE);
//        forgotPasswordButton.setVisibility(View.INVISIBLE);
//        goToSignUpButton.setVisibility(View.INVISIBLE);
//        goToSignInButton.setVisibility(View.INVISIBLE);
//
        signUpButton.setVisibility(View.INVISIBLE);
        signInButton.setVisibility(View.INVISIBLE);

        circularProgressIndicator.show();
        circularProgressIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
//        checkBoxRemember.setVisibility(View.VISIBLE);
//        forgotPasswordButton.setVisibility(View.VISIBLE);
//        goToSignUpButton.setVisibility(View.VISIBLE);
//        goToSignInButton.setVisibility(View.VISIBLE);
//
        signUpButton.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.VISIBLE);

        circularProgressIndicator.hide();
        circularProgressIndicator.setVisibility(View.INVISIBLE);
    }
}