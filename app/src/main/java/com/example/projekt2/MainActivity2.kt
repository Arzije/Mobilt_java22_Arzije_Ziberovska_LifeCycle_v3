package com.example.projekt2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        ageEditText = findViewById(R.id.ageEditText)
        drivingLicenseCheckBox = findViewById(R.id.drivingLicenseCheckBox)
        genderRadioGroup = findViewById(R.id.genderRadioGroup)
        emailEditText = findViewById(R.id.emailEditText)

        val submitButton: Button = findViewById(R.id.submitButton)

        userId = intent.getStringExtra("userId") // Hämta användarens dokument-ID från föregående aktivitet

        submitButton.setOnClickListener {
            val age = ageEditText.text.toString()
            val hasDrivingLicense = drivingLicenseCheckBox.isChecked

            // Hämta valt kön från RadioButton
            val selectedGenderId = genderRadioGroup.checkedRadioButtonId
            val selectedGender: String = when (selectedGenderId) {
                R.id.maleRadioButton -> "Manlig"
                R.id.femaleRadioButton -> "Kvinnlig"
                R.id.otherRadioButton -> "Annat"
                else -> ""
            }

            val email = emailEditText.text.toString()

            // Spara datan till användarens dokument i Firestore
            saveUserDataToFirestore(userId, age, hasDrivingLicense, selectedGender, email)

            val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("age", age)
            editor.putBoolean("hasDrivingLicense", hasDrivingLicense)
            editor.putString("gender", selectedGender)
            editor.putString("email", email)
            editor.apply()


            // Exempel på navigering tillbaka till MainActivity:
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
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
        Log.d("Navigation", "onCreateOptionsMenu called")
        menuInflater.inflate(R.menu.navigation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> {
                Log.d("Navigation", "Home menu item clicked")

                // Navigera tillbaka till MainActivity eller annan aktivitet/fragment
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            // Lägg till fler menyelement här om du behöver navigera till andra aktiviteter
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

        // Uppdatera dina UI-komponenter med de sparade värdena
        ageEditText.setText(age)
        drivingLicenseCheckBox.isChecked = hasDrivingLicense

        // ... (Uppdatera övriga UI-komponenter här)

        when (gender) {
            "Manlig" -> genderRadioGroup.check(R.id.maleRadioButton)
            "Kvinnlig" -> genderRadioGroup.check(R.id.femaleRadioButton)
            "Annat" -> genderRadioGroup.check(R.id.otherRadioButton)
        }

        emailEditText.setText(email)
    }


}

