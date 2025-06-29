package com.fit2081.assignment1ChanulPiyadigama34961496.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.fit2081.assignment1ChanulPiyadigama34961496.data.Converters

@TypeConverters(Converters::class)
@Entity(
    tableName = "food_intake",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["userId"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    //each patient can only have one food intake record, so we set the patientId as unique (constraint)
    indices = [Index(value = ["patientId"], unique = true)]

)
data class FoodIntake(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: String,
    val foodCategories: Map<String, Boolean>,
    val persona: String,
    val timings: Map<String, String>
)