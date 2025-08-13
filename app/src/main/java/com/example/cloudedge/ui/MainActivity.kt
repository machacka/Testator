package com.example.cloudedge.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.cloudedge.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var oneTapClient: SignInClient

    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val email = credential.id
                Timber.i("Connecté avec Google : $email")
                updateUI(true, email)
            } catch (e: ApiException) {
                Timber.e(e, "Erreur connexion Google")
                updateUI(false, null)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())
        oneTapClient = Identity.getSignInClient(this)

        binding.signInButton.setOnClickListener { beginSignIn() }
        binding.signOutButton.setOnClickListener { signOut() }
        binding.enableAllButton.setOnClickListener { enableMotionDetection() }
        binding.disableAllButton.setOnClickListener { disableMotionDetection() }
    }

    private fun beginSignIn() {
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId("TON_CLIENT_ID_WEB") // à remplacer par ton client_id
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    signInLauncher.launch(result.pendingIntent.intent)
                } catch (e: Exception) {
                    Timber.e(e, "Erreur lancement One Tap")
                }
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Echec One Tap")
            }
    }

    private fun signOut() {
        oneTapClient.signOut().addOnCompleteListener {
            Timber.i("Déconnexion réussie")
            updateUI(false, null)
        }
    }

    private fun enableMotionDetection() {
        Timber.i("Activer détection de mouvement - TODO API CloudEdge")
    }

    private fun disableMotionDetection() {
        Timber.i("Désactiver détection de mouvement - TODO API CloudEdge")
    }

    private fun updateUI(isSignedIn: Boolean, email: String?) {
        binding.statusText.text = if (isSignedIn) {
            "Connecté : $email"
        } else {
            "Non connecté"
        }

        binding.signInButton.visibility = if (isSignedIn) android.view.View.GONE else android.view.View.VISIBLE
        binding.signOutButton.visibility = if (isSignedIn) android.view.View.VISIBLE else android.view.View.GONE
        binding.enableAllButton.visibility = if (isSignedIn) android.view.View.VISIBLE else android.view.View.GONE
        binding.disableAllButton.visibility = if (isSignedIn) android.view.View.VISIBLE else android.view.View.GONE
    }
}
