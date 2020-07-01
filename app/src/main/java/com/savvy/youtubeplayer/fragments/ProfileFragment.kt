package com.savvy.youtubeplayer.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bot.box.appusage.handler.Monitor
import bot.box.appusage.utils.DurationRange
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.savvy.youtubeplayer.R
import com.savvy.youtubeplayer.data.MySharedPreferences
import com.savvy.youtubeplayer.views.LoginActivity
import kotlinx.android.synthetic.main.fragment_profile.*
import java.util.*
import java.util.concurrent.TimeUnit


class ProfileFragment : Fragment() {

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleLogin()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setUpView()
        super.onActivityCreated(savedInstanceState)
    }

    private fun googleLogin() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
    }


    private fun formatTime(millis: Long): String {
        return String.format(
            "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
        )
    }


    private fun setTime() {
        var millis: Long = 0
        Monitor.scan().queryFor { appData, duration ->
            millis = appData.mUsageTime
        }.whichPackage("com.savvy.youtubeplayer").fetchFor(DurationRange.TODAY)


        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                millis += 1000
                activity?.runOnUiThread {
                    if (tvTimeUsage != null) tvTimeUsage.text = formatTime(millis)
                    
                }
            }
        }, 0, 1000)

    }


    @SuppressLint("SetTextI18n")
    private fun setUpView() {
        if (Monitor.hasUsagePermission()) setTime()
        else Monitor.requestUsagePermission()
        tvName.text = MySharedPreferences.getName()
        tvEmail.text = MySharedPreferences.getEmail()

        btnLogout.setOnClickListener {
            googleSignInClient.signOut()
            LoginManager.getInstance().logOut()
            MySharedPreferences.setLogin(isLogin = false)
            MySharedPreferences.setName("")
            MySharedPreferences.setEmail("")
            requireActivity().startActivity(Intent(requireActivity(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
