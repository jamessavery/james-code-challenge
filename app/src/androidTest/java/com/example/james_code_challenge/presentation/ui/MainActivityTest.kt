package com.example.james_code_challenge.presentation.ui

import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.swipeDown
import androidx.test.espresso.Espresso
import com.example.james_code_challenge.mock.MockData
import com.example.james_code_challenge.mock.MockData.Companion.procedureDetailMock
import com.example.james_code_challenge.mock.MockData.Companion.procedureMock
import com.example.james_code_challenge.presentation.ui.MainActivity.Companion.FAVOURITE_BUTTON_TEST_TAG
import com.example.james_code_challenge.presentation.ui.MainActivity.Companion.PROCEDURE_LIST_TEST_TAG
import com.example.james_code_challenge.presentation.ui.MainActivity.Companion.PROGRESS_ICON_TEST_TAG
import com.example.james_code_challenge.presentation.ui.components.BottomSheet.Companion.BOTTOM_SHEET_TEST_TAG
import com.example.james_code_challenge.presentation.ui.components.Card.Companion.IMAGE_CONTENT_DESCRIPTION
import com.example.james_code_challenge.presentation.ui.screens.ProceduresScreen
import com.example.james_code_challenge.presentation.viewmodel.MainViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val errorScreenMessage = "Something went wrong, please try again later"

    private val mockProcedureList = mutableListOf(
        MockData.procedureMock,
        MockData.procedureMock.copy(uuid = "1"),
        MockData.procedureMock.copy(uuid = "2"),
        MockData.procedureMock.copy(uuid = "3"),
        MockData.procedureMock.copy(uuid = "4")
    )

    @Before
    fun setup() {
        hiltRule.inject()
        composeTestRule.onRoot(useUnmergedTree = true).printToLog("ComposeTestingTag") // Print tags present on screen
    }

    @Test
    fun testProceduresTab_scrollDownAndClickOnProcedure_opensBottomSheetAndDismisses_favouriteStatePersistsBetweenBoth() {
        val itemToClick = "itemToClick"
        val uuidToClick = "7"
        mockProcedureList.add(procedureMock.copy(uuid = "5"))
        mockProcedureList.add(procedureMock.copy(uuid = "6"))
        mockProcedureList.add(procedureMock.copy(uuid = uuidToClick, name = itemToClick))
        mockProcedureList.add(procedureMock.copy(uuid = "8"))

        val favouriteUnselected = Color.LightGray
        val favouriteSelected = Color.Red

        composeTestRule.activity.setContent {
            ProcedureScreenUnderTest(
                uiState = MainViewModel.ProceduresState(
                    isLoading = false,
                    items = mockProcedureList,
                    selectedProcedureDetail = procedureDetailMock
                )
            )
        }

        // Correct tab & items displayed
        composeTestRule.onNodeWithTag(PROCEDURE_LIST_TEST_TAG).assertIsDisplayed()
        composeTestRule.onNodeWithTag(errorScreenMessage).assertIsNotDisplayed()
        composeTestRule.onNodeWithTag(BOTTOM_SHEET_TEST_TAG).assertIsNotDisplayed()
        composeTestRule.onAllNodes(androidx.compose.ui.test.hasContentDescription(IMAGE_CONTENT_DESCRIPTION))
            .assertAll(hasText(procedureMock.name))

        // Scroll down & click
        composeTestRule.onNodeWithTag(PROCEDURE_LIST_TEST_TAG).performScrollToIndex(mockProcedureList.size - 1)
        composeTestRule.onNodeWithText(itemToClick).performClick()
        composeTestRule.waitForIdle()

        // Bottom sheet displayed & favourite button works
        composeTestRule.onNodeWithTag(BOTTOM_SHEET_TEST_TAG).assertIsDisplayed()
        composeTestRule.onNodeWithTag("$uuidToClick$FAVOURITE_BUTTON_TEST_TAG$favouriteUnselected").assertIsDisplayed() // Assert button in unclicked state
        composeTestRule.onNodeWithTag("$uuidToClick$FAVOURITE_BUTTON_TEST_TAG$favouriteUnselected").performClick()
        composeTestRule.onNodeWithTag("$uuidToClick$FAVOURITE_BUTTON_TEST_TAG$favouriteSelected").assertIsDisplayed() // Assert button in clicked state

        // Back press should dismiss Bottomsheet
        Espresso.pressBack()

        // Bottomsheet dismissed & favourite button state reflects what was set in Bottomsheet
        composeTestRule.onNodeWithTag(BOTTOM_SHEET_TEST_TAG).assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("$uuidToClick$FAVOURITE_BUTTON_TEST_TAG$favouriteSelected").assertIsDisplayed()
        composeTestRule.onNodeWithTag("$uuidToClick$FAVOURITE_BUTTON_TEST_TAG$favouriteSelected").performClick()
        composeTestRule.onNodeWithTag("$uuidToClick$FAVOURITE_BUTTON_TEST_TAG$favouriteUnselected").assertIsDisplayed()

        // Reopen Bottomsheet
        composeTestRule.onNodeWithText(itemToClick).performClick()

        // Ensure swiping down dismisses the Bottomsheet
        composeTestRule.onNodeWithTag(BOTTOM_SHEET_TEST_TAG).performTouchInput {
            swipeDown(durationMillis = 5000L)
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(BOTTOM_SHEET_TEST_TAG).assertIsNotDisplayed()
    }

    @Test
    fun testProceduresScreen_loadingState_ShowsLoading() {
        composeTestRule.activity.setContent {
            ProcedureScreenUnderTest(
                uiState = MainViewModel.ProceduresState(isLoading = true)
            )
        }

        // Shows progress icon
        composeTestRule.onNodeWithTag(PROGRESS_ICON_TEST_TAG).assertIsDisplayed()

        composeTestRule.activity.setContent {
            ProcedureScreenUnderTest(
                uiState = MainViewModel.ProceduresState(isLoading = false)
            )
        }

        // Shows correct tab after swapping
        composeTestRule.onNodeWithTag(PROGRESS_ICON_TEST_TAG).assertIsNotDisplayed()
    }

    @Test
    fun testProceduresScreen_errorState_showsCorrectText() {
        composeTestRule.activity.setContent {
            ProcedureScreenUnderTest(
                uiState = MainViewModel.ProceduresState(error = "notNull")
            )
        }

        // Shows progress icon
        composeTestRule.onNodeWithText(errorScreenMessage).assertIsDisplayed()

        composeTestRule.activity.setContent {
            ProcedureScreenUnderTest(
                uiState = MainViewModel.ProceduresState(error = null)
            )
        }

        // Shows correct tab after swapping
        composeTestRule.onNodeWithTag(PROGRESS_ICON_TEST_TAG).assertIsNotDisplayed()
    }

    @Composable
    fun ProcedureScreenUnderTest(uiState: MainViewModel.ProceduresState) {
        ProceduresScreen(
            uiState = uiState,
            fetchProcedureDetailEvent = {},
            onFavouriteToggleEvent = {},
            fetchProceduresAndFavourites = {},
            isFavourite = { false }
        )
    }

    // D_N: Not going for 100% coverage here, hopefully the tests above & spread across the project demonstrate what I'm capable of

}