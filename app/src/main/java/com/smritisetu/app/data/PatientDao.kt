package com.smritisetu.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {

    @Query("SELECT * FROM patients ORDER BY id ASC")
    fun getAllPatients(): Flow<List<PatientEntity>>

    @Insert
    suspend fun insertPatient(patient: PatientEntity)

    @Query("SELECT COUNT(*) FROM patients")
    suspend fun getCount(): Int
}