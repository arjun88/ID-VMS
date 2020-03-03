package com.idbsoftek.vms.setup

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

import com.idbsoftek.vms.util.PrefUtil
import com.idbsoftek.vms.setup.dashboard.VMSDashboardActivity
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.login.CompCodeEnterActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefUtil = PrefUtil(this)

        Handler().postDelayed({
             if (prefUtil.isLoggedIn!!)
                 moveToDashboard()
             else
            moveToLoginScreen()
        }, 2500)
    }

    private fun moveToLoginScreen() {
        startActivity(
            Intent(
                this@MainActivity,
                CompCodeEnterActivity::class.java
            )
        )
        finish()
    }

    private fun moveToDashboard() {
        startActivity(
            Intent(
                this@MainActivity,
                VMSDashboardActivity::class.java
            )
        )
        finish()
    }
}
