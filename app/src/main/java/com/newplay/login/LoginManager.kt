package com.newplay.login

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

/**
 * https://developers.google.com/identity/sign-in/android/start-integrating?hl=zh-cn
 * 谷歌授权登录
 */
class LoginManager {
    companion object {

        private const val RC_SIGN_IN = 2200
        private const val TAG = "LoginManager"
    }

    fun login(context: Activity) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Constant.GOOGLE_AUTH_CLIENT_ID)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(context, gso)

        val account = GoogleSignIn.getLastSignedInAccount(context)
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        context.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun handleCallback(
        requestCode: Int,
        data: Intent?,
        success: (idToken: String) -> Unit,
        fail: () -> Unit
    ) {
        when (requestCode) {
            RC_SIGN_IN -> {
                try {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(data)
                    val result =
                        task.getResult(ApiException::class.java)
                    val idToken = result.idToken
                    if (idToken.isNullOrEmpty()) {
                        Log.d("MainActivity", "onclick idtoken${idToken}")
                        fail.invoke()
                        return
                    }
                    Log.d("MainActivity", "onclick ${idToken}")
                    success.invoke(idToken)
                } catch (e: ApiException) {
                    Log.d("MainActivity", "onclick ${e.message}")
                    fail.invoke()
                }
            }
        }
    }
}