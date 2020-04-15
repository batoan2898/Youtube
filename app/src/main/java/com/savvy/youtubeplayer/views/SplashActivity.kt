package com.savvy.youtubeplayer.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.facebook.FacebookSdk
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.Wave
import com.savvy.youtubeplayer.R
import com.savvy.youtubeplayer.data.MySharedPreferences
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {
    private var handler: Handler = Handler()
    private val runnable = Runnable {
        handler.postDelayed({
            if (MySharedPreferences.checkLogin()) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
        }, 1000)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initActivity()
    }

    private fun initActivity() {
        FacebookSdk.sdkInitialize(applicationContext) // Bo
        var mSpinkit: Sprite = Wave() // Bo
        spinkit.setIndeterminateDrawable(mSpinkit)
        runnable.run()
    }

    public override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
