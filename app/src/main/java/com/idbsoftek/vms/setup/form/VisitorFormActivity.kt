package com.idbsoftek.vms.setup.form

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.VmsMainActivity
import com.idbsoftek.vms.setup.api.*
import com.idbsoftek.vms.setup.login.TokenRefresh
import com.idbsoftek.vms.setup.login.TokenRefreshable
import com.idbsoftek.vms.util.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class VisitorFormActivity() : VmsMainActivity(), AdapterView.OnItemSelectedListener,
    DateTimeSelectable, EmpSelectionClickable, Parcelable, SearchItemClickable, TokenRefreshable {
    private var toMeetSpinner: AppCompatSpinner? = null
    private var idCardSpinner: AppCompatSpinner? = null
    private var categorySpinner: AppCompatSpinner? = null
    private var purposeSpinner: AppCompatSpinner? = null

    private var toMeetSel: String? = ""
    private var idCardSel: String? = ""
    private var categorySel: String? = ""
    private var purposeSel: String? = ""
    private var fromDateSel: String? = ""
    private var toDateSel: String? = ""
    private var fromTimeSel: String? = ""
    private var toTimeSel: String? = ""

    private var toMeelSelPos: Int? = 0
    private var idCardSelPos: Int? = 0
    private var categorySelPos: Int? = 0
    private var purposeSelPos: Int? = 0

    private var context: Activity? = null

    private val CAMERA_ACCESS_REQ_CODE = 88
    private val CAMERA_RETURN_CODE = 888

    private var visitorCategories = ArrayList<String>()
    private var visitorPurposes = ArrayList<String>()
    private var toMeetForDD = ArrayList<String>()
    private var idCardForDD = ArrayList<String>()

    private var addAssociateBtn: AppCompatTextView? = null

    private var visitorCategoriesList = ArrayList<CategoryListItem>()
    private var visitorPurposesList = ArrayList<PurposeListItem>()
    private var toMeetList = ArrayList<EmpListItem>()
    private var idCardList = ArrayList<IdproofListItem>()

    private var fromDateTV: AppCompatTextView? = null
    private var toDateTV: AppCompatTextView? = null
    private var fromTimeTV: AppCompatTextView? = null
    private var toTimeTV: AppCompatTextView? = null
    private var associateCountTV: AppCompatTextView? = null
    private var assetsCountTxtIp: TextInputLayout? = null

    private var fromDateView: View? = null
    private var toDateView: View? = null

    private var associateNumberTxtIp: TextInputLayout? = null
    private var personalIdNumTxtIP: TextInputLayout? = null
    private var visitorIdNumTxtIP: TextInputLayout? = null
    private var vehNumTxtIP: TextInputLayout? = null
    private var nameTxtIP: TextInputLayout? = null
    private var mobTxtIP: TextInputLayout? = null
    private var compTxtIP: TextInputLayout? = null
    private var assetsTxtIP: TextInputLayout? = null

    private var progress: ProgressBar? = null
    private var submitBtn: MaterialButton? = null

    private var addIV: AppCompatImageView? = null
    private var visitorPhotoIV: AppCompatImageView? = null

    private var visitorImg: String? = ""

    private var multiEntry: Boolean? = false

    private var augDatePicker: AugDatePicker? = null

    private var appointWithTV: AppCompatTextView? = null
    private var visitorPhotoTV: AppCompatTextView? = null

    private var deptTV: MaterialTextView? = null
    private var designationTV: MaterialTextView? = null

    private var fromTimeView: LinearLayoutCompat? = null
    private var toTimeView: LinearLayoutCompat? = null

    private var isMeOption: Boolean? = false
    private var isForSelfApproval: Boolean? = false
    private var prefUtil: PrefUtil? = null

    private var emailTxtIP: TextInputLayout? = null
    private var commentsTxtIP: TextInputLayout? = null
    private var bodyTempTxtIP: TextInputLayout? = null

    private var visitorID = 0
    private var passID = 0

    private var tokenRefreshSel: TokenRefreshable? = null
    private var tokenRefresh: TokenRefresh? = null

    private var selfApprovalView: View? = null
    private var entryPassView: View? = null

    private val permissionsReq = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private var searchViewCV: LinearLayoutCompat? = null

    override fun onEmpSelectionClick(emp: EmpListItem) {
        //val empCodeName = "${emp.code} - ${emp.name}"
        appointWithTV!!.text = emp.employeeFullName
        deptTV!!.text = "Department: ${emp.departmentCode}"
        designationTV!!.text = "Designation: ${emp.designationCode}"
        toMeetSel = emp.employeeId

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visitor_form)

        isForSelfApproval = intent.getBooleanExtra("SELF_APPROVAL", false)

        if (!isForSelfApproval!!)
            setActionBarTitle("Visitor Entry Pass")
        else
            setActionBarTitle("Self Approval")

        tokenRefreshSel = this
        tokenRefresh = TokenRefresh().getTokenRefreshInstance(tokenRefreshSel)
        context = this
        prefUtil = PrefUtil(context!!)

        augDatePicker = AugDatePicker(context!!, this)

        initView()
    }

    private fun initView() {
        context = this

        entryPassView = findViewById(R.id.entry_pass_view)
        selfApprovalView = findViewById(R.id.self_approval_view_id)

        fromTimeView = findViewById(R.id.from_time_view)
        toTimeView = findViewById(R.id.to_time_view)

        associateNumberTxtIp = findViewById(R.id.associates_count_txt_ip_form_vms)
        assetsCountTxtIp = findViewById(R.id.assets_count_txt_ip_form_vms)

        if (!isForSelfApproval!!) {
            entryPassView!!.visibility = View.VISIBLE
            selfApprovalView!!.visibility = View.GONE

            emailTxtIP = entryPassView!!.findViewById(R.id.email_txt_ip_form_vms)
            nameTxtIP = entryPassView!!.findViewById(R.id.name_txt_ip_form_vms)
            mobTxtIP = entryPassView!!.findViewById(R.id.mob_txt_ip_form_vms)
            compTxtIP = entryPassView!!.findViewById(R.id.comp_txt_ip_form_vms)
            fromDateView = entryPassView!!.findViewById(R.id.from_date_view_from_vms)
            toDateView = entryPassView!!.findViewById(R.id.to_date_view_from_vms)
            fromDateTV = entryPassView!!.findViewById(R.id.from_date_tv_form_vms)
            toDateTV = entryPassView!!.findViewById(R.id.to_date_tv_form_mvs)
            deptTV = entryPassView!!.findViewById(R.id.dept_visitor_form_tv)
            designationTV = entryPassView!!.findViewById(R.id.designation_visitor_form_tv)
            appointWithTV = entryPassView!!.findViewById(R.id.app_with_tv)
            searchViewCV = entryPassView!!.findViewById(R.id.search_view_in_form)
            categorySpinner = entryPassView!!.findViewById(R.id.category_spinner_form_vms)
            purposeSpinner = entryPassView!!.findViewById(R.id.purpose_spinner_form_vms)
            idCardSpinner = entryPassView!!.findViewById(R.id.id_spinner_vms_form)

            progress = entryPassView!!.findViewById(R.id.vms_form_progress)
            submitBtn = entryPassView!!.findViewById(R.id.form_submit_btn_vms)
        } else {
            entryPassView!!.visibility = View.GONE
            selfApprovalView!!.visibility = View.VISIBLE

            emailTxtIP = selfApprovalView!!.findViewById(R.id.email_txt_ip_form_vms)
            nameTxtIP = selfApprovalView!!.findViewById(R.id.name_txt_ip_form_vms)
            mobTxtIP = selfApprovalView!!.findViewById(R.id.mob_txt_ip_form_vms)
            compTxtIP = selfApprovalView!!.findViewById(R.id.comp_txt_ip_form_vms)
            fromDateView = selfApprovalView!!.findViewById(R.id.from_date_view_from_vms)
            toDateView = selfApprovalView!!.findViewById(R.id.to_date_view_from_vms)
            fromDateTV = selfApprovalView!!.findViewById(R.id.from_date_tv_form_vms)
            toDateTV = selfApprovalView!!.findViewById(R.id.to_date_tv_form_mvs)
            deptTV = selfApprovalView!!.findViewById(R.id.dept_visitor_form_tv)
            designationTV = selfApprovalView!!.findViewById(R.id.designation_visitor_form_tv)
            appointWithTV = selfApprovalView!!.findViewById(R.id.app_with_tv)
            searchViewCV = selfApprovalView!!.findViewById(R.id.search_view_in_form)
            categorySpinner = selfApprovalView!!.findViewById(R.id.category_spinner_form_vms)
            purposeSpinner = selfApprovalView!!.findViewById(R.id.purpose_spinner_form_vms)


            progress = selfApprovalView!!.findViewById(R.id.vms_form_progress)
            submitBtn = selfApprovalView!!.findViewById(R.id.form_submit_btn_vms)
        }

        commentsTxtIP = findViewById(R.id.comments_txt_ip_form_vms)
        bodyTempTxtIP = findViewById(R.id.body_temp_txt_ip_form_vms)

        toMeetSpinner = findViewById(R.id.to_meet_form_spinner_vms)

        vehNumTxtIP = findViewById(R.id.id_veh_num_txt_ip_form_vms)
        visitorIdNumTxtIP = findViewById(R.id.id_num_txt_ip_form_vms)
        personalIdNumTxtIP = findViewById(R.id.personal_id_txt_ip_form_vms)
        assetsTxtIP = findViewById(R.id.assets_txt_ip_form_vms)

        visitorPhotoIV = findViewById(R.id.visitor_photo_iv_form_vms)
        addIV = findViewById(R.id.add_photo_iv_form_vms)

        associateCountTV = findViewById(R.id.associate_added_count_tv)

        fromTimeTV = findViewById(R.id.from_time_vms_form)
        toTimeTV = findViewById(R.id.to_time_form_vms)

        addAssociateBtn = findViewById(R.id.add_associate_btn_form)

        associateCountTV!!.setOnClickListener {
            /*if (associatesAddedList.size > 0)
                moveToAssociatesFragment()*/
        }

        assetsCountTxtIp!!.editText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(
                s: CharSequence?, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.isNotEmpty()) {
                    if (s.toString()  == "0") {
                       // assetsTxtIP!!.visibility = View.GONE
                        disableEditing(assetsTxtIP!!)
                    } else {
                        enableEditing(assetsTxtIP!!)
                       // assetsTxtIP!!.visibility = View.VISIBLE
                    }
                } else {
                    disableEditing(assetsTxtIP!!)
                    //assetsTxtIP!!.visibility = View.GONE
                }
            }
        })

        /*idCardList =
            VMSUtil.getIdCardTypes()*/

        var todayDate = CalendarUtils.getTodayDate()
        todayDate = CalendarUtils.getDateInRequestedFormat(
            "MM-dd-yyyy",
            "dd-MM-yyyy",
            todayDate
        )

        fromDateTV!!.text = todayDate
        toDateTV!!.text = todayDate

        fromDateTV!!.setOnClickListener {
            augDatePicker!!.showDatePicker(
                isFromDate = true, isSingleDate = false, fromDate = "", toDate = "",
                futureDateCanbeSelected = true
            )
        }

        toDateTV!!.setOnClickListener {
            augDatePicker!!.showDatePicker(
                isFromDate = false, isSingleDate = false, fromDate = "", toDate = "",
                futureDateCanbeSelected = true
            )
        }

        searchViewCV!!.setOnClickListener {
            moveToEmpSelectScreen(1)
        }

        /*fromTimeTV!!.setOnClickListener {
            augDatePicker!!.showTimePicker(
                isFromTime = true, inTime = "", outTime = ""
            )
        }

        toTimeTV!!.setOnClickListener {
            augDatePicker!!.showTimePicker(
                isFromTime = false, inTime = "", outTime = ""
            )
        }*/

        findViewById<MaterialCardView>(R.id.camera_visitor_img_cv).setOnClickListener {
            imageFromPopUp = false
            setUpCamera()
        }

        if (this.isForSelfApproval!!) {
            appointWithTV!!.text = PrefUtil.getEmpName()
            val empCodeName = PrefUtil.getEmpName().split(" - ")
            toMeetSel = empCodeName[0]
        }

        appointWithTV!!.setOnClickListener {
//            if (!this.isForSelfApproval!!) {
            moveToEmpSelectScreen(0)
            //  }
        }

        afterFormSubmit()

        addAssociateBtn!!.setOnClickListener {
            val associateCountString = associateNumberTxtIp!!.editText!!.text.toString()
            var associateCount = 0
            if (associateCountString.isNotEmpty()) {
                associateCount = associateCountString.toInt()
            }

            val addedAscCount = associatesAddedList.size

            if (associateCount > 0) {
                if (addedAscCount < associateCount)
                    showAddAssociatesPopUp()
                else {
                    showToast("Please increase the number of associates to add the same.")
                }
            } else {
                showToast("Please mention number of associates first.")
            }
        }

        val activity = this

        submitBtn!!.setOnClickListener {
            if (AppUtil.isInternetThere(context!!)) {
                if (!isForSelfApproval!!) {
                    var formData = VisitorFormSubmitData()

                    var associates = ArrayList<AssociatesItem>()
                    associates = associatesAddedList

                    formData.associates = associates
                    formData.appointmentWith = toMeetSel

                    formData.visitorName = nameTxtIP!!.editText!!.text.toString()
                    formData.mob = mobTxtIP!!.editText!!.text.toString()
                    formData.visitorCompany = compTxtIP!!.editText!!.text.toString()

                    formData.idNum = personalIdNumTxtIP!!.editText!!.text.toString()
                    formData.visitorIdNum = personalIdNumTxtIP!!.editText!!.text.toString()
                    formData.assets = assetsTxtIP!!.editText!!.text.toString()
                    formData.assetsNum = ""

                    formData.fromDate = fromDateSel
                    formData.toDate = toDateSel
                    formData.fromTime = fromTimeSel
                    formData.toTime = toTimeSel

                    formData.visitorPhoto = visitorImg
                    formData.visitorCategory = categorySel
                    formData.visitorPurpose = purposeSel
                    formData.idProof = idCardSel
                    val prefUtil = PrefUtil(activity)
                    formData.empID = prefUtil.userName

                    val vehNum = vehNumTxtIP!!.editText!!.text.toString()
                    formData.vehNum = vehNum

                    // V 2.0 Changes
                    val submitData = AddVisitorPost()
                    submitData.employeeId = toMeetSel!!
                    submitData.visitorName = nameTxtIP!!.editText!!.text.toString()
                    submitData.visitorMobile = mobTxtIP!!.editText!!.text.toString()
                    submitData.visitorCompany = compTxtIP!!.editText!!.text.toString()
                    submitData.visitorEmail = emailTxtIP!!.editText!!.text.toString()
                    submitData.bodyTemp = bodyTempTxtIP!!.editText!!.text.toString()
                    submitData.comments = commentsTxtIP!!.editText!!.text.toString()

                    val assetNumString = assetsCountTxtIp!!.editText!!.text.toString()
                    var assetsCount = 0
                    if (assetNumString.isNotEmpty())
                        assetsCount = assetNumString.toInt()

                    val associateCountString = associateNumberTxtIp!!.editText!!.text.toString()
                    var associateCount = 0
                    if (associateCountString.isNotEmpty())
                        associateCount = associateCountString.toInt()

                    submitData.vehicleNumber = vehNumTxtIP!!.editText!!.text.toString()
                    submitData.associateCount = associatesList.size
                    submitData.assetName = assetsTxtIP!!.editText!!.text.toString()
                    submitData.assetNumber = assetsCount
                    submitData.visitorID = visitorID
                    submitData.visitorPassID = passID

                    fromDateSel = CalendarUtils.getDateInRequestedFormat(
                        "dd-MM-yyyy",
                        "yyyy-MM-dd", fromDateTV!!.text.toString()
                    )

                    toDateSel = CalendarUtils.getDateInRequestedFormat(
                        "dd-MM-yyyy",
                        "yyyy-MM-dd", toDateTV!!.text.toString()
                    )

                    submitData.fromDate = fromDateSel
                    submitData.toDate = toDateSel

                    /*submitData.fromTime = fromTimeSel
                submitData.toTime = toTimeSel*/

                    submitData.purposeCode = purposeSel
                    submitData.categoryCode = categorySel
                    submitData.proofDetails = personalIdNumTxtIP!!.editText!!.text.toString()
                    submitData.iDProofCode = idCardSel

                    submitData.imageData = visitorImg

                    /*   var associatesToAdd = ArrayList<AscItem>()
                      associatesToAdd = associatesList*/
                    submitData.asc = associatesList

                    //  AppUtil.EMP_ID_VMS //

                    if (validateFieldsAndContinue(submitData)) {
                        reqNewVisit(submitData)
                    }
                } else {
                    // Validation For Self Approval
                    var formData = SelfApprovalPost()
                    formData.employeeId = toMeetSel
                    formData.categoryCode = categorySel
                    val urlBase = PrefUtil.getBaseUrl()
                    val urlArray = urlBase.split("api/")
                    formData.origin = urlArray[0]

                    fromDateSel = CalendarUtils.getDateInRequestedFormat(
                        "dd-MM-yyyy",
                        "yyyy-MM-dd", fromDateTV!!.text.toString()
                    )

                    toDateSel = CalendarUtils.getDateInRequestedFormat(
                        "dd-MM-yyyy",
                        "yyyy-MM-dd", toDateTV!!.text.toString()
                    )

                    formData.fromDate = fromDateSel
                    formData.toDate = toDateSel
                    formData.purposeCode = categorySel

                    formData.visitorName = nameTxtIP!!.editText!!.text.toString()
                    formData.visitorMobile = mobTxtIP!!.editText!!.text.toString()
                    formData.visitorCompany = compTxtIP!!.editText!!.text.toString()
                    formData.visitorEmail = emailTxtIP!!.editText!!.text.toString()

                    if (validateSelfApproval(formData)) {
                        selfApproval(formData)
                    }
                }

            } else {
                showToast("No Internet!")
            }
        }


        if (AppUtil.isInternetThere(context!!)) {
            // getVisitorCategoryApi()
            loadSetUpApi()
            //  getToMeetApi()
            // getPurposeApi()
            // getIdCardsApi()
        } else {
            showToast("No Internet!")
        }
    }

    // IMAGE **********************

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    private fun getThumbnailImage(bitmap: Bitmap): Bitmap? {
        val thumbImage: Bitmap
        thumbImage = ThumbnailUtils.extractThumbnail(
            bitmap,
            164,
            196
        )
        return thumbImage
    }

    private fun getRotatedBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(90f)
        return Bitmap.createBitmap(
            bitmap, 0, 0,
            bitmap.width, bitmap.height, matrix, true
        )
    }

    //*****************************
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        //if(data != null) {
        if (requestCode == CAMERA_RETURN_CODE && resultCode == RESULT_OK) {
            val cameraBitmap: Bitmap?
            val thumbnailImage: Bitmap
            if (photoFile != null) {
                cameraBitmap = getBitmapFromUri(Uri.fromFile(photoFile!!))

                //FOR ACTUAL IMG ************
                // visitorImg = ImageUtil.encodeImage(getRotatedBitmap(cameraBitmap!!))

                //   thumbnailImage = ImageUtil.getThumbnailImage(cameraBitmap, 164, 196)

                val capturePhoto = ImageUtil.rotateImageIfRequired(
                    cameraBitmap!!, Uri.fromFile(photoFile!!)
                )
                thumbnailImage =
//                    ImageUtil.getThumbnailImage(getRotatedBitmap(cameraBitmap!!), 164, 196)
                    ImageUtil.getThumbnailImage(capturePhoto, 164, 196)

                if (!imageFromPopUp) {
                    visitorPhotoIV!!.setImageBitmap(getThumbnailImage(thumbnailImage))
                    val image: String = ImageUtil.encodeImage(thumbnailImage)
                    visitorImg = image
                } else {
                    assocIVPopUp!!.setImageBitmap(getThumbnailImage(thumbnailImage))
                    val image: String = ImageUtil.encodeImage(thumbnailImage)
                    associateImage = image
                }

                Toast.makeText(context, "Photo Added!", Toast.LENGTH_SHORT).show()

                //SET DD Data
                categorySelPos = PrefUtil.getCatPosVMS()
                categorySpinner!!.setSelection(categorySelPos!!)

                purposeSelPos = PrefUtil.getPurposePosVMS()
                purposeSpinner!!.setSelection(purposeSelPos!!)

                if (!isForSelfApproval!!) {
                    idCardSelPos = PrefUtil.getIdPosVMS()
                    idCardSpinner!!.setSelection(idCardSelPos!!)
                    Log.e("---", "ID SEL: ${idCardSelPos}")

                    if (!imageFromPopUp)
                        toggleImageView()
                    else
                        toggleImageViewInPopUp()
                }

            } else {
                Toast.makeText(context, "Please add Photo Again!", Toast.LENGTH_SHORT).show()
            }
        }
        /* }
         else {
             Toast.makeText(context, "Please take Photo Again!", Toast.LENGTH_SHORT).show()
         }*/
    }

    private fun checkForCameraPermission(): Boolean {
        for (permission in permissionsReq) {
            if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun reqCameraAccess() {
        ActivityCompat.requestPermissions(
            context!!,
            permissionsReq, CAMERA_ACCESS_REQ_CODE
        )
    }

    private var photoFile: File? = null
    private var cameraImageUri: Uri? = null

    private fun openCamera() {

        Log.e("---", "ID IN SEL: ${idCardSelPos}")

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            try {
                val timeInMs = System.currentTimeMillis()
                // val timeInMs = CalendarUtils.ge
                photoFile = ImageUtil.createImageFile(
                    context!!,
                    "jpg", "Visitor${timeInMs}"
                )
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            // Continue only if the File was successfully created
            try {
                if (photoFile != null) {
                    try {
                        cameraImageUri = FileProvider.getUriForFile(
                            context!!,
                            "com.idbsoftek.vms.fileprovider", photoFile!!
                        )
                        val addFlags = takePictureIntent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION)

                        takePictureIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION or FLAG_GRANT_WRITE_URI_PERMISSION)

                        val resInfoList: List<ResolveInfo> =
                            this.packageManager.queryIntentActivities(
                                takePictureIntent,
                                PackageManager.MATCH_DEFAULT_ONLY
                            )

                        for (resolveInfo in resInfoList) {
                            val packageName: String = resolveInfo.activityInfo.packageName
                            grantUriPermission(
                                packageName,
                                cameraImageUri,
                                FLAG_GRANT_WRITE_URI_PERMISSION or FLAG_GRANT_READ_URI_PERMISSION
                            )
                        }
                        //takePictureIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
                        takePictureIntent.clipData = ClipData.newRawUri("vms", cameraImageUri)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
                        startActivityForResult(takePictureIntent, CAMERA_RETURN_CODE)
                    } catch (e: Exception) {
                        showToast("Please take photo again!")
                    }
                }
            } catch (e: Exception) {
                showToast("Please take photo again!")
            }
        }
    }

    private var imageFromPopUp: Boolean = false

    private fun setUpCamera() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkForCameraPermission()) {
                reqCameraAccess()
            } else
            //  dispatchTakePictureIntent()
                openCamera()
        } else
        // dispatchTakePictureIntent()
            openCamera()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            -1 -> {

            }
            CAMERA_ACCESS_REQ_CODE -> {
                if (grantResults.isNotEmpty()) {

                    if ((grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        && (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    )
                    // dispatchTakePictureIntent()
                        openCamera()
                    else
                        reqCameraAccess()
                } else {
                    reqCameraAccess()
                }
            }
        }
    }

    //API ****************************

    private fun loadSetUpApi() {
        // onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        tokenRefreshSel = this
        val prefUtil = PrefUtil(this)
        val url =
            "${PrefUtil.getBaseUrl()}/VMC/VMCInit" //"${prefUtil.appBaseUrl}EmployeeList"
        apiCallable.getSettingsApi(
            url, prefUtil.getApiToken()
        )
            .enqueue(object : Callback<VmsInitApiResponse> {
                override fun onResponse(
                    call: Call<VmsInitApiResponse>,
                    response: Response<VmsInitApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                /*  toMeetList.clear()
                                toMeetForDD.clear()*/

                                visitorCategoriesList.clear()
                                visitorCategories.clear()

                                for (element in visitorLogApiResponse.categoryList!!) {
                                    visitorCategoriesList.add(element)
                                }

                                setCategoryDD()

                                visitorPurposesList.clear()
                                visitorPurposes.clear()

                                for (element in visitorLogApiResponse.purposeList!!) {
                                    visitorPurposesList.add(element)
                                }

                                setPurposeDD()

                                idCardList.clear()
                                idCardForDD.clear()

                                for (element in visitorLogApiResponse.idproofList!!) {
                                    idCardList.add(element)
                                }

                                if (!isForSelfApproval!!)
                                    setIdCardDD()

                                // SET UP

                                prefUtil.saveMeetCompleteReq(visitorLogApiResponse.vpSetting!!.isMeetCompleteRequired!!)
                                prefUtil.saveVisitorImgReq(visitorLogApiResponse.vpSetting.isImageRequired!!)
                                prefUtil.saveAssociateInfoReq(visitorLogApiResponse.vpSetting.associateInfoRequired!!)
                                prefUtil.saveAssociateImgReq(visitorLogApiResponse.vpSetting.isImageRequiredAsc!!)

                                /* for (element in visitorLogApiResponse.empList!!) {
                                    toMeetList.add(element!!)
                                }*/

                                //setToMeetDD()
                                //afterLoad()
                            } else {
                                //afterLoad()
                                showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 401 -> {
                            tokenRefresh!!.doTokenRefresh(context!!, tokenRefreshSel)
                        }
                        response.code() == 500 -> {
                            //  afterLoad()
                        }
                    }
                }

                override fun onFailure(call: Call<VmsInitApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    // afterLoad()
                }
            })
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun moveToEmpSelectScreen(fromScreen: Int?) {
        val fragment = EmpPickerFragment()

        val bundle = Bundle()

        fragment.empClickInit(this, this)
        fragment.arguments = bundle
        moveToFragment(fragment, fromScreen)
    }

    private fun moveToFragment(tarFragment: Fragment, fromScreen: Int?) {
        val bundle = Bundle()
        bundle.putBoolean("IS_FOR_REF_NUM", false)
        bundle.putInt("FROM_SCREEN", fromScreen!!)
        tarFragment.arguments = bundle

        val fragmentManager = supportFragmentManager

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(
            R.id.form_view,
            tarFragment
        )
        fragmentTransaction.addToBackStack(null).commit()
    }

    @SuppressLint("DefaultLocale")
    private fun setCategoryDD() {
        for (i in 0 until visitorCategoriesList.size) {
            val name = visitorCategoriesList[i].categoryName!!.toLowerCase().capitalize()

            visitorCategories.add(name)
        }

        val adapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_item, visitorCategories
        )

        // Set layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        categorySpinner!!.adapter = adapter
        categorySpinner!!.onItemSelectedListener = this
    }

    @SuppressLint("DefaultLocale")
    private fun setIdCardDD() {
        idCardForDD.clear()
        for (element in idCardList) {
            val name = element.iDProofName!!.toLowerCase().capitalize()

            idCardForDD.add(name)
        }
        val adapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_item, idCardForDD
        )

        // Set layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        idCardSpinner!!.adapter = adapter
        idCardSpinner!!.onItemSelectedListener = this
    }

    @SuppressLint("DefaultLocale")
    private fun setPurposeDD() {
        for (i in 0 until visitorPurposesList.size) {
            val name = visitorPurposesList[i].purposeName!!.toLowerCase().capitalize()

            visitorPurposes.add(name)
        }

        val adapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_item, visitorPurposes
        )
        // Set layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        purposeSpinner!!.adapter = adapter
        purposeSpinner!!.onItemSelectedListener = this
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        when (p0) {
            categorySpinner -> {
                categorySel = visitorCategoriesList[p2].categoryCode

                // categorySelPos = p2
                PrefUtil.savePosOfCategoryVMS(p2)

                /* multiEntry = false//visitorCategoriesList[p2].meOption
                isMeOption = multiEntry
                if (multiEntry!!) {
                    fromDateView!!.visibility = View.VISIBLE
                    toDateView!!.visibility = View.VISIBLE

                    fromTimeView!!.visibility = View.GONE
                    toTimeView!!.visibility = View.GONE


                } else {
                    fromDateView!!.visibility = View.GONE
                    toDateView!!.visibility = View.GONE

                    fromTimeView!!.visibility = View.VISIBLE
                    toTimeView!!.visibility = View.VISIBLE
                    fromDateSel = ""
                    toDateSel = ""
                    fromDateTV!!.text = ""
                    toDateTV!!.text = ""
                }*/
            }

            purposeSpinner -> {
                purposeSel = visitorPurposesList[p2].purposeCode
                // purposeSelPos = p2
                PrefUtil.savePosOfPurposeVMS(p2)
            }

            toMeetSpinner -> {
                toMeetSel = toMeetList[p2].code!!
                toMeelSelPos = p2
            }

            idCardSpinner -> {
                idCardSel = idCardList[p2].iDProofName!!
                //  idCardSelPos = p2
                PrefUtil.savePosOfIdVMS(p2)
            }

            idSpinnerInPopUp -> {
                idSelInPopUp = idCardList[p2].iDProofCode!!
            }
        }
    }

    override fun onFromDateSelected(date: String) {
        var dateSel = date
        dateSel = CalendarUtils.getDateInRequestedFormat(
            "yyyy-MM-dd",
            "dd-MM-yyyy", date
        )

        fromDateSel = dateSel
        fromDateTV!!.text = dateSel
    }

    override fun onToDateSelected(date: String) {
        var dateSel = date
        dateSel = CalendarUtils.getDateInRequestedFormat(
            "yyyy-MM-dd",
            "dd-MM-yyyy", date
        )

        toDateSel = dateSel
        toDateTV!!.text = dateSel
    }

    override fun onDateSelected(date: String) {
        //NOTHING
    }

    override fun onFromTimeSelected(time: String) {
        this.fromTimeSel = time
        fromTimeTV!!.text = time
    }

    override fun onToTimeSelected(time: String) {
        this.toTimeSel = time
        toTimeTV!!.text = time
    }

    private fun onFormSubmit() {
        submitBtn!!.isClickable = false
        submitBtn!!.isEnabled = false

        progress!!.visibility = View.VISIBLE
    }

    private fun afterFormSubmit() {
        submitBtn!!.isClickable = true
        submitBtn!!.isEnabled = true

        progress!!.visibility = View.GONE
    }

    private fun toggleImageView() {
        if ((visitorImg != null) || (visitorImg!!.isNotEmpty())) {
            addIV!!.visibility = View.GONE
            visitorPhotoIV!!.visibility = View.VISIBLE
        } else {
            addIV!!.visibility = View.VISIBLE
            visitorPhotoIV!!.visibility = View.GONE
        }
    }

    private fun toggleImageViewInPopUp() {
        if (associateImage.isNotEmpty()) {
            addIVPopUp!!.visibility = View.GONE
            assocIVPopUp!!.visibility = View.VISIBLE
        } else {
            addIVPopUp!!.visibility = View.VISIBLE
            assocIVPopUp!!.visibility = View.GONE
        }
    }

    private var associateImage = ""

    //SELF Approval Validation
    private fun validateSelfApproval(reqFormData: SelfApprovalPost): Boolean {
        var isValidated = false

        when {
            reqFormData.visitorName == "" -> showToast("Please Enter Visitor Name")
            //  reqFormData.visitorMobile == "" -> showToast("Please Enter Visitor Mobile Number")
            //    reqFormData.visitorCompany == "" -> showToast("Please Enter Visitor Company")
            reqFormData.visitorName == "" -> showToast("Please Enter Visitor Name")

            reqFormData.employeeId == "" -> showToast("Please Select Employee To Meet With")

            reqFormData.categoryCode == "" -> showToast("Please Select Visitor Category")
            reqFormData.purposeCode == "" -> showToast("Please Add Visitor Purpose")

            reqFormData.fromDate!!.isEmpty() -> showToast("Please Select From Date")
            reqFormData.toDate!!.isEmpty() -> showToast("Please Select To Date")

            else -> isValidated = true
        }
        return isValidated
    }
    //REQ VISIT API AND VALIDATION

    private fun validateFieldsAndContinue(reqFormData: AddVisitorPost): Boolean {
        var isValidated = false

        when {
            prefUtil!!.isVisitorImgReq() -> {
                if (reqFormData.imageData == "")
                    showToast("Please Add Visitor Photo")
            }

            reqFormData.visitorName == "" -> showToast("Please Enter Visitor Name")
            reqFormData.visitorMobile == "" -> showToast("Please Enter Visitor Mobile Number")
            reqFormData.visitorEmail == "" -> showToast("Please Enter Visitor Email")
            !AppUtil.isValidEmail(reqFormData.visitorEmail) -> showToast("Please Provide Valid Visitor Email")

            reqFormData.proofDetails == "" -> showToast("Please Enter Visitor ID Proof Number")
            reqFormData.bodyTemp == "" -> showToast("Please Enter Visitor Body Temperature")

            reqFormData.employeeId == "" -> showToast("Please Select Employee To Meet With")

            reqFormData.categoryCode == "" -> showToast("Please Select Visitor Category")
            reqFormData.purposeCode == "" -> showToast("Please Add Visitor Purpose")

            reqFormData.fromDate!!.isEmpty() -> showToast("Please Select From Date")
            reqFormData.toDate!!.isEmpty() -> showToast("Please Select To Date")

            !CalendarUtils.isFirstDateLesserThanSecondDate(
                reqFormData.fromDate,
                reqFormData.toDate, "yyyy-MM-dd"
            ) -> showToast("From Date can't be greater than To Date")

            prefUtil!!.isAssociateInfoReq() -> {
                val associateCountString = associateNumberTxtIp!!.editText!!.text.toString()
                var associateCount = 0
                if (associateCountString.isNotEmpty())
                    associateCount = associateCountString.toInt()
               // if (reqFormData.associateCount!! > 0) {
                    when {
                        associateCount > 0 && reqFormData.associateCount==0  -> {
                            showToast("Please Provide Associate Information")
                        }
                        associateCount != reqFormData.associateCount -> {
                            showToast("Please add same number of Associates as Mentioned")
                        }
                        else -> isValidated = true
                    }
              //  }
            }

            else -> isValidated = true
        }
        return isValidated
    }

    private fun selfApproval(visitData: SelfApprovalPost) {
        onFormSubmit()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )

        val gson = Gson()
        val requestBody: RequestBody = RequestBody.create(
            MediaType.parse("application/json"), gson.toJson(visitData)
        )

        tokenRefreshSel = this
        val prefUtil = PrefUtil(context!!)
        val url =
            "${PrefUtil.getBaseUrl()}ISelfApprover/AddSelfAprrovalPass"//"${prefUtil.appBaseUrl}VisitorEntryPass"

        apiCallable.selfApprovalApi(
            url,
            requestBody,
            prefUtil.getApiToken()
        ).enqueue(object : Callback<VisitorActionApiResponse> {
            override fun onResponse(
                call: Call<VisitorActionApiResponse>,
                response: Response<VisitorActionApiResponse>
            ) {
                when {
                    response.code() == 200 -> {
                        if (response.body()?.status == true) {
                            showToast(response.body()!!.message!!)
                            PrefUtil.savePosOfCategoryVMS(0)
                            PrefUtil.savePosOfPurposeVMS(0)
                            // PrefUtil.savePosOfIdVMS(0)
                            finish()
                        } else {
                            afterFormSubmit()
                            showToast(response.body()!!.message!!)
                        }
                    }
                    response.code() == 401 -> {
                        tokenRefresh!!.doTokenRefresh(context!!, tokenRefreshSel)
                    }
                    response.code() == 500 -> {
                        showToast("Server Error!")
                        afterFormSubmit()
                    }
                }
            }

            override fun onFailure(call: Call<VisitorActionApiResponse>, t: Throwable) {
                t.printStackTrace()
                afterFormSubmit()
            }
        })
    }


    private fun reqNewVisit(visitData: AddVisitorPost) {
        onFormSubmit()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )

        val gson = Gson()
        val requestBody: RequestBody = RequestBody.create(
            MediaType.parse("application/json"), gson.toJson(visitData)
        )

        tokenRefreshSel = this
        val prefUtil = PrefUtil(context!!)
        val url =
            "${PrefUtil.getBaseUrl()}VMC/AddVisitPass"//"${prefUtil.appBaseUrl}VisitorEntryPass"

        apiCallable.submitFormApi(
            url,
            requestBody,
            prefUtil.getApiToken()
        ).enqueue(object : Callback<VisitorActionApiResponse> {
            override fun onResponse(
                call: Call<VisitorActionApiResponse>,
                response: Response<VisitorActionApiResponse>
            ) {
                when {
                    response.code() == 200 -> {
                        if (response.body()?.status == true) {
                            showToast(response.body()!!.message!!)
                            PrefUtil.savePosOfCategoryVMS(0)
                            PrefUtil.savePosOfPurposeVMS(0)
                            PrefUtil.savePosOfIdVMS(0)
                            finish()
                        } else {
                            afterFormSubmit()
                            showToast(response.body()!!.message!!)
                        }
                    }
                    response.code() == 401 -> {
                        tokenRefresh!!.doTokenRefresh(context!!, tokenRefreshSel)
                    }
                    response.code() == 500 -> {
                        showToast("Server Error!")
                        afterFormSubmit()
                    }
                }
            }

            override fun onFailure(call: Call<VisitorActionApiResponse>, t: Throwable) {
                t.printStackTrace()
                afterFormSubmit()
            }
        })
    }

    //ASSOCIATES ADD POP-UP *******************

    private var addAssociatesSheet: BottomSheetDialog? = null
    private var idSpinnerInPopUp: AppCompatSpinner? = null
    private var idSelInPopUp: String? = ""

    var associatesAddedList: ArrayList<AssociatesItem> = ArrayList()
    var associatesList: ArrayList<AscItem> = ArrayList()

    var assocIVPopUp: AppCompatImageView? = null
    var addIVPopUp: AppCompatImageView? = null

    constructor(parcel: Parcel) : this() {
        toMeetSel = parcel.readString()
        idCardSel = parcel.readString()
        categorySel = parcel.readString()
        purposeSel = parcel.readString()
        fromDateSel = parcel.readString()
        toDateSel = parcel.readString()
        fromTimeSel = parcel.readString()
        toTimeSel = parcel.readString()
        toMeelSelPos = parcel.readValue(Int::class.java.classLoader) as? Int
        idCardSelPos = parcel.readValue(Int::class.java.classLoader) as? Int
        categorySelPos = parcel.readValue(Int::class.java.classLoader) as? Int
        purposeSelPos = parcel.readValue(Int::class.java.classLoader) as? Int
        visitorImg = parcel.readString()
        multiEntry = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        cameraImageUri = parcel.readParcelable(Uri::class.java.classLoader)
        imageFromPopUp = parcel.readByte() != 0.toByte()
        associateImage = parcel.readString()!!
        idSelInPopUp = parcel.readString()
    }

    @SuppressLint("DefaultLocale")
    private fun showAddAssociatesPopUp() {
        associateImage = ""
        addAssociatesSheet = BottomSheetDialog(this)
        val view: View = layoutInflater.inflate(R.layout.add_associates_popup, null)

        var nameTxtIP: TextInputLayout? = null
        var mobTxtIP: TextInputLayout? = null
        var emailTxtIP: TextInputLayout? = null
        var idNumTxtIP: TextInputLayout? = null
        var visitorIdTxtIP: TextInputLayout? = null
        var assetsTxtIP: TextInputLayout? = null

        var bodyTempTxtIP: TextInputLayout? = null
        var vehNumTxtIP: TextInputLayout? = null
        var assetsCountTxtIP: TextInputLayout? = null

        nameTxtIP = view.findViewById(R.id.name_txt_ip_form_vms_pop)
        mobTxtIP = view.findViewById(R.id.mob_txt_ip_form_vms_pop)
        emailTxtIP = view.findViewById(R.id.email_txt_ip_form_vms_pop)
        idNumTxtIP = view.findViewById(R.id.personal_id_txt_ip_form_vms_pop)
        visitorIdTxtIP = view.findViewById(R.id.id_num_txt_ip_form_vms_pop)
        assetsTxtIP = view.findViewById(R.id.assets_txt_ip_form_vms_pop)

        bodyTempTxtIP = view.findViewById(R.id.body_temp_txt_ip_form_vms_pop)
        vehNumTxtIP = view.findViewById(R.id.veh_txt_ip_form_vms_pop)
        assetsCountTxtIP = view.findViewById(R.id.assets_count_txt_ip_form_vms_pop)

        idSpinnerInPopUp = view.findViewById(R.id.id_spinner_vms_form_pop)

        addIVPopUp = view.findViewById(R.id.add_photo_iv_pop_up)
        assocIVPopUp = view.findViewById(R.id.ass_photo_iv_form_vms)

        view.findViewById<MaterialCardView>(R.id.camera_visitor_img_cv).setOnClickListener {
            imageFromPopUp = true
            setUpCamera()
        }

        idCardForDD.clear()

        for (element in idCardList) {
            val name = element.iDProofName!!.toLowerCase().capitalize()

            idCardForDD.add(name)
        }

        val adapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_item, idCardForDD
        )

        // Set layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        idSpinnerInPopUp!!.adapter = adapter
        idSpinnerInPopUp!!.onItemSelectedListener = this

        view.findViewById<View>(R.id.add_associates_submit_btn)
            .setOnClickListener {
                var associate = AscItem()

                associate.ascVisitorName = nameTxtIP.editText!!.text.toString()
                associate.ascVisitorMobile = mobTxtIP.editText!!.text.toString()
                associate.ascProofDetails = idNumTxtIP.editText!!.text.toString()
                associate.asciDProofCode = visitorIdTxtIP.editText!!.text.toString()
                associate.asAssetName = assetsTxtIP.editText!!.text.toString()
                associate.ascBodyTemp = bodyTempTxtIP.editText!!.text.toString()
                associate.ascVisitorEmail = emailTxtIP.editText!!.text.toString()
                val assetCountString = assetsCountTxtIP.editText!!.text.toString()
                var assetCount = 0
                if (assetCountString.isNotEmpty())
                    assetCount = assetCountString.toInt()
                associate.asAssetNumber = assetCount
                associate.asVehicleNumber = vehNumTxtIP.editText!!.text.toString()
                associate.ascImageData = associateImage
                associate.asciDProofCode = idSelInPopUp

                when {
                    associate.ascVisitorName!!.isEmpty() -> {
                        showToast("Please Provide Associate Name")
                    }
                    associate.ascVisitorMobile!!.isEmpty() -> {
                        showToast("Please Provide Associate Mobile Number")
                    }
                    associate.ascVisitorEmail!!.isEmpty() -> {
                        showToast("Please Provide Associate Email ID")
                    }
                    !AppUtil.isValidEmail(associate.ascVisitorEmail) -> showToast("Please Provide Valid Associate Email")
                    idSelInPopUp!!.isEmpty() -> {
                        showToast("Please Select ID Proof")
                    }
                    associate.ascProofDetails!!.isEmpty() -> {
                        showToast("Please Provide ID Proof Number")
                    }
                    associate.ascBodyTemp!!.isEmpty() -> {
                        showToast("Please Provide Associate Body Temperature")
                    }
                    prefUtil!!.isAssociateImgReq() -> {
                        if (associateImage.isEmpty())
                            showToast("Please Provide Associate Image")
                        else {
                            addAssociatesSheet!!.dismiss()
                            addAssociate(associate)
                        }
                    }
                    else -> {
                        addAssociatesSheet!!.dismiss()
                        addAssociate(associate)
                    }
                }
            }

        addAssociatesSheet!!.setContentView(view)
        addAssociatesSheet!!.show()
    }

    //ADD Associates Entry
    private fun addAssociate(associate: AscItem) {
        associatesList.add(associate)
        associateCountTV!!.text = "${associatesList.size} Associates Added"
        showToast("New Associate Added Successfully!")
        associateImage = ""
    }

    fun removeAssociate(pos: Int) {
        try {
            val item = associatesAddedList[pos]
            associatesAddedList.remove(item)
            showToast("1 Associate Removed")

            if (associatesAddedList.size > 0) {
                supportFragmentManager.popBackStack()
                moveToAssociatesFragment()
            } else {
                supportFragmentManager.popBackStack()
            }

            associateCountTV!!.text = "${associatesAddedList.size} Associates Added"
        } catch (e: Exception) {
            showToast("Unable to remove!")
        }
    }

    private fun moveToAssociatesFragment() {
        val fragment =
            AssociatesListFragment()

        val arg = Bundle()
        arg.putParcelableArrayList("ASSOCIATES", associatesAddedList)
        arg.putBoolean("IS_FORM", true)
        fragment.arguments = arg

        val fm = supportFragmentManager
        val fT = fm.beginTransaction()
        fT.replace(R.id.vms_form_view, fragment)
        fT.addToBackStack(null)
        fT.commit()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(toMeetSel)
        parcel.writeString(idCardSel)
        parcel.writeString(categorySel)
        parcel.writeString(purposeSel)
        parcel.writeString(fromDateSel)
        parcel.writeString(toDateSel)
        parcel.writeString(fromTimeSel)
        parcel.writeString(toTimeSel)
        parcel.writeValue(toMeelSelPos)
        parcel.writeValue(idCardSelPos)
        parcel.writeValue(categorySelPos)
        parcel.writeValue(purposeSelPos)
        parcel.writeString(visitorImg)
        parcel.writeValue(multiEntry)
        parcel.writeParcelable(cameraImageUri, flags)
        parcel.writeByte(if (imageFromPopUp) 1 else 0)
        parcel.writeString(associateImage)
        parcel.writeString(idSelInPopUp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VisitReqFormActivity> {
        override fun createFromParcel(parcel: Parcel): VisitReqFormActivity {
            return VisitReqFormActivity(parcel)
        }

        override fun newArray(size: Int): Array<VisitReqFormActivity?> {
            return arrayOfNulls(size)
        }
    }

    override fun onSearchItemClick(searchData: SearchResultsItem) {
        // Auto Fill Fields
        getVisitorInfoApi(searchData.visitorID!!.toInt())
    }

    // Visitor Info API

    private fun getVisitorInfoApi(visitorID: Int?) {
        // onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        // val prefUtil = PrefUtil(activity!!)
        tokenRefreshSel = this
        val url =
            "${PrefUtil.getBaseUrl()}/VMC/getVisitorbyId"//"${prefUtil.appBaseUrl}EmployeeList"

        apiCallable.getVisitorInfoApi(
            url, visitorID!!,
            prefUtil!!.getApiToken()
        )
            .enqueue(object : Callback<VisitorInfoApiResponse> {
                override fun onResponse(
                    call: Call<VisitorInfoApiResponse>,
                    response: Response<VisitorInfoApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                val visitorInfo = visitorLogApiResponse.visitorInfo
                                autoFillFields(visitorInfo!!)
                            } else {
                                showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 401 -> {
                            tokenRefresh!!.doTokenRefresh(context!!, tokenRefreshSel)
                        }
                        response.code() == 500 -> {
                            // afterLoad()
                            showToast("Server Error!")
                        }
                    }
                }

                override fun onFailure(call: Call<VisitorInfoApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    // afterLoad()
                }
            })
    }

    private fun autoFillFields(visitorInfo: VisitorInfo) {
        nameTxtIP!!.editText!!.setText(visitorInfo.visitorName!!)
        compTxtIP!!.editText!!.setText(visitorInfo.visitorCompany!!)
        emailTxtIP!!.editText!!.setText(visitorInfo.visitorEmail!!)
        mobTxtIP!!.editText!!.setText(visitorInfo.visitorMobile!!)

        val idPos = getSelectedIdPos(visitorInfo.iDProofCode!!)
        if (!isForSelfApproval!!) {
            idCardSpinner!!.setSelection(idPos)
            personalIdNumTxtIP!!.editText!!.setText(visitorInfo.proofDetails!!)
        }

        visitorID = visitorInfo.visitorID!!
        disableEditing(nameTxtIP!!)
        disableEditing(compTxtIP!!)
        disableEditing(emailTxtIP!!)
        disableEditing(mobTxtIP!!)
        disableEditing(personalIdNumTxtIP!!)
    }

    private fun disableEditing(txtIp: TextInputLayout) {
        txtIp.editText!!.isClickable = false
        txtIp.editText!!.isEnabled = false
    }

    private fun enableEditing(txtIp: TextInputLayout) {
        txtIp.editText!!.isClickable = true
        txtIp.editText!!.isEnabled = true

    }

    private fun getSelectedCategoryPos(savedItem: String): Int {
        var pos = 0

        for (i in 0..visitorCategoriesList.size) {
            if (visitorCategoriesList[i].categoryCode == savedItem) {
                pos = i
                break
            }
        }

        return pos
    }

    private fun getSelectedIdPos(savedItem: String): Int {
        var pos = 0

        for (i in 0 until idCardList.size) {
            if (idCardList[i].iDProofCode == savedItem) {
                pos = i
                break
            }
        }
        return pos
    }

    // Image Capture 2.0 Logic

    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = System.currentTimeMillis()
            .toString() //SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "EntryPassImage${timeStamp}", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                photoFile = try {
                    createImageFile()

                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    showToast(ex.message!!)
                    null
                }
                // Continue only if the File was successfully created
                try {
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "com.idbsoftek.vms.fileprovider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        takePictureIntent.flags =
                            FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivityForResult(takePictureIntent, CAMERA_ACCESS_REQ_CODE)
                    }
                } catch (e: Exception) {
                    showToast("Seems like storage is less!")
                }

            }
        }
    }

    override fun onTokenRefresh(responseCode: Int, token: String) {
        afterFormSubmit()
        when (responseCode) {
            401 -> {
                AppUtil.onSessionOut(context!!)
            }
            200 -> {
                loadSetUpApi()
            }
            else -> {
                AppUtil.onSessionOut(context!!)
            }
        }
    }

}