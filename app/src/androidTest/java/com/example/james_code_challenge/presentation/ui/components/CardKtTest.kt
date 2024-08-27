package com.example.james_code_challenge.presentation.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.example.james_code_challenge.mock.MockData
import com.example.james_code_challenge.presentation.ui.components.Card.Companion.IMAGE_CONTENT_DESCRIPTION
import org.junit.Rule
import org.junit.Test

class CardKtTest {

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Test
    fun testCard_showsCorrectDetails() {
        composeTestRule.setContent {
            ProcedureDetailCard(
                procedure = MockData.procedureMock,
                favouritesList = listOf(MockData.procedureMock),
                onCardClickEvent = {},
                onFavouriteToggleEvent = {},
                isFavourite = { true }
            )
        }

        composeTestRule.onNodeWithText(MockData.procedureMock.name).assertIsDisplayed()
        composeTestRule.onNodeWithText("Phase Count: ${MockData.procedureMock.phases.size}").assertIsDisplayed()

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription(IMAGE_CONTENT_DESCRIPTION).assertExists()
    }

}