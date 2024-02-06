package com.newplay.login

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

class LoginCredentialManager {

    suspend fun login(context: Context, success: (idToken: String) -> Unit, fail: () -> Unit) {
        val signInOption = GetSignInWithGoogleOption
            .Builder(Constant.GOOGLE_AUTH_CLIENT_ID)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInOption)
            .build()

        try {

            val result = CredentialManager.create(context).getCredential(
                request = request,
                context = context,
            )
            val credential = result.credential
            if (credential !is CustomCredential || credential.type != TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                return
            }
            try {
                val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                success.invoke(googleCredential.idToken)
            } catch (e: GoogleIdTokenParsingException) {
                fail.invoke()
            }
        } catch (e: GetCredentialException) {
            e.type
            fail.invoke()
        }
    }
}