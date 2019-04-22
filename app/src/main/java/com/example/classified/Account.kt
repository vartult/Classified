package com.example.classified

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

class Account : AppCompatActivity() {

    private var mAuth: FirebaseAuth.AuthStateListener? = null
    private var button: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        button=findViewById(R.id.button)
        setupFirebaseAuth()
        button!!.setOnClickListener( View.OnClickListener {
            FirebaseAuth.getInstance().signOut()
        })
    }
    private fun setupFirebaseAuth() {
        Log.d("Yeah", "setupFirebaseAuth: started")

        mAuth = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                } else {
                    Toast.makeText(this@Account, "Signed Out Succesfully", Toast.LENGTH_SHORT)
                        .show()
                    FirebaseAuth.getInstance().signOut()

                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                }

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
