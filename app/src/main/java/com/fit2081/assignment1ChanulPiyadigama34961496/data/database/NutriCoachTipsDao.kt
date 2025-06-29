package com.fit2081.assignment1ChanulPiyadigama34961496.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fit2081.assignment1ChanulPiyadigama34961496.data.models.NutriCoachTips

@Dao
interface NutriCoachTipsDao {
    @Insert
    suspend fun insertTip(tip: NutriCoachTips)

    @Query("SELECT tip FROM nutri_coach_tips WHERE patientId = :patientId")
    suspend fun getAllTipsByPatientId(patientId: String): List<String>

    //HD feature
    @Query("DELETE FROM nutri_coach_tips WHERE patientId = :patientId")
    suspend fun deleteAllTipsByPatientId(patientId: String)
}