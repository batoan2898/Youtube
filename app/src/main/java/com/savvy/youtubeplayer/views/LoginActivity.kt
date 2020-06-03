package com.savvy.youtubeplayer.views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.savvy.youtubeplayer.R
import com.savvy.youtubeplayer.data.MySharedPreferences
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {
    private val callbackManager = CallbackManager.Factory.create()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var name : String
    private lateinit var email : String
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
        btnFbLogin.setPermissions("email", "public_profile","user_birthday","user_friends")
        btnFbLogin.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.login_successful),
                    Toast.LENGTH_SHORT
                ).show()
                val accessToken = AccessToken.getCurrentAccessToken()
                MySharedPreferences.setId(accessToken.userId.toString())
                var request: GraphRequest = GraphRequest.newMeRequest(loginResult.accessToken
                ) { `object`, _ ->
                    email = `object`?.getString("email").toString()
                    name = `object`?.getString("name").toString()
                    onLoginSuccessful()

                }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email,gender, birthday")
                request.parameters = parameters
                request.executeAsync()

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
            MySharedPreferences.setId(id = account?.id.toString())
            name = account?.displayName.toString()
            email = account?.email.toString()

            onLoginSuccessful()
        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }

    private fun onLoginSuccessful() {
        Toast.makeText(this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show()
        MySharedPreferences.setName(name)
        MySharedPreferences.setEmail(email)
        intent = Intent(this@LoginActivity,MainActivity::class.java)
        startActivity(intent)
        finish()
        MySharedPreferences.setLogin(isLogin = true)
    }

}
