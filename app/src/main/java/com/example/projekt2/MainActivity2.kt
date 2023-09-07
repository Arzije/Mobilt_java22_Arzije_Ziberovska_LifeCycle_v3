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
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity2 : AppCompatActivity() {
    private lateinit var ageEditText: EditText
    private lateinit var drivingLicenseCheckBox: CheckBox
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var emailEditText: EditText
    private val db = FirebaseFirestore.getInstance()

    private var userId: String? = null

    private var isLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val sharedPreferences = getSharedPreferences("AppData", Context.MODE_PRIVATE)
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if(isLoggedIn) {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            toolbar.visibility = View.VISIBLE
            setSupportActionBar(toolbar)
        }

        ageEditText = findViewById(R.id.ageEditText)
        drivingLicenseCheckBox = findViewById(R.id.drivingLicenseCheckBox)
        genderRadioGroup = findViewById(R.id.genderRadioGroup)
        emailEditText = findViewById(R.id.emailEditText)

        val submitButton: Button = findViewById(R.id.submitButton)

        userId = intent.getStringExtra("userId")

        submitButton.setOnClickListener {
            val age = ageEditText.text.toString()
            val hasDrivingLicense = drivingLicenseCheckBox.isChecked

            val selectedGenderId = genderRadioGroup.checkedRadioButtonId
            val selectedGender: String = when (selectedGenderId) {
                R.id.maleRadioButton -> "Manlig"
                R.id.femaleRadioButton -> "Kvinnlig"
                R.id.otherRadioButton -> "Annat"
                else -> ""
            }

            val email = emailEditText.text.toString()

            saveUserDataToFirestore(userId, age, hasDrivingLicense, selectedGender, email)

            val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("age", age)
            editor.putBoolean("hasDrivingLicense", hasDrivingLicense)
            editor.putString("gender", selectedGender)
            editor.putString("email", email)
            editor.apply()
        }
    }

    private fun saveUserDataToFirestore(
        userId: String?,
        age: String,
        hasDrivingLicense: Boolean,
        gender: String,
        email: String
    ) {
        val userData: MutableMap<String, Any> = hashMapOf(
            "age" to age,
            "hasDrivingLicense" to hasDrivingLicense,
            "gender" to gender,
            "email" to email
        )

        if (userId != null) {
            db.collection("users")
                .document(userId)
                .update(userData)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Datan har sparats i Firestore för dokument med ID: $userId",
                        Toast.LENGTH_SHORT
                    ).show()

                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Kunde inte spara datan i Firestore: $e",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> {
                val intent = Intent(this, MainActivity4::class.java)
                startActivity(intent)
                return true
            }
            R.id.menu_register ->{
                val intent = Intent(this, MainActivity3::class.java)
                startActivity(intent)
                return true
            }
            else -> {
                Log.d("Navigation", "Unknown menu item clicked: ${item.itemId}")
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val age = sharedPreferences.getString("age", "")
        val hasDrivingLicense = sharedPreferences.getBoolean("hasDrivingLicense", false)
        val gender = sharedPreferences.getString("gender", "")
        val email = sharedPreferences.getString("email", "")

        ageEditText.setText(age)
        drivingLicenseCheckBox.isChecked = hasDrivingLicense

        when (gender) {
            "Manlig" -> genderRadioGroup.check(R.id.maleRadioButton)
            "Kvinnlig" -> genderRadioGroup.check(R.id.femaleRadioButton)
            "Annat" -> genderRadioGroup.check(R.id.otherRadioButton)
        }

        emailEditText.setText(email)
    }


}

