package com.fit2081.assignment1ChanulPiyadigama34961496.screens.FoodQuestionnaireScreen

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.widget.TimePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.Calendar

//receives the reference to the timingQuestionsMap state, and creates the timingquestion componenets where the reference is passed
// down in a setter function to allow the question componenet to change the state
@Composable
fun TimingQuestions(
    timingQuestionsMap: MutableMap<String, String>,
    onTimeChanged: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        timingQuestionsMap.forEach { (question, time) ->
            CreateTimingQuestion(
                questionText = question,
                time = time,
                setTime = {selectedTime ->
                    timingQuestionsMap[question] = selectedTime
                    onTimeChanged()
                }
            )
        }
    }
}

//displays question and timepickerdialog in a row
@SuppressLint("DefaultLocale")
@Composable
fun CreateTimingQuestion(questionText: String, time: String, setTime: (String) -> Unit) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = questionText, modifier = Modifier.weight(1f))
        Button(onClick = { showDialog = true }) {
            //when time is picked the rerender will display it here
            Text(time)
        }
    }


    if (showDialog) {
        //create calender object and use it to extract the current hour and minute
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        //timedialog is created with access to app through context so it can display dialog on screen, we pass hour and minute
        //so thats the default time displayed and a listener function that when the time is selecetd by the user the hour and minute
        //are saved to time state
        TimePickerDialog(
            context,
            { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                val newTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                setTime(newTime)
                showDialog = false
            },
            hour,
            minute,
            true // is24HourView
        ).apply {
            //extras for this composable, when we click outside it closes so change state, and make sure the dialog is shown immediatly
            setOnCancelListener { showDialog = false }
            show()
        }
    }
}