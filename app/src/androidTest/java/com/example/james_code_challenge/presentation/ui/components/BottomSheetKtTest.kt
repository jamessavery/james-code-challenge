package com.example.james_code_challenge.presentation.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToIndex
import com.example.james_code_challenge.mock.MockData
import com.example.james_code_challenge.presentation.ui.MainActivity.Companion.FAVOURITE_BUTTON_TEST_TAG
import com.example.james_code_challenge.presentation.ui.components.BottomSheet.Companion.BOTTOM_SHEET_ERROR_TEST_TAG
import com.example.james_code_challenge.presentation.ui.components.BottomSheet.Companion.BOTTOM_SHEET_IMAGE_GRID_TAG
import com.example.james_code_challenge.presentation.ui.components.BottomSheet.Companion.BOTTOM_SHEET_TEST_TAG
import com.example.james_code_challenge.util.toLocalDate
import org.junit.Rule
import org.junit.Test

class BottomSheetKtTest {

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Test
    fun testBottomSheet_showsCorrectDetails() {
        val procedureDetailMocked = MockData.procedureDetailMock
        val correctFormattedDate = procedureDetailMocked.datePublished.toLocalDate()
        val buttonColour = Color.Red

        composeTestRule.setContent {
            PhaseBottomSheet(
                procedureDetail = procedureDetailMocked,
                favouritesList = listOf(MockData.procedureMock),
                onFavouriteToggleEvent = {},
                isFavourite = { true }
            )
        }

        composeTestRule.onNodeWithTag(BOTTOM_SHEET_TEST_TAG).assertIsDisplayed()
        composeTestRule.onNodeWithText(procedureDetailMocked.name).assertIsDisplayed()
        composeTestRule.onNodeWithText("Total duration: ${procedureDetailMocked.duration} minutes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Creation date: $correctFormattedDate").assertIsDisplayed()
        composeTestRule.onNodeWithTag("${procedureDetailMocked.uuid}$FAVOURITE_BUTTON_TEST_TAG$buttonColour").assertIsDisplayed()
        composeTestRule.onNodeWithTag(BOTTOM_SHEET_IMAGE_GRID_TAG).assertIsDisplayed()
    }

    @Test
    fun testBottomSheet_phaseImageGrid_scrollsProperly() {
        composeTestRule.setContent {
            PhaseImageGrid(
                phaseData = MockData.procedureDetailMock.phases
            )
        }

        composeTestRule.onNodeWithTag(BOTTOM_SHEET_IMAGE_GRID_TAG)
            .performScrollToIndex(MockData.procedureDetailMock.phases.size - 1)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(MockData.procedureDetailMock.phases.last().name)
            .assertIsDisplayed()
    }

    @Test
    fun testBottomSheet_errorScenario() {
        val errorMessage = "Something went wrong, please try again later"
        composeTestRule.setContent {
            PhaseBottomSheet(
                procedureDetail = null,
                favouritesList = listOf(MockData.procedureMock),
                onFavouriteToggleEvent = {},
                isFavourite = { true }
            )
        }

        // Correct state rendered w correct message
        composeTestRule.onNodeWithTag(BOTTOM_SHEET_ERROR_TEST_TAG).assertIsDisplayed()
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

}