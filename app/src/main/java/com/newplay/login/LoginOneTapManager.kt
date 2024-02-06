package com.newplay.login

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes

/**
 * https://developers.google.com/identity/one-tap/android/overview?hl=zh-cn
 * 谷歌一键登录,当设备没谷歌登录，报错16: Cannot find a matching credential，建议使用谷歌授权登录
 *
 *             loginOneTapManager.loginOneTap(this@LoginActivity) { msg ->
 *                 hideLoadingDialog()
 *                 ToastUtils.showShort(msg)
 *            }
 *
 *
 *         loginOneTapManager.handleCallback(requestCode, data, { idToken ->
 *             loginViewModel.sendUiIntent(
 *                 LoginIntent.Login(
 *                     this@LoginActivity, idToken, firebaseToken
 *                 )
 *             )
 *         }, {
 *             hideLoadingDialog()
 *         })
 *
 */
class LoginOneTapManager {
    companion object {
        private const val TAG = "LoginOneTapManager"
        private const val REQ_ONE_TAP = 2000
    }

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest


    fun loginOneTap(context: Activity, fail: (msg: String) -> Unit): SignInClient {
        initGoogleLogin(context)
        oneTapClient.beginSignIn(signInRequest).addOnSuccessListener(context) { result ->
            try {
                context.startIntentSenderForResult(
                    result.pendingIntent.intentSender, REQ_ONE_TAP, null, 0, 0, 0, null
                )
            } catch (e: IntentSender.SendIntentException) {

            }
        }.addOnFailureListener(context) { e ->
            e.localizedMessage?.let {
                fail.invoke(it)
            }
        }
        return oneTapClient
    }

    private fun initGoogleLogin(context: Activity) {
        oneTapClient = Identity.getSignInClient(context)
        signInRequest = BeginSignInRequest.builder().setPasswordRequestOptions(
            BeginSignInRequest.PasswordRequestOptions.builder().setSupported(true).build()
        ).setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(Constant.GOOGLE_AUTH_CLIENT_ID)
                .setFilterByAuthorizedAccounts(false).build()
        ).setAutoSelectEnabled(true).build()
    }

    fun handleCallback(
        requestCode: Int, data: Intent?, success: (idToken: String) -> Unit, fail: () -> Unit
    ) {
        when (requestCode) {
            REQ_ONE_TAP -> {

                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            success.invoke(idToken)
                        }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {

                        }

                        CommonStatusCodes.NETWORK_ERROR -> {

                        }

                        else -> {

                        }
                    }
                    fail.invoke()
                }
            }
        }
    }
}