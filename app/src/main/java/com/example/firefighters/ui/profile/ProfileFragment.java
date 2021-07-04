package com.example.firefighters.ui.profile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.app.MediaRouteButton;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firefighters.R;
import com.example.firefighters.adapters.EmergencyPointAdapter;
import com.example.firefighters.adapters.FirefighterAdapter;
import com.example.firefighters.adapters.WaterPointAdapter;
import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.models.UnitModel;
import com.example.firefighters.models.UserModel;
import com.example.firefighters.models.WaterPointModel;
import com.example.firefighters.tools.ConstantsValues;
import com.example.firefighters.tools.FirebaseManager;
import com.example.firefighters.viewmodels.EmergencyViewModel;
import com.example.firefighters.viewmodels.UnitViewModel;
import com.example.firefighters.viewmodels.UserViewModel;
import com.example.firefighters.viewmodels.WaterPointViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ProfileFragment extends Fragment {

    //Toggle buttons
    MaterialCheckBox checkBoxRemember;
    MotionLayout pageProfileConnexion;
    ConstraintLayout pageProfileHome;
    //Fab buttons
    ExtendedFloatingActionButton floatingButtonAdd;
    FloatingActionButton floatingButtonAddFireFighter;
    FloatingActionButton floatingButtonAddUnit;
    private Context context;
    //Boolean
    private boolean isMyPointsPanel;
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
    private MaterialButton pointsButton;
    private MaterialButton forgotPasswordButton;
    private CircularProgressIndicator circularProgressIndicatorConnexion;
    private CircularProgressIndicator circularProgressIndicatorCreation;
    private CircularProgressIndicator circularProgressPoints;
    //Card views
    private MaterialButtonToggleGroup toggleButton;
    private MaterialButton emergencyPointButton;
    private MaterialButton waterPointButton;
    private MaterialButton firefighterPointButton;
    //Recyclers views
    private RecyclerView recyclerViewPoints;

    private UserViewModel userViewModel;
    private EmergencyViewModel emergencyViewModel;
    private WaterPointViewModel waterPointViewModel;
    private UnitViewModel unitViewModel;

    private ConstraintLayout constraintFloatingAction;
    private LinearLayout linearMyPointExpand;

    private final int loadQte = 10;

    ArrayList<EmergencyModel> emergencies;
    ArrayList<WaterPointModel> waterPoints;
    ArrayList<UserModel> fireFighters;

    private UserModel currentUser = new UserModel();
    private EmergencyPointAdapter emergencyPointAdapter;
    private FirefighterAdapter firefighterAdapter;
    private WaterPointAdapter waterPointAdapter;

    DocumentSnapshot lastDocumentFireFighter;
    DocumentSnapshot lastDocumentEmergency;
    DocumentSnapshot lastDocumentWaterPoint;
    private int limitCount = 10;
    private LinearLayoutManager layoutManagerEmergencies;
    private LinearLayoutManager layoutManagerWaterPoints;
    private LinearLayoutManager layoutManagerFirefighters;
    private LinearLayout linearWorkingOn;
    private LinearLayout linearRecyclerPoints;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getContext();
        initViews(view);
        setupRecyclersViews();
        initViewModel();
        setViewStates();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkInteractions(view);
    }

    private void initViewModel() {
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.init();

        emergencyViewModel = new ViewModelProvider(requireActivity()).get(EmergencyViewModel.class);
        emergencyViewModel.init();

        waterPointViewModel = new ViewModelProvider(requireActivity()).get(WaterPointViewModel.class);
        waterPointViewModel.init();

        unitViewModel = new ViewModelProvider(requireActivity()).get(UnitViewModel.class);
        unitViewModel.init();
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
        pointsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPointsPanel();
            }
        });
        toggleButton.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (checkedId == emergencyPointButton.getId()) {
                    emergencies.clear();
                    lastDocumentEmergency = null;
                    showLoadingPoints();
                    loadEmergencyRecycler();
                    loadMoreEmergencies();
                } else if (checkedId == waterPointButton.getId()) {
                    waterPoints.clear();
                    lastDocumentWaterPoint = null;
                    showLoadingPoints();
                    loadWaterRecycler();
                    loadMoreWaterPoints();
                } else if (checkedId == firefighterPointButton.getId()) {
                    fireFighters.clear();
                    lastDocumentFireFighter = null;
                    showLoadingPoints();
                    loadFirefighterRecycler();
                    loadMoreFirefighters();
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
                hideAllFab();
                unitViewModel.getUnitsQuery().observe(requireActivity(), new Observer<QuerySnapshot>() {
                    @Override
                    public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0){
                            showAddFirefighterDialog(queryDocumentSnapshots.getDocuments());
                        }else{
                            Toast.makeText(context, "No unit found !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        floatingButtonAddUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllFab();
                showAddUnitDialog();
            }
        });
    }

    private void showLoadingPoints() {
        circularProgressPoints.show();
        circularProgressPoints.setVisibility(View.VISIBLE);
    }

    private void hideLoadingPoints() {
        circularProgressPoints.hide();
        circularProgressPoints.setVisibility(View.GONE);
    }

    private void setupRecyclersViews() {
        emergencies = new ArrayList<>();
        waterPoints = new ArrayList<>();
        fireFighters = new ArrayList<>();

        //Setup recycler view for emergencies
        emergencies = new ArrayList<>();
        layoutManagerEmergencies = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        emergencyPointAdapter = new EmergencyPointAdapter(context, emergencies);

        //Setup recycler view for firefighters
        fireFighters = new ArrayList<>();
        layoutManagerFirefighters = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        firefighterAdapter = new FirefighterAdapter(requireContext(), fireFighters);

        //Setup recycler view for waters points
        waterPoints = new ArrayList<>();
        layoutManagerWaterPoints = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        waterPointAdapter = new WaterPointAdapter(context, waterPoints);
//
//        recyclerViewMyFirefighter.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (!recyclerView.canScrollVertically(1)){
//                    loadMoreFirefighters();
//                }
//            }
//        });
//        recyclerViewMyEmergencies.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (!recyclerView.canScrollVertically(1)){
//                    loadMoreEmergencies();
//                }
//            }
//        });
//        recyclerViewMyWaterPoints.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (!recyclerView.canScrollVertically(1)){
//                    loadMoreWaterPoints();
//                }
//            }
//        });
    }

    private void loadMoreEmergencies() {
        emergencyViewModel.getEmergenciesQuery(lastDocumentEmergency, null, null, limitCount).observe(requireActivity(), new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                hideLoadingPoints();
                int startPositionUpdate = 0;
                if (emergencies != null && emergencies.size() > 0)
                    startPositionUpdate = emergencies.size();
                for (DocumentSnapshot document:queryDocumentSnapshots) {
                    emergencies.add(document.toObject(EmergencyModel.class));
                }
                if (emergencies.size() > 0)
                    lastDocumentEmergency = queryDocumentSnapshots.getDocuments().get(0);
                    //Update now the view
                int endPositionUpdate = 0;
                if (emergencies.size() > 0)
                    endPositionUpdate = emergencies.size()-1;
                if (emergencyPointAdapter != null)
                    emergencyPointAdapter.notifyItemRangeInserted(startPositionUpdate, endPositionUpdate);
                if (getActivity() != null)
                    emergencyViewModel.getEmergenciesQuery(lastDocumentEmergency, null, null, limitCount).removeObservers(requireActivity());
            }
        });
    }

    private void loadMoreFirefighters() {
        userViewModel.loadFireFighters(lastDocumentFireFighter, limitCount).observe(requireActivity(), new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                hideLoadingPoints();
                int startPositionUpdate = 0;
                if (fireFighters != null && fireFighters.size() > 0)
                    startPositionUpdate = fireFighters.size();
                for (DocumentSnapshot document:queryDocumentSnapshots) {
                    fireFighters.add(document.toObject(UserModel.class));
                }
                if (fireFighters.size() > 0)
                    lastDocumentFireFighter = queryDocumentSnapshots.getDocuments().get(0);

                //Update now the view
                int endPositionUpdate = 0;
                if (fireFighters != null && fireFighters.size() > 0)
                    endPositionUpdate = fireFighters.size()-1;
                if (firefighterAdapter != null)
                    firefighterAdapter.notifyItemRangeInserted(startPositionUpdate, endPositionUpdate);
                if (getActivity() != null)
                    userViewModel.loadFireFighters(lastDocumentFireFighter, limitCount).removeObservers(requireActivity());
            }
        });
    }

    private void loadMoreWaterPoints() {
        waterPointViewModel.getWaterPointsQuery(lastDocumentWaterPoint, limitCount).observe(requireActivity(), new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                hideLoadingPoints();
                int startPositionUpdate = 0;
                if (waterPoints != null && waterPoints.size() > 0)
                    startPositionUpdate = waterPoints.size();
                for (DocumentSnapshot document:queryDocumentSnapshots) {
                    waterPoints.add(document.toObject(WaterPointModel.class));
                }
                if (waterPoints.size() > 0)
                    lastDocumentWaterPoint = queryDocumentSnapshots.getDocuments().get(0);

                //Update now the view
                int endPositionUpdate = 0;
                if (waterPoints != null && waterPoints.size() > 0)
                    endPositionUpdate = waterPoints.size()-1;
                if (waterPointAdapter != null)
                    waterPointAdapter.notifyItemRangeInserted(startPositionUpdate, endPositionUpdate);
                if (getActivity() != null)
                    waterPointViewModel.getWaterPointsQuery(lastDocumentWaterPoint, limitCount).removeObservers(requireActivity());
            }
        });
    }

    private void loadEmergencyRecycler() {
        recyclerViewPoints.removeAllViews();
        recyclerViewPoints.setLayoutManager(layoutManagerEmergencies);
        recyclerViewPoints.setAdapter(emergencyPointAdapter);
    }

    private void loadFirefighterRecycler() {
        recyclerViewPoints.removeAllViews();
        recyclerViewPoints.setLayoutManager(layoutManagerFirefighters);
        recyclerViewPoints.setAdapter(firefighterAdapter);
    }

    private void loadWaterRecycler() {
        recyclerViewPoints.removeAllViews();
        recyclerViewPoints.setLayoutManager(layoutManagerWaterPoints);
        recyclerViewPoints.setAdapter(waterPointAdapter);
    }

    private void tryToSignUp() {
        showLoading();
        String userName = Objects.requireNonNull(userNameSignUp.getText()).toString();
        String userMail = Objects.requireNonNull(mailSignUp.getText()).toString();
        String userPassword = Objects.requireNonNull(passwordSignUp2.getText()).toString();
        userViewModel.createNewUser(userName, userMail, userPassword, false).observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer >= 1){
                    Toast.makeText(context, "Registered !", Toast.LENGTH_SHORT).show();
                    loadUser(userMail);
                }else{
                    hideLoading();
                    Toast.makeText(context, "Error sign up !", Toast.LENGTH_SHORT).show();
                }
                if (getActivity() != null)
                    userViewModel.createNewUser(userName, userMail, userPassword, false).removeObservers(requireActivity());
            }
        });
    }

    private void tryToSignIn() {
        showLoading();
        String userMail = Objects.requireNonNull(mailSignIn.getText()).toString();
        String userPassword = Objects.requireNonNull(passwordSignIn.getText()).toString();
        userViewModel.signInUser(userMail, userPassword).observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer >= 1){
                    loadUser(userMail);
                }else{
                    hideLoading();
                    Toast.makeText(context, "Error sign in !", Toast.LENGTH_SHORT).show();
                }
                if (getActivity() != null)
                    userViewModel.signInUser(userMail, userPassword).removeObservers(requireActivity());
            }
        });
    }

    private void loadUser(String myMail) {
        userViewModel.loadUserModel(myMail).observe(requireActivity(), new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel userModel) {
                hideLoading();
                setViewStates();
                if (FirebaseManager.getInstance().getCurrentAuthUser() == null || (currentUser == null && FirebaseManager.getInstance().getCurrentAuthUser() == null)){
                    Toast.makeText(context, "Error sign in !", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Logged !", Toast.LENGTH_SHORT).show();
                }
                if (getActivity() != null)
                    userViewModel.loadUserModel(myMail).removeObservers(requireActivity());
            }
        });
    }

    public void signOut(){
        userViewModel.logOut().observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                hideLoading();
                if (integer >= 1){
                    setViewStates();
                    Toast.makeText(context, "Disconnected !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setViewStates() {
        hideLoadingPoints();

        //Set the correct page if the user is auth
        if (FirebaseManager.getInstance().getCurrentAuthUser() == null || (currentUser == null && FirebaseManager.getInstance().getCurrentAuthUser() == null)){
            //Set the connexion page
            userViewModel.logOut();
            goToSignInPage();
        }else{
            if (FirebaseManager.getInstance().getCurrentAuthUser() != null && FirebaseManager.getInstance().getCurrentAuthUser().getEmail() != null){
                if(FirebaseManager.getInstance().getCurrentAuthUser().getEmail().equals(ConstantsValues.ADMIN_EMAIL)){
                    //Go to admin page
                    goToProfileHomePageAdmin();
                }else{
                    userViewModel.loadUserModel(FirebaseManager.getInstance().getCurrentAuthUser().getEmail()).observe(requireActivity(), new Observer<UserModel>() {
                        @Override
                        public void onChanged(UserModel userModel) {
                            if(userModel != null){
                                if (userModel.isFireFighter()){
                                    //Go to firefighter page
                                    goToProfileHomePageFireFighter(userModel.isChief(), userModel.getUnit());
                                }else{
                                    //Go to basic user page
                                    goToProfileHomePageDefault();
                                }
                            }else {
                                //Go to basic user page
                                goToProfileHomePageDefault();
                            }
                            if (getActivity() != null)
                                userViewModel.loadUserModel(FirebaseManager.getInstance().getCurrentAuthUser().getEmail()).removeObservers(requireActivity());
                        }
                    });
                }
            }
        }

        if (isMyPointsPanel) {
            linearMyPointExpand.setVisibility(View.VISIBLE);
        } else {
            linearMyPointExpand.setVisibility(View.GONE);
        }

        //Set for action floating buttons
        if (isAllFabVisible) {
            floatingButtonAddFireFighter.setVisibility(View.VISIBLE);
            floatingButtonAddUnit.setVisibility(View.VISIBLE);
            floatingButtonAdd.extend();
        } else {
            floatingButtonAddFireFighter.setVisibility(View.GONE);
            floatingButtonAddUnit.setVisibility(View.GONE);
            floatingButtonAdd.shrink();
        }
    }

    private void goToProfileHomePageAdmin() {
        if(FirebaseManager.getInstance().getCurrentAuthUser().getDisplayName() == null){
            textProfileName.setText("");
        }else {
            textProfileName.setText(FirebaseManager.getInstance().getCurrentAuthUser().getDisplayName());
        }
        textProfileMail.setText(FirebaseManager.getInstance().getCurrentAuthUser().getEmail());
        pageProfileHome.setVisibility(View.VISIBLE);
        pageProfileConnexion.setVisibility(View.GONE);
        linearWorkingOn.setVisibility(View.GONE);
        constraintFloatingAction.setVisibility(View.VISIBLE);
    }
    private void goToProfileHomePageFireFighter(boolean chief, String unit) {
        ConstantsValues.setIsFirefighter(true);
        ConstantsValues.setIsChief(chief);
        ConstantsValues.setUnit(unit);
        if(FirebaseManager.getInstance().getCurrentAuthUser().getDisplayName() == null){
            textProfileName.setText("");
        }else {
            textProfileName.setText(FirebaseManager.getInstance().getCurrentAuthUser().getDisplayName());
        }
        firefighterPointButton.setVisibility(View.GONE);
        textProfileMail.setText(FirebaseManager.getInstance().getCurrentAuthUser().getEmail());
        pageProfileHome.setVisibility(View.VISIBLE);
        pageProfileConnexion.setVisibility(View.GONE);
        linearWorkingOn.setVisibility(View.VISIBLE);
        constraintFloatingAction.setVisibility(View.VISIBLE);
        floatingButtonAddFireFighter.setVisibility(View.GONE);
        floatingButtonAddUnit.setVisibility(View.GONE);
    }
    private void goToProfileHomePageDefault() {
        if(FirebaseManager.getInstance().getCurrentAuthUser().getDisplayName() == null){
            textProfileName.setText("");
        }else {
            textProfileName.setText(FirebaseManager.getInstance().getCurrentAuthUser().getDisplayName());
        }
        firefighterPointButton.setVisibility(View.GONE);
        textProfileMail.setText(FirebaseManager.getInstance().getCurrentAuthUser().getEmail());
        pageProfileHome.setVisibility(View.VISIBLE);
        pageProfileConnexion.setVisibility(View.GONE);
        linearWorkingOn.setVisibility(View.GONE);
        constraintFloatingAction.setVisibility(View.GONE);
    }

    private void goToSignInPage() {
        textProfileName.setText("");
        textProfileMail.setText("");
        pageProfileHome.setVisibility(View.GONE);
        pageProfileConnexion.setVisibility(View.VISIBLE);
        linearWorkingOn.setVisibility(View.GONE);
        constraintFloatingAction.setVisibility(View.GONE);
    }

    private void hideAllFab() {
        floatingButtonAddFireFighter.hide();
        floatingButtonAddUnit.hide();
        isAllFabVisible = false;
        floatingButtonAdd.shrink();
    }

    private void showAllFab() {
        floatingButtonAddFireFighter.show();
        floatingButtonAddUnit.show();
        isAllFabVisible = true;
        floatingButtonAdd.extend();
    }

    private void getPointsPanel() {
        hideLoadingPoints();
        if (isMyPointsPanel) {
            isMyPointsPanel = false;
            //Now hide my points panel
            pointsButton.setIcon(requireActivity().getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_24));
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
            pointsButton.setIcon(requireActivity().getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_up_24));
            linearMyPointExpand.setAlpha(1);
            linearMyPointExpand.setVisibility(View.VISIBLE);
        }
    }

    private void showAddFirefighterDialog(List<DocumentSnapshot> documents) {
        hideAllFab();
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_new_firefighter);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        dialog.findViewById(R.id.button_close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Spinner spinnerUnit = dialog.findViewById(R.id.firefighter_unit);
        //Setup adapter
        ArrayList<String> spinnerArray = new ArrayList<>();
        for (DocumentSnapshot document:documents) {
            if(document.toObject(UnitModel.class) != null){
                String tempUnitName = document.toObject(UnitModel.class).getUnitName();
                if (tempUnitName != null)
                    spinnerArray.add(tempUnitName);
            }
        }
        // Array of choices
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                spinnerArray
        ); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(spinnerArrayAdapter);

        UserModel firefighter = new UserModel();

        SwitchMaterial switchChief = dialog.findViewById(R.id.firefighter_chief);
        switchChief.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    firefighter.setChief(true);
                }
            }
        });
        dialog.findViewById(R.id.button_save_firefighter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText textMail = dialog.findViewById(R.id.text_input_mail);
                //Temp values
                String tempMail = "";
                String tempUnit = "";
                //
                tempMail = String.valueOf(textMail.getText());
                tempUnit = spinnerUnit.getSelectedItem().toString();
                firefighter.setMail(tempMail);
                firefighter.setFireFighter(true);
                firefighter.setUnit(tempUnit);
                if (tempMail.isEmpty()){
                    Toast.makeText(context, "Please enter valid mail name !", Toast.LENGTH_SHORT).show();
                }else{
                    userViewModel.loadUserModel(tempMail).observe(requireActivity(), new Observer<UserModel>() {
                        @Override
                        public void onChanged(UserModel userModel) {
                            if (userModel == null){
                                userViewModel.saveFireFighter(firefighter).observe(requireActivity(), new Observer<Integer>() {
                                    @Override
                                    public void onChanged(Integer integer) {
                                        dialog.dismiss();
                                        if (integer >= 1){
                                            Toast.makeText(context, "Firefighter saved !", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(context, "Error saving firefighter !", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else {
                                userViewModel.updateFireFighter(firefighter).observe(requireActivity(), new Observer<Integer>() {
                                    @Override
                                    public void onChanged(Integer integer) {
                                        dialog.dismiss();
                                        if (integer >= 1){
                                            Toast.makeText(context, "Firefighter added !", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(context, "Error saving firefighter !", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                Toast.makeText(context, "This firefighter already registered !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        dialog.show();
    }

    private void showAddUnitDialog() {
        hideAllFab();
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_new_unit);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        dialog.findViewById(R.id.button_close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.button_save_unit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText textInputEditText = dialog.findViewById(R.id.text_input_unit_name);
                String tempUnitName = "";
                tempUnitName = String.valueOf(textInputEditText.getText());
                if (tempUnitName.isEmpty()){
                    Toast.makeText(context, "Please enter valid unit name !", Toast.LENGTH_SHORT).show();
                }else{
                    String finalTempUnitName = tempUnitName;
                    unitViewModel.getUnitModel(tempUnitName).observe(requireActivity(), new Observer<UnitModel>() {
                        @Override
                        public void onChanged(UnitModel unitModel) {
                            dialog.dismiss();
                            if (unitModel == null){
                                UnitModel unitModelSave = new UnitModel();
                                unitModelSave.setUnitName(finalTempUnitName);
                                unitViewModel.saveUnit(unitModelSave).observe(requireActivity(), new Observer<Integer>() {
                                    @Override
                                    public void onChanged(Integer integer) {
                                        if (integer >= 1){
                                            Toast.makeText(context, "Unit saved !", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(context, "Error saving ! " +integer, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                dialog.dismiss();
                                Toast.makeText(context, "This unit already registered !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        dialog.show();
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
        pointsButton = root.findViewById(R.id.button_points);
        forgotPasswordButton = root.findViewById(R.id.button_forgot_password);

        checkBoxRemember = root.findViewById(R.id.checkbox_remember_me);
        circularProgressIndicatorConnexion = root.findViewById(R.id.progress_circular_connexion);
        circularProgressIndicatorCreation = root.findViewById(R.id.progress_circular_create);
        circularProgressPoints = root.findViewById(R.id.progress_points);


        //Motions layouts
        pageProfileConnexion = root.findViewById(R.id.motion_layout_profile);
        pageProfileHome = root.findViewById(R.id.container);

        //Recyclers
        recyclerViewPoints = root.findViewById(R.id.recycler_points);

        //Toggle Groups
        toggleButton = root.findViewById(R.id.toggle_button);
        emergencyPointButton = root.findViewById(R.id.button_manage_emergency_points);
        waterPointButton = root.findViewById(R.id.button_manage_water_points);
        firefighterPointButton = root.findViewById(R.id.button_manage_firefighter_points);

        //Floating buttons
        floatingButtonAdd = root.findViewById(R.id.floating_action_button_add);
        floatingButtonAddFireFighter = root.findViewById(R.id.floating_action_button_fire_fighter);
        floatingButtonAddUnit = root.findViewById(R.id.floating_action_button_unit);

        //LinearLayouts
        linearMyPointExpand = root.findViewById(R.id.linear_manage_points_expand);
        constraintFloatingAction = root.findViewById(R.id.constraint_floating_action_buttons);
        linearWorkingOn = root.findViewById(R.id.linear_working_on);
        linearRecyclerPoints = root.findViewById(R.id.linear_recycler_points);
    }

    private void showLoading() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                signUpButton.setVisibility(View.GONE);
                signInButton.setVisibility(View.GONE);

                circularProgressIndicatorConnexion.show();
                circularProgressIndicatorConnexion.setVisibility(View.VISIBLE);

                circularProgressIndicatorCreation.show();
                circularProgressIndicatorCreation.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideLoading() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                signUpButton.setVisibility(View.VISIBLE);
                signInButton.setVisibility(View.VISIBLE);

                circularProgressIndicatorConnexion.hide();
                circularProgressIndicatorConnexion.setVisibility(View.INVISIBLE);

                circularProgressIndicatorCreation.hide();
                circularProgressIndicatorCreation.setVisibility(View.INVISIBLE);
            }
        });
    }
}