package com.example.projekt2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar

class MainActivity4 : AppCompatActivity() {

    private var isLoggedIn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        val sharedPreferences = getSharedPreferences("AppData", Context.MODE_PRIVATE)
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        val userId = intent.getStringExtra("userId")

        if (isLoggedIn || userId != null) {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            toolbar.visibility = View.VISIBLE
            setSupportActionBar(toolbar)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_form -> {
                val intent = Intent(this, MainActivity2::class.java)
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
}