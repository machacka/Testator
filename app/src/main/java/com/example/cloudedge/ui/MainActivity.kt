package com.example.cloudedge.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.cloudedge.databinding.ActivityMainBinding
import com.example.cloudedge.core.CloudEdgeClient
import com.example.cloudedge.core.CloudEdgeClientStub
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private val ioScope = CoroutineScope(Dispatchers.IO)

    // Replace with a real client once you integrate CloudEdge SDK
    private val cloudEdgeClient: CloudEdgeClient = CloudEdgeClientStub()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())

        // Configure Google One Tap (client-only). You MUST replace the serverClientId string.
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(com.example.cloudedge.R.string.server_client_id_placeholder))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(false)
            .build()

        binding.signInButton.setOnClickListener { startSignIn() }
        binding.signOutButton.setOnClickListener {
            oneTapClient.signOut()
            cloudEdgeClient.signOut()
            updateUiSignedOut()
        }
        binding.enableAllButton.setOnClickListener { enableAll() }
        binding.disableAllButton.setOnClickListener { disableAll() }

        showImportantNote()
    }

    private fun showImportantNote() {
        AlertDialog.Builder(this)
            .setTitle(com.example.cloudedge.R.string.cloudedge_note_title)
            .setMessage(com.example.cloudedge.R.string.cloudedge_note_msg)
            .setPositiveButton(com.example.cloudedge.R.string.ok, null)
            .show()
    }

    private fun startSignIn() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, 1001, null, 0, 0, 0, null
                    )
                } catch (e: Exception) {
                    Timber.e(e, "One Tap: intent error")
                }
            }
            .addOnFailureListener { e ->
                Timber.e(e, "One Tap failed to start")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                val displayName = credential.displayName ?: credential.id ?: "Utilisateur"
                if (idToken != null) {
                    ioScope.launch {
                        val ok = cloudEdgeClient.connectWithGoogleIdToken(idToken)
                        withContext(Dispatchers.Main) {
                            if (ok) updateUiSignedIn(displayName) else updateUiSignedOut()
                        }
                    }
                } else {
                    updateUiSignedOut()
                }
            } catch (e: ApiException) {
                Timber.e(e, "Sign-in ApiException")
                updateUiSignedOut()
            } catch (e: Exception) {
                Timber.e(e, "Sign-in exception")
                updateUiSignedOut()
            }
        }
    }

    private fun updateUiSignedIn(name: String) {
        binding.statusText.text = getString(com.example.cloudedge.R.string.status_signed_in, name)
        binding.signInButton.visibility = android.view.View.GONE
        binding.signOutButton.visibility = android.view.View.VISIBLE
    }

    private fun updateUiSignedOut() {
        binding.statusText.text = getString(com.example.cloudedge.R.string.status_signed_out)
        binding.signInButton.visibility = android.view.View.VISIBLE
        binding.signOutButton.visibility = android.view.View.GONE
    }

    private fun enableAll() {
        ioScope.launch {
            val ok = cloudEdgeClient.enableMotionAll()
            withContext(Dispatchers.Main) {
                android.widget.Toast.makeText(this@MainActivity,
                    if (ok) "Détection activée sur toutes les caméras" else "Échec d’activation (non connecté)",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun disableAll() {
        ioScope.launch {
            val ok = cloudEdgeClient.disableMotionAll()
            withContext(Dispatchers.Main) {
                android.widget.Toast.makeText(this@MainActivity,
                    if (ok) "Détection désactivée sur toutes les caméras" else "Échec de désactivation (non connecté)",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
