package com.idbsoftek.vms.setup.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginPostJson(

	@Expose
	@field:SerializedName("password")
	var password: String? = null,

	@Expose
	@field:SerializedName("appVer")
	var appVer: String? = null,

	@Expose
	@field:SerializedName("model")
	var model: String? = null,

	@Expose
	@field:SerializedName("fcmKey")
	var fcmKey: String? = null,

	@Expose
	@field:SerializedName("deviceId")
	var deviceId: String? = null,

	@Expose
	@field:SerializedName("email")
	var email: String? = null,

	@Expose
	@field:SerializedName("osVer")
	var osVer: String? = null

) {
    override fun toString(): String {
        return "LoginPostJson(password=$password, appVer=$appVer, model=$model, fcmKey=$fcmKey, deviceId=$deviceId, email=$email, osVer=$osVer)"
    }
}
