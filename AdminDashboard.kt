package com.example.homify

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView

// AdminDashboard activity: dashboard for all admin functions
class AdminDashboard : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var adminDrawer: DrawerLayout
    private lateinit var adminFragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // Show the action bar
        supportActionBar?.show()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // Retrieve the user ID from the intent
        val uid = this.intent.getStringExtra("uid")

        // Initialize the DrawerLayout and NavigationView
        adminDrawer = findViewById(R.id.admin_drawer_layout)
        val adminNavView: NavigationView = findViewById(R.id.admin_nav_view)

        // Setting the ActionBarDrawerToggle to handle open and close events of the drawer
        val adminToggle = ActionBarDrawerToggle(
            this, adminDrawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        adminDrawer.addDrawerListener(adminToggle)
        adminToggle.syncState()

        // Enable the home button in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set the navigation item selected listener
        adminNavView.setNavigationItemSelectedListener(this)

        // Load the default fragment when the activity starts
        if (savedInstanceState == null) {
            loadFragment(ViewFeedbacks(), uid)
            adminNavView.setCheckedItem(R.id.feedback_nav)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val uid = this.intent.getStringExtra("uid")

        // Handling navigation link for each item click
        when (item.itemId) {
            R.id.feedback_nav -> {
                loadFragment(ViewFeedbacks(), uid)
            }
            R.id.landlord_nav -> {
                loadFragment(ViewLandlords(), uid)
            }
            R.id.addLandlord_nav -> {
                loadFragment(New_Landlord(), uid)
            }
            R.id.logout_nav -> {
                logout()
            }
        }
        // Close the navigation drawer
        adminDrawer.closeDrawer(GravityCompat.START)
        return true
    }

    // Function to load a fragment and pass the user ID as an argument
    private fun loadFragment(fragment: Fragment, uid: String?) {
        val args = Bundle()
        args.putString("uid", uid)
        fragment.arguments = args

        adminFragmentManager = supportFragmentManager
        adminFragmentManager.beginTransaction().replace(R.id.adminFragmentContainer, fragment).commit()
    }

    // Handle back button press: closes the drawer if open
    override fun onBackPressed() {
        if (adminDrawer.isDrawerOpen(GravityCompat.START)) {
            adminDrawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // Handle options menu item selections
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            if (adminDrawer.isDrawerOpen(GravityCompat.START)) {
                adminDrawer.closeDrawer(GravityCompat.START)
            } else {
                adminDrawer.openDrawer(GravityCompat.START)
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
