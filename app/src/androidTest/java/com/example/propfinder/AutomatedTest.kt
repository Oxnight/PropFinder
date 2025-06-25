package com.example.propfinder

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Classe de test instrumenté simulant un parcours utilisateur complet dans l'application :
 * - Inscription
 * - Publication d'annonce
 * - Visualisation et interaction
 * - Envoi de message
 * - Suppression de l'annonce
 */
@RunWith(AndroidJUnit4::class)
class FullComposeFlowTest {

    // Règle permettant de lancer l'activité principale dans l’environnement de test Compose
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun fullUserJourneyTest() {
        // Création d’un email unique pour éviter les doublons
        val testEmail = "test${System.currentTimeMillis()}@mail.com"
        val testPassword = "testpass123"

        // Fonction utilitaire de pause (pas idéale mais simple pour éviter des erreurs de timing)
        fun wait(ms: Long) = Thread.sleep(ms)

        // Déconnexion si un utilisateur est déjà connecté
        try {
            composeTestRule.onNodeWithTag("profile_button").performClick()
            wait(1000)
            composeTestRule.onNodeWithTag("logout_button").performClick()
            wait(2000)
        } catch (_: AssertionError) {
            // Ignorer si déjà déconnecté
        }

        // Remplissage du formulaire d'inscription
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

        // Publication d’une annonce
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

        // Envoi de l’annonce
        composeTestRule.onNodeWithTag("submit_add_button", useUnmergedTree = true)
            .performScrollTo()
            .performClick()
        wait(2000)

        // Retour vers la page d'accueil
        composeTestRule.onNodeWithTag("home_button").performClick()
        wait(1000)

        // Attente que les annonces se chargent, et sélection de l'une d'elles
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("annonce_card", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onAllNodesWithTag("annonce_card")[1].performClick()
        wait(2000)

        // Cliquer sur "Contacter"
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("contact_button", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("contact_button").performClick()
        wait(1000)

        // Envoi d’un message
        val messageText = "Bonjour, je suis intéressé par votre annonce !"
        composeTestRule.onNodeWithTag("message_input").performTextInput(messageText)
        wait(2000)
        composeTestRule.onNodeWithTag("send_button").performClick()
        wait(2000)

        // Vérification que le message a bien été affiché dans l’interface
        composeTestRule.onNodeWithText(messageText).assertIsDisplayed()
        wait(1000)

        // Retour à la page d'accueil
        composeTestRule.onNodeWithTag("back_button", useUnmergedTree = true)
            .assertExists()
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithTag("home_button", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        wait(1000)

        // Sélection de la première annonce pour suppression
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("annonce_card", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onAllNodesWithTag("annonce_card")[0].performClick()

        // Suppression de l’annonce
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

        // Retour au point de départ
        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithTag("home_button", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("home_button").performClick()

        wait(3000)
    }
}