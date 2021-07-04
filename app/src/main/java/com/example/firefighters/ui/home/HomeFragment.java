package com.example.firefighters.ui.home;

import android.Manifest;
import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firefighters.R;
import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.models.MessageModel;
import com.example.firefighters.tools.ConstantsValues;
import com.example.firefighters.tools.FirebaseManager;
import com.example.firefighters.tools.PermissionsManager;
import com.example.firefighters.ui.mapview.MapViewFragment;
import com.example.firefighters.viewmodels.EmergencyViewModel;
import com.example.firefighters.viewmodels.MessageViewModel;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class HomeFragment extends Fragment {
    private final int phoneNumber = 000;
    EmergencyViewModel emergencyViewModel;
    MessageViewModel messageViewModel;
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
    private FragmentManager fragmentManager;
    private MediaRecorder mediaRecorder;
    private Handler handlerRecordVoice = new Handler();
    private Runnable runnableRecordVoice;
    private boolean canRunThread = true;
    private ActivityResultLauncher<Intent> imageActivityResult;
    private ActivityResultLauncher<Intent> videoActivityResult;

    private MaterialTextView textImageResource;
    private String audioPath = "";
    private CircularProgressIndicator circularProgressLoadingMedia;
    private MaterialButton buttonMediaSendEmergency;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        imageActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getData() != null){
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R){
                        Bitmap tempBitmap = (Bitmap) result.getData().getExtras().get("data");
                        String tempImagePath = saveImageToInternalStorage(tempBitmap);
                        textImageResource.setText(tempImagePath);
                        Toast.makeText(context, "Image received !", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "No implementation for Android 11+ !", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        videoActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getData() != null){
                        Toast.makeText(context, result.getData()+"", Toast.LENGTH_LONG).show();
                }
            }
        });
        super.onCreate(savedInstanceState);
    }

    private String saveImageToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
        // path to /data/data/covid_app/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File myPath =new File(directory,"emergencyimage.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (fos != null){
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        }

        return directory.getAbsolutePath()+"/emergencyimage.jpg";
    }

    private String saveVideoToInternalStorage(Uri bitmapImage) {
        ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
        // path to /data/data/covid_app/app_data/imageDir
        File directory = cw.getDir("videoDir", Context.MODE_PRIVATE);

        return directory.getAbsolutePath()+"/emergencyvideo.mp4";
    }

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

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkInteractions(view);
    }

    private void observeLiveData() {
//        tryToGetCallPermissions(view, bottomSheet);
    }

    private void initViewModels(View view) {
        emergencyViewModel = new ViewModelProvider(requireActivity()).get(EmergencyViewModel.class);
        emergencyViewModel.init();

        messageViewModel = new ViewModelProvider(requireActivity()).get(MessageViewModel.class);
        messageViewModel.init();
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

    private void setBluetoothViews(BottomSheetDialog bottomSheet) {
        if( PermissionsManager.getInstance().isBluetoothPermissions(requireActivity()))
            return;
        TextView deviceName = bottomSheet.findViewById(R.id.text_device_name);
        ImageView buttonDisconnect = bottomSheet.findViewById(R.id.image_button_disconnect);
        ConstraintLayout cardBluetoothDevices = bottomSheet.findViewById(R.id.constraint_button_device_connected);
        //Getting bluetooth connected info
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if (pairedDevices.size() > 0){
            bottomSheet.findViewById(R.id.relative_device_connected).setVisibility(View.VISIBLE);
            bottomSheet.findViewById(R.id.relative_no_device_connected).setVisibility(View.GONE);
            String deviceNameText = "";
            String deviceNHarWareAddress = "";
            for (BluetoothDevice device : pairedDevices){
                deviceNameText = device.getName();
                deviceNHarWareAddress = device.getAddress();
            }
            deviceName.setText(deviceNameText + "");
            buttonDisconnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                }
            });
            cardBluetoothDevices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                }
            });
        }else {
            bottomSheet.findViewById(R.id.relative_device_connected).setVisibility(View.GONE);
            bottomSheet.findViewById(R.id.relative_no_device_connected).setVisibility(View.VISIBLE);
        }
    }

    private void getBluetoothPermissions(BottomSheetDialog bottomSheet) {
        PermissionsManager.getInstance().isBluetoothPermissions(requireActivity());
    }

    private void showMapView(View v) {
//        Intent myIntent = new Intent(activity, MapViewActivity.class);
//        myIntent.putExtra("mapView", "Default"); //Optional parameters
//        activity.startActivity(myIntent);
//
//        MapViewFragment mapViewFragment = MapViewFragment.newInstance(context);
//        mapViewFragment.show(activity.getSupportFragmentManager(), ConstantsValues.MAP_VIEW_TAG);

        if(PermissionsManager.getInstance().isLocationPermissions(requireActivity())) {
            fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.setCustomAnimations(R.anim.anim_tanslate_scale_in, R.anim.anim_tanslate_scale_out);
            ft.add(R.id.main_frame_layout, new MapViewFragment()).addToBackStack(null);
            ft.commit();
        }else{
            showBottomSheetDialogLocationPermissions(v);
        }
    }

    private void showBottomSheetDialogSettings(View view) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        bottomSheet.setContentView(R.layout.bottom_sheet_dialog_bluetooth);
        bottomSheet.setCancelable(true);
        bottomSheet.setCanceledOnTouchOutside(true);
        bottomSheet.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        getBluetoothPermissions(bottomSheet);
        bottomSheet.show();
    }

    private void showBottomSheetDialogHelp(View view) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        bottomSheet.setContentView(R.layout.bottom_sheet_dialog_settings);
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
                bottomSheetSos.dismiss();
                if (countDownTimerSos != null)
                    countDownTimerSos.cancel();
                showBottomSheetDialogAddMedia(v, bottomSheetSos);
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
                bottomSheetSos.dismiss();
                PermissionsManager.getInstance().requestLocationPermission(activity);
            }
        });
        tryToGetSosPermissions(view, bottomSheetSos);
        bottomSheetSos.show();
    }    //Show bottom sheet dialog send sos

    private void showBottomSheetDialogLocationPermissions(View view) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        bottomSheet.setContentView(R.layout.bottom_sheet_dialog_location_permissions);
        bottomSheet.setCancelable(false);
        bottomSheet.setCanceledOnTouchOutside(false);
        bottomSheet.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        bottomSheet.findViewById(R.id.button_cancel_permission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
            }
        });
        bottomSheet.findViewById(R.id.button_get_permissions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheet != null)
                    bottomSheet.dismiss();
                PermissionsManager.getInstance().requestLocationPermission(activity);
            }
        });
        bottomSheet.show();
    }

    private void showBottomSheetDialogAddMedia(View v, BottomSheetDialog bottomSheetSos) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_media);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        textImageResource = dialog.findViewById(R.id.text_image_resource);
        textImageResource.setText("");
        dialog.findViewById(R.id.button_close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.button_add_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PermissionsManager.getInstance().isCameraPermissions(requireActivity())){
                    getImageFromActivityResult();
                }else {
                    showDialogGetPermissions("camera", ConstantsValues.CAMERA_PERMISSION_CODE);
                }
            }
        });
        dialog.findViewById(R.id.button_add_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PermissionsManager.getInstance().isCameraPermissions(requireActivity())){
                    getVideoFromActivityResult();
                }else {
                    showDialogGetPermissions("camera", ConstantsValues.CAMERA_PERMISSION_CODE);
                }
            }
        });
        dialog.findViewById(R.id.button_add_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PermissionsManager.getInstance().isAudioRecordingPermissions(requireActivity())){
                    recordAudio(dialog);
                }else {
                    showDialogGetPermissions("audio record", ConstantsValues.AUDIO_RECORD_PERMISSION_CODE);
                }
            }
        });
        dialog.findViewById(R.id.button_stop_recording_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecordingVoiceAndSave(dialog);
            }
        });
        dialog.findViewById(R.id.button_remove_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAudioResource(dialog);
            }
        });
        //Button send emergency
        buttonMediaSendEmergency = dialog.findViewById(R.id.button_send_emergency);
        //Circular progress indicator
        circularProgressLoadingMedia = dialog.findViewById(R.id.progress_indicator_add_media);
        dialog.findViewById(R.id.button_send_emergency).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialTextView videoUrl = dialog.findViewById(R.id.text_video_resource);
                MaterialTextView audioUrl = dialog.findViewById(R.id.text_audio_resource);
                TextInputEditText message = dialog.findViewById(R.id.text_input_emergency_message);
                String tempMessage = "";
                if (message.getText() != null)
                    tempMessage = message.getText().toString();
                sendMediaSOS(dialog, textImageResource.getText().toString(), videoUrl.getText().toString(), audioUrl.getText().toString(), tempMessage);
            }
        });
        dialog.show();
    }

    private void showDialogGetPermissions(String title, int permissionCode) { BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        bottomSheet.setContentView(R.layout.bottom_sheet_dialog_get_permissions);
        bottomSheet.setCancelable(false);
        bottomSheet.setCanceledOnTouchOutside(false);
        bottomSheet.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        MaterialTextView textViewTitle = bottomSheet.findViewById(R.id.permission_title);
        if (textViewTitle != null){
            String tempTitle;
            if (title != null && !title.isEmpty()){
                tempTitle = "No " + title + " permissions found !";
            }else {
                tempTitle = "No permissions found !";
            }
            textViewTitle.setText(tempTitle);
        }
        bottomSheet.findViewById(R.id.button_cancel_permission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
            }
        });
        bottomSheet.findViewById(R.id.button_get_permissions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
                if (permissionCode == ConstantsValues.AUDIO_RECORD_PERMISSION_CODE){
                    PermissionsManager.getInstance().requestAudioRecordPermission(requireActivity());
                }else if (permissionCode == ConstantsValues.CAMERA_PERMISSION_CODE){
                    PermissionsManager.getInstance().requestCameraPermission(requireActivity());
                }
            }
        });
        bottomSheet.show();
    }

    private void clearAudioResource(Dialog dialog) {
        dialog.findViewById(R.id.button_remove_audio).setVisibility(View.GONE);
        dialog.findViewById(R.id.button_add_audio).setVisibility(View.VISIBLE);
        MaterialTextView recordingTime = dialog.findViewById(R.id.text_audio_resource);
        recordingTime.setText("");
    }

    private void recordAudio(Dialog dialog) {
        showRecordingView(dialog);
        MaterialTextView recordingTimeTextView = dialog.findViewById(R.id.text_recording_audio_time);
        recordingTimeTextView.setText("0");

        //Start recording
        String tempFileName = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            android.icu.util.Calendar calendar = android.icu.util.Calendar.getInstance();
            tempFileName += calendar.get(android.icu.util.Calendar.YEAR);
            tempFileName += calendar.get(android.icu.util.Calendar.MONTH);
            tempFileName += calendar.get(android.icu.util.Calendar.DAY_OF_MONTH);
            tempFileName += calendar.get(android.icu.util.Calendar.HOUR);
            tempFileName += calendar.get(android.icu.util.Calendar.MINUTE);
            tempFileName += calendar.get(android.icu.util.Calendar.SECOND);
            tempFileName += calendar.get(android.icu.util.Calendar.MILLISECOND);
        }else {
            java.util.Calendar calendar = null;
            calendar = java.util.Calendar.getInstance();
            tempFileName += calendar.get(java.util.Calendar.YEAR);
            tempFileName += calendar.get(java.util.Calendar.MONTH);
            tempFileName += calendar.get(java.util.Calendar.DAY_OF_MONTH);
            tempFileName += calendar.get(java.util.Calendar.HOUR);
            tempFileName += calendar.get(java.util.Calendar.MINUTE);
            tempFileName += calendar.get(java.util.Calendar.SECOND);
            tempFileName += calendar.get(java.util.Calendar.MILLISECOND);
        }
        tempFileName = tempFileName + ".3pg";
        audioPath = "";
        audioPath = getFilePath(tempFileName);
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
        }
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(audioPath);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            runnableRecordVoice = new Runnable() {
                @Override
                public void run() {
                    if (canRunThread){
                        float newDuration = Float.parseFloat(recordingTimeTextView.getText().toString());
                        newDuration += 1;
                        updateRecordingDuration(recordingTimeTextView, newDuration);
                        handlerRecordVoice.postDelayed(this, 1000);
                    }
                }
            };
            //Start
            handlerRecordVoice.postDelayed(runnableRecordVoice, 1000);
        } catch (IOException e) {
            e.printStackTrace();
            mediaRecorder = null;
            hideRecordingView(dialog);
            Toast.makeText(context, "Unknown error "+ e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private String getFilePath(String tempFileName) {
        ContextWrapper contextWrapper = new ContextWrapper(requireActivity().getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, tempFileName);
        return file.getPath();
    }

    private void updateRecordingDuration(MaterialTextView materialTextView, float newDuration) {
        String finalTempDuration = ""+newDuration;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                materialTextView.setText(finalTempDuration);
            }
        });
    }

    private void stopRecordingVoiceAndSave(Dialog dialog) {
        hideRecordingView(dialog);
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            handlerRecordVoice.removeCallbacks(runnableRecordVoice);
            Toast.makeText(context, "Voice Registered !", Toast.LENGTH_SHORT).show();
        }
        MaterialTextView textAudioResource = dialog.findViewById(R.id.text_audio_resource);
        textAudioResource.setText(audioPath);
    }

    private void showRecordingView(Dialog dialog) {
        dialog.findViewById(R.id.relative_record_audio).setVisibility(View.GONE);
        dialog.findViewById(R.id.relative_recording_audio).setVisibility(View.VISIBLE);
    }
    private void hideRecordingView(Dialog dialog){
        dialog.findViewById(R.id.relative_record_audio).setVisibility(View.VISIBLE);
        dialog.findViewById(R.id.relative_recording_audio).setVisibility(View.GONE);

        MaterialTextView recordingTime = dialog.findViewById(R.id.text_recording_audio_time);
        recordingTime.setText("0");

        dialog.findViewById(R.id.button_remove_audio).setVisibility(View.VISIBLE);
        dialog.findViewById(R.id.button_add_audio).setVisibility(View.GONE);
        dialog.findViewById(R.id.relative_record_audio).setVisibility(View.VISIBLE);
        dialog.findViewById(R.id.relative_recording_audio).setVisibility(View.GONE);
    }

    private void getVideoFromActivityResult() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);;
        videoActivityResult.launch(intent);
    }

    private void getImageFromActivityResult() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageActivityResult.launch(intent);
    }

    private void tryToGetSosPermissions(View view, BottomSheetDialog bottomSheet) {
        int startCount = 5;
        if (PermissionsManager.getInstance().isLocationPermissions(activity)) {
            bottomSheet.findViewById(R.id.linear_no_permissions).setVisibility(View.GONE);
            bottomSheet.findViewById(R.id.linear_waiting).setVisibility(View.VISIBLE);
            bottomSheet.findViewById(R.id.button_add_media_dialog).setVisibility(View.VISIBLE);
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
            bottomSheet.findViewById(R.id.button_add_media_dialog).setVisibility(View.GONE);
        }
    }

    private void sendSOS() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(context)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(@NotNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(context)
                                .removeLocationUpdates(this);
                        if (locationResult.getLocations().size() > 0) {
                            EmergencyModel emergencyModel = new EmergencyModel();
                            if (FirebaseManager.getInstance().getCurrentAuthUser() != null)
                                emergencyModel.setSenderMail(FirebaseManager.getInstance().getCurrentAuthUser().getEmail());
                            emergencyModel.setLongitude(locationResult.getLocations().get(locationResult.getLocations().size() - 1).getLongitude());
                            emergencyModel.setLatitude(locationResult.getLocations().get(locationResult.getLocations().size() - 1).getLatitude());
                            emergencyViewModel.saveEmergency(emergencyModel).observe(requireActivity(), new Observer<Integer>() {
                                @Override
                                public void onChanged(Integer integer) {
                                    if (integer >= 1){
                                        Toast.makeText(context, "SOS Sent !", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(context, "SOS not sent !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(context, "SOS not sent !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLocationAvailability(LocationAvailability locationAvailability) {
                        super.onLocationAvailability(locationAvailability);
                    }
                }, Looper.getMainLooper());
    }

    private void sendMediaSOS(Dialog dialog, String imageUl, String videoUrl, String audioUrl, String message) {
        showLoadingMediaAdding();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            hideLoadingMediaAdding();
            return;
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(context)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(@NotNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(context)
                                .removeLocationUpdates(this);
                        if (locationResult.getLocations().size() > 0) {
                            EmergencyModel emergencyModel = new EmergencyModel();
                            if (FirebaseManager.getInstance().getCurrentAuthUser() != null)
                                emergencyModel.setSenderMail(FirebaseManager.getInstance().getCurrentAuthUser().getEmail());
                            emergencyModel.setLongitude(locationResult.getLocations().get(locationResult.getLocations().size() - 1).getLongitude());
                            emergencyModel.setLatitude(locationResult.getLocations().get(locationResult.getLocations().size() - 1).getLatitude());
                            MessageModel messageModel = new MessageModel();
                            messageModel.setImagesSrc(imageUl);
                            messageModel.setVideoSrc(videoUrl);
                            messageModel.setAudioSrc(audioUrl);
                            messageModel.setMessage(message);
                            sendMessageMediaEmergency(dialog, messageModel, emergencyModel);
                        } else {
                            hideLoadingMediaAdding();
                            Toast.makeText(context, "SOS not sent !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLocationAvailability(LocationAvailability locationAvailability) {
                        super.onLocationAvailability(locationAvailability);
                    }
                }, Looper.getMainLooper());
    }


    private void showLoadingMediaAdding() {
        if (circularProgressLoadingMedia != null && buttonMediaSendEmergency != null) {
            circularProgressLoadingMedia.show();
            circularProgressLoadingMedia.setVisibility(View.VISIBLE);
            buttonMediaSendEmergency.setVisibility(View.GONE);
        }
    }

    private void hideLoadingMediaAdding() {
        if (circularProgressLoadingMedia != null && buttonMediaSendEmergency != null){
            circularProgressLoadingMedia.hide();
            circularProgressLoadingMedia.setVisibility(View.GONE);
            buttonMediaSendEmergency.setVisibility(View.VISIBLE);
        }
    }

    private void sendMessageMediaEmergency(Dialog dialog, MessageModel messageModel, EmergencyModel emergencyModel) {
        messageViewModel.saveDataToStorage("images/messages/", messageModel.getImagesSrc()).observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String imageUrl) {
                if (imageUrl != null){
                    messageModel.setImagesSrc(imageUrl);
                    messageViewModel.saveDataToStorage("audios/messages/", messageModel.getAudioSrc()).observe(requireActivity(), new Observer<String>() {
                        @Override
                        public void onChanged(String audioUrl) {
                            if (audioUrl != null){
                                messageModel.setAudioSrc(audioUrl);
                                messageViewModel.saveDataToStorage("videos/messages/", messageModel.getVideoSrc()).observe(requireActivity(), new Observer<String>() {
                                    @Override
                                    public void onChanged(String videoUrl) {
                                        if (videoUrl != null){
                                            //Now send message
                                            messageModel.setVideoSrc(videoUrl);
                                            sendMessage(dialog, messageModel, emergencyModel);
                                        }else {
                                            hideLoadingMediaAdding();
                                            Toast.makeText(context, "Error sending video !", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else {
                                hideLoadingMediaAdding();
                                Toast.makeText(context, "Error sending audio !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    hideLoadingMediaAdding();
                    Toast.makeText(context, "Error sending image !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendMessage(Dialog dialog, MessageModel messageModel, EmergencyModel emergencyModel){
        messageViewModel.saveMessage(messageModel).observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer >= 1){
                    emergencyModel.setMessageId(integer);
                    emergencyViewModel.saveEmergency(emergencyModel).observe(requireActivity(), new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer integer) {
                            hideLoadingMediaAdding();
                            if (integer >= 1){
                                dialog.dismiss();
                                Toast.makeText(requireContext(), "Message media sent !", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(requireContext(), "Message media not send !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    hideLoadingMediaAdding();
                    Toast.makeText(requireContext(), "Message media not send !", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        handlerRecordVoice.removeCallbacks(runnableRecordVoice);
    }
}