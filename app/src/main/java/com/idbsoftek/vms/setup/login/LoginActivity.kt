package com.idbsoftek.vms.setup.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.idbsoftek.vms.R
import com.idbsoftek.vms.api_retrofit.ApiClient
import com.idbsoftek.vms.api_retrofit.CommonApiResponse
import com.idbsoftek.vms.setup.dashboard.VMSDashboardActivity
import com.idbsoftek.vms.util.AppUtil
import com.idbsoftek.vms.util.DeviceInfo
import com.idbsoftek.vms.util.DialogUtil
import com.idbsoftek.vms.util.PrefUtil
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private var userNameTxtIP: TextInputLayout? = null
    private var pwdTxtIP: TextInputLayout? = null
    private var submitBtn: MaterialButton? = null
    private var activity: LoginActivity? = null
    private var dialogUtil: DialogUtil? = null
    private var prefUtil: PrefUtil? = null

    private var loader: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        activity = this
        prefUtil = PrefUtil(activity!!)
        dialogUtil = DialogUtil(activity!!)

        initView()
    }

    private fun initView() {
        userNameTxtIP = findViewById(R.id.emp_id_txt_ip_login)
        pwdTxtIP = findViewById(R.id.pwd_txt_ip_login)

        submitBtn = findViewById(R.id.login_submit_btn)
        loader = findViewById(R.id.login_progress)

        submitBtn!!.setOnClickListener {
            if (AppUtil.isInternetThere(activity!!)) {
                val uName = userNameTxtIP!!.editText!!.text.toString()
                val pwd = pwdTxtIP!!.editText!!.text.toString()

                when {
                    uName.isEmpty() -> {
                        dialogUtil!!.showToast("Please Provide Username")
                    }
                    pwd.isEmpty() -> {
                        dialogUtil!!.showToast("Please Provide Password")
                    }
                    else -> {
                        //LOGIN API
                        loginApi(uName, pwd)
                    }
                }
            } else {
                dialogUtil!!.showToast("No Internet!")
            }
        }
    }

    private fun moveToDashboardScreen() {
        val intent = Intent(
            activity,
            VMSDashboardActivity::class.java
        )
        activity!!.startActivity(
            intent
        )

        activity!!.finishAffinity()
       // activity!!.finish()
    }

    private fun onLoad() {
        loader!!.visibility = View.VISIBLE
        submitBtn!!.visibility = View.GONE
    }

    private fun afterLoad() {
        loader!!.visibility = View.GONE
        submitBtn!!.visibility = View.VISIBLE
    }

    private fun loginApi(userName: String, pwd: String) {
        onLoad()
        val apiCallable = ApiClient.getRetrofit()!!.create(
            LoginApiCallable::class.java
        )

        val prefUtil = PrefUtil(activity!!)
        val appUrl = PrefUtil.getBaseUrl() + "Accounts/Authenticate"

        Log.e("LOGIN_URL: ", appUrl)

//        val osVer = AppUtil.

        val deviceInfo: DeviceInfo? = AppUtil.getDeviceInfo(this)

        val imei = deviceInfo?.imei
        val osVer = deviceInfo?.osVersion
        val model = deviceInfo?.model
        val appVer = deviceInfo?.appVersion

        val loginPost = LoginPostJson()
        loginPost.appVer = appVer
        loginPost.model = model
        loginPost.deviceId = imei
        loginPost.fcmKey = prefUtil.fcmKey!!
        loginPost.email = userName// "info@idbssoftware.com"//
        loginPost.password = pwd//"admin"//pwd
        loginPost.osVer = osVer

        val gson = Gson()
        val requestBody: RequestBody = RequestBody.create(
            MediaType.parse("application/json")
            , gson.toJson(loginPost)
        )

        apiCallable.login(
            appUrl,
           requestBody
        )
            .enqueue(object : Callback<CommonApiResponse> {
                override fun onResponse(
                    call: Call<CommonApiResponse>,
                    response: Response<CommonApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val loginResponse = response.body()
                          //  if (response.body()?.status == true) {
                                afterLoad()
                                val prefUtil = PrefUtil(activity!!)
                                prefUtil.saveUserName(userName)
                                PrefUtil.saveEmpID(loginResponse!!.id.toString())
                                prefUtil.saveLogin(true)

                               /* val admin = response.body()!!.admin
                                val sec = response.body()!!.security

                                PrefUtil.saveEmpName(response.body()!!.empName!!)
                                PrefUtil.saveImageOptional(response.body()!!.isVisitorImgOptional!!)
                                PrefUtil.saveSelfApproval(response.body()!!.selfApproval!!)*/

                                val role = loginResponse.userType //"admin"

                                /*if (sec == true && admin == false) {
                                    role = "security"
                                } else if (sec == false && admin == true) {
                                    role = "admin"
                                } else {
                                    role = "approver"
                                    prefUtil.saveApproveAccess(true)
                                }*/

                                PrefUtil.saveVmsEmpRole(role!!)
                                prefUtil.saveApiToken(loginResponse.apiToken!!)

                                //******** Set-Up for local URL *********

//                                val appUrl = "http://192.168.20.134/IDVMS/"
//                                PrefUtil.saveBaseUrl(appUrl)
//                                PrefUtil.saveVmsImageBaseUrl(appUrl)

                                //****************************************

                              //  prefUtil.saveSessionID(loginResponse!!.session_id!!)
                               // val msg = response.body()!!.message

                                dialogUtil!!.showToast("Logged In Successfully!")
                                moveToDashboardScreen()

                           /* } else {
                                afterLoad()
                              *//*  val msg = response.body()!!.message
                                dialogUtil!!.showToast(msg!!)*//*
                                dialogUtil!!.showToast("Username or Password is incorrect!")
                            }*/
                        }
                        response.code() == 400 -> {
                            afterLoad()
                            dialogUtil!!.showToast("Username or Password is incorrect!")
                        }
                        response.code() == 500 -> {
                            afterLoad()
                            dialogUtil!!.showToast("Server Error!")
                        }
                    }
                }

                override fun onFailure(call: Call<CommonApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    afterLoad()
                    Log.e("LOGIN", "EXC: $t")
                }
            })
    }
}
