package com.fit2081.assignment1ChanulPiyadigama34961496.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fit2081.assignment1ChanulPiyadigama34961496.data.models.FoodIntake

@Dao
interface FoodIntakeDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertFoodIntake(foodIntake: FoodIntake)

    @Query("SELECT * FROM food_intake WHERE patientId = :patientId")
    suspend fun getFoodIntakeByPatientId(patientId: String): FoodIntake?

    @Update
    suspend fun updateFoodIntake(foodIntake: FoodIntake)


}