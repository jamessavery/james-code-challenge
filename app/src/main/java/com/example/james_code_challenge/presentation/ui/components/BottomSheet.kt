package com.example.james_code_challenge.presentation.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.james_code_challenge.R
import com.example.james_code_challenge.data.model.Phase
import com.example.james_code_challenge.data.model.ProcedureDetail
import com.example.james_code_challenge.mock.MockData
import com.example.james_code_challenge.presentation.ui.components.BottomSheet.Companion.BOTTOM_SHEET_IMAGE_GRID_TAG
import com.example.james_code_challenge.presentation.ui.components.BottomSheet.Companion.BOTTOM_SHEET_TEST_TAG
import com.example.james_code_challenge.presentation.ui.components.button.FavouriteButton
import com.example.james_code_challenge.util.toLocalDate

class BottomSheet {
    companion object {
        const val BOTTOM_SHEET_TEST_TAG = "bottom_sheet_test_tag"
        const val BOTTOM_SHEET_IMAGE_GRID_TAG = "bottom_sheet_image_grid_tag"
    }
}

@Composable
fun PhaseBottomSheet(
    modifier: Modifier = Modifier,
    procedureDetail: ProcedureDetail?
) {
    if (procedureDetail == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .testTag(BOTTOM_SHEET_TEST_TAG)
        ) {
            Text(
                text = stringResource(R.string.generic_error_message),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
        AsyncImage(
            model = procedureDetail.card.url,
            contentDescription = "",
            modifier = modifier
                .border(
                    width = 2.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(8.dp)
                )
                .testTag(BOTTOM_SHEET_TEST_TAG)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .padding(3.dp)
        ) {
            Text(text = procedureDetail.name, fontWeight = FontWeight.Bold)
            Spacer(modifier.weight(1f))
            FavouriteButton()
        }
        Text(text = stringResource(id = R.string.total_duration, procedureDetail.duration))
        Text(
            text = stringResource(
                id = R.string.creation_date,
                procedureDetail.datePublished.toLocalDate()
            )
        )
        Card(
            modifier
                .padding(4.dp)
        ) {
            PhaseImageGrid(
                phaseData = procedureDetail.phases
            )
        }
    }
}

@Composable
fun PhaseImageGrid(phaseData: List<Phase>, modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .testTag(BOTTOM_SHEET_IMAGE_GRID_TAG)
    ) {
        itemsIndexed(phaseData) { _, item ->
            ImageCard(modifier, item.icon.url, item.name)
        }
    }
}

@Composable
fun ImageCard(modifier: Modifier = Modifier, imageUrl: String, title: String) {
    Box(
        modifier = modifier
            .size(200.dp)
            .padding(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = modifier.fillMaxHeight(0.9f)
            )
            Text(
                text = title,
                modifier = modifier.paddingFromBaseline(top = 24.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
@Preview
fun PhaseImageGridPreview() {
    PhaseImageGrid(phaseData = MockData.procedureDetailMock.phases)
}

// Previewing ModalBottomSheet appears bugged
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
@Preview(showBackground = true)
fun PhaseBottomSheetPreview() {
    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(),
        onDismissRequest = {},
        sheetState = SheetState(true)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 100.dp)
        ) {
            PhaseBottomSheet(procedureDetail = MockData.procedureDetailMock)
        }
    }
}