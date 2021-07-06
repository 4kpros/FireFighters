package com.example.firefighters.ui.emergency;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firefighters.R;
import com.example.firefighters.adapters.EmergencyAdapter;
import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.models.MessageModel;
import com.example.firefighters.models.UserModel;
import com.example.firefighters.tools.ConstantsValues;
import com.example.firefighters.tools.FirebaseManager;
import com.example.firefighters.viewmodels.EmergencyViewModel;
import com.example.firefighters.viewmodels.MessageViewModel;
import com.example.firefighters.viewmodels.UserViewModel;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EmergencyFragment extends Fragment {

    private EmergencyViewModel emergencyViewModel;
    private MessageViewModel messageViewModel;
    private UserViewModel userViewModel;

    CircularProgressIndicator progressIndicator;

    private EmergencyAdapter emergencyAdapter;
    private RecyclerView emergenciesRecyclerView;
    private Context context;

    //Image views
    private ImageView buttonFilter;
    private ImageView buttonOrder;
    //Text views
    private TextView textTopTitle;
    //Linear layouts
    private LinearLayoutManager layoutManager;

    private final int loadQte = 10;
    private ArrayList<EmergencyModel> emergencies;

    private String lastFilter = ConstantsValues.FILTER_NAME;
    private Query.Direction lastOrder = Query.Direction.DESCENDING;
    DocumentSnapshot lastDocument;
    private int limitCount = 10;

    MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler handlePlayingVoice = new Handler();
    private boolean isPlaying;
    private boolean canRunThread;
    private FragmentActivity activity;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency, container, false);
        context = view.getContext();
        activity = getActivity();
        lastFilter = ConstantsValues.FILTER_NAME;
        lastOrder = Query.Direction.DESCENDING;
        lastDocument = null;
        emergencies = new ArrayList<>();
        lastDocument = null;
        initViews(view);
        getUserType();
        return view;
    }

    private void getUserType() {
        //Set the correct page if the user is auth
        if (FirebaseManager.getInstance().getCurrentAuthUser() == null){
            //Set the connexion page
            userViewModel.logOut();
        }else{
            if (FirebaseManager.getInstance().getCurrentAuthUser() != null && FirebaseManager.getInstance().getCurrentAuthUser().getEmail() != null){
                userViewModel.loadUserModel(FirebaseManager.getInstance().getCurrentAuthUser().getEmail()).observe(requireActivity(), new Observer<UserModel>() {
                    @Override
                    public void onChanged(UserModel userModel) {
                        if(userModel != null){
                            if (userModel.isFireFighter()){
                                //Go to firefighter page
                                ConstantsValues.setIsFirefighter(true);
                                ConstantsValues.setIsChief(userModel.isChief());
                                ConstantsValues.setUnit(userModel.getUnit());
                            }
                            checkInteractions();
                            initRecyclerView();
                            loadMoreEmergencies();
                        }
                        if (getActivity() != null)
                            userViewModel.loadUserModel(FirebaseManager.getInstance().getCurrentAuthUser().getEmail()).removeObservers(requireActivity());
                    }
                });
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void checkInteractions() {
        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialogFilter(v);
            }
        });
        buttonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialogOrder(v);
            }
        });
        emergenciesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.canScrollVertically(1)){
                    loadMoreEmergencies();
                }
            }
        });
    }

    private void loadMoreEmergencies() {
        showLoadingEmergenciesView();
        emergencyViewModel.getEmergenciesQuery(lastDocument, lastFilter, lastOrder, limitCount).observe(requireActivity(), new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document:queryDocumentSnapshots) {
                    emergencies.add(document.toObject(EmergencyModel.class));
                }
                emergencyAdapter.notifyItemRangeInserted(emergencyAdapter.getItemCount(), emergencies.size());
                hideLoadingEmergencies();
                if (queryDocumentSnapshots.size() > 0 && queryDocumentSnapshots.getDocuments().size() > 0)
                    lastDocument = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
            }
        });
    }

    private void showLoadingEmergenciesView() {
        progressIndicator.show();
        progressIndicator.setVisibility(View.VISIBLE);
        textTopTitle.setVisibility(View.INVISIBLE);
    }

    private void hideLoadingEmergencies() {
        progressIndicator.hide();
        progressIndicator.setVisibility(View.GONE);
        textTopTitle.setVisibility(View.VISIBLE);
    }

    private void showDetailsDialog(int position) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_emergency_detail);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        dialog.findViewById(R.id.button_close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        MaterialTextView id = dialog.findViewById(R.id.text_emergency_id);
        MaterialTextView status = dialog.findViewById(R.id.text_emergency_status);
        MaterialTextView gravity = dialog.findViewById(R.id.text_emergency_gravity);
        MaterialTextView assignedUnit = dialog.findViewById(R.id.text_emergency_unit);
        MaterialTextView latitude = dialog.findViewById(R.id.text_emergency_latitude);
        MaterialTextView longitude = dialog.findViewById(R.id.text_emergency_longitude);
        MaterialTextView sender = dialog.findViewById(R.id.text_emergency_sender_mail);
        String tempId = "",
                tempStatus = "",
                tempGravity = "",
                tempAssignedUnit = "<< None >>",
                tempLatitude = "",
                tempLongitude = "",
                tempSender = "<< Unknown >>";

        tempId = "EM" +emergencies.get(position).getId();
        tempStatus = emergencies.get(position).getStatus();
        tempGravity = emergencies.get(position).getGravity();
        tempLatitude = emergencies.get(position).getLatitude()+"";
        tempLongitude = emergencies.get(position).getLongitude()+"";
        if (emergencies.get(position).getCurrentUnit() != null && !emergencies.get(position).getCurrentUnit().isEmpty())
            tempAssignedUnit = emergencies.get(position).getCurrentUnit();
        if (emergencies.get(position).getSenderMail() != null && !emergencies.get(position).getSenderMail().isEmpty())
            tempSender = emergencies.get(position).getSenderMail();

        id.setText(tempId);
        status.setText(tempStatus);
        gravity.setText(tempGravity);
        assignedUnit.setText(tempAssignedUnit);
        latitude.setText(tempLatitude);
        longitude.setText(tempLongitude);
        sender.setText(tempSender);

        if(emergencies.get(position).getMessageId() > 0){
            RelativeLayout relativeLayoutPlayView = dialog.findViewById(R.id.relative_play_audio_view);
            RelativeLayout relativeLayoutNoAudioView = dialog.findViewById(R.id.relative_no_audio_view);
            SeekBar seekBar = dialog.findViewById(R.id.seek_bar_audio_progress);
            ImageView buttonPlayStopAudio = dialog.findViewById(R.id.button_emergency_play_pause_audio);
            SimpleDraweeView imagePlace = dialog.findViewById(R.id.image_emergency_message);
            MaterialTextView message = dialog.findViewById(R.id.text_emergency_message);
            messageViewModel.loadMessageModel(emergencies.get(position).getMessageId()).observe(requireActivity(), new Observer<MessageModel>() {
                @Override
                public void onChanged(MessageModel messageModel) {
                    if (messageModel != null){
                        message.setText(messageModel.getMessage());
                        imagePlace.setImageURI(messageModel.getImagesSrc());
                        if (messageModel.getAudioSrc() != null && !messageModel.getAudioSrc().isEmpty()){
                            showEmergencyAudio(relativeLayoutPlayView, relativeLayoutNoAudioView);
                            prepareEmergencyAudio(messageModel.getAudioSrc(), buttonPlayStopAudio, seekBar, buttonPlayStopAudio);
                        }else {
                            hideEmergencyAudio(relativeLayoutPlayView, relativeLayoutNoAudioView);
                        }
                    }
                }
            });
        }
        dialog.show();
    }

    private void hideEmergencyAudio(RelativeLayout relativeLayoutPlayView, RelativeLayout relativeLayoutNoAudioView) {
        relativeLayoutPlayView.setVisibility(View.GONE);
        relativeLayoutNoAudioView.setVisibility(View.VISIBLE);
    }

    private void showEmergencyAudio(RelativeLayout relativeLayoutPlayView, RelativeLayout relativeLayoutNoAudioView) {
        relativeLayoutPlayView.setVisibility(View.VISIBLE);
        relativeLayoutNoAudioView.setVisibility(View.GONE);
    }

    private void prepareEmergencyAudio(String audioSrc, ImageView buttonPlayAudio, SeekBar seekBar, ImageView buttonPlayStopAudio) {
        buttonPlayAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying){
                    playAudio(audioSrc, seekBar, buttonPlayStopAudio);
                }else{
                    stopPlay();
                    setPlayingView(false, buttonPlayStopAudio);
                }
            }
        });
    }

    private void playAudio(String audioSrc, SeekBar seekBar, ImageView buttonPlayStopAudio) {
        resetProgress(seekBar);
        stopPlay();
        setPlayingView(true, buttonPlayStopAudio);
        mediaPlayer = new MediaPlayer();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
        } else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        try {
            mediaPlayer.setDataSource(audioSrc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        isPlaying = true;
        canRunThread = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (canRunThread){
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            Toast.makeText(context, mediaPlayer.getDuration()*1000 +"", Toast.LENGTH_SHORT).show();
                            mediaPlayer.start();
                            long duration = mediaPlayer.getDuration();
                            seekBar.setMax((int)(duration/100));
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    setPlayingView(false, buttonPlayStopAudio);
                                    resetProgress(seekBar);
                                    stopPlay();
                                }
                            });
                            Runnable runnablePlayVoice = new Runnable() {
                                @Override
                                public void run() {
                                    if (mediaPlayer != null){
                                        int temMediaPlayerProgress = mediaPlayer.getCurrentPosition();
                                        int progress = temMediaPlayerProgress / 100;
                                        setProgress(seekBar, progress);
                                        handlePlayingVoice.postDelayed(this, 100);
                                    }else {
                                        resetProgress(seekBar);
                                    }
                                }
                            };
                            //Start
                            handlePlayingVoice.postDelayed(runnablePlayVoice, 100);
                        }
                    });
                }
            }
        }).start();
    }

    private void stopPlay() {
        canRunThread = false;
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        isPlaying = false;
        mediaPlayer = null;
    }

    private void setPlayingView(boolean play, ImageView buttonPlayStopAudio){
        if(play){
            buttonPlayStopAudio.setImageDrawable(getResources().getDrawable(R.drawable.ic_round_stop_24));
        }else{
            buttonPlayStopAudio.setImageDrawable(getResources().getDrawable(R.drawable.ic_round_play_arrow_24));
        }
    }

    private void setProgress(SeekBar seekBar, int progress){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            seekBar.setProgress(progress, true);
        }else {
            seekBar.setProgress(progress);
        }
    }

    private void resetProgress(SeekBar seekBar){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            seekBar.setProgress(0, true);
        }else {
            seekBar.setProgress(0);
        }
    }

    private void showStreetMap(int position) {
        //
    }

    private void showBottomSheetDialogEmergencyMore(int position) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        bottomSheet.setContentView(R.layout.bottom_sheet_layout_emergency_more);
        bottomSheet.setCancelable(true);
        bottomSheet.setCanceledOnTouchOutside(true);
        bottomSheet.setDismissWithAnimation(true);
        bottomSheet.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);

        //Check if is working or not and if it's firefighter
        if(ConstantsValues.isIsFirefighter() && ConstantsValues.isIsChief()){
            if (emergencies.get(position).getStatus().equals(ConstantsValues.WORKING)) {
                bottomSheet.findViewById(R.id.linear_work_emergency).setVisibility(View.GONE);
                bottomSheet.findViewById(R.id.linear_finish_work_emergency).setVisibility(View.VISIBLE);
            } else if(!emergencies.get(position).getStatus().equals(ConstantsValues.WORKING) && !emergencies.get(position).getStatus().equals(ConstantsValues.FINISHED)) {
                bottomSheet.findViewById(R.id.linear_work_emergency).setVisibility(View.VISIBLE);
                bottomSheet.findViewById(R.id.linear_finish_work_emergency).setVisibility(View.GONE);
            }else{
                bottomSheet.findViewById(R.id.linear_work_emergency).setVisibility(View.GONE);
                bottomSheet.findViewById(R.id.linear_finish_work_emergency).setVisibility(View.GONE);
            }
        }else {
            bottomSheet.findViewById(R.id.linear_work_emergency).setVisibility(View.GONE);
            bottomSheet.findViewById(R.id.linear_finish_work_emergency).setVisibility(View.GONE);
        }
        bottomSheet.findViewById(R.id.button_close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
            }
        });
        bottomSheet.findViewById(R.id.button_work_on).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEmergencyStatus(bottomSheet, position, ConstantsValues.WORKING);
            }
        });
        bottomSheet.findViewById(R.id.button_finish_emergency).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEmergencyStatus(bottomSheet, position, ConstantsValues.FINISHED);
            }
        });
        bottomSheet.findViewById(R.id.button_details_emergency).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
                showDetailsDialog(position);
            }
        });
        bottomSheet.show();
    }

    private void setEmergencyStatus(BottomSheetDialog bottomSheet, int position, String status) {
        bottomSheet.dismiss();
        EmergencyModel em = emergencies.get(position);
        em.setStatus(status);
        String tempUnit = ConstantsValues.getUnit();
        em.setCurrentUnit(tempUnit);
        if (tempUnit != null && !tempUnit.isEmpty()){
            emergencyViewModel.getEmergencyWorkingOnModel(tempUnit).observe(activity, new Observer<EmergencyModel>() {
                @Override
                public void onChanged(EmergencyModel emergencyModel) {
                    if (emergencyModel != null){
                        Toast.makeText(context, "You cannot work on 2 places !", Toast.LENGTH_SHORT).show();
                    }else {
                        emergencyViewModel.updateEmergency(em).observe(requireActivity(), new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer integer) {
                                if (integer >= 1){
                                    emergencies.set(position, em);
                                    emergencyAdapter.notifyItemChanged(position);
                                    Toast.makeText(context, "Updated !", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(context, "Error  update !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });

        }else {
            Toast.makeText(context, "You are not allowed !", Toast.LENGTH_SHORT).show();
        }
    }

    private void showBottomSheetDialogFilter(View view) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        bottomSheet.setContentView(R.layout.bottom_sheet_layout_emergency_filter);
        bottomSheet.setCancelable(true);
        bottomSheet.setCanceledOnTouchOutside(true);
        bottomSheet.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        bottomSheet.findViewById(R.id.button_close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
            }
        });
        bottomSheet.findViewById(R.id.button_name_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterResultsBy(ConstantsValues.FILTER_NAME);
                bottomSheet.dismiss();
            }
        });
        bottomSheet.findViewById(R.id.button_degree_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterResultsBy(ConstantsValues.FILTER_DEGREE);
                bottomSheet.dismiss();
            }
        });
        bottomSheet.findViewById(R.id.button_status_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterResultsBy(ConstantsValues.FILTER_STATUS);
                bottomSheet.dismiss();
            }
        });
        bottomSheet.show();
    }

    private void filterResultsBy(String filterName) {
        lastFilter = filterName;
        emergencies.clear();
        emergencyAdapter.notifyDataSetChanged();
        lastDocument = null;
        loadMoreEmergencies();
    }

    private void showBottomSheetDialogOrder(View view) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        bottomSheet.setContentView(R.layout.bottom_sheet_layout_emergency_order);
        bottomSheet.setCancelable(true);
        bottomSheet.setCanceledOnTouchOutside(true);
        bottomSheet.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        bottomSheet.findViewById(R.id.button_close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
            }
        });
        bottomSheet.findViewById(R.id.button_asc_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reorderResultsBy(Query.Direction.ASCENDING);
                bottomSheet.dismiss();
            }
        });
        bottomSheet.findViewById(R.id.button_desc_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reorderResultsBy(Query.Direction.DESCENDING);
                bottomSheet.dismiss();
            }
        });
        bottomSheet.show();
    }

    private void reorderResultsBy(Query.Direction order) {
        lastOrder = order;
        emergencies.clear();
        lastDocument = null;
        loadMoreEmergencies();
    }

    private void initRecyclerView() {
        emergencies = new ArrayList<>();
        layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        emergencyAdapter = new EmergencyAdapter(context, emergencies, new EmergencyAdapter.OnItemClickListener() {
            @Override
            public void onItemMoreClick(int position) {
                showBottomSheetDialogEmergencyMore(position);
            }
            @Override
            public void onItemStreetClick(int position) {
                showStreetMap(position);
            }

            @Override
            public void onItemCardClick(int position) {
                showDetailsDialog(position);
            }
        });
        emergenciesRecyclerView.setLayoutManager(layoutManager);
        emergenciesRecyclerView.setAdapter(emergencyAdapter);
    }

    private void initViewModel() {
        emergencyViewModel = new ViewModelProvider(requireActivity()).get(EmergencyViewModel.class);
        emergencyViewModel.init();

        messageViewModel = new ViewModelProvider(requireActivity()).get(MessageViewModel.class);
        messageViewModel.init();

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.init();
    }

    private void initViews(View view) {
        emergenciesRecyclerView = view.findViewById(R.id.recycler_emergencies);
        progressIndicator = view.findViewById(R.id.progress_loading_emergencies);

        //Buttons
        buttonFilter = view.findViewById(R.id.button_image_filter);
        buttonOrder = view.findViewById(R.id.button_image_order);

        textTopTitle = view.findViewById(R.id.text_emergency_top_title);
    }

    @Override
    public void onStart() {
        super.onStart();
        canRunThread = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        canRunThread = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        canRunThread = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        canRunThread = false;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        canRunThread = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        canRunThread = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        canRunThread = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        canRunThread = false;
    }
}