package com.example.firefighters.viewmodels.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.firefighters.models.MessageModel
import com.example.firefighters.repositories.MessageRepository

class MessageViewModel(private val repository: MessageRepository) : ViewModel() {

    fun loadMessageModel(id: Int): LiveData<MessageModel?> {
        return repository.loadMessageModel(id)
    }

    fun saveDataToStorage(parent: String?, path: String?): LiveData<String?> {
        return repository.saveDataToStorage(parent!!, path)
    }

    fun saveMessage(messageModel: MessageModel?): LiveData<Int> {
        return repository.saveMessage(messageModel!!)
    }

    fun deleteMessage(id: Int): LiveData<Int> {
        return repository.deleteMessage(id)
    }

    fun updateMessage(messageModel: MessageModel?): LiveData<Int> {
        return repository.updateMessage(messageModel!!)
    }

    class Factory(
        private val repository: MessageRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MessageViewModel(repository) as T
        }
    }
}
