package com.example.firefighters.ui.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firefighters.R
import com.example.firefighters.adapters.EmergencyPointAdapter
import com.example.firefighters.adapters.FirefighterAdapter
import com.example.firefighters.adapters.WaterPointAdapter
import com.example.firefighters.databinding.FragmentProfileBinding
import com.example.firefighters.models.EmergencyModel
import com.example.firefighters.models.UnitModel
import com.example.firefighters.models.UserModel
import com.example.firefighters.models.WaterPointModel
import com.example.firefighters.utils.ConstantsValues
import com.example.firefighters.utils.ConstantsValues.isIsChief
import com.example.firefighters.utils.ConstantsValues.isIsFirefighter
import com.example.firefighters.utils.ConstantsValues.unit
import com.example.firefighters.utils.FirebaseUtils.Companion.instance
import com.example.firefighters.utils.InjectorUtils
import com.example.firefighters.viewmodels.models.EmergencyViewModel
import com.example.firefighters.viewmodels.models.UnitViewModel
import com.example.firefighters.viewmodels.models.UserViewModel
import com.example.firefighters.viewmodels.models.WaterPointViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Objects

class ProfileFragment : Fragment() {

    private lateinit var mDataBinding: FragmentProfileBinding

    //View models
    private val mUserViewModel by viewModels<UserViewModel> {
        InjectorUtils.provideUserViewModel()
    }
    private val mEmergencyViewModel by viewModels<EmergencyViewModel> {
        InjectorUtils.provideEmergencyViewModel()
    }
    private val mWaterPointViewModel by viewModels<WaterPointViewModel> {
        InjectorUtils.provideWaterPointViewModel()
    }
    private val mUnitViewModel by viewModels<UnitViewModel> {
        InjectorUtils.provideUserViewModel()
    }

    //Boolean
    private var mIsMyPointsPanel: Boolean = false
    private var mIsAllFabVisible: Boolean = false

    private var mEmergencies: ArrayList<EmergencyModel>? = null
    private var mWaterPoints: ArrayList<WaterPointModel>? = null
    private var mFireFighters: ArrayList<UserModel>? = null

    private val mCurrentUser: UserModel = UserModel()

    private var mLayoutManagerEmergencies: LinearLayoutManager? = null
    private var mLayoutManagerFirefighters: LinearLayoutManager? = null
    private var mLayoutManagerWaterPoints: LinearLayoutManager? = null

    private var mEmergencyPointAdapter: EmergencyPointAdapter? = null
    private var mFirefighterAdapter: FirefighterAdapter? = null
    private var mWaterPointAdapter: WaterPointAdapter? = null

    private var mLastDocumentFireFighter: DocumentSnapshot? = null
    private var mLastDocumentEmergency: DocumentSnapshot? = null
    private var mLastDocumentWaterPoint: DocumentSnapshot? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        //Set content with data biding util
        mDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        val view = mDataBinding.root

        //Load your UI content
        setupRecyclersViews()
        checkInteractions()
        setViewStates()

        return view
    }

    private fun loadWorkingUnit() {
        if (unit != null && unit!!.isNotEmpty()) {
            mEmergencyViewModel.getEmergencyWorkingOn(unit)
                .observe(requireActivity()
                ) { value ->
                    if ((value?.documents?.size ?: 0) > 0) {
                        val emergencyModel: EmergencyModel? =
                            value?.documents?.get(0)?.toObject(
                                EmergencyModel::class.java
                            )
                        if (emergencyModel != null) {
                            setCurrentWorkingViews(emergencyModel)
                        } else {
                            removeCurrentWorkingViews()
                        }
                    } else {
                        removeCurrentWorkingViews()
                    }
                }
        } else {
            removeCurrentWorkingViews()
        }
    }

    private fun setCurrentWorkingViews(emergencyModel: EmergencyModel) {
        val tempName: String = "Emergency : EM" + emergencyModel.id
        mDataBinding.layoutPageProfileHome.workingEmergencyName.text = tempName
        val tempUnit: String = "Unit : " + emergencyModel.currentUnit
        mDataBinding.layoutPageProfileHome.workingEmergencyUnit.text = tempUnit
        val tempStatus: String = "Status : " + emergencyModel.status
        mDataBinding.layoutPageProfileHome.workingEmergencyStatus.text = tempStatus
    }

    private fun removeCurrentWorkingViews() {
        mDataBinding.layoutPageProfileHome.workingEmergencyName.text = ""
        mDataBinding.layoutPageProfileHome.workingEmergencyUnit.text = "None !"
        mDataBinding.layoutPageProfileHome.workingEmergencyStatus.text = ""
    }

    private fun checkInteractions() {
        mDataBinding.layoutPageProfileConnexion.buttonSignIn.setOnClickListener { tryToSignIn() }
        mDataBinding.layoutPageProfileConnexion.buttonSignUp.setOnClickListener { tryToSignUp() }
        mDataBinding.layoutPageProfileHome.buttonLogOut.setOnClickListener { signOut() }
        mDataBinding.layoutPageProfileHome.buttonPoints.setOnClickListener { pointsPanel }
        mDataBinding.layoutPageProfileHome.buttonImageAddWaterPoint.setOnClickListener { goToMapViewAddWaterPoint() }
        mDataBinding.layoutPageProfileHome.toggleButton.addOnButtonCheckedListener { _, checkedId, _ ->
            when (checkedId) {
                mDataBinding.layoutPageProfileHome.buttonManageEmergencyPoints.id -> {
                    mEmergencies?.clear()
                    mLastDocumentEmergency = null
                    showLoadingPoints()
                    loadEmergencyRecycler()
                    loadMoreEmergencies()
                }
                mDataBinding.layoutPageProfileHome.buttonManageWaterPoints.id -> {
                    mWaterPoints?.clear()
                    mLastDocumentWaterPoint = null
                    showLoadingPoints()
                    loadWaterRecycler()
                    loadMoreWaterPoints()
                }
                mDataBinding.layoutPageProfileHome.buttonManageFirefighterPoints.id -> {
                    mFireFighters?.clear()
                    mLastDocumentFireFighter = null
                    showLoadingPoints()
                    loadFirefighterRecycler()
                    loadMoreFirefighters()
                }
            }
        }
        //Get action for fab action button
        mDataBinding.floatingActionButtonAdd.setOnClickListener {
            if (mIsAllFabVisible) {
                hideAllFab()
            } else {
                showAllFab()
            }
        }
        mDataBinding.floatingActionButtonFireFighter.setOnClickListener {
            hideAllFab()
            mUnitViewModel.unitsQuery.observe(
                requireActivity()
            ) { queryDocumentSnapshots ->
                if (queryDocumentSnapshots.size() > 0) {
                    showAddFirefighterDialog(queryDocumentSnapshots.documents)
                } else {
                    Toast.makeText(context, "No unit found !", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        mDataBinding.floatingActionButtonUnit.setOnClickListener {
            hideAllFab()
            showAddUnitDialog()
        }
    }

    private fun goToMapViewAddWaterPoint() {
        val fragmentManager = activity?.supportFragmentManager
        val ft: FragmentTransaction? = fragmentManager?.beginTransaction()
        ft?.setCustomAnimations(R.anim.anim_tanslate_scale_in, R.anim.anim_tanslate_scale_out)
        ft?.add(R.id.main_frame_layout, AddWaterPointFragment())
            ?.addToBackStack(null)
        ft?.commit()
    }

    private fun showLoadingPoints() {
        mDataBinding.layoutPageProfileHome.progressPoints.show()
        mDataBinding.layoutPageProfileHome.progressPoints.visibility = View.VISIBLE
    }

    private fun hideLoadingPoints() {
        mDataBinding.layoutPageProfileHome.progressPoints.hide()
        mDataBinding.layoutPageProfileHome.progressPoints.visibility = View.GONE
    }

    private fun setupRecyclersViews() {
        //Setup recycler view for mEmergencies
        mEmergencies = ArrayList()
        mLayoutManagerEmergencies = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mEmergencyPointAdapter = EmergencyPointAdapter(requireContext(), mEmergencies ?: arrayListOf())

        //Setup recycler view for firefighters
        mFireFighters = ArrayList()
        mLayoutManagerFirefighters = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mFirefighterAdapter = FirefighterAdapter(requireContext(), mFireFighters ?: arrayListOf())

        //Setup recycler view for waters points
        mWaterPoints = ArrayList()
        mLayoutManagerWaterPoints = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mWaterPointAdapter = WaterPointAdapter(requireContext(), mWaterPoints ?: arrayListOf())
    }

    private fun loadMoreEmergencies() {
        mEmergencyViewModel.getEmergenciesQuery(mLastDocumentEmergency, null, null, LIMIT_COUNT)
            .observe(requireActivity()
            ) { queryDocumentSnapshots ->
                hideLoadingPoints()
                var startPositionUpdate: Int = 0
                if (mEmergencies != null && (mEmergencies?.size ?: 0) > 0) startPositionUpdate =
                    (mEmergencies?.size ?: 0)
                for (document: DocumentSnapshot in queryDocumentSnapshots) {
                    document.toObject(EmergencyModel::class.java)?.let { mEmergencies?.add(it) }
                }
                if ((mEmergencies?.size ?: 0) > 0) mLastDocumentEmergency =
                    queryDocumentSnapshots.getDocuments().get(0)
                //Update now the view
                var endPositionUpdate: Int = 0
                if ((mEmergencies?.size ?: 0) > 0) endPositionUpdate = (mEmergencies?.size ?: 0) - 1
                mEmergencyPointAdapter?.notifyItemRangeInserted(
                    startPositionUpdate,
                    endPositionUpdate
                )
                mEmergencyViewModel.getEmergenciesQuery(
                    mLastDocumentEmergency,
                    null,
                    null,
                    LIMIT_COUNT
                ).removeObservers(requireActivity())
            }
    }

    private fun loadMoreFirefighters() {
        mUserViewModel.loadFireFighters(mLastDocumentFireFighter, LIMIT_COUNT)
            .observe(requireActivity()
            ) { queryDocumentSnapshots ->
                hideLoadingPoints()
                var startPositionUpdate: Int = 0
                if (mFireFighters != null && (mFireFighters?.size ?: 0) > 0) startPositionUpdate =
                    (mFireFighters?.size ?: 0)
                for (document: DocumentSnapshot in queryDocumentSnapshots) {
                    document.toObject(UserModel::class.java)?.let { mFireFighters?.add(it) }
                }
                if ((mFireFighters?.size ?: 0) > 0) mLastDocumentFireFighter =
                    queryDocumentSnapshots.documents.get(0)

                //Update now the view
                var endPositionUpdate: Int = 0
                if (mFireFighters != null && (mFireFighters?.size ?: 0) > 0) endPositionUpdate =
                    (mFireFighters?.size ?: 0) - 1
                mFirefighterAdapter?.notifyItemRangeInserted(
                    startPositionUpdate,
                    endPositionUpdate
                )
                mUserViewModel.loadFireFighters(
                    mLastDocumentFireFighter,
                    LIMIT_COUNT
                ).removeObservers(requireActivity())
            }
    }

    private fun loadMoreWaterPoints() {
        mWaterPointViewModel.getWaterPointsQuery(mLastDocumentWaterPoint, LIMIT_COUNT)
            .observe(requireActivity()
            ) { queryDocumentSnapshots ->
                hideLoadingPoints()
                var startPositionUpdate = 0
                if (mWaterPoints != null && (mWaterPoints?.size ?: 0) > 0) startPositionUpdate =
                    (mWaterPoints?.size ?: 0)
                for (document: DocumentSnapshot in queryDocumentSnapshots) {
                    document.toObject(WaterPointModel::class.java)?.let { mWaterPoints?.add(it) }
                }
                if ((mWaterPoints?.size ?: 0) > 0) mLastDocumentWaterPoint =
                    queryDocumentSnapshots.getDocuments().get(0)

                //Update now the view
                var endPositionUpdate = 0
                if (mWaterPoints != null && (mWaterPoints?.size ?: 0) > 0) endPositionUpdate =
                    (mWaterPoints?.size ?: 0) - 1
                mWaterPointAdapter?.notifyItemRangeInserted(
                    startPositionUpdate,
                    endPositionUpdate
                )
                mWaterPointViewModel.getWaterPointsQuery(
                    mLastDocumentWaterPoint,
                    LIMIT_COUNT
                ).removeObservers(requireActivity())
            }
    }

    private fun loadEmergencyRecycler() {
        mDataBinding.layoutPageProfileHome.recyclerPoints.removeAllViews()
        mDataBinding.layoutPageProfileHome.recyclerPoints.layoutManager = mLayoutManagerEmergencies
        mDataBinding.layoutPageProfileHome.recyclerPoints.adapter = mEmergencyPointAdapter
    }

    private fun loadFirefighterRecycler() {
        mDataBinding.layoutPageProfileHome.recyclerPoints.removeAllViews()
        mDataBinding.layoutPageProfileHome.recyclerPoints.layoutManager = mLayoutManagerFirefighters
        mDataBinding.layoutPageProfileHome.recyclerPoints.adapter = mFirefighterAdapter
    }

    private fun loadWaterRecycler() {
        mDataBinding.layoutPageProfileHome.recyclerPoints.removeAllViews()
        mDataBinding.layoutPageProfileHome.recyclerPoints.layoutManager = mLayoutManagerWaterPoints
        mDataBinding.layoutPageProfileHome.recyclerPoints.adapter = mWaterPointAdapter
    }

    private fun tryToSignUp() {
        showLoading()
        val userName: String = Objects.requireNonNull(mDataBinding.layoutPageProfileConnexion.textInputSignUpUserName.text).toString()
        val userMail: String = Objects.requireNonNull(mDataBinding.layoutPageProfileConnexion.textInputSignUpMail.getText()).toString()
        val userPassword: String = Objects.requireNonNull(
            mDataBinding.layoutPageProfileConnexion.textInputSignUpPasswordRepeat.getText()
        ).toString()
        mUserViewModel.createNewUser(userName, userMail, userPassword, false)
            .observe(requireActivity()) { integer ->
                if (integer >= 1) {
                    Toast.makeText(context, "Registered !", Toast.LENGTH_SHORT).show()
                    loadUser(userMail)
                } else {
                    hideLoading()
                    Toast.makeText(context, "Error sign up !", Toast.LENGTH_SHORT).show()
                }
                if (activity != null) mUserViewModel.createNewUser(
                    userName,
                    userMail,
                    userPassword,
                    false
                ).removeObservers(requireActivity())
            }
    }

    private fun tryToSignIn() {
        showLoading()
        val userMail: String = mDataBinding.layoutPageProfileConnexion.textInputSignInMail.text.toString()
        val userPassword: String = mDataBinding.layoutPageProfileConnexion.textInputSignInPassword.text.toString()
        mUserViewModel.signInUser(userMail, userPassword)
            .observe(requireActivity()) { integer ->
                if (integer >= 1) {
                    loadUser(userMail)
                } else {
                    hideLoading()
                    Toast.makeText(context, "Error sign in !", Toast.LENGTH_SHORT).show()
                }
                mUserViewModel.signInUser(userMail, userPassword)
                    .removeObservers(requireActivity())
            }
    }

    private fun loadUser(myMail: String) {
        mUserViewModel.loadUserModel(myMail)
            .observe(requireActivity()) {
                if (instance?.currentAuthUser == null || (mCurrentUser == null && instance?.currentAuthUser == null)) {
                    Toast.makeText(context, "Error sign in !", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Logged !", Toast.LENGTH_SHORT).show()
                }
                mUserViewModel.loadUserModel(myMail).removeObservers(requireActivity())
                hideLoading()
                setViewStates()
            }
    }

    private fun signOut() {
        mUserViewModel.logOut().observe(requireActivity()
        ) { integer ->
            hideLoading()
            if (integer >= 1) {
                setViewStates()
                Toast.makeText(context, "Disconnected !", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setViewStates() {
        hideLoadingPoints()

        //Set the correct page if the user is auth
        if (instance?.currentAuthUser == null || (mCurrentUser == null && instance?.currentAuthUser == null)) {
            //Set the connexion page
            mUserViewModel.logOut()
            goToSignInPage()
        } else {
            if (instance?.currentAuthUser != null && instance?.currentAuthUser?.email != null) {
                if ((instance?.currentAuthUser?.email == ConstantsValues.ADMIN_EMAIL)) {
                    //Go to admin page
                    goToProfileHomePageAdmin()
                } else {
                    mUserViewModel.loadUserModel(instance?.currentAuthUser?.email)
                        .observe(requireActivity()
                        ) { userModel ->
                            if (userModel != null) {
                                if (userModel.isFireFighter) {
                                    //Go to firefighter page
                                    goToProfileHomePageFireFighter(
                                        userModel.isChief,
                                        userModel.unit
                                    )
                                } else {
                                    //Go to basic user page
                                    goToProfileHomePageDefault()
                                }
                            } else {
                                //Go to basic user page
                                goToProfileHomePageDefault()
                            }
                            loadWorkingUnit()
                            if (activity != null) mUserViewModel.loadUserModel(instance?.currentAuthUser?.email)
                                .removeObservers(requireActivity())
                        }
                }
            }
        }
        if (mIsMyPointsPanel) {
            mDataBinding.layoutPageProfileHome.linearManagePointsExpand.setVisibility(View.VISIBLE)
        } else {
            mDataBinding.layoutPageProfileHome.linearManagePointsExpand.setVisibility(View.GONE)
        }

        //Set for action floating buttons
        if (mIsAllFabVisible) {
            mDataBinding.floatingActionButtonFireFighter.visibility = View.VISIBLE
            mDataBinding.floatingActionButtonUnit.visibility = View.VISIBLE
            mDataBinding.floatingActionButtonAdd.extend()
        } else {
            mDataBinding.floatingActionButtonFireFighter.visibility = View.GONE
            mDataBinding.floatingActionButtonUnit.visibility = View.GONE
            mDataBinding.floatingActionButtonAdd.shrink()
        }
    }

    private fun goToProfileHomePageAdmin() {
        if (instance?.currentAuthUser?.displayName == null) {
            mDataBinding.layoutPageProfileHome.textProfileUserName.text = ""
        } else {
            mDataBinding.layoutPageProfileHome.textProfileUserName.text = instance?.currentAuthUser?.displayName
        }
        mDataBinding.layoutPageProfileHome.textProfileMail.text = instance?.currentAuthUser?.email
        mDataBinding.layoutPageProfileHome.container.visibility = View.VISIBLE
        mDataBinding.layoutPageProfileConnexion.motionLayoutProfile.visibility = View.GONE
        mDataBinding.layoutPageProfileHome.linearWorkingOn.visibility = View.GONE
        mDataBinding.constraintFloatingActionButtons.visibility = View.VISIBLE
        mDataBinding.layoutPageProfileHome.linearPoints.visibility = View.VISIBLE
    }

    private fun goToProfileHomePageFireFighter(chief: Boolean, unit: String?) {
        isIsFirefighter = true
        isIsChief = chief
        ConstantsValues.unit = unit
        if (instance?.currentAuthUser?.displayName == null) {
            mDataBinding.layoutPageProfileHome.textProfileUserName.text = ""
        } else {
            mDataBinding.layoutPageProfileHome.textProfileUserName.text = instance?.currentAuthUser?.displayName
        }
        mDataBinding.layoutPageProfileHome.buttonManageFirefighterPoints.visibility = View.GONE
        mDataBinding.layoutPageProfileHome.textProfileMail.text = instance?.currentAuthUser?.email
        mDataBinding.layoutPageProfileHome.container.visibility = View.VISIBLE
        mDataBinding.layoutPageProfileConnexion.motionLayoutProfile.visibility = View.GONE
        mDataBinding.layoutPageProfileHome.linearWorkingOn.visibility = View.VISIBLE
        mDataBinding.constraintFloatingActionButtons.visibility = View.GONE
        mDataBinding.floatingActionButtonFireFighter.visibility = View.GONE
        mDataBinding.floatingActionButtonUnit.visibility = View.GONE
        mDataBinding.layoutPageProfileHome.linearPoints.visibility = View.VISIBLE
    }

    private fun goToProfileHomePageDefault() {
        if (instance?.currentAuthUser?.displayName == null) {
            mDataBinding.layoutPageProfileHome.textProfileUserName.text = ""
        } else {
            mDataBinding.layoutPageProfileHome.textProfileUserName.text = instance?.currentAuthUser?.displayName
        }
        mDataBinding.layoutPageProfileHome.buttonManageFirefighterPoints.visibility = View.GONE
        mDataBinding.layoutPageProfileHome.linearPoints.visibility = View.GONE
        mDataBinding.layoutPageProfileHome.textProfileMail.text = instance?.currentAuthUser?.email
        mDataBinding.layoutPageProfileHome.container.visibility = View.VISIBLE
        mDataBinding.layoutPageProfileConnexion.motionLayoutProfile.visibility = View.GONE
        mDataBinding.layoutPageProfileHome.linearWorkingOn.visibility = View.GONE
        mDataBinding.constraintFloatingActionButtons.visibility = View.GONE
    }

    private fun goToSignInPage() {
        mDataBinding.layoutPageProfileHome.textProfileUserName.text = ""
        mDataBinding.layoutPageProfileHome.textProfileMail.text = ""
        mDataBinding.layoutPageProfileHome.container.visibility = View.GONE
        mDataBinding.layoutPageProfileConnexion.motionLayoutProfile.visibility = View.VISIBLE
        mDataBinding.layoutPageProfileHome.linearWorkingOn.visibility = View.GONE
        mDataBinding.constraintFloatingActionButtons.visibility = View.GONE
    }

    private fun hideAllFab() {
        mDataBinding.floatingActionButtonFireFighter.hide()
        mDataBinding.floatingActionButtonUnit.hide()
        mIsAllFabVisible = false
        mDataBinding.floatingActionButtonAdd.shrink()
    }

    private fun showAllFab() {
        mDataBinding.floatingActionButtonFireFighter.show()
        mDataBinding.floatingActionButtonUnit.show()
        mIsAllFabVisible = true
        mDataBinding.floatingActionButtonAdd.extend()
    }

    private val pointsPanel: Unit
        get() {
            hideLoadingPoints()
            if (mIsMyPointsPanel) {
                mIsMyPointsPanel = false
                //Now hide my points panel
                mDataBinding.layoutPageProfileHome.buttonPoints.setIcon(
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24)
                )
                mDataBinding.layoutPageProfileHome.linearManagePointsExpand.animate()
                    .alpha(0f)
                    .setDuration(100)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            mDataBinding.layoutPageProfileHome.linearManagePointsExpand.visibility =
                                View.GONE
                        }
                    })
            } else {
                mIsMyPointsPanel = true
                //Now show my points panel
                mDataBinding.layoutPageProfileHome.buttonPoints.setIcon(
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_up_24)
                )
                mDataBinding.layoutPageProfileHome.linearManagePointsExpand.alpha = 1f
                mDataBinding.layoutPageProfileHome.linearManagePointsExpand.visibility = View.VISIBLE
            }
        }

    private fun showAddFirefighterDialog(documents: List<DocumentSnapshot>) {
        hideAllFab()
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_new_firefighter)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawable(AppCompatResources.getDrawable(requireContext(), R.color.transparent))
        dialog.findViewById<View>(R.id.button_close_dialog)
            .setOnClickListener { dialog.dismiss() }
        val spinnerUnit: Spinner = dialog.findViewById(R.id.firefighter_unit)
        //Setup adapter
        val spinnerArray: ArrayList<String> = ArrayList()
        for (document: DocumentSnapshot in documents) {
            if (document.toObject(UnitModel::class.java) != null) {
                val tempUnitName: String? = document.toObject(UnitModel::class.java)?.unitName
                if (tempUnitName != null) spinnerArray.add(tempUnitName)
            }
        }
        // Array of choices
        val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinnerArray
        ) //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUnit.adapter = spinnerArrayAdapter
        val firefighter = UserModel()
        val switchChief: SwitchMaterial = dialog.findViewById(R.id.firefighter_chief)
        switchChief.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                firefighter.isChief = true
            }
        }
        dialog.findViewById<View>(R.id.button_save_firefighter)
            .setOnClickListener {
                val textMail: TextInputEditText = dialog.findViewById(R.id.text_input_mail)
                //Temp values

                firefighter.mail = textMail.text.toString()
                firefighter.isFireFighter = true
                firefighter.unit = spinnerUnit.selectedItem.toString()
                if (textMail.text.isNullOrEmpty()) {
                    Toast.makeText(
                        context,
                        "Please enter valid mail name !",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    mUserViewModel.loadUserModel(textMail.text.toString())
                        .observe(
                            requireActivity()
                        ) { userModel ->
                            if (userModel == null) {
                                mUserViewModel.saveFireFighter(firefighter)
                                    .observe(
                                        requireActivity()
                                    ) { integer ->
                                        dialog.dismiss()
                                        if ((integer ?: 0) >= 1) {
                                            Toast.makeText(
                                                context,
                                                "Firefighter saved !",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Error saving firefighter !",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                mUserViewModel.updateFireFighter(firefighter)
                                    .observe(
                                        requireActivity()
                                    ) { integer ->
                                        dialog.dismiss()
                                        if (integer >= 1) {
                                            Toast.makeText(
                                                context,
                                                "Firefighter added !",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Error saving firefighter !",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                Toast.makeText(
                                    context,
                                    "This firefighter already registered !",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        dialog.show()
    }

    private fun showAddUnitDialog() {
        hideAllFab()
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_new_unit)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawable(AppCompatResources.getDrawable(requireContext(), R.color.transparent))
        dialog.findViewById<View>(R.id.button_close_dialog)
            .setOnClickListener { dialog.dismiss() }
        dialog.findViewById<View>(R.id.button_save_unit)
            .setOnClickListener {
                val textInputEditText: TextInputEditText =
                    dialog.findViewById(R.id.text_input_unit_name)
                if (textInputEditText.text.isNullOrEmpty()) {
                    Toast.makeText(
                        context,
                        "Please enter valid unit name !",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    mUnitViewModel.getUnitModel(textInputEditText.text.toString())
                        .observe(
                            requireActivity()
                        ) { value ->
                            dialog.dismiss()
                            if (value == null) {
                                val unitModelSave = UnitModel()
                                unitModelSave.unitName = textInputEditText.text.toString()
                                mUnitViewModel.saveUnit(unitModelSave)
                                    .observe(
                                        requireActivity()
                                    ) { integer ->
                                        if ((integer ?: 0) >= 1) {
                                            Toast.makeText(
                                                context,
                                                "Unit saved !",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Error saving ! $integer",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                dialog.dismiss()
                                Toast.makeText(
                                    context,
                                    "This unit already registered !",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        dialog.show()
    }

    private fun showLoading() {
        requireActivity().runOnUiThread {
            mDataBinding.layoutPageProfileConnexion.buttonSignUp.visibility = View.GONE
            mDataBinding.layoutPageProfileConnexion.buttonSignIn.visibility = View.GONE
            mDataBinding.layoutPageProfileConnexion.progressCircularConnexion.show()
            mDataBinding.layoutPageProfileConnexion.progressCircularConnexion.visibility =
                View.VISIBLE
            mDataBinding.layoutPageProfileConnexion.progressCircularCreate.show()
            mDataBinding.layoutPageProfileConnexion.progressCircularCreate.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        requireActivity().runOnUiThread {
            mDataBinding.layoutPageProfileConnexion.buttonSignUp.visibility = View.VISIBLE
            mDataBinding.layoutPageProfileConnexion.buttonSignIn.visibility = View.VISIBLE
            mDataBinding.layoutPageProfileConnexion.progressCircularConnexion.hide()
            mDataBinding.layoutPageProfileConnexion.progressCircularConnexion.visibility =
                View.INVISIBLE
            mDataBinding.layoutPageProfileConnexion.progressCircularCreate.hide()
            mDataBinding.layoutPageProfileConnexion.progressCircularCreate.visibility =
                View.INVISIBLE
        }
    }

    companion object {
        const val LIMIT_COUNT: Int = 10
    }
}