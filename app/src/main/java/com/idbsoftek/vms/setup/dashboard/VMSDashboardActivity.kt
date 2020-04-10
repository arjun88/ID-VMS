package com.idbsoftek.vms.setup.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.analytics.VmsAnalyticsActivity
import com.idbsoftek.vms.setup.form.VisitReqFormActivity
import com.idbsoftek.vms.setup.log_list.VMSLogListActivity
import com.idbsoftek.vms.setup.profile.ProfileActivity
import com.idbsoftek.vms.util.PrefUtil

class VMSDashboardActivity : AppCompatActivity(), DashboardItemClickable {
    override fun onClickItem(type: Int?, menu: String?) {
        when (menu) {
            "Visitor Log" -> {
                moveToVisitorLogScreen()
            }

            "Add Visitor" -> {
                moveToReqScreen()
            }

            "Self Approval" -> {
                moveToReqScreen()
            }

            "Analytics" -> {
                moveToAnalyticsScreen()
            }


        }


    }

    private var dashboardRV: RecyclerView? = null
    private var isSecurity: Boolean = false

    private var activity: VMSDashboardActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vms_activity_dashboard)
        dashboardRV = findViewById(R.id.dashboard_grid_rv)
        dashboardRV!!.layoutManager = GridLayoutManager(this@VMSDashboardActivity, 2)

        activity = this

        var pref = PrefUtil(this)

        Log.e("ROLE", "VMS: " + PrefUtil.getVmsEmpROle())

        // setUpDashboardForMgr()
        when {
            PrefUtil.getVmsEmpROle() == "admin" -> setUpDashboardForAdmin()
            PrefUtil.getVmsEmpROle() == "security" -> {
                setUpDashboardForSecurity()
            }
            else -> {
                setUpDashboardForMgr()
            }
        }
        //setUpDashboardForSecurity()
    }

    ///////// ******** Dashboard for ADMIN ***********

    private fun setUpDashboardForAdmin() {
        //   PrefUtil.saveVmsEmpRole("admin")
        isSecurity = true
        val dashboardMenuList = ArrayList<DashboardMenu>()

        var menu: DashboardMenu?

        menu = DashboardMenu(
            "Analytics",
            R.drawable.ic_analytics,
            0
        )
        dashboardMenuList.add(menu)

        val adapter =
            VMSDashboardGridAdapter(
                dashboardMenuList, this
            )

        dashboardRV!!.adapter = adapter
        dashboardRV!!.setHasFixedSize(true)
    }

///////// ******** Dashboard for security ***********

    private fun setUpDashboardForSecurity() {
        // PrefUtil.saveVmsEmpRole("security")
        isSecurity = true
        val dashboardMenuList = ArrayList<DashboardMenu>()

        var menu: DashboardMenu?

        menu = DashboardMenu(
            "Visitor Log",
            R.drawable.ic_body_scan,
            0
        )
        dashboardMenuList.add(menu)

        menu = DashboardMenu(
            "Add Visitor",
            R.drawable.ic_policeman,
            0
        )
        dashboardMenuList.add(menu)

        val adapter =
            VMSDashboardGridAdapter(
                dashboardMenuList, this
            )

        dashboardRV!!.adapter = adapter
        dashboardRV!!.setHasFixedSize(true)
    }

    private fun setUpDashboardForMgr() {
        //   PrefUtil.saveVmsEmpRole("approver")
        isSecurity = false
        val dashboardMenuList = ArrayList<DashboardMenu>()

        var menu: DashboardMenu?

        menu = DashboardMenu(
            "Visitor Log",
            R.drawable.ic_visitor_log,
            0
        )
        dashboardMenuList.add(menu)

        menu = DashboardMenu(
            "Self Approval",
            R.drawable.ic_self_approval,
            0
        )
        dashboardMenuList.add(menu)
        val adapter =
            VMSDashboardGridAdapter(
                dashboardMenuList, this
            )

        dashboardRV!!.adapter = adapter
        dashboardRV!!.setHasFixedSize(true)
    }

    private fun moveToVisitorLogScreen() {
        val intent = Intent(
            this,
            VMSLogListActivity::class.java
        )
        intent.putExtra("IS_SECURITY", isSecurity)
        startActivity(intent)
    }

    private fun moveToAnalyticsScreen() {
        val intent = Intent(
            this,
            VmsAnalyticsActivity::class.java
        )
        startActivity(intent)
    }

    private fun moveToReqScreen() {
        val intent = Intent(
            this,
            VisitReqFormActivity::class.java
        )
        if (PrefUtil.getVmsEmpROle() == "approver") {
            intent.putExtra("SELF_APPROVAL", true)
        }

        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.dashboard, menu)
        return true
    }

    private fun moveToProfile() {
        val profileIntent = Intent(activity, ProfileActivity::class.java)
        startActivity(profileIntent)
    }

    /*  private fun moveToNotifications() {
          val dashboardIntent = Intent(this@DashboardActivity, NotificationActivity::class.java)
          startActivity(dashboardIntent)
      }*/


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notification -> {
                //moveToNotifications()
                true
            }

            R.id.action_profile -> {
                //   if (PrefUtil.getVmsEmpROle() == "approver")
                /*  if (PrefUtil.getVmsEmpROle() == "admin") {
                      Toast.makeText(
                          activity,
                          "Profile data not found.",
                          Toast.LENGTH_SHORT
                      ).show()
                  } else {*/
                moveToProfile()
                // }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
