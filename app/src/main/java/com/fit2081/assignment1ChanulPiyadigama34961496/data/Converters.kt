package com.fit2081.assignment1ChanulPiyadigama34961496.data

import androidx.room.TypeConverter
import com.fit2081.assignment1ChanulPiyadigama34961496.data.models.Score
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    //since sqllite only supports primitive types, we use these functions to covert complex types, when saving and retriving.
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        //gson is a java library that converts java objs to json and vice versa, in our case it can identify Lists so we just pass it in
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        //due to java type erasure, we save the list type in a  TypeToken obj through generics, and then pass it to the
        //fromJson method
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromStringMap(value: Map<String, String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringMap(value: String): Map<String, String> {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, mapType)
    }

    @TypeConverter
    fun fromBooleanMap(value: Map<String, Boolean>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toBooleanMap(value: String): Map<String, Boolean> {
        val mapType = object : TypeToken<Map<String, Boolean>>() {}.type
        return gson.fromJson(value, mapType)
    }

    @TypeConverter
    fun fromScoreList(value: List<Score>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toScoreList(value: String): List<Score> {
        val listType = object : TypeToken<List<Score>>() {}.type
        return gson.fromJson(value, listType)
    }


}