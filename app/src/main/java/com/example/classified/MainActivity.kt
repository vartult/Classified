package com.example.classified

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.nostra13.universalimageloader.core.ImageLoader

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private var mAuth: FirebaseAuth.AuthStateListener? = null

    //widgets
    private var mRegister: TextView? = null
    private var mEmail: EditText? = null
    private var mPassword:EditText? = null
    private var mLogin: Button? = null
    private var mProgressBar: ProgressBar? = null
    private var mLogo: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRegister = findViewById(R.id.link_register)
        mEmail = findViewById(R.id.input_email)
        mPassword = findViewById(R.id.input_password)
        mLogin = findViewById(R.id.btn_login)
        mLogo = findViewById(R.id.logo)

        //initImageLoader()
        initProgressBar()
        setupFirebaseAuth()
        init()
    }

    private fun init() {

mLogin?.setOnClickListener {
    //check if the fields are filled out
    if ((!isEmpty(mEmail?.text.toString()) && !isEmpty(mPassword?.text.toString()))) {
        Log.d(TAG, "onClick: attempting to authenticate.")

        showProgressBar()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail?.text.toString(),
            mPassword?.text.toString())
            .addOnCompleteListener { hideProgressBar() }.addOnFailureListener {
                Toast.makeText(this@MainActivity, "Authentication Failed", Toast.LENGTH_SHORT).show()
                hideProgressBar()
            }
    } else {
        Toast.makeText(this@MainActivity, "You didn't fill in all the fields.", Toast.LENGTH_SHORT).show()
    }
}

        mRegister?.setOnClickListener {
            Log.d(TAG, "onClick: Navigating to Register Screen")
            val intent = Intent(this@MainActivity, Register::class.java)
            startActivity(intent)
        }

        //UniversalImageLoader.setImage("res://money_icon.png", mLogo!!);

        hideSoftKeyboard()
}

    private fun isEmpty(string: String): Boolean {
        return string == ""
    }


    private fun showProgressBar() {
        mProgressBar?.setVisibility(View.VISIBLE)

    }

    private fun hideProgressBar() {
        if (mProgressBar?.getVisibility() == View.VISIBLE) {
            mProgressBar?.setVisibility(View.INVISIBLE)
        }
    }


    /*private fun initImageLoader() {
        val imageLoader = UniversalImageLoader(this@MainActivity)
        ImageLoader.getInstance().init(imageLoader.getConfig())
    }*/

    private fun initProgressBar() {
        mProgressBar = findViewById(R.id.progressBar) as ProgressBar
        mProgressBar!!.setVisibility(View.INVISIBLE)
    }

    private fun hideSoftKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    //<-----------------FIREBASE AUTHENTICATON---------->//
    private fun setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started")

        mAuth = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {

                //check if email is verified
                if (user.isEmailVerified) {
                    Log.d(TAG, "onAuthStateChanged: signed_in: " + user.uid)
                    Toast.makeText(this@MainActivity, "Authenticated with: " + user.email!!, Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@MainActivity, SearchActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this@MainActivity, "Email is not Verified.\nCheck your Inbox", Toast.LENGTH_SHORT)
                        .show()
                    FirebaseAuth.getInstance().signOut()
                }

            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged: signed_out")
            }
            // ...
        }
    }

    public override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuth!!)
    }

    public override fun onStop() {
        super.onStop()
        if (mAuth != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuth!!)
        }
    }
}


