package com.savvy.youtubeplayer.views

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.inputmethodservice.KeyboardView
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.savvy.youtubeplayer.R
import com.savvy.youtubeplayer.fragments.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*


class MainActivity : AppCompatActivity() {

    private var dialogLoading: Dialog? = null
    private val homeFragment = HomeFragment.newInstance()
    private val playlistFragment = PlaylistFragment.newInstance()
    private val profileFragment = ProfileFragment.newInstance()
    private lateinit var isFirstFragment: Fragment

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> {
                    loadFragment(homeFragment)
                }
                R.id.navPlaylist -> {
                    loadFragment(playlistFragment)
                }
                R.id.navProfile -> {
                    loadFragment(profileFragment)
                }
            }
            true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        initToolbar()
        addFragment(homeFragment, playlistFragment, profileFragment)
        dialogLoading = Dialog(this)
        dialogLoading?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogLoading?.setContentView(R.layout.item_loading)
        initViews(s = edtSearch.editableText)
    }


    private fun initViews(s: Editable) {
        var countDownTimer: CountDownTimer? = null

        edtSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(
                v: TextView,
                actionId: Int,
                event: KeyEvent?
            ): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    var inputMethod: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethod.hideSoftInputFromWindow(edtSearch.windowToken, 0)
                    homeFragment.searchVideo(s)
                    selectItem(R.id.navHome)
                    return true

                }
                return true
            }
        })

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length != 0) {
                    countDownTimer?.cancel()
                    countDownTimer = object : CountDownTimer(1500,1500){
                        override fun onTick(millisUntilFinished: Long) {}

                        override fun onFinish() {
                            Log.i("info", "----- Count Down Timer")
                            homeFragment.searchVideo(s)
                            selectItem(R.id.navHome)
                        }
                    }
                    countDownTimer?.start()

                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }


    @SuppressLint("ResourceAsColor")
    private fun initToolbar() {
        setSupportActionBar(toolbarMain)
        toolbarMain.setTitleTextColor(R.color.titleToolbar)
        toolbarMain.setLogo(R.drawable.im_logo)

    }


    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }

    @SuppressLint("NewApi")
    private fun loadFragment(fragment: Fragment) {
        if (isFirstFragment == fragment)
            return
        supportFragmentManager.inTransaction {
            show(fragment)
            hide(isFirstFragment)
        }
        isFirstFragment = fragment
    }

    private fun addFragment(vararg fragment: Fragment) {
        supportFragmentManager.inTransaction {
            for (i in fragment.indices) {
                add(R.id.frameContainer, fragment[i]).hide(fragment[i])
            }
            isFirstFragment = fragment[0]
            show(isFirstFragment)
        }
    }

    fun selectItem(itemId: Int){
        navigationView.selectedItemId = itemId
    }
}
