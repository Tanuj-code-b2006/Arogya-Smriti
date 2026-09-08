package com.smritisetu.app.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PatientViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PatientRepository(
        AppDatabase.getInstance(application).patientDao()
    )

    val patients: StateFlow<List<PatientEntity>> = repository.allPatients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch { repository.seedIfEmpty() }
    }

    fun addPatient(name: String, age: Int, gender: String, address: String, phone: String) {
        viewModelScope.launch {
            repository.addPatient(
                PatientEntity(name = name, age = age, gender = gender, address = address, phone = phone)
            )
        }
    }
}