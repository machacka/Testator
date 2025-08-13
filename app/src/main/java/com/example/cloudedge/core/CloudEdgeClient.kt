package com.example.cloudedge.core

import kotlinx.coroutines.delay

/**
 * Simplified client interface to control CloudEdge cameras.
 * Replace the Stub implementation with a real one (e.g., based on the Meari SDK
 * as used by the CloudEdge4Tasker project) to actually talk to the cameras.
 */
interface CloudEdgeClient {
    suspend fun connectWithGoogleIdToken(idToken: String): Boolean
    suspend fun enableMotionAll(): Boolean
    suspend fun disableMotionAll(): Boolean
    fun signOut()
}

/**
 * Stub implementation: pretends to work so the app compiles/runs.
 * TODO: replace with a real client using CloudEdge APIs or the Meari SDK.
 */
class CloudEdgeClientStub : CloudEdgeClient {
    private var connected = false

    override suspend fun connectWithGoogleIdToken(idToken: String): Boolean {
        // There is no known public API to exchange a Google ID token for CloudEdge credentials.
        // Keep a local "connected" state so the UI can be tested.
        delay(500)
        connected = idToken.isNotBlank()
        return connected
    }

    override suspend fun enableMotionAll(): Boolean {
        delay(500)
        return connected
    }

    override suspend fun disableMotionAll(): Boolean {
        delay(500)
        return connected
    }

    override fun signOut() {
        connected = false
    }
}
