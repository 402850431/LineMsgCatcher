package com.example.linemsgcatch.ui.normal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.linemsgcatch.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    companion object {
        const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initOnclick()
    }

    private fun initOnclick() {
        val email = et_email.text.toString()
        val password = et_password.text.toString()

        img_profile_pic.setOnClickListener {

        }

        btn_register.setOnClickListener {
            register(email, password)
        }

        btn_login.setOnClickListener {
            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) return

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {

            }
            .addOnFailureListener {

            }
    }

    private fun register(email: String, password: String) {

        if (email.isEmpty() || password.isEmpty()) return

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "register succeed.", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "create user uid = ${it.result?.user?.uid}")
                    val intent = Intent(this, MainActivity::class.java)
//                intent.addFlags()
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "register failed. ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
