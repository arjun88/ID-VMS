package com.idbsoftek.vms.setup.profile


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.idbsoftek.vms.R
import com.idbsoftek.vms.api_retrofit.ApiClient
import com.idbsoftek.vms.setup.VmsMainActivity
import com.idbsoftek.vms.setup.login.LoginApiCallable
import com.idbsoftek.vms.util.AppUtil
import com.idbsoftek.vms.util.DialogUtil
import com.idbsoftek.vms.util.PrefUtil
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileActivity : VmsMainActivity() {
    private var dialogUtil: DialogUtil? = null
    private var prefUtil: PrefUtil? = null
    private var activity: ProfileActivity? = null

    //Init All Labels
    private var nameTV: AppCompatTextView? = null
    private var mobTV: AppCompatTextView? = null
    private var empIDTV: AppCompatTextView? = null
    private var locTV: AppCompatTextView? = null
    private var deptTV: AppCompatTextView? = null

    private var divTV: AppCompatTextView? = null
    private var desigTV: AppCompatTextView? = null
    private var shiftTV: AppCompatTextView? = null
    private var woTV: AppCompatTextView? = null
    private var dojTV: AppCompatTextView? = null
    private var noProfileView: LinearLayoutCompat? = null
    private var profileIV: CircleImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        activity = this

        dialogUtil = DialogUtil(this@ProfileActivity)
        prefUtil = PrefUtil(this@ProfileActivity)

        setActionBarTitle("Profile")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 0f

        initView()

        findViewById<View>(R.id.logout_profile_btn).setOnClickListener {
            DialogUtil.showLogoutPopUp(this@ProfileActivity)
        }
    }

    private fun initView() {
        profileIV = findViewById(R.id.profile_iv)
        noProfileView = findViewById(R.id.no_profile_view)
        nameTV = findViewById(R.id.name_tv)
        mobTV = findViewById(R.id.mob_tv)
        empIDTV = findViewById(R.id.emp_id_tv)
        deptTV = findViewById(R.id.dept_tv)
        desigTV = findViewById(R.id.role_tv)

        divTV = findViewById(R.id.div_tv)
        locTV = findViewById(R.id.loc_tv)
        shiftTV = findViewById(R.id.shift_tv)
        woTV = findViewById(R.id.weak_off_tv)
        dojTV = findViewById(R.id.doj_tv)
    }

    override fun onStart() {
        super.onStart()
        if (AppUtil.isInternetThere(this@ProfileActivity))
            loadProfile()
        else
            dialogUtil!!.showToast("No Internet!")
    }

    private fun onLoad() {
        findViewById<View>(R.id.profile_loaded_View).visibility = View.GONE
        findViewById<View>(R.id.profile_loading).visibility = View.VISIBLE
        noProfileView!!.visibility = View.GONE
    }

    private fun afterLoad() {
        findViewById<View>(R.id.profile_loaded_View).visibility = View.VISIBLE
        findViewById<View>(R.id.profile_loading).visibility = View.GONE
        noProfileView!!.visibility = View.GONE
    }

    private fun noProfile() {
        findViewById<View>(R.id.profile_loaded_View).visibility = View.GONE
        findViewById<View>(R.id.profile_loading).visibility = View.GONE
        noProfileView!!.visibility = View.VISIBLE
    }

    private fun setProfile(empInfo: ProfileApiResponse) {
        nameTV!!.text = empInfo.name
        mobTV!!.text = empInfo.mobilenumber

        empIDTV!!.text = "Emp ID: " + empInfo.empid
        deptTV!!.text = "Department: " + empInfo.department
        desigTV!!.text = "Designation: " + empInfo.designation

        divTV!!.text = "Division: " + empInfo.division
        locTV!!.text = "Location: " + empInfo.location
        woTV!!.text = "Week-Off: " + empInfo.weekoff
        shiftTV!!.text = "Shift: " + empInfo.shift
        dojTV!!.text = "DOJ: " + empInfo.empdoj

        val url: String = empInfo.imageurl!!

        val fullUrl = "${PrefUtil.getVmsImageBaseUrl()}${url}"

        setImage(fullUrl)
    }

    private fun setImage(url: String) {
        try {
            Glide
                .with(activity!!)
                .load(url)
                .placeholder(R.drawable.account)
                .apply(RequestOptions.placeholderOf(R.drawable.account).error(R.drawable.account))
                .into(profileIV!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadProfile() {
        if (AppUtil.isInternetThere(this)) {
            onLoad()
            val apiCallable = ApiClient.getRetrofit()!!.create(
                LoginApiCallable::class.java
            )
            val url = "${PrefUtil.getBaseUrl()}VMSuserprofile"
            apiCallable.getProfile(
                url,
                prefUtil!!.userName,
                prefUtil!!.sessionID
            ).enqueue(object : Callback<ProfileApiResponse> {
                override fun onResponse(
                    call: Call<ProfileApiResponse>,
                    response: Response<ProfileApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            if (response.body()?.status == true) {
                                afterLoad()
                                setProfile(
                                    response.body()!!
                                )
                            } else {
                                dialogUtil!!.showToast(response.body()!!.message!!)
                                afterLoad()
                                noProfile()
                            }
                        }
                        response.code() == 500 -> {
                            afterLoad()
                            noProfile()
                            dialogUtil?.showToast("Server Error!")
                        }
                        else -> {
                            afterLoad()
                            noProfile()
                        }
                    }
                }

                override fun onFailure(call: Call<ProfileApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("LOGIN", "EXC: $t")
                    afterLoad()
                    noProfile()
                    dialogUtil?.showToast(t.message!!)
                }
            })
        } else {
            dialogUtil?.showToast("No Internet Connection!")
        }
    }
}
