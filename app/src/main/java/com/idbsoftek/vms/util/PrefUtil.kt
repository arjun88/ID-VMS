package com.idbsoftek.vms.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class PrefUtil(private val context: Context) {

    private val prefEditor: SharedPreferences.Editor?
        get() {
            if (editor == null) {
                sharedPreferences = getAppPreference(context)
                if (sharedPreferences != null) {
                    editor = sharedPreferences!!.edit()
                }
            }
            return editor
        }
    private var staticContext: Context? = null

    init {
        sharedPreferences = getAppPreference(context)
        staticContext = context
    }

    //MODULES

    val vmsModule: Boolean?
        get() = sharedPreferences!!.getBoolean(
            "VMS_MODULE",
            false
        )

    fun saveVmsAccess(canBeShown: Boolean) {
        prefEditor!!.putBoolean("VMS_MODULE", canBeShown)
        prefEditor!!.apply()
    }

    val attendanceModule: Boolean?
        get() = sharedPreferences!!.getBoolean(
            "ATT_MODULE",
            false
        )

    fun saveAttendanceAccess(canBeShown: Boolean) {
        prefEditor!!.putBoolean("ATT_MODULE", canBeShown)
        prefEditor!!.apply()
    }

    val leaveModule: Boolean?
        get() = sharedPreferences!!.getBoolean(
            "LEAVE_MODULE",
            false
        )

    fun saveLeaveAccess(canBeShown: Boolean) {
        prefEditor!!.putBoolean("LEAVE_MODULE", canBeShown)
        prefEditor!!.apply()
    }

    val payRollModule: Boolean?
        get() = sharedPreferences!!.getBoolean(
            "PAYROLL_MODULE",
            false
        )

    fun savePayRollAccess(canBeShown: Boolean) {
        prefEditor!!.putBoolean("PAYROLL_MODULE", canBeShown)
        prefEditor!!.apply()
    }

    val approver: Boolean?
        get() = sharedPreferences!!.getBoolean(
            "APPROVER",
            false
        )

    fun saveApproveAccess(canBeShown: Boolean) {
        prefEditor!!.putBoolean("APPROVER", canBeShown)
        prefEditor!!.apply()
    }

    // APP URL

    val appBaseUrl: String?
        get() = sharedPreferences!!.getString(
            "APP_BASE_URL",
            "http://idepl.idpaytek.in/idpaytekapi/api/"
        )

    fun saveAppBaseUrl(appBaseUrl: String) {
        val urlBase = "${appBaseUrl}api/"
        prefEditor!!.putString("APP_BASE_URL", urlBase)
        AppUtil.BASE_URL = appBaseUrl
        prefEditor!!.apply()
    }

    val sessionID: String?
        get() = sharedPreferences!!.getString(
            "SESSION_ID",
            "1234"
        )

    fun saveSessionID(sessionID: String) {
        prefEditor!!.putString("SESSION_ID", sessionID)
        prefEditor!!.apply()
    }

    fun saveUserName(username: String) {
        prefEditor!!.putString("UserName", username)
        prefEditor!!.apply()
    }

    fun saveUserType(userType: String) {
        prefEditor!!.putString("UserType", userType)
        prefEditor!!.apply()
    }

    fun getUserType(): String {
        return sharedPreferences!!.getString("UserType", "")!!
    }

    var userName: String? = null
        get() = sharedPreferences!!.getString("UserName", "")

    fun saveLogin(isLoggedIn: Boolean) {
        prefEditor!!.putBoolean("LOGGED_IN", isLoggedIn)
        prefEditor!!.apply()
    }

    var isLoggedIn: Boolean? = null
        get() = sharedPreferences!!.getBoolean("LOGGED_IN", false)

    fun clearData() {
        prefEditor!!.putBoolean("LOGGED_IN", false)
        prefEditor!!.apply()
    }

    var fcmKey: String? = null
        get() = sharedPreferences!!.getString("FCM_KEY", "fcm")

    fun saveFcmKey(fcmKey: String) {
        prefEditor!!.putString("FCM_KEY", fcmKey)
        prefEditor!!.apply()
    }

    var empName: String? = null
        get() = sharedPreferences!!.getString("EMP_NAME", "fcm")

    fun saveEmpName(empName: String) {
        prefEditor!!.putString("EMP_NAME", empName)
        prefEditor!!.apply()
    }

    var empMob: String? = null
        get() = sharedPreferences!!.getString("EMP_MOB", "")

    fun saveEmpMob(empMob: String) {
        prefEditor!!.putString("EMP_MOB", empMob)
        prefEditor!!.apply()
    }

    fun saveAnalyticsShown(shown: Boolean) {
        prefEditor!!.putBoolean("ANALYTICS_VMS", shown)
        prefEditor!!.apply()
    }

    fun isAnalyticsShown(): Boolean {
        return sharedPreferences!!.getBoolean("ANALYTICS_VMS", false)
    }

    fun saveSecurityShown(shown: Boolean) {
        prefEditor!!.putBoolean("SECURITY_VMS", shown)
        prefEditor!!.apply()
    }

    fun isSecurityShown(): Boolean {
        return sharedPreferences!!.getBoolean("SECURITY_VMS", false)
    }

    // Version 2.0

    // Visitor Image Mandatory

    fun saveVisitorImgReq(req: Boolean) {
        prefEditor!!.putBoolean("VISITOR_IMG_REQ", req)
        prefEditor!!.apply()
    }

    fun isVisitorImgReq(): Boolean {
        return sharedPreferences!!.getBoolean("VISITOR_IMG_REQ", false)
    }

    // Associate Info Mandatory

    fun saveAssociateInfoReq(req: Boolean) {
        prefEditor!!.putBoolean("ASS_INFO_REQ", req)
        prefEditor!!.apply()
    }

    fun isAssociateInfoReq(): Boolean {
        return sharedPreferences!!.getBoolean("ASS_INFO_REQ", false)
    }

    // Associate Image Mandatory

    fun saveAssociateImgReq(req: Boolean) {
        prefEditor!!.putBoolean("ASS_IMG_REQ", req)
        prefEditor!!.apply()
    }

    fun isAssociateImgReq(): Boolean {
        return sharedPreferences!!.getBoolean("ASS_IMG_REQ", false)
    }

    // Associate Info Mandatory

    fun saveMeetCompleteReq(req: Boolean) {
        prefEditor!!.putBoolean("MEET_COMP_REQ", req)
        prefEditor!!.apply()
    }

    fun isMeetCompleteReq(): Boolean {
        return sharedPreferences!!.getBoolean("MEET_COMP_REQ", false)
    }

    fun saveApiToken(token: String) {
        prefEditor!!.putString("API_TOKEN", token)
        prefEditor!!.apply()
    }

    fun getApiToken(): String {
        return sharedPreferences!!.getString("API_TOKEN","")!!
    }

    companion object {
        private var sharedPreferences: SharedPreferences? = null

        private var editor: SharedPreferences.Editor? = null
        var staticCon: Context? = null

        fun getAppPreference(context: Context): SharedPreferences? {
            staticCon = context
            if (sharedPreferences == null) {
                sharedPreferences = context.getSharedPreferences(
                    "ID-VMS",
                    Context.MODE_PRIVATE
                )
            }
            return sharedPreferences
        }

        @SuppressLint("CommitPrefEdits")
        fun saveBaseUrl(url: String) {
            editor = sharedPreferences!!.edit()
            val urlBase = "${url}api/"
            editor!!.putString("BASE_URL", urlBase)
            editor!!.apply()
        }

        fun getBaseUrl(): String {
            return sharedPreferences!!.getString(
                "BASE_URL",
                "http://idepl.idpaytek.in/idpaytekapi/api/"
            )!!
        }

        @SuppressLint("CommitPrefEdits")
        fun saveVmsImageBaseUrl(url: String) {
            editor = sharedPreferences!!.edit()
            // val urlBase = "${url}/api/"
            editor!!.putString("VMS_IMG_BASE_URL", url)
            editor!!.apply()
        }

        fun getVmsImageBaseUrl(): String {
            return sharedPreferences!!.getString(
                "VMS_IMG_BASE_URL",
                "http://idepl.idpaytek.in/idpaytekapi/api/"
            )!!
        }

        @SuppressLint("CommitPrefEdits")
        fun saveVmsEmpRole(role: String) {
            editor = sharedPreferences!!.edit()
            // val urlBase = "${url}/api/"
            editor!!.putString("VMS_EMP_ROLE", role)
            editor!!.apply()
        }

        fun getVmsEmpROle(): String {
            return sharedPreferences!!.getString("VMS_EMP_ROLE", "")!!
        }

        //

        @SuppressLint("CommitPrefEdits")
        fun saveEmpName(role: String) {
            editor = sharedPreferences!!.edit()
            // val urlBase = "${url}/api/"
            editor!!.putString("EMP_NAME", role)
            editor!!.apply()
        }

        fun getEmpName(): String {
            return sharedPreferences!!.getString("EMP_NAME", "")!!
        }

        @SuppressLint("CommitPrefEdits")
        fun savePosOfCategoryVMS(pos: Int) {
            editor = sharedPreferences!!.edit()
            // val urlBase = "${url}/api/"
            editor!!.putInt("CAT_POS", pos)
            editor!!.apply()
        }

        fun getCatPosVMS(): Int {
            return sharedPreferences!!.getInt("CAT_POS", 0)
        }

        @SuppressLint("CommitPrefEdits")
        fun savePosOfIdVMS(pos: Int) {
            editor = sharedPreferences!!.edit()
            // val urlBase = "${url}/api/"
            editor!!.putInt("ID_POS", pos)
            editor!!.apply()
        }

        fun getIdPosVMS(): Int {
            return sharedPreferences!!.getInt("ID_POS", 0)
        }

        @SuppressLint("CommitPrefEdits")
        fun saveImageOptional(optional: Boolean) {
            editor = sharedPreferences!!.edit()
            // val urlBase = "${url}/api/"
            editor!!.putBoolean("OPTIONAL_IMG", optional)
            editor!!.apply()
        }

        fun isVisitorImgOptional(): Boolean {
            return sharedPreferences!!.getBoolean("OPTIONAL_IMG", false)

        }

        //SELF APPROVAL

        @SuppressLint("CommitPrefEdits")
        fun saveSelfApproval(optional: Boolean) {
            editor = sharedPreferences!!.edit()
            // val urlBase = "${url}/api/"
            editor!!.putBoolean("SELF_APPROVAL", optional)
            editor!!.apply()
        }

        fun selfApprovalModule(): Boolean {
            return sharedPreferences!!.getBoolean("SELF_APPROVAL", false)

        }

        @SuppressLint("CommitPrefEdits")
        fun savePosOfPurposeVMS(pos: Int) {
            editor = sharedPreferences!!.edit()
            // val urlBase = "${url}/api/"
            editor!!.putInt("PUR_POS", pos)
            editor!!.apply()
        }

        fun getPurposePosVMS(): Int {
            return sharedPreferences!!.getInt("PUR_POS", 0)
        }

        @SuppressLint("CommitPrefEdits")
        fun saveEmpID(empID: String) {
            editor = sharedPreferences!!.edit()
            editor!!.putString("EMP_ID_VMS", empID)
            editor!!.apply()
        }

        fun geVmsEmpID(): String {
            return sharedPreferences!!.getString("EMP_ID_VMS", "")!!
        }

        fun reInitSharedPref() {
            sharedPreferences = getAppPreference(context = staticCon!!)
        }
    }
}
