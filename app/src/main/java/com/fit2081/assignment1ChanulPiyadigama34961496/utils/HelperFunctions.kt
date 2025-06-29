package com.fit2081.assignment1ChanulPiyadigama34961496.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.KParameter

//simply creates a checkbox with a text label next to it
@Composable
fun CheckboxWithLabel(label: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(label)
    }
}

//grab the sharedpref belonging to specific userid, based on key
fun getFoodQuestionnairePreferences(context: Context, userId: String): SharedPreferences {
    return context.getSharedPreferences("NutriTrack_${userId}_sp", Context.MODE_PRIVATE)
}


//load the data from the csv file, returns an array of arrays where each line is split into columns
fun loadDataFromCSV(context: Context): List<List<String>> {
    val inputStream = context.assets.open("clientNutritionData.csv")
    val reader = BufferedReader(InputStreamReader(inputStream))
    val data = mutableListOf<List<String>>()
    reader.useLines { lines ->
        lines.drop(1).forEach{ line->
            val values = line.split(",").map {it.trim()}
            data.add(values)
        }
    }
    return data
}


//helper function that creates an instance of a class by mapping a list of strings to the respective parameters of the constructor
//thus the list should be matching the order of the constructor parameters, as if you were creating a new instance of the class
//we use Generics to allow this function to be used for any data class, inline and refined to preserve the type information, refinied remebers the type and inline
//copies the function to the call site, so the type can be passed in
inline fun <reified T : Any> mapCsvRowToDataClass(
    row: List<String>,
    excludeParams: Set<String> = setOf("password", "name")
): T? {
    //grab the constructor of the class
    val constructor = T::class.primaryConstructor ?: return null

    //maps the parameter to its value
    val args = mutableMapOf<KParameter, Any?>()

    //for each parameter, grab its matching value from the list, and we use index since they should be in the same order
    var csvIndex = 0
    constructor.parameters.forEach { param ->
        if (param.name in excludeParams) {
            // Skip setting excluded parameters, they'll use default values
            return@forEach
        }

        if (csvIndex < row.size) {
            val value = row[csvIndex]
            args[param] = when (param.type.classifier) {
                String::class -> value
                Double::class -> value.toDoubleOrNull() ?: 0.0
                Int::class -> value.toIntOrNull() ?: 0
                Long::class -> value.toLongOrNull() ?: 0L
                Boolean::class -> value.toBooleanStrictOrNull() ?: false
                else -> null
            }
        }
        csvIndex++
    }
    //call constructor with the parameter values to create instance of object
    val patient = constructor.callBy(args)
    return patient
}