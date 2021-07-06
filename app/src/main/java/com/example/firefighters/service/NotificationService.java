package com.example.firefighters.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.viewmodels.EmergencyViewModel;
import com.example.firefighters.viewmodels.MessageViewModel;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

public class NotificationService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void setViewModels(EmergencyViewModel emergencyViewModel){
        emergencyViewModel.getLastQuerySnapshot(null, null).observe((LifecycleOwner) getBaseContext(), new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null){
                    if (queryDocumentSnapshots.getDocumentChanges().size() > 0){
                        for (DocumentChange document: queryDocumentSnapshots.getDocumentChanges()) {
                            if (document.getOldIndex() < 0){
                                EmergencyModel emergencyModel = document.getDocument().toObject(EmergencyModel.class);
//                                createNotification(emergencyModel);
                            }
                        }
                    }
                }
            }
        });
    }

//    private Notification createNotification(EmergencyModel emergencyModel){
////        NotificationCompat.Builder builder = MediaStyleHelper.from(this, mSession);
////        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
////                .setShowActionsInCompactView(0, 1, 2, 3)
////                .setMediaSession(mSession.getSessionToken()));
////        builder.setSmallIcon(R.drawable.ic_baseline_music_note_24)
////                .setColor(getResources().getColor(R.color.greyColor5));
////        builder.addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", MediaStyleHelper.getActionIntent(this, PREV_ACTION));
////        builder.addAction(playPauseButton, "PlayPause", MediaStyleHelper.getActionIntent(this, TOGGLE_PAUSE_ACTION));
////        builder.addAction(R.drawable.ic_baseline_skip_next_24, "Next", MediaStyleHelper.getActionIntent(this, NEXT_ACTION));
//        return builder.build();
//    }
//    private void notificationChannel(){
//        if (Build.VERSION.SDK_INT < 26) {
//            return;
//        }
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        NotificationChannel channel = null;
//        channel = new NotificationChannel(MediaStyleHelper.NOTIFICATION_ID, "musicX", NotificationManager.IMPORTANCE_LOW);
//        manager.createNotificationChannel(channel);
//    }
}
