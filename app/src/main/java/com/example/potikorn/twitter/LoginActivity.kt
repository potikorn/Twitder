package com.example.potikorn.twitter

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        btn_login.setOnClickListener {
            login()
        }

        btn_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    override fun onStart() {
        super.onStart()
        loadTweets()
    }

    private fun login() {
        when {
            et_email.text.toString().isEmpty() -> et_email.error = "Error is Empty"
            et_password.text.toString().isEmpty() -> et_password.error = "Error is Empty"
            et_password.text.toString().length < 8 -> et_password.error = "Password must contain at least 8 letter"
            else -> loginToFirebase(et_email.text.toString().trim(), et_password.text.toString().trim())
        }
    }

    private fun loginToFirebase(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Toast.makeText(applicationContext, "Successful login", Toast.LENGTH_LONG).show()
                    loadTweets()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(applicationContext, "Failed to Login :  ${exception.message}", Toast.LENGTH_LONG).show()
                }
    }


    private fun loadTweets() {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", currentUser.email)
            intent.putExtra("uid", currentUser.uid)
            startActivity(intent)
        }
    }

}
