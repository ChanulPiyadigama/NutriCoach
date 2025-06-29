package com.fit2081.assignment1ChanulPiyadigama34961496.screens.FoodQuestionnaireScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fit2081.assignment1ChanulPiyadigama34961496.R

//needs a setter for selectedpersona state, since when state values get passed down they are no longer state
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonaSelection(personas: Map<String, String>, selectedPersona: String, onPersonaSelected: (String) -> Unit) {
    var personaToView by remember { mutableStateOf("") }
    var modalOpen by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Your Persona")
        Spacer(modifier = Modifier.height(10.dp))
        Text("People can be broadly classified into 6 different types based on their eating preferences. Click on each button below to find out the different types and select the type that best fits you!")

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            personas.keys.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowItems.forEach { persona ->
                        Button(
                            onClick = {
                                personaToView = persona
                                modalOpen = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        ) {
                            Text(persona)
                        }
                    }
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            TextField(
                value = selectedPersona,
                onValueChange = {},
                label = { Text("Which persona best fits you") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Icon"
                    )
                },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    //align the dropdown with this textField
                    .menuAnchor()
            )
            //opens based on state value
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                personas.keys.forEach { persona ->
                    DropdownMenuItem(
                        text = { Text(persona) },
                        onClick = {
                            onPersonaSelected(persona)
                            expanded = false
                        }
                    )
                }
            }
        }
    }


    if (modalOpen) {
        PersonaModal(persona = personaToView, description = personas[personaToView] ?: "", onClose = { modalOpen = false })
    }
}

//displays modal and when closed change state in personaselection to rerender and not display modal
@Composable
fun PersonaModal(persona: String, description: String, onClose: () -> Unit) {
    val personaImages = mapOf(
        "Health Devotee" to R.drawable.health_devotee,
        "Mindful Eater" to R.drawable.mindful_eater,
        "Wellness Striver" to R.drawable.wellness_striver,
        "Balance Seeker" to R.drawable.balance_seeker,
        "Health Procrastinator" to R.drawable.health_procrastinator,
        "Food Carefree" to R.drawable.food_carefree
    )

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(persona) },
        //image is in text since alert dialog doesnt have an image param?
        text = {
            Column {
                Image(
                    painter = painterResource(id = personaImages[persona] ?: R.drawable.mindful_eater),
                    contentDescription = "$persona image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(description)
            }
        },
        confirmButton = {
            Button(onClick = onClose) {
                Text("Close")
            }
        }
    )
}