package com.example.homify

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView

// LandlordDashboard activity: dashboard for all landlords functions
class LandlordDashboard : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landlord_dashboard)

        // Passing the User ID from the LoginActivity to CurrentActivity
        val uid = this.intent.getStringExtra("uid")

        drawer = findViewById(R.id.landlord_drawer_layout)
        val navView: NavigationView = findViewById(R.id.landlord_nav_view)

        //control for the menu dropdown
        val toggle = ActionBarDrawerToggle(
            this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        //enabling actionbar to display menu navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener(this)

        // Load the default fragment when the activity starts
        if (savedInstanceState == null) {
            loadFragment(ViewProperties(), uid)
            navView.setCheckedItem(R.id.properties_nav)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        //fetching the user-id of the current user
        val uid = this.intent.getStringExtra("uid")

        //setting the menu navigation links for each fragment
        when (item.itemId) {
            R.id.properties_nav -> {
                loadFragment(ViewProperties(), uid)
            }
            R.id.addProperties_nav -> {
                loadFragment(NewProperty(), uid)
            }
            R.id.notifications_nav -> {
                loadFragment(ViewNotification(), uid)
            }
            R.id.reset_nav -> {
                loadFragment(PasswordReset(), uid)
            }
            R.id.logout_nav -> {
                logout()
            }
        }
        // Close the navigation drawer
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    // Function to load a fragment and pass the user ID as an argument
    private fun loadFragment(fragment: Fragment, uid: String?) {
        val args = Bundle()
        args.putString("uid", uid)
        fragment.arguments = args

        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.landlordFragmentContainer, fragment).commit()
    }

    // Handle back button press: closes the drawer if open
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // Handle options menu item selections
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            } else {
                drawer.openDrawer(GravityCompat.START)
            }
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    // Function to log out the user and navigate to the main activity
    private fun logout() {
        // Clear the user session
        val user = getSharedPreferences("uid", MODE_PRIVATE)
        val destroySession = user.edit()
        destroySession.clear()
        destroySession.apply()

        // Navigate to MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
