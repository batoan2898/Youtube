package com.savvy.youtubeplayer.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.activity_login.*
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.savvy.youtubeplayer.data.MySharedPreferences
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.savvy.youtubeplayer.R


class LoginActivity : AppCompatActivity() {
    private val callbackManager = CallbackManager.Factory.create()
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private val REQUESTCODE_SIGN_IN = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupView()
    }

    private fun setupView() {
        btnLoginFb.setOnClickListener {
            onLoginWithFacebook()
        }
        btnLoginGg.setOnClickListener {
            onLoginWithGoogle()
        }
    }

    private fun onLoginWithFacebook() {
        val btnFbLogin = LoginButton(this)
        btnFbLogin.performClick()
        btnFbLogin.setPermissions("email", "public_profile")
        btnFbLogin.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.login_successful),
                    Toast.LENGTH_SHORT
                ).show()
                onLoginSuccessful()

                val accessToken = AccessToken.getCurrentAccessToken()

                MySharedPreferences.setId(accessToken.userId.toString())
                Log.e("toan",MySharedPreferences.getId())
            }

            override fun onCancel() {
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.login_cancelled),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onError(exception: FacebookException) {
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.login_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun onLoginWithGoogle() {
        var googleSignInOption =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)
        var account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        var signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, REQUESTCODE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUESTCODE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.e("abc",account?.idToken)
            MySharedPreferences.setId(id = account?.id.toString())
            Log.e("toan",MySharedPreferences.getId())
            onLoginSuccessful()
        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }

    private fun onLoginSuccessful() {
        Toast.makeText(this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
        MySharedPreferences.setLogin(isLogin = true)
    }

}
