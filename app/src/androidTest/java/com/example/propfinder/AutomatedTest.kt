package com.example.propfinder

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.propfinder.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FullComposeFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun fullUserJourneyTest() {
        val testEmail = "test${System.currentTimeMillis()}@mail.com"
        val testPassword = "testpass123"

        fun wait(ms: Long) = Thread.sleep(ms)

        try {
            composeTestRule.onNodeWithTag("profile_button").performClick()
            wait(1000)
            composeTestRule.onNodeWithTag("logout_button").performClick()
            wait(2000)
        } catch (_: AssertionError) {}

        composeTestRule.onNodeWithTag("signup_button").performClick()
        wait(1000)
        composeTestRule.onNodeWithTag("email_field").performTextInput(testEmail)
        wait(1000)
        composeTestRule.onNodeWithTag("nom_field").performTextInput("Dupont")
        wait(1000)
        composeTestRule.onNodeWithTag("prenom_field").performTextInput("Jean")
        wait(1000)
        composeTestRule.onNodeWithTag("age_field").performTextInput("30")
        wait(1000)
        composeTestRule.onNodeWithTag("password_field").performTextInput(testPassword)
        wait(1000)
        composeTestRule.onNodeWithTag("create_account_button").performClick()
        wait(3000)

        composeTestRule.onNodeWithTag("publish_button").performClick()
        wait(1000)
        composeTestRule.onNodeWithTag("radio_louer").performClick()
        wait(1000)
        composeTestRule.onNodeWithTag("titre_field").performTextInput("Appartement T3")
        wait(1000)
        composeTestRule.onNodeWithTag("description_field").performTextInput(
            "Appartement T3 de 65m2 situé en plein cœur du centre-ville, à proximité des transports et commerces. Composé de deux chambres, d’un salon spacieux, d’une cuisine équipée et d’un balcon. Idéal pour un couple ou une petite famille."
        )
        wait(1000)
        composeTestRule.onNodeWithTag("prix_field").performTextInput("900")
        wait(1000)
        composeTestRule.onNodeWithTag("carateristiques_field").performTextInput(
            "Balcon exposé sud, cuisine entièrement équipée, rangements intégrés, double vitrage, proximité immédiate des transports en commun et des commerces."
        )
        wait(1000)
        composeTestRule.onNodeWithTag("adress_field", useUnmergedTree = true)
            .performTextInput("7 rue Ernest Thierry Mieg, 90000 Belfort")
        wait(1000)

        composeTestRule.onNodeWithTag("submit_add_button", useUnmergedTree = true)
            .performScrollTo()
            .performClick()
        wait(2000)

        composeTestRule.onNodeWithTag("home_button").performClick()
        wait(1000)

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("annonce_card", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onAllNodesWithTag("annonce_card")[1].performClick()
        wait(2000)

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("contact_button", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("contact_button").performClick()
        wait(1000)

        val messageText = "Bonjour, je suis intéressé par votre annonce !"
        composeTestRule.onNodeWithTag("message_input").performTextInput(messageText)
        wait(2000)
        composeTestRule.onNodeWithTag("send_button").performClick()
        wait(2000)

        composeTestRule.onNodeWithText(messageText).assertIsDisplayed()
        wait(1000)

        composeTestRule.onNodeWithTag("back_button", useUnmergedTree = true)
            .assertExists()
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithTag("home_button", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        wait(1000)

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("annonce_card", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onAllNodesWithTag("annonce_card")[0].performClick()

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("delete_annonce", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("delete_annonce").performClick()

        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithTag("confirm_delete_button", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("confirm_delete_button").performClick()
        wait(3000)

        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithTag("home_button", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("home_button").performClick()

        wait(3000)
    }
}