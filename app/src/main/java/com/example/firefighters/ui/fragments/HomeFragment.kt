package com.example.firefighters.ui.fragments

import android.Manifest
import android.animation.FloatEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.Intent.URI_INTENT_SCHEME
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import com.example.firefighters.R
import com.example.firefighters.databinding.FragmentHomeBinding
import com.example.firefighters.models.EmergencyModel
import com.example.firefighters.models.MessageModel
import com.example.firefighters.ui.activities.BluetoothActivity
import com.example.firefighters.utils.ConstantsValues
import com.example.firefighters.utils.FirebaseUtils
import com.example.firefighters.utils.InjectorUtils
import com.example.firefighters.utils.PermissionsManager
import com.example.firefighters.viewmodels.models.EmergencyViewModel
import com.example.firefighters.viewmodels.models.MessageViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class HomeFragment : Fragment() {

    private lateinit var mDataBinding: FragmentHomeBinding

    //View models
    private val mEmergencyViewModel by viewModels<EmergencyViewModel> {
        InjectorUtils.provideEmergencyViewModel()
    }
    private val mMessageViewModel by viewModels<MessageViewModel> {
        InjectorUtils.provideMessageViewModel()
    }

    private val phoneNumber: Int = 0
    var countDownTimerCall: CountDownTimer? = null
    var countDownTimerSos: CountDownTimer? = null

    private var mediaRecorder: MediaRecorder? = null
    private val handlerRecordVoice: Handler = Handler(Looper.getMainLooper())
    private var runnableRecordVoice: Runnable? = null
    private val canRunThread: Boolean = true
    private var imageActivityResult: ActivityResultLauncher<Intent>? = null
    private var videoActivityResult: ActivityResultLauncher<Intent>? = null
    private var audioPath: String = ""

    //UI Text
    private var mTextImageResource: MaterialTextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Set content with data biding util
        mDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val view = mDataBinding.root

        setAnimations()
        registerMediaResults()
        checkInteractions()

        return view
    }

    private fun registerMediaResults() {
        imageActivityResult = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result?.data != null) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                    val tempBitmap: Bitmap? = result.data?.extras?.get("data") as Bitmap?
                    val uri = result.data?.toUri(URI_INTENT_SCHEME)
                    Toast.makeText(context, "${uri}", Toast.LENGTH_SHORT).show()
                    val tempImagePath: String = saveImageToInternalStorage(tempBitmap)
                    mTextImageResource?.text = tempImagePath
                    Toast.makeText(context, "Image received !", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        context,
                        "No implementation for Android 11+ !",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        videoActivityResult = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result?.data != null) {
                Toast.makeText(context, result.data.toString() + "", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun saveImageToInternalStorage(bitmapImage: Bitmap?): String {
        val cw: ContextWrapper = ContextWrapper(requireContext())
        // path to /data/data/covid_app/app_data/imageDir
        val directory: File = cw.getDir("imageDir", Context.MODE_PRIVATE)
        // Create imageDir
        val myPath: File = File(directory, "emergencyimage.jpg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(myPath)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        if (fos != null) {
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage!!.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        }
        return directory.absolutePath + "/emergencyimage.jpg"
    }
    private fun saveVideoToInternalStorage(bitmapImage: Uri): String {
        val cw: ContextWrapper = ContextWrapper(requireContext())
        // path to /data/data/covid_app/app_data/imageDir
        val directory: File = cw.getDir("videoDir", Context.MODE_PRIVATE)
        return directory.absolutePath + "/emergencyvideo.mp4"
    }

    private fun setAnimations() {
        //val RED: Int = -0xfbabb
        //val TRANSPARENT: Int = -0x1
        val colorAnim: ObjectAnimator = ObjectAnimator.ofFloat(mDataBinding.relativeSosBackground, "alpha", 0f, 1f)
        colorAnim.setDuration(2000)
        colorAnim.interpolator = AccelerateDecelerateInterpolator()
        colorAnim.setEvaluator(FloatEvaluator())
        colorAnim.repeatCount = ValueAnimator.INFINITE
        colorAnim.repeatMode = ValueAnimator.REVERSE
        colorAnim.start()
    }

    private fun checkInteractions() {
        mDataBinding.buttonImageBluetooth.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                showBluetoothActivity()
            }
        })
        mDataBinding.buttonImageSettings.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                showBottomSheetDialogSettings()
            }
        })
        mDataBinding.buttonCallNow.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                showBottomSheetDialogCallNow()
            }
        })
        mDataBinding.buttonSmsNow.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if (PermissionsManager.isMessagePermissions(requireActivity())) {
                    showDialogSendSmsNow()
                } else {
                    showDialogGetPermissions("sms", ConstantsValues.SMS_PERMISSION_CODE)
                }
            }
        })
        mDataBinding.buttonTextSos.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                showBottomSheetDialogSos()
            }
        })
        mDataBinding.buttonShowMap.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                showMapView()
            }
        })
    }

    private fun showDialogSendSmsNow() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_send_sms)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawable(AppCompatResources.getDrawable(requireContext(), R.color.transparent))
        dialog.findViewById<View>(R.id.button_close_dialog)
            .setOnClickListener { dialog.dismiss() }
        val textInputEditPhoneNumber: TextInputEditText =
            dialog.findViewById(R.id.text_input_sms_phone_number)
        val textInputEditMessage: TextInputEditText =
            dialog.findViewById(R.id.text_input_sms_message)
        dialog.findViewById<View>(R.id.button_send_sms_unit)
            .setOnClickListener {
                var tempNumber = ""
                var tempMessage = ""
                if (textInputEditPhoneNumber.text != null && textInputEditMessage.text != null) {
                    tempNumber = textInputEditPhoneNumber.text.toString()
                    tempMessage = textInputEditMessage.text.toString()
                }
                if (tempNumber.isNotEmpty() && tempMessage.isNotEmpty()) {
                    dialog.dismiss()
                    sendSMS(tempNumber, tempMessage)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please enter valid phone number and message !",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        dialog.show()
    }

    private fun showDialogCallCustomPhoneNow() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_call)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawable(AppCompatResources.getDrawable(requireContext(), R.color.transparent))
        dialog.findViewById<View>(R.id.button_close_dialog)
            .setOnClickListener { dialog.dismiss() }
        val textInputEditPhoneNumber: TextInputEditText =
            dialog.findViewById(R.id.text_input_call_phone_number)
        dialog.findViewById<View>(R.id.button_call_unit)
            .setOnClickListener {
                var tempNumber = ""
                if (textInputEditPhoneNumber.text != null) {
                    tempNumber = textInputEditPhoneNumber.text.toString()
                }
                if (tempNumber.isNotEmpty()) {
                    dialog.dismiss()
                    callCustomUnit(tempNumber)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please enter valid phone number !",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        dialog.show()
    }

    private fun callCustomUnit(tempNumber: String) {
        if (PermissionsManager.isCallPermissions(requireActivity())) {
            val intent = Intent(Intent.ACTION_CALL)
            intent.setData(Uri.parse("tel:$tempNumber"))
            startActivity(intent)
        } else {
            Toast.makeText(context, "No permissions found !", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendSMS(tempNumber: String, tempMessage: String) {
        if (PermissionsManager.isMessagePermissions(requireActivity())) {
            val smsManager: SmsManager? = ContextCompat.getSystemService(requireContext(), SmsManager::class.java)
            smsManager?.sendTextMessage(tempNumber, null, tempMessage, null, null)
            Toast.makeText(context, "SMS sent without report!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No permissions found !", Toast.LENGTH_SHORT).show()
        }
    }

    //Show bottom sheet dialog for call
    private fun showBottomSheetDialogCallNow() {
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(R.layout.bottom_sheet_dialog_call_now)
        bottomSheet.setCancelable(false)
        bottomSheet.setCanceledOnTouchOutside(false)
        bottomSheet.window
            ?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(
                R.color.transparent
            )
        bottomSheet.findViewById<View>(R.id.button_cancel_call)
            ?.setOnClickListener(View.OnClickListener {
                bottomSheet.dismiss()
                countDownTimerCall?.cancel()
            })
        bottomSheet.findViewById<View>(R.id.button_get_permissions)
            ?.setOnClickListener {
                bottomSheet.dismiss()
                PermissionsManager.requestCallPermission(activity)
            }
        bottomSheet.findViewById<View>(R.id.button_custom_call)
            ?.setOnClickListener {
                bottomSheet.dismiss()
                if (PermissionsManager.isCallPermissions(requireActivity())) {
                    countDownTimerCall?.cancel()
                    showDialogCallCustomPhoneNow()
                } else {
                    showDialogGetPermissions("call", ConstantsValues.CALL_PERMISSION_CODE)
                }
            }
        tryToGetCallPermissions(bottomSheet)
        bottomSheet.show()
    }

    private fun tryToGetCallPermissions(bottomSheet: BottomSheetDialog) {
        //val startCount: Int = 5
        if (PermissionsManager.isCallPermissions(activity)) {
            bottomSheet.findViewById<View>(R.id.linear_no_permissions)?.visibility = View.GONE
            bottomSheet.findViewById<View>(R.id.linear_waiting)?.visibility = View.VISIBLE
            val text: MaterialTextView =
                bottomSheet.findViewById<View>(R.id.text_progression) as MaterialTextView
            countDownTimerCall = object : CountDownTimer(6000, 1000) {
                public override fun onTick(millisUntilFinished: Long) {
                    text.setText("" + millisUntilFinished / 1000)
                    //here you can have your logic to set text to edittext
                }

                public override fun onFinish() {
                    text.setText("")
                    callNow()
                    bottomSheet.dismiss()
                }
            }
            countDownTimerCall?.start()
        } else {
            bottomSheet.findViewById<View>(R.id.linear_no_permissions)?.visibility = View.VISIBLE
            bottomSheet.findViewById<View>(R.id.linear_waiting)?.visibility = View.GONE
        }
    }

    private fun callNow() {
        if ((ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            )
                    != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(Manifest.permission.CALL_PHONE),
                ConstantsValues.CALL_PERMISSION_CODE
            )
        } else {
            val intent = Intent(Intent.ACTION_CALL)
            intent.setData(Uri.parse("tel:$phoneNumber"))
            startActivity(intent)
        }
    }

    private fun getBluetoothPermissions(bottomSheet: BottomSheetDialog) {
        //val startCount: Int = 5
        if (PermissionsManager.isBluetoothPermissions(activity)) {
            bottomSheet.findViewById<View>(R.id.linear_no_permissions)?.visibility = View.GONE
            bottomSheet.findViewById<View>(R.id.linear_waiting)?.visibility = View.VISIBLE
            val text: MaterialTextView =
                bottomSheet.findViewById<View>(R.id.text_progression) as MaterialTextView
            countDownTimerCall = object : CountDownTimer(6000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    text.text = "" + millisUntilFinished / 1000
                    //here you can have your logic to set text to edittext
                }

                override fun onFinish() {
                    text.text = ""
                    bottomSheet.dismiss()
                }
            }
            countDownTimerCall?.start()
        } else {
            bottomSheet.findViewById<View>(R.id.linear_no_permissions)?.visibility = View.VISIBLE
            bottomSheet.findViewById<View>(R.id.linear_waiting)?.visibility = View.GONE
        }
    }

    private fun showMapView() {
        if (PermissionsManager.isLocationPermissions(requireActivity())) {
            val fm = activity?.supportFragmentManager
            val ft: FragmentTransaction? = fm?.beginTransaction()
            ft?.setCustomAnimations(R.anim.anim_tanslate_scale_in, R.anim.anim_tanslate_scale_out)
            ft?.add(R.id.main_frame_layout, MapViewFragment())?.addToBackStack(null)
            ft?.commit()
        } else {
            showBottomSheetDialogLocationPermissions()
        }
    }

    private fun showBluetoothActivity() {
        if (PermissionsManager.isBluetoothPermissions(requireActivity())) {
            val myIntent = Intent(activity, BluetoothActivity::class.java)
            activity?.startActivity(myIntent)
        } else {
            showBottomSheetDialogBluetoothPermissions()
        }
    }

    private fun showBottomSheetDialogSettings() {
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(R.layout.bottom_sheet_dialog_settings)
        bottomSheet.setCancelable(true)
        bottomSheet.setCanceledOnTouchOutside(true)
        bottomSheet.window
            ?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(
                R.color.transparent
            )
        bottomSheet.findViewById<View>(R.id.button_close_dialog)
            ?.setOnClickListener { bottomSheet.dismiss() }
        val switchMaterial: SwitchMaterial? =
            bottomSheet.findViewById(R.id.switch_theme)
        if (switchMaterial != null) {
            //Set theme
            val sharedPref: SharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE)!!
            val theme: String = sharedPref.getString(getString(R.string.save_theme), "").toString()
            switchMaterial.isChecked = (theme == "dark")
            switchMaterial.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    setTheme("dark")
                } else {
                    setTheme("light")
                }
            })
        }
        bottomSheet.show()
    }

    private fun setTheme(theme: String) {
        val sharedPref: SharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE)!!
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(getString(R.string.save_theme), theme)
        editor.apply()
        if ((theme == "dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    //Show bottom sheet dialog send sos
    private fun showBottomSheetDialogSos() {
        val bottomSheetSos = BottomSheetDialog(requireContext())
        bottomSheetSos.setContentView(R.layout.bottom_sheet_dialog_sos)
        bottomSheetSos.setCancelable(false)
        bottomSheetSos.setCanceledOnTouchOutside(false)
        bottomSheetSos.window
            ?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(
                R.color.transparent
            )
        bottomSheetSos.findViewById<View>(R.id.button_add_media_dialog)
            ?.setOnClickListener {
                bottomSheetSos.dismiss()
                if (countDownTimerSos != null) countDownTimerSos?.cancel()
                showBottomSheetDialogAddMedia()
            }
        bottomSheetSos.findViewById<View>(R.id.button_cancel_sos)
            ?.setOnClickListener {
                bottomSheetSos.dismiss()
                countDownTimerSos?.cancel()
            }
        bottomSheetSos.findViewById<View>(R.id.button_get_permissions)
            ?.setOnClickListener {
                bottomSheetSos.dismiss()
                PermissionsManager.requestLocationPermission(activity)
            }
        tryToGetSosPermissions(bottomSheetSos)
        bottomSheetSos.show()
    } //Show bottom sheet dialog send sos

    private fun showBottomSheetDialogLocationPermissions() {
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(R.layout.bottom_sheet_dialog_location_permissions)
        bottomSheet.setCancelable(false)
        bottomSheet.setCanceledOnTouchOutside(false)
        bottomSheet.window
            ?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(
                R.color.transparent
            )
        bottomSheet.findViewById<View>(R.id.button_cancel_permission)
            ?.setOnClickListener { bottomSheet.dismiss() }
        bottomSheet.findViewById<View>(R.id.button_get_permissions)
            ?.setOnClickListener {
                bottomSheet.dismiss()
                PermissionsManager.requestLocationPermission(activity)
            }
        bottomSheet.show()
    }

    private fun showBottomSheetDialogBluetoothPermissions() {
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(R.layout.bottom_sheet_dialog_bluetooth_permission)
        bottomSheet.setCancelable(false)
        bottomSheet.setCanceledOnTouchOutside(false)
        bottomSheet.window
            ?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(
                R.color.transparent
            )
        bottomSheet.findViewById<View>(R.id.button_cancel_permission)
            ?.setOnClickListener { bottomSheet.dismiss() }
        bottomSheet.findViewById<View>(R.id.button_get_permissions)
            ?.setOnClickListener {
                bottomSheet.dismiss()
                PermissionsManager.requestBluetoothPermission(activity)
            }
        bottomSheet.show()
    }

    private fun showBottomSheetDialogAddMedia() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_media)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawable(AppCompatResources.getDrawable(requireContext(), R.color.transparent))
        mTextImageResource = dialog.findViewById(R.id.text_image_resource)
        mTextImageResource?.text = ""
        dialog.findViewById<View>(R.id.button_close_dialog)
            .setOnClickListener { dialog.dismiss() }
        dialog.findViewById<View>(R.id.button_add_image)
            .setOnClickListener {
                if (PermissionsManager.isCameraPermissions(requireActivity())) {
                    imageFromActivityResult
                } else {
                    showDialogGetPermissions("camera", ConstantsValues.CAMERA_PERMISSION_CODE)
                }
            }
        dialog.findViewById<View>(R.id.button_add_video)
            .setOnClickListener {
                if (PermissionsManager.isCameraPermissions(requireActivity())) {
                    videoFromActivityResult
                } else {
                    showDialogGetPermissions("camera", ConstantsValues.CAMERA_PERMISSION_CODE)
                }
            }
        dialog.findViewById<View>(R.id.button_add_audio)
            .setOnClickListener {
                if (PermissionsManager.isAudioRecordingPermissions(requireActivity())
                ) {
                    recordAudio(dialog)
                } else {
                    showDialogGetPermissions(
                        "audio record",
                        ConstantsValues.AUDIO_RECORD_PERMISSION_CODE
                    )
                }
            }
        dialog.findViewById<View>(R.id.button_stop_recording_audio)
            .setOnClickListener(object : View.OnClickListener {
                public override fun onClick(v: View) {
                    stopRecordingVoiceAndSave(dialog)
                }
            })
        dialog.findViewById<View>(R.id.button_remove_audio)
            .setOnClickListener { clearAudioResource(dialog) }
        //Circular progress indicator
        dialog.findViewById<View>(R.id.button_send_emergency)
            .setOnClickListener {
                val videoUrl: MaterialTextView =
                    dialog.findViewById(R.id.text_video_resource)
                val audioUrl: MaterialTextView =
                    dialog.findViewById(R.id.text_audio_resource)
                val message: TextInputEditText =
                    dialog.findViewById(R.id.text_input_emergency_message)
                sendMediaSOS(
                    dialog,
                    mTextImageResource?.text.toString(),
                    videoUrl.text.toString(),
                    audioUrl.text.toString(),
                    message.text.toString()
                )
            }
        dialog.show()
    }

    private fun showDialogGetPermissions(title: String?, permissionCode: Int) {
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(R.layout.bottom_sheet_dialog_get_permissions)
        bottomSheet.setCancelable(false)
        bottomSheet.setCanceledOnTouchOutside(false)
        bottomSheet.window
            ?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(
                R.color.transparent
            )
        val textViewTitle: MaterialTextView? =
            bottomSheet.findViewById(R.id.permission_title)
        if (textViewTitle != null) {
            val tempTitle: String = if (!title.isNullOrEmpty()) {
                "No $title permissions found !"
            } else {
                "No permissions found !"
            }
            textViewTitle.text = tempTitle
        }
        bottomSheet.findViewById<View>(R.id.button_cancel_permission)
            ?.setOnClickListener { bottomSheet.dismiss() }
        bottomSheet.findViewById<View>(R.id.button_get_permissions)
            ?.setOnClickListener {
                bottomSheet.dismiss()
                when (permissionCode) {
                    ConstantsValues.AUDIO_RECORD_PERMISSION_CODE -> {
                        PermissionsManager.requestAudioRecordPermission(requireActivity())
                    }
                    ConstantsValues.CAMERA_PERMISSION_CODE -> {
                        PermissionsManager.requestCameraPermission(requireActivity())
                    }
                    ConstantsValues.CALL_PERMISSION_CODE -> {
                        PermissionsManager.requestCallPermission(requireActivity())
                    }
                    ConstantsValues.SMS_PERMISSION_CODE -> {
                        PermissionsManager.requestMessagePermission(requireActivity())
                    }
                }
            }
        bottomSheet.show()
    }

    private fun clearAudioResource(dialog: Dialog) {
        dialog.findViewById<View>(R.id.button_remove_audio).visibility = View.GONE
        dialog.findViewById<View>(R.id.button_add_audio).visibility = View.VISIBLE
        val recordingTime: MaterialTextView =
            dialog.findViewById(R.id.text_audio_resource)
        recordingTime.text = ""
    }

    private fun recordAudio(dialog: Dialog) {
        showRecordingView(dialog)
        val recordingTimeTextView: MaterialTextView =
            dialog.findViewById(R.id.text_recording_audio_time)
        recordingTimeTextView.text = "0"

        //Start recording
        var tempFileName = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val calendar: Calendar = Calendar.getInstance()
            tempFileName += calendar.get(Calendar.YEAR)
            tempFileName += calendar.get(Calendar.MONTH)
            tempFileName += calendar.get(Calendar.DAY_OF_MONTH)
            tempFileName += calendar.get(Calendar.HOUR)
            tempFileName += calendar.get(Calendar.MINUTE)
            tempFileName += calendar.get(Calendar.SECOND)
            tempFileName += calendar.get(Calendar.MILLISECOND)
        } else {
            val calendar = java.util.Calendar.getInstance()
            tempFileName += calendar.get(java.util.Calendar.YEAR)
            tempFileName += calendar.get(java.util.Calendar.MONTH)
            tempFileName += calendar.get(java.util.Calendar.DAY_OF_MONTH)
            tempFileName += calendar.get(java.util.Calendar.HOUR)
            tempFileName += calendar.get(java.util.Calendar.MINUTE)
            tempFileName += calendar.get(java.util.Calendar.SECOND)
            tempFileName += calendar.get(java.util.Calendar.MILLISECOND)
        }
        tempFileName = "$tempFileName.3pg"
        audioPath = ""
        audioPath = getFilePath(tempFileName)
        if (mediaRecorder != null) {
            mediaRecorder?.stop()
            mediaRecorder?.release()
        }
        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
             MediaRecorder(requireContext())
        }else {
            MediaRecorder()
        }
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setOutputFile(audioPath)
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            runnableRecordVoice = object : Runnable {
                override fun run() {
                    if (canRunThread) {
                        var newDuration: Float =
                            recordingTimeTextView.text.toString().toFloat()
                        newDuration += 1f
                        updateRecordingDuration(recordingTimeTextView, newDuration)
                        handlerRecordVoice.postDelayed(this, 1000)
                    }
                }
            }
            //Start
            handlerRecordVoice.postDelayed(runnableRecordVoice!!, 1000)
        } catch (e: IOException) {
            e.printStackTrace()
            mediaRecorder = null
            hideRecordingView(dialog)
            Toast.makeText(context, "Unknown error ${e.toString()}", Toast.LENGTH_LONG).show()
        }
    }

    private fun getFilePath(tempFileName: String): String {
        val contextWrapper =
            ContextWrapper(requireActivity().applicationContext)
        val musicDirectory: File = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)!!
        val file = File(musicDirectory, tempFileName)
        return file.path
    }

    private fun updateRecordingDuration(materialTextView: MaterialTextView, newDuration: Float) {
        val finalTempDuration: String = "" + newDuration
        activity?.runOnUiThread { materialTextView.text = finalTempDuration }
    }

    private fun stopRecordingVoiceAndSave(dialog: Dialog) {
        hideRecordingView(dialog)
        if (mediaRecorder != null) {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            handlerRecordVoice.removeCallbacks((runnableRecordVoice)!!)
            Toast.makeText(context, "Voice Registered !", Toast.LENGTH_SHORT).show()
        }
        val textAudioResource: MaterialTextView =
            dialog.findViewById<MaterialTextView>(R.id.text_audio_resource)
        textAudioResource.setText(audioPath)
    }

    private fun showRecordingView(dialog: Dialog) {
        dialog.findViewById<View>(R.id.relative_record_audio).visibility = View.GONE
        dialog.findViewById<View>(R.id.relative_recording_audio).visibility = View.VISIBLE
    }

    private fun hideRecordingView(dialog: Dialog) {
        dialog.findViewById<View>(R.id.relative_record_audio).visibility = View.VISIBLE
        dialog.findViewById<View>(R.id.relative_recording_audio).visibility = View.GONE
        val recordingTime: MaterialTextView =
            dialog.findViewById(R.id.text_recording_audio_time)
        recordingTime.setText("0")
        dialog.findViewById<View>(R.id.button_remove_audio).visibility = View.VISIBLE
        dialog.findViewById<View>(R.id.button_add_audio).visibility = View.GONE
        dialog.findViewById<View>(R.id.relative_record_audio).visibility = View.VISIBLE
        dialog.findViewById<View>(R.id.relative_recording_audio).visibility = View.GONE
    }

    private val videoFromActivityResult: Unit
        get() {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            videoActivityResult?.launch(intent)
        }
    private val imageFromActivityResult: Unit
        get() {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            imageActivityResult?.launch(intent)
        }

    private fun tryToGetSosPermissions(bottomSheet: BottomSheetDialog) {
        //val startCount: Int = 5
        if (PermissionsManager.isLocationPermissions(activity)) {
            bottomSheet.findViewById<View>(R.id.linear_no_permissions)?.visibility = View.GONE
            bottomSheet.findViewById<View>(R.id.linear_waiting)?.visibility = View.VISIBLE
            bottomSheet.findViewById<View>(R.id.button_add_media_dialog)?.visibility = View.VISIBLE
            val textView =
                bottomSheet.findViewById<MaterialTextView>(R.id.text_progression)
            countDownTimerSos = object : CountDownTimer(6000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    textView?.text = "${millisUntilFinished / 1000}"
                    //here you can have your logic to set text to edittext
                }

                override fun onFinish() {
                    textView?.text = ""
                    sendSOS()
                    bottomSheet.dismiss()
                }
            }
            countDownTimerSos?.start()
        } else {
            bottomSheet.findViewById<View>(R.id.linear_no_permissions)?.visibility = View.VISIBLE
            bottomSheet.findViewById<View>(R.id.linear_waiting)?.visibility = View.GONE
            bottomSheet.findViewById<View>(R.id.button_add_media_dialog)?.visibility = View.GONE
        }
    }

    private fun sendSOS() {
        if ((ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            return
        }
        val locationRequest: LocationRequest = LocationRequest.Builder(3000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateIntervalMillis(10000)
            .build()
        LocationServices.getFusedLocationProviderClient(requireContext())
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(requireContext())
                        .removeLocationUpdates(this)
                    if (locationResult.locations.size > 0) {
                        val emergencyModel = EmergencyModel()
                        if (FirebaseUtils.instance?.currentAuthUser != null) emergencyModel.senderMail =
                            FirebaseUtils.instance?.currentAuthUser?.email
                        emergencyModel.longitude = locationResult.locations[locationResult.locations.size - 1].longitude
                        emergencyModel.latitude = locationResult.locations[locationResult.locations.size - 1].latitude
                        mEmergencyViewModel.saveEmergency(emergencyModel)
                            .observe(requireActivity()) {
                                if (it >= 1) {
                                    Toast.makeText(context, "SOS Sent !", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "SOS not sent !",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "SOS not sent !", Toast.LENGTH_SHORT).show()
                    }
                }
            }, Looper.getMainLooper())
    }

    private fun sendMediaSOS(
        dialog: Dialog,
        imageUl: String,
        videoUrl: String,
        audioUrl: String,
        message: String
    ) {
        showLoadingMediaAdding(dialog)
        if ((ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            hideLoadingMediaAdding(dialog)
            return
        }
        val locationRequest: LocationRequest = LocationRequest.Builder(3000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateIntervalMillis(10000)
            .build()
        LocationServices.getFusedLocationProviderClient(requireContext())
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(requireContext())
                        .removeLocationUpdates(this)
                    if (locationResult.locations.size > 0) {
                        val emergencyModel = EmergencyModel()
                        if (FirebaseUtils.instance?.currentAuthUser != null) emergencyModel.senderMail =
                            FirebaseUtils.instance?.currentAuthUser?.email
                        emergencyModel.longitude = locationResult.locations
                            .get(locationResult.locations.size - 1).longitude
                        emergencyModel.latitude = locationResult.locations
                            .get(locationResult.locations.size - 1).getLatitude()
                        val messageModel = MessageModel()
                        messageModel.imagesSrc = imageUl
                        messageModel.videoSrc = videoUrl
                        messageModel.audioSrc = audioUrl
                        messageModel.message = message
                        sendMessageMediaEmergency(dialog, messageModel, emergencyModel)
                    } else {
                        hideLoadingMediaAdding(dialog)
                        Toast.makeText(context, "SOS not sent !", Toast.LENGTH_SHORT).show()
                    }
                }
            }, Looper.getMainLooper())
    }

    private fun showLoadingMediaAdding(dialog: Dialog) {
        dialog.findViewById<CircularProgressIndicator>(R.id.progress_indicator_add_media).show()
        dialog.findViewById<View>(R.id.progress_indicator_add_media).visibility = View.VISIBLE
        dialog.findViewById<View>(R.id.button_send_emergency).visibility = View.GONE
    }

    private fun hideLoadingMediaAdding(dialog: Dialog) {
        dialog.findViewById<CircularProgressIndicator>(R.id.progress_indicator_add_media).hide()
        dialog.findViewById<View>(R.id.progress_indicator_add_media).visibility = View.GONE
        dialog.findViewById<View>(R.id.button_send_emergency).visibility = View.VISIBLE
    }

    private fun sendMessageMediaEmergency(
        dialog: Dialog,
        messageModel: MessageModel,
        emergencyModel: EmergencyModel
    ) {
        mMessageViewModel.saveDataToStorage("images/messages/", messageModel.imagesSrc)
            .observe(requireActivity()) {
                if (it != null) {
                    messageModel.imagesSrc = it
                    mMessageViewModel.saveDataToStorage(
                        "audios/messages/",
                        messageModel.audioSrc
                    ).observe(requireActivity()) { it2 ->
                        if (it2 != null) {
                            messageModel.audioSrc = it2
                            mMessageViewModel.saveDataToStorage(
                                "videos/messages/",
                                messageModel.videoSrc
                            ).observe(requireActivity()
                            ) { it3 ->
                                if (it3 != null) {
                                    //Now send message
                                    messageModel.videoSrc = it3
                                    sendMessage(dialog, messageModel, emergencyModel)
                                } else {
                                    hideLoadingMediaAdding(dialog)
                                    Toast.makeText(
                                        context,
                                        "Error sending video !",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            hideLoadingMediaAdding(dialog)
                            Toast.makeText(
                                context,
                                "Error sending audio !",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    hideLoadingMediaAdding(dialog)
                    Toast.makeText(context, "Error sending image !", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendMessage(
        dialog: Dialog,
        messageModel: MessageModel,
        emergencyModel: EmergencyModel
    ) {
        mMessageViewModel.saveMessage(messageModel)
            .observe(requireActivity()) { value ->
                if ((value ?: 0) >= 1) {
                    emergencyModel.messageId = value
                    mEmergencyViewModel.saveEmergency(emergencyModel)
                        .observe(
                            requireActivity()
                        ) { value2 ->
                            hideLoadingMediaAdding(dialog)
                            if ((value2 ?: 0) >= 1) {
                                dialog.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    "Message media sent !",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Message media not send !",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    hideLoadingMediaAdding(dialog)
                    Toast.makeText(
                        requireContext(),
                        "Message media not send !",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            handlerRecordVoice.removeCallbacks((runnableRecordVoice)!!)
        }catch (error: Throwable) {
            error.printStackTrace()
        }
    }
}