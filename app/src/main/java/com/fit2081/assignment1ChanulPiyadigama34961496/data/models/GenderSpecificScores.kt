package com.fit2081.assignment1ChanulPiyadigama34961496.data.models

import androidx.room.TypeConverters
import com.fit2081.assignment1ChanulPiyadigama34961496.data.Converters

@TypeConverters(Converters::class)
data class GenderSpecificScores(
    val userId: String,
    val sex: String,
    val scores: List<Score>
)