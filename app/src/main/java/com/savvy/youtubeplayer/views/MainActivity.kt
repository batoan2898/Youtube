package com.savvy.youtubeplayer.views

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.savvy.youtubeplayer.R
import com.savvy.youtubeplayer.fragments.HomeFragment
import com.savvy.youtubeplayer.fragments.PlaylistFragment
import com.savvy.youtubeplayer.fragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*


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
                    toolbarMain.title = "Home"
                }
                R.id.navPlaylist -> {
                    loadFragment(playlistFragment)
                    toolbarMain.title = "Playlist"

                }
                R.id.navProfile -> {
                    loadFragment(profileFragment)
                    toolbarMain.title = "Profile"
                }
            }
            true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initToolbar()
        }
        addFragment(homeFragment, playlistFragment, profileFragment)
        dialogLoading = Dialog(this)
        dialogLoading?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogLoading?.setContentView(R.layout.item_loading)
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val myActionMenuItem: MenuItem = menu!!.findItem(R.id.action_search)
        var searchView = myActionMenuItem.actionView as SearchView
        var countDownTimer: CountDownTimer? = null

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if( !searchView.isIconified) {
                    searchView.isIconified = true
                }
                myActionMenuItem.collapseActionView()
                homeFragment.searchVideo(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText?.length != 0) {
                    countDownTimer?.cancel()
                    countDownTimer = object : CountDownTimer(1500,1500){
                        override fun onTick(millisUntilFinished: Long) {}

                        override fun onFinish() {
                            Log.i("info", "----- Count Down Timer")
                            homeFragment.searchVideo(newText)
                            selectItem(R.id.navHome)
                        }
                    }
                    countDownTimer?.start()

                }
                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    private fun initToolbar() {
        setSupportActionBar(toolbarMain)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbarMain.setTitleTextColor(getColor(R.color.white))
        }
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

    override fun onBackPressed() {
        val intent = Intent()
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
        super.onBackPressed()
    }
}
