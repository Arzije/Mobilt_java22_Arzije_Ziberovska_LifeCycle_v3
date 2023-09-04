package com.example.projekt2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        val loginButton: Button = findViewById(R.id.loginButton)
        val registerButton: Button = findViewById(R.id.registerButton)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Hämta användarinformation från Firestore baserat på användarnamnet
            db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val userDocument = documents.documents[0]
                        val savedPassword = userDocument.getString("password")

                        if (savedPassword == password) {
                            val userId = userDocument.id

                            val intent = Intent(this, MainActivity2::class.java)
                            intent.putExtra("userId", userId)
                            startActivity(intent)
                        } else {
                            // Visa felmeddelande om inloggningen misslyckades
                            Toast.makeText(this, "Ogiltiga inloggningsuppgifter", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Visa felmeddelande om användaren inte hittades
                        Toast.makeText(this, "Användaren hittades inte", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Kunde inte hämta användarinformation: $e", Toast.LENGTH_SHORT).show()
                }
        }


        registerButton.setOnClickListener {
            // Hämta användarens inmatning
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Kontrollera att användaren har fyllt i både användarnamn och lösenord
            if (username.isNotEmpty() && password.isNotEmpty()) {
                saveUserToFirestore(username, password)
                // Registrera användaren (här kan du lägga till Firestore-sparning om så önskas)
                Toast.makeText(this, "Registrering lyckad!", Toast.LENGTH_SHORT).show()
            } else {
                // Visa felmeddelande om användaren inte fyllt i båda fälten
                Toast.makeText(this, "Fyll i både användarnamn och lösenord", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserToFirestore(username: String, password: String) {
        // Skapa ett nytt dokument med användarens information i Firestore
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
                Log.d("Usersp", "User sparad")

            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Kunde inte lägga till användaren i Firestore: $e",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
