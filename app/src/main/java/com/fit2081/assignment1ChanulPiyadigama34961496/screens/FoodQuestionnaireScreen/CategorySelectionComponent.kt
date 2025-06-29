package com.fit2081.assignment1ChanulPiyadigama34961496.screens.FoodQuestionnaireScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fit2081.assignment1ChanulPiyadigama34961496.utils.CheckboxWithLabel


//returns grid of checkboxes for given map, where each catergory checkbox will be tied to a boolean state in checkbox map
@Composable
fun CategorySelectionComponent(
    checkedCategories: MutableMap<String, Boolean>,
    onCategoryChanged: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Select food categories you can eat:")

        checkedCategories.entries.chunked(3).forEach { chunk ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                chunk.forEach { (category, isChecked) ->
                    CheckboxWithLabel(
                        label = category,
                        isChecked = isChecked,
                        onCheckedChange = {
                            checkedCategories[category] = it
                            onCategoryChanged()
                        }
                    )
                }
            }
        }
    }
}