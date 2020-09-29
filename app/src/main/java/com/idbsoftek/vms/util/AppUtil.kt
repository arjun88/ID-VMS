package com.idbsoftek.vms.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import com.idbsoftek.vms.setup.login.CompCodeEnterActivity

class AppUtil {

    private fun turnGPSOff(context: Context) {
        val provider = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.LOCATION_PROVIDERS_ALLOWED
        )

        if (provider.contains("gps")) { //if gps is enabled
            val poke = Intent()
            poke.setClassName(
                "com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider"
            )
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE)
            poke.data = Uri.parse("3")
            context.sendBroadcast(poke)
        }
    }

    companion object {
        var BASE_URL = "http://idpaytek.idbssoftware.com/api/"

        val APP_BASE_URL_FOR_VMS = PrefUtil.getBaseUrl()
           // "http://192.168.20.112/IDVMS/api/" //PrefUtil.getBaseUrl() //"http://192.168.20.121/IDVMS/api/"
        val IMG_BASE_URL_FOR_VMS =
               PrefUtil.getVmsImageBaseUrl()//"http://192.168.20.112/IDVMS/" // PrefUtil.getVmsImageBaseUrl() //""http://192.168.20.121/IDVMS/"

        val VMS_ADMIN = "320339"//PrefUtil.geVmsEmpID()// "320339"
        val EMP_ID_VMS =
            PrefUtil.geVmsEmpID() //"320112"//VMS_ADMIN  320339 - Saniya 320112 - Arjun 369852 - Shweta

        @SuppressLint("HardwareIds")
        fun getImeiNumberOfDevice(context: Context): String {
            var deviceID = ""

            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "OK"
            }

            deviceID = try {
                telephonyManager.deviceId
            } catch (e: Exception){
                Settings.Secure.getString(context.contentResolver,
                    Settings.Secure.ANDROID_ID)
            }

            return deviceID
        }

        fun getAppVersion(context: Context): String {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val appVersion: String

            appVersion =
                pInfo.versionName
            return appVersion
        }

        @SuppressLint("DefaultLocale")
        fun getDeviceName(): String? {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }
        }

        private fun capitalize(s: String?): String {
            if (s == null || s.isEmpty()) {
                return ""
            }
            val first = s[0]
            return if (Character.isUpperCase(first)) {
                s
            } else {
                Character.toUpperCase(first).toString() + s.substring(1)
            }
        }

        fun getDeviceInfo(context: Context): DeviceInfo? {
            val deviceInfo: DeviceInfo?

            deviceInfo = DeviceInfo(
                Build.MANUFACTURER,
                getDeviceName(),
                getImeiNumberOfDevice(context),
                Build.VERSION.RELEASE,
                //"1.0"
                getAppVersion(context)
            )
            return deviceInfo
        }

        val isDeviceNeedsRunTimePermission: Boolean
            get() = Build.VERSION.SDK_INT >= 23

        fun isInternetThere(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnectedOrConnecting
        }

        fun isGPSSEnabled(context: Context): Boolean {
            val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

        fun turnGPSOn(context: Context) {
            val gpsOptionsIntent = Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS
            )
            context.startActivity(gpsOptionsIntent)
        }

        // STATUS CHECKING OF RECORDS ***********

        fun getStatus(status: String): String {
            var statusToDisplay = "NA"
            val statusSplit = status.split("-")

            if(statusSplit[0] == statusSplit[1]){
                statusToDisplay = statusSplit[0]
            }
            else if((statusSplit[0]=="Rejected") || (statusSplit[1]=="Rejected") ){
                statusToDisplay = "Rejected"
            }
            else if((statusSplit[0]=="Pending") || (statusSplit[1]=="Pending")){
                statusToDisplay = "Pending"
            }
            else if((statusSplit[0]=="Approved") && (statusSplit[1]=="Approved")){
                statusToDisplay = "Approved"
            }
            return statusToDisplay
        }

        fun onSessionOut(context: Activity){
            val dialogUtil: DialogUtil? = DialogUtil(context)
            dialogUtil!!.showToast("Session Out!")
            val otpIntent = Intent(context, CompCodeEnterActivity::class.java)
            context.startActivity(otpIntent)
            context.finishAffinity()
        }
        // *************************************
    }
}
