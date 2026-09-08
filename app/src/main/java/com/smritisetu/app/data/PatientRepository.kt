package com.smritisetu.app.data

import kotlinx.coroutines.flow.Flow

class PatientRepository(private val dao: PatientDao) {

    val allPatients: Flow<List<PatientEntity>> = dao.getAllPatients()

    suspend fun addPatient(patient: PatientEntity) {
        dao.insertPatient(patient)
    }

    // App pehli baar khulne par Anil Baruah ka record seed kar dega
    suspend fun seedIfEmpty() {
        if (dao.getCount() == 0) {
            dao.insertPatient(
                PatientEntity(
                    name = "Anil Baruah",
                    age = 74,
                    gender = "Male",
                    address = "Sonapur, Kamrup, Assam",
                    phone = "98xxxxxx21"
                )
            )
        }
    }
}