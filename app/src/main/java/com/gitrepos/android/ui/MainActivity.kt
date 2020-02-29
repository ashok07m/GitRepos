package com.gitrepos.android.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.gitrepos.android.R
import com.gitrepos.android.ui.login.LoggedInUserView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private var loggedInUserView: LoggedInUserView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_repos,
                R.id.navigation_settings
            )
        )
        findViewById<Toolbar>(R.id.toolbar)
            .setupWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.navigation_login -> {
                toolbarContainer.visibility = View.GONE
                nav_view.visibility = View.GONE
            }
            else -> {
                toolbarContainer.visibility = View.VISIBLE
                nav_view.visibility = View.VISIBLE
            }
        }
    }

    fun setLoggedInUser(loggedInUserView: LoggedInUserView) {
        this.loggedInUserView = loggedInUserView
    }

    fun getLoggedInUser(): LoggedInUserView? {
        return loggedInUserView
    }
}
