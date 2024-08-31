package com.example.james_code_challenge.presentation.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import com.example.james_code_challenge.data.model.Procedure
import com.example.james_code_challenge.presentation.ui.MainActivity.Companion.FAVOURITE_BUTTON_TEST_TAG

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FavouriteButton(
    modifier: Modifier = Modifier,
    onClickEvent: () -> Unit,
    currentItemUuid: String,
    favouritesList: List<Procedure>,
    isFavourite: (String) -> Boolean
) {
    val isFavouriteBoolean = isFavourite(currentItemUuid)
    var isSelected by remember { mutableStateOf(isFavouriteBoolean) }

    LaunchedEffect(favouritesList, currentItemUuid) { // Trigger on list or uuid change
        isSelected = isFavourite(currentItemUuid)
    }

    val colour = if (isSelected) {
        Color.Red
    } else {
        Color.LightGray
    }

    val selectedColour = "$currentItemUuid$FAVOURITE_BUTTON_TEST_TAG$colour"

    IconButton(
        modifier = modifier.semantics {
                        testTagsAsResourceId = true
                    }
                    .testTag(selectedColour),
        onClick = {
            isSelected = !isSelected
            onClickEvent()
        }
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = colour,
            modifier = modifier.size(32.dp)
        )
    }
}