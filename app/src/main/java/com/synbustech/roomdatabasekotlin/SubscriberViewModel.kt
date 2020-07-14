package com.synbustech.roomdatabasekotlin

import android.util.Patterns
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synbustech.roomdatabasekotlin.db.Subscriber
import com.synbustech.roomdatabasekotlin.db.SubscriberRepository
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SubscriberViewModel(private val repository: SubscriberRepository) : ViewModel(), Observable {

    val subscribers = repository.subscribers
    private var isUpdateOrDelete = false
    private lateinit var subscriberToUpdateOrDelete: Subscriber

    @Bindable
    val inputName = MutableLiveData<String>()

    @Bindable
    val inputEmail = MutableLiveData<String>()

    @Bindable
    val saveOrUpdateButtonText = MutableLiveData<String>()

    @Bindable
    val clearAllOrDeleteButtonText = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        saveOrUpdateButtonText.value = "Save"
        clearAllOrDeleteButtonText.value = "Clear All"
    }

    fun saveOrUpdate() {

        if (inputName.value == null) {
            statusMessage.value = Event("Please enter subscriber's name")
        } else if (inputEmail.value == null) {
            statusMessage.value = Event("Please enter subscriber's email")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()) {
            statusMessage.value = Event("Please enter valid email address")
        } else {
            if (isUpdateOrDelete) {
                subscriberToUpdateOrDelete.name = inputName.value!!
                subscriberToUpdateOrDelete.email = inputEmail.value!!
                update(subscriberToUpdateOrDelete)
            } else {
                val name = inputName.value!!
                val email = inputEmail.value!!
                insert(Subscriber(0, name, email))
                inputName.value = null
                inputEmail.value = null
            }
        }
    }

    fun clearAllOrDelete() {
        if(isUpdateOrDelete) {
            delete(subscriberToUpdateOrDelete)
        } else {
            clearAll()
        }
    }

    fun initUpdateAndDelete(subscriber: Subscriber) {
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email

        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = subscriber
        saveOrUpdateButtonText.value = "Update"
        clearAllOrDeleteButtonText.value = "Delete"
    }

    fun insert(subscriber: Subscriber) = viewModelScope.launch {
        val newRowId: Long = repository.insert(subscriber)
        if(newRowId > -1) {
            statusMessage.value = Event("Subscriber inserted successfully")
        } else {
            statusMessage.value = Event("Error Occurred")
        }

    }

    fun update(subscriber: Subscriber) = viewModelScope.launch {
        val noOfRows: Int = repository.update(subscriber)
        if(noOfRows > 0) {
            inputName.value = null
            inputEmail.value = null

            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = "Save"
            clearAllOrDeleteButtonText.value = "Clear All"

            statusMessage.value = Event("Subscriber updated successfully")
        } else {
            statusMessage.value = Event("Error Occurred")
        }

    }

    fun delete(subscriber: Subscriber) = viewModelScope.launch {
        val deletedId: Int = repository.delete(subscriber)
        if(deletedId > 0) {
            inputName.value = null
            inputEmail.value = null

            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = "Save"
            clearAllOrDeleteButtonText.value = "Clear All"
            statusMessage.value = Event("Subscriber deleted successfully")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }

    fun clearAll() = viewModelScope.launch {
        val deletedIds: Int = repository.deleteAll()
        if(deletedIds > 0) {
            statusMessage.value = Event("All subscriber deleted successfully")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

}