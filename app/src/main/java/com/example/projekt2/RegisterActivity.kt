package com.example.projekt2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private val db = FirebaseFirestore.getInstance()

    private var isLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        val sharedPreferences = getSharedPreferences("AppData", Context.MODE_PRIVATE)
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if(isLoggedIn) {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            toolbar.visibility = View.VISIBLE
            setSupportActionBar(toolbar)
        }

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)


        val registerButton: Button = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                saveUserToFirestore(username, password)
                Toast.makeText(this, "Registrering lyckad!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Fyll i både användarnamn och lösenord", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserToFirestore(username: String, password: String) {
        val user = hashMapOf(
            "username" to username,
            "password" to password
        )

        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                val userIdInFirestore = documentReference.id
                Toast.makeText(
                    this,
                    "Användaren har lagts till i Firestore med ID: $userIdInFirestore",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Kunde inte lägga till användaren i Firestore: $e",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menu_form ->{
                val intent = Intent(this, FormActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> {
                Log.d("Navigation", "Unknown menu item clicked: ${item.itemId}")
                return super.onOptionsItemSelected(item)
            }
        }
    }
}