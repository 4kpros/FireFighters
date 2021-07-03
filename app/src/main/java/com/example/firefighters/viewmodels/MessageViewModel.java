package com.example.firefighters.viewmodels;

import android.app.Activity;

import com.example.firefighters.models.MessageModel;
import com.example.firefighters.models.MessageModel;
import com.example.firefighters.models.UnitModel;
import com.example.firefighters.repositories.MessageRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class MessageViewModel extends ViewModel {

    private MessageRepository repository;

    public void init() {
        if (repository != null)
            return;
        repository = MessageRepository.getInstance();
    }

    public LiveData<MessageModel> loadMessageModel(int id) {
        return repository.loadMessageModel(id);
    }
    public LiveData<String> saveDataToStorage(String parent, String path) {
        return repository.saveDataToStorage(parent, path);
    }
    public LiveData<Integer> saveMessage(MessageModel messageModel ) {
        return repository.saveMessage(messageModel);
    }
    public LiveData<Integer> deleteMessage(int id) {
        return repository.deleteMessage(id);
    }
    public LiveData<Integer> updateMessage(MessageModel messageModel ) {
        return repository.updateMessage(messageModel);
    }
}
