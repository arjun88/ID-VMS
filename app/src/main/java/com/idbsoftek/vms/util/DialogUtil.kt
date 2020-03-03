package com.idbsoftek.vms.util

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.login.CompCodeEnterActivity

class DialogUtil(val context: Context) {
    fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun showLogoutPopUp(context: Context) {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(R.layout.logout_pop_up)
            dialog.findViewById<View>(R.id.yes_logout_btn).setOnClickListener {
                dialog.dismiss()
                val prefUtil = PrefUtil(context)
                prefUtil.clearData()

                val otpIntent = Intent(context, CompCodeEnterActivity::class.java)
                context.startActivity(otpIntent)
            }

            dialog.findViewById<View>(R.id.no_logout_btn)
                .setOnClickListener {
                    dialog.dismiss()
                }

            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.show()
            dialog.window!!.attributes = lp
        }
    }
}