package com.idbsoftek.vms.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object RuntimePermissionUtil {
    const val PHONE_STATE_READ_CODE = 1
    const val ACCESS_LOCATION_CODE = 2
    const val ALL_PERM_CODE = 99

    private val locPermissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

    private val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION)

    fun reqDeviceStatePermission(activity: Activity) {
        val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE)
        ActivityCompat.requestPermissions(
                activity, permissions, PHONE_STATE_READ_CODE
        )
    }

    fun isDeviceStateCanBeRead(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
    }

    //REQ LOCATION

    fun reqLocation(activity: Activity) {
        ActivityCompat.requestPermissions(
                activity, locPermissions, ACCESS_LOCATION_CODE
        )
    }

    fun isLocationAccessGiven(context: Context): Boolean {
        var isGiven = false
        for (perm in locPermissions) {
            isGiven = ContextCompat.checkSelfPermission(
                    context,
                    perm
            ) == PackageManager.PERMISSION_GRANTED
            if (!isGiven)
                break
        }
        return isGiven
    }

    fun isDeviceNeedsRunTimePermission(): Boolean {
        var requiresPerm = false

        if (Build.VERSION.SDK_INT >= 23) {
            requiresPerm = true
        }

        return requiresPerm
    }

    fun isHasPermissions(context: Context): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    fun reqAllPermissions(context: Activity) {
        ActivityCompat.requestPermissions(context, permissions, ALL_PERM_CODE)
    }
}
