package com.fit2081.assignment1ChanulPiyadigama34961496.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.fit2081.assignment1ChanulPiyadigama34961496.data.Converters

@TypeConverters(Converters::class)
@Entity(
    tableName = "nutri_coach_tips",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["userId"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ]
)
data class NutriCoachTips(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: String,
    val tip: String,
)