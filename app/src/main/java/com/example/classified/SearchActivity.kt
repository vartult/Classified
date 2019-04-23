package com.example.classified

import android.Manifest
import android.content.Intent
import android.support.design.widget.TabLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import kotlinx.android.synthetic.main.activity_search2.*
import android.support.annotation.NonNull
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.Manifest.permission
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE









class SearchActivity : AppCompatActivity() {

    private val REQUEST_CODE = 1
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search2)
        verifyPermissions()
        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        fab.setOnClickListener { view ->
            appbar.setExpanded(false, false)
            appbar.visibility = View.GONE
            fab.alpha = 0F
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,PostFragment.newInstance()).addToBackStack(null).commit()
/*
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        appbar.setExpanded(true, true)
        appbar.visibility = View.VISIBLE
        fab.alpha = 1F
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_search, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            val intent = Intent(this@SearchActivity, Account::class.java)
            startActivity(intent)
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? = when (position) {
            0 -> FragmentOne.newInstance()
            1 -> FragmentTwo.newInstance()
            else -> null
        }

        override fun getCount(): Int {
            return 2
        }
    }

    private fun verifyPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )

        if (ContextCompat.checkSelfPermission(this.applicationContext, permissions[0]) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this.applicationContext, permissions[1]) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this.applicationContext, permissions[2]) == PackageManager.PERMISSION_GRANTED
        ) {
            //setupViewPager()
        } else {
            ActivityCompat.requestPermissions(
                this@SearchActivity,
                permissions,
                REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        verifyPermissions()
    }
}
