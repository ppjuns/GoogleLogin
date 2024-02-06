package com.newplay

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.newplay.login.LoginCredentialManager
import com.newplay.login.LoginManager
import com.newplay.login.LoginOneTapManager
import com.newplay.ui.theme.GoogleLoginTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    val TAG = "MainActivity"
    private val loginCredentialManager by lazy { LoginCredentialManager() }
    private val loginManager by lazy { LoginManager() }
    private val loginOneTapManager by lazy { LoginOneTapManager() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoogleLoginTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {


                    Greeting({
                        loginManager.login(this@MainActivity)
                    }, {
                        loginOneTapManager.loginOneTap(this@MainActivity) { fail() }
                    }, {
                        lifecycleScope.launch {
                            loginCredentialManager.login(this@MainActivity,
                                { idToken -> success(idToken) },
                                { fail() })
                        }
                    })
                }
            }
        }
    }

    private fun fail() {
        Log.d(TAG, "fail")
    }

    private fun success(idToken: String) {
        Log.d(TAG, idToken)

    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loginManager.handleCallback(requestCode, data, { idToken -> success(idToken) }, { fail() })
        loginOneTapManager.handleCallback(requestCode,
            data,
            { idToken -> success(idToken) },
            { fail() })
    }


    @Composable
    fun Greeting(onClick1: () -> Unit, onClick2: () -> Unit, onClick3: () -> Unit) {

        val idToken by remember { mutableStateOf("") }
        Column {
            Button(onClick = {
                Log.d("MainActivity", "onclick")
                onClick1.invoke()
            }) {
                Text(
                    text = "谷歌授权登录"
                )
            }
            Button(onClick = {
                Log.d("MainActivity", "onclick")
                onClick2.invoke()
            }) {
                Text(
                    text = "谷歌一键登录"
                )
            }
            Button(onClick = {
                Log.d("MainActivity", "onclick")
                onClick3.invoke()
            }) {
                Text(
                    text = "谷歌凭证登录"
                )
            }
            Text(
                text = "idToken:$idToken"
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GoogleLoginTheme {

    }
}