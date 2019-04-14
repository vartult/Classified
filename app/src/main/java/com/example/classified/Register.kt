package com.example.classified

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {

    private val TAG = "RegisterActivity"

    //Firebase
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null

    //widgets
    private var mEmail: EditText? = null
    private var mName:EditText? = null
    private var mPassword:EditText? = null
    private var mConfirmPassword:EditText? = null
    private var mRegister: Button? = null
    private var mProgressBar: ProgressBar? = null

    //vars
    private var mContext: Context? = null
    private var email: String? = null
    private var name:String? = null
    private var password:String? = null
    private var mUser: User?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mRegister = findViewById(R.id.btn_register)
        mEmail = findViewById(R.id.input_email)
        mPassword = findViewById(R.id.input_password)
        mConfirmPassword = findViewById(R.id.input_confirm_password)
        mName = findViewById(R.id.input_name)
        mContext = this@Register
        mUser = User()
        Log.d(TAG, "onCreate: started")

        initProgressBar()
        setupFirebaseAuth()
        init()

        hideSoftKeyboard()
    }

    private fun init() {


        mRegister?.setOnClickListener(View.OnClickListener {
            email = mEmail?.text.toString()
            name = mName?.text.toString()
            password = mPassword?.text.toString()

            if (checkInputs(email!!, name!!, password!!, mConfirmPassword?.text.toString())) {
                if (doStringsMatch(password!!, mConfirmPassword?.text.toString())) {
                    registerNewEmail(email!!, password!!)
                } else {
                    Toast.makeText(mContext, "passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(mContext, "All fields must be filled", Toast.LENGTH_SHORT).show()
            }
        })
    }


    /**
     * Return true if @param 's1' matches @param 's2'
     * @param s1
     * @param s2
     * @return
     */
    private fun doStringsMatch(s1: String, s2: String): Boolean {
        return s1 == s2
    }


    /**
     * Checks all the input fields for null
     * @param email
     * @param username
     * @param password
     * @return
     */
    private fun checkInputs(email: String, username: String, password: String, confirmPassword: String): Boolean {
        Log.d(TAG, "checkInputs: checking inputs for null values")
        if (email == "" || username == "" || password == "" || confirmPassword == "") {
            Toast.makeText(mContext, "All fields must be filled out", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    private fun showProgressBar() {
        mProgressBar?.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        mProgressBar?.visibility = View.GONE
    }

    private fun initProgressBar() {
        mProgressBar = findViewById(R.id.progressBar)
        mProgressBar?.visibility = View.INVISIBLE
    }


    private fun hideSoftKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    /*
    ---------------------------Firebase-----------------------------------------
     */


    private fun setupFirebaseAuth() {

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser

            if (user != null) {
                // User is authenticated
                Log.d(TAG, "onAuthStateChanged: signed_in: " + user.uid)

            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged: signed_out")

            }
        }

    }

    /**
     * Register a new email and password to Firebase Authentication
     * @param email
     * @param password
     */
    fun registerNewEmail(email: String, password: String) {

        showProgressBar()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                Log.d(TAG, "registerNewEmail: onComplete: " + task.isSuccessful)

                if (task.isSuccessful) {
                    //send email verificaiton
                    sendVerificationEmail()

                    //add user details to firebase database
                    addNewUser()
                }
                if (!task.isSuccessful) {
                    Toast.makeText(
                        mContext, "Someone with that email already exists",
                        Toast.LENGTH_SHORT
                    ).show()
                    hideProgressBar()

                }
                hideProgressBar()
                // ...
            }
    }

    /**
     * Adds data to the node: "users"
     */
    private fun addNewUser() {

        //add data to the "users" node
        val userid = FirebaseAuth.getInstance().currentUser!!.uid

        Log.d(TAG, "addNewUser: Adding new User: \n user_id:$userid")
        mUser?.setName(name!!)
        mUser?.setUser_id(userid)

        val reference = FirebaseDatabase.getInstance().reference
        

        //insert into users node
        reference.child(getString(R.string.node_users))
            .child(userid)
            .setValue(mUser)

        FirebaseAuth.getInstance().signOut()
        redirectLoginScreen()
    }

    /**
     * sends an email verification link to the user
     */
    fun sendVerificationEmail() {
        val user = FirebaseAuth.getInstance().currentUser

        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {

            } else {
                Toast.makeText(mContext, "couldn't send email", Toast.LENGTH_SHORT).show()
                hideProgressBar()
            }
        }

    }

    /**
     * Redirects the user to the login screen
     */
    private fun redirectLoginScreen() {
        Log.d(TAG, "redirectLoginScreen: redirecting to login screen.")
        Toast.makeText(applicationContext,"Registration Succesfull please verify your email",Toast.LENGTH_LONG).show()
        val intent = Intent(this@Register, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    public override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener!!)
    }

    public override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener!!)
        }
    }
}
