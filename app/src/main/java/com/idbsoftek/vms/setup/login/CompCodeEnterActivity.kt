package com.idbsoftek.vms.setup.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.idbsoftek.vms.R
import com.idbsoftek.vms.api_retrofit.ApiClient
import com.idbsoftek.vms.api_retrofit.CommonApiResponse
import com.idbsoftek.vms.util.AppUtil
import com.idbsoftek.vms.util.DialogUtil
import com.idbsoftek.vms.util.PrefUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompCodeEnterActivity : AppCompatActivity() {
    private var comTxtIP: TextInputLayout? = null
    private var submitBtn: MaterialButton? = null
    private var activity: CompCodeEnterActivity? = null
    private var dialogUtil: DialogUtil? = null
    private var prefUtil: PrefUtil? = null

    private var loader: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comp_code_enter)

        activity = this
        prefUtil = PrefUtil(activity!!)
        dialogUtil = DialogUtil(activity!!)

        comTxtIP = findViewById(R.id.com_code_txt_ip_login)
        submitBtn = findViewById(R.id.comp_code_submit_btn)
        loader = findViewById(R.id.comp_code_progress)

        submitBtn!!.setOnClickListener {
            if (AppUtil.isInternetThere(activity!!)) {
                val compCode = comTxtIP!!.editText!!.text.toString()

                if (compCode.isNotEmpty()) {
                    apiCheckCompanyCode(compCode)
                } else {
                    dialogUtil!!.showToast("Please Provide Company Code")
                }
            } else {
                dialogUtil!!.showToast("No Internet!")
            }
        }
    }

    private fun onLoad() {
        loader!!.visibility = View.VISIBLE
        submitBtn!!.visibility = View.GONE
    }

    private fun afterLoad() {
        loader!!.visibility = View.GONE
        submitBtn!!.visibility = View.VISIBLE
    }

    // Company Code API

    private fun apiCheckCompanyCode(compCode: String) {
        onLoad()
        val apiCallable = ApiClient.getRetrofit()!!.create(
            LoginApiCallable::class.java
        )

        val url = "http://api.idbssoftware.com/api/apiclientvalidation"
        //  val url = "http://192.168.20.125/mobileapp/api/apiclientvalidation"
        apiCallable.companyCodeValidation(url, compCode)
            .enqueue(object : Callback<CommonApiResponse> {
                override fun onResponse(
                    call: Call<CommonApiResponse>,
                    response: Response<CommonApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val loginResponse = response.body()
                            if (response.body()?.status == true) {
                                val prefUtil = PrefUtil(activity!!)

                                afterLoad()

                                val baseUrl = loginResponse!!.appUrl!!
                                    //.replace("https", "http")
                                PrefUtil.saveBaseUrl(baseUrl)
                                prefUtil.saveAppBaseUrl(baseUrl)
                                PrefUtil.saveVmsImageBaseUrl("${baseUrl}assets/images/visitorimages/")

                                moveToLoginScreen()
                            } else {
                                afterLoad()
                                dialogUtil!!.showToast("${response.body()!!.message}")
                            }
                        }
                        response.code() == 500 -> {
                            afterLoad()
                            dialogUtil!!.showToast("Server Error!")
                        }
                        else -> {
                            afterLoad()
                        }
                    }
                }

                override fun onFailure(call: Call<CommonApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    afterLoad()
                    dialogUtil!!.showToast("${t.message}")
                    Log.e("LOGIN", "EXC: ${t.cause}")
                }
            })
    }

    private fun moveToLoginScreen() {
        val intent = Intent(
            activity,
            LoginActivity::class.java
        )
        activity!!.startActivity(
            intent
        )
    }
}
