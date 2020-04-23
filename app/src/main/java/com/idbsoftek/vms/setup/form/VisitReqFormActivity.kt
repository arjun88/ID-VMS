package com.idbsoftek.vms.setup.form

import android.Manifest
import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
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
import com.google.gson.Gson
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.VmsMainActivity
import com.idbsoftek.vms.setup.api.*
import com.idbsoftek.vms.util.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class VisitReqFormActivity() : VmsMainActivity(), AdapterView.OnItemSelectedListener,
    DateTimeSelectable, EmpSelectionClickable, Parcelable {
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

    private var visitorCategoriesList = ArrayList<VisitorCategoryList>()
    private var visitorPurposesList = ArrayList<VisitorPurposeList>()
    private var toMeetList = ArrayList<EmpListItem>()
    private var idCardList = ArrayList<VisitorCategoryList>()

    private var fromDateTV: AppCompatTextView? = null
    private var toDateTV: AppCompatTextView? = null
    private var fromTimeTV: AppCompatTextView? = null
    private var toTimeTV: AppCompatTextView? = null
    private var assocaiteCountTV: AppCompatTextView? = null

    private var fromDateView: View? = null
    private var toDateView: View? = null

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

    private var fromTimeView: LinearLayoutCompat? = null
    private var toTimeView: LinearLayoutCompat? = null

    private var isMeOption: Boolean? = false
    private var isForSelfApproval: Boolean? = false

    private val permissionsReq = arrayOf(
        CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onEmpSelectionClick(emp: EmpListItem) {
        //val empCodeName = "${emp.code} - ${emp.name}"
        appointWithTV!!.text = emp.name
        toMeetSel = emp.code
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_visit_req_form)
        isForSelfApproval = intent.getBooleanExtra("SELF_APPROVAL", false)

        setActionBarTitle("Visit Request")

        context = this
        augDatePicker = AugDatePicker(context!!, this)

        initView()
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
        if (requestCode == CAMERA_RETURN_CODE && resultCode == RESULT_OK) {
            assert(data != null)
            val cameraBitmap: Bitmap?
            val thumbnailImage: Bitmap
            cameraBitmap = getBitmapFromUri(Uri.fromFile(photoFile))

            //FOR ACTUAL IMG ************
            // visitorImg = ImageUtil.encodeImage(getRotatedBitmap(cameraBitmap!!))

            thumbnailImage = ImageUtil.getThumbnailImage(cameraBitmap, 164, 196)
//            thumbnailImage = ImageUtil.getThumbnailImage(getRotatedBitmap(cameraBitmap!!), 164, 196)

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

            idCardSelPos = PrefUtil.getIdPosVMS()
            idCardSpinner!!.setSelection(idCardSelPos!!)
            Log.e("---", "ID SEL: ${idCardSelPos}")

            /*  toMeelSelPos = data!!.getIntExtra("TO_MEET_POS",0)
              toMeetSpinner!!.setSelection(toMeelSelPos!!)

              categorySelPos = data.getIntExtra("CAT_POS",0)
              categorySpinner!!.setSelection(categorySelPos!!)

              purposeSelPos = data.getIntExtra("PURPOSE_POS",0)
              purposeSpinner!!.setSelection(purposeSelPos!!)

              idCardSelPos = data.getIntExtra("ID_CARD_POS",0)
              idCardSpinner!!.setSelection(idCardSelPos!!)*/

            if (!imageFromPopUp)
                toggleImageView()
            else
                toggleImageViewInPopUp()
        }
    }

    private fun checkForCameraPermission(): Boolean {

        for (permission in permissionsReq) {
            if (ActivityCompat.checkSelfPermission(context!!, CAMERA)
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
        /* PrefUtil.savePosOfCategoryVMS(categorySelPos!!)
         PrefUtil.savePosOfPurposeVMS(purposeSelPos!!)
         PrefUtil.savePosOfIdVMS(idCardSelPos!!)*/

        Log.e("---", "ID IN SEL: ${idCardSelPos}")

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            try {
                photoFile = ImageUtil.createImageFile(
                    context!!,
                    "jpg", "Visitor"
                )
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                cameraImageUri = FileProvider.getUriForFile(
                    context!!,
                    "com.idbsoftek.vms.fileprovider",
                    photoFile!!
                )
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                takePictureIntent.clipData = ClipData.newRawUri(null, cameraImageUri)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
                /* takePictureIntent.putExtra("TO_MEET_POS", toMeelSelPos!!)
                 takePictureIntent.putExtra("CAT_POS", categorySelPos!!)
                 takePictureIntent.putExtra("PURPOSE_POS", purposeSelPos!!)
                 takePictureIntent.putExtra("ID_CARD_POS", idCardSelPos!!)*/
                startActivityForResult(takePictureIntent, CAMERA_RETURN_CODE)
            }
        }
    }

    private var imageFromPopUp: Boolean = false

    private fun setUpCamera() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkForCameraPermission()) {
                reqCameraAccess()
            } else
                openCamera()
        } else
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
                        openCamera()
                    else
                        reqCameraAccess()
                } else {
                    reqCameraAccess()
                }
            }
        }
    }

    private fun initView() {
        fromTimeView = findViewById(R.id.from_time_view)
        toTimeView = findViewById(R.id.to_time_view)

        appointWithTV = findViewById(R.id.app_with_tv)
        toMeetSpinner = findViewById(R.id.to_meet_form_spinner_vms)
        categorySpinner = findViewById(R.id.category_spinner_form_vms)
        purposeSpinner = findViewById(R.id.purpose_spinner_form_vms)
        idCardSpinner = findViewById(R.id.id_spinner_vms_form)

        nameTxtIP = findViewById(R.id.name_txt_ip_form_vms)
        mobTxtIP = findViewById(R.id.mob_txt_ip_form_vms)
        compTxtIP = findViewById(R.id.comp_txt_ip_form_vms)
        vehNumTxtIP = findViewById(R.id.id_veh_num_txt_ip_form_vms)
        visitorIdNumTxtIP = findViewById(R.id.id_num_txt_ip_form_vms)
        personalIdNumTxtIP = findViewById(R.id.personal_id_txt_ip_form_vms)
        assetsTxtIP = findViewById(R.id.assets_txt_ip_form_vms)

        visitorPhotoIV = findViewById(R.id.visitor_photo_iv_form_vms)
        addIV = findViewById(R.id.add_photo_iv_form_vms)

        fromDateView = findViewById(R.id.from_date_view_from_vms)
        toDateView = findViewById(R.id.to_date_view_from_vms)

        assocaiteCountTV = findViewById(R.id.associate_added_count_tv)
        fromDateTV = findViewById(R.id.from_date_tv_form_vms)
        toDateTV = findViewById(R.id.to_date_tv_form_mvs)
        fromTimeTV = findViewById(R.id.from_time_vms_form)
        toTimeTV = findViewById(R.id.to_time_form_vms)

        progress = findViewById(R.id.vms_form_progress)
        submitBtn = findViewById(R.id.form_submit_btn_vms)
        addAssociateBtn = findViewById(R.id.add_associate_btn_form)

        assocaiteCountTV!!.setOnClickListener {
            if (associatesAddedList.size > 0)
                moveToAssociatesFragment()
        }

        /*idCardList =
            VMSUtil.getIdCardTypes()*/

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

        fromTimeTV!!.setOnClickListener {
            augDatePicker!!.showTimePicker(
                isFromTime = true, inTime = "", outTime = ""
            )
        }

        toTimeTV!!.setOnClickListener {
            augDatePicker!!.showTimePicker(
                isFromTime = false, inTime = "", outTime = ""
            )
        }

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
            if (!this.isForSelfApproval!!) {
                moveToEmpSelectScreen()
            }
        }

        afterFormSubmit()

        addAssociateBtn!!.setOnClickListener {
            showAddAssociatesPopUp()
        }

        val activity = this

        submitBtn!!.setOnClickListener {
            if (AppUtil.isInternetThere(context!!)) {
                var formData = VisitorFormSubmitData()

                var associates = ArrayList<AssociatesItem>()
                associates = associatesAddedList

                formData.associates = associates
                formData.appointmentWith = toMeetSel

                formData.visitorName = nameTxtIP!!.editText!!.text.toString()
                formData.mob = mobTxtIP!!.editText!!.text.toString()
                formData.visitorCompany = compTxtIP!!.editText!!.text.toString()

                formData.idNum = personalIdNumTxtIP!!.editText!!.text.toString()
                formData.visitorIdNum = visitorIdNumTxtIP!!.editText!!.text.toString()
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
                //  AppUtil.EMP_ID_VMS //

                if (validateFieldsAndContinue(formData)) {
                    reqNewVisit(formData)
                }

            } else {
                showToast("No Internet!")
            }
        }

        setIdCardDD()
        if (AppUtil.isInternetThere(context!!)) {
            getVisitorCategoryApi()
            getToMeetApi()
            getPurposeApi()
            getIdCardsApi()
        } else {
            showToast("No Internet!")
        }
    }

    //API ****************************

    private fun getToMeetApi() {
        // onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(this)
        val url = "${prefUtil.appBaseUrl}EmployeeList"


        apiCallable.getToMeetList(
            url, prefUtil.userName, prefUtil.userName
        )
            .enqueue(object : Callback<ToMeetApiResponse> {
                override fun onResponse(
                    call: Call<ToMeetApiResponse>,
                    response: Response<ToMeetApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                toMeetList.clear()
                                toMeetForDD.clear()

                                for (element in visitorLogApiResponse.empList!!) {
                                    toMeetList.add(element!!)
                                }

                                setToMeetDD()
                                //afterLoad()
                            } else {
                                //afterLoad()
                                showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 500 -> {
                            //  afterLoad()
                        }
                    }
                }

                override fun onFailure(call: Call<ToMeetApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    // afterLoad()
                }
            })
    }

    private fun getPurposeApi() {
        // onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(this)
        val url = "${prefUtil.appBaseUrl}VisitorPurpose"

        apiCallable.getVisitorPurpose(
            url, prefUtil.userName, prefUtil.userName
        )
            .enqueue(object : Callback<VisitorPurposeApiResponse> {
                override fun onResponse(
                    call: Call<VisitorPurposeApiResponse>,
                    response: Response<VisitorPurposeApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                visitorPurposesList.clear()
                                visitorPurposes.clear()

                                for (i in 0 until visitorLogApiResponse.visitorPurposeList.size) {
                                    visitorPurposesList.add(visitorLogApiResponse.visitorPurposeList[i])
                                }

                                setPurposeDD()
                                //afterLoad()
                            } else {
                                //afterLoad()
                                showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 500 -> {
                            //  afterLoad()
                        }
                    }
                }

                override fun onFailure(call: Call<VisitorPurposeApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    // afterLoad()
                }
            })
    }

    private fun getIdCardsApi() {
        // onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(this)
        Log.e("BASE_URL: ", "" + prefUtil.appBaseUrl)
        val url = "${prefUtil.appBaseUrl}IDProof"

        apiCallable.getVisitorCategories(
            url, prefUtil.userName, prefUtil.sessionID
        )
            .enqueue(object : Callback<VisitorCategoryApiResponse> {
                override fun onResponse(
                    call: Call<VisitorCategoryApiResponse>,
                    response: Response<VisitorCategoryApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                idCardList.clear()
                                idCardForDD.clear()

                                for (i in 0 until visitorLogApiResponse.idProofList.size) {
                                    idCardList.add(visitorLogApiResponse.idProofList[i])
                                }

                                setIdCardDD()
                                //afterLoad()
                            } else {
                                //afterLoad()
                                showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 500 -> {
                            //  afterLoad()
                        }
                    }
                }

                override fun onFailure(call: Call<VisitorCategoryApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    // afterLoad()
                }
            })
    }


    private fun getVisitorCategoryApi() {
        // onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(this)
        Log.e("BASE_URL: ", "" + prefUtil.appBaseUrl)
        val url = "${prefUtil.appBaseUrl}VisitorCategory"

        apiCallable.getVisitorCategories(
            url, prefUtil.userName, prefUtil.userName
        )
            .enqueue(object : Callback<VisitorCategoryApiResponse> {
                override fun onResponse(
                    call: Call<VisitorCategoryApiResponse>,
                    response: Response<VisitorCategoryApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                visitorCategoriesList.clear()
                                visitorCategories.clear()

                                for (i in 0 until visitorLogApiResponse.visitorCategoryList.size) {
                                    visitorCategoriesList.add(visitorLogApiResponse.visitorCategoryList[i])
                                }

                                setCategoryDD()
                                //afterLoad()
                            } else {
                                //afterLoad()
                                showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 500 -> {
                            //  afterLoad()
                        }
                    }
                }

                override fun onFailure(call: Call<VisitorCategoryApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    // afterLoad()
                }
            })
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("DefaultLocale")
    private fun setToMeetDD() {
        for (i in 0 until toMeetList.size) {
            val name = toMeetList[i].name!!//.toLowerCase().capitalize()
            toMeetForDD.add(name)
        }

        val adapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_item, toMeetForDD
        )

        // Set layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        toMeetSpinner!!.adapter = adapter
        toMeetSpinner!!.onItemSelectedListener = this
    }

    private fun moveToEmpSelectScreen() {
        val fragment = EmpPickerFragment()

        val bundle = Bundle()


        fragment.empClickInit(this)
        fragment.arguments = bundle
        moveToFragment(fragment)
    }

    private fun moveToFragment(tarFragment: Fragment) {
        val bundle = Bundle()
        bundle.putBoolean("IS_FOR_REF_NUM", false)
        tarFragment.arguments = bundle

        val fragmentManager = supportFragmentManager

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(
            R.id.vms_form_view,
            tarFragment
        )
        fragmentTransaction.addToBackStack(null).commit()
    }

    @SuppressLint("DefaultLocale")
    private fun setCategoryDD() {
        for (i in 0 until visitorCategoriesList.size) {
            val name = visitorCategoriesList[i].name.toLowerCase().capitalize()

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
            val name = element.name!!.toLowerCase().capitalize()

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
            val name = visitorPurposesList[i].name.toLowerCase().capitalize()

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
                categorySel = visitorCategoriesList[p2].code

                // categorySelPos = p2
                PrefUtil.savePosOfCategoryVMS(p2)

                multiEntry = visitorCategoriesList[p2].meOption
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
                }
            }

            purposeSpinner -> {
                purposeSel = visitorPurposesList[p2].code
                // purposeSelPos = p2
                PrefUtil.savePosOfPurposeVMS(p2)
            }

            toMeetSpinner -> {
                toMeetSel = toMeetList[p2].code!!
                toMeelSelPos = p2
            }

            idCardSpinner -> {
                idCardSel = idCardList[p2].code!!
                //  idCardSelPos = p2
                PrefUtil.savePosOfIdVMS(p2)
            }

            idSpinnerInPopUp -> {
                idSelInPopUp = idCardList[p2].code!!
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

    //REQ VISIT API AND VALIDATION

    private fun validateFieldsAndContinue(reqFormData: VisitorFormSubmitData): Boolean {
        var isValidated = false

        when {
            reqFormData.appointmentWith == "" -> showToast("Please Select Employee To Meet With")
            reqFormData.visitorName == "" -> showToast("Please Enter Visitor Name")
            reqFormData.mob == "" -> showToast("Please Enter Visitor Mobile Number")
            //    reqFormData.visitorCompany == "" -> showToast("Please Enter Visitor Company")
            reqFormData.visitorName == "" -> showToast("Please Enter Visitor Name")

            reqFormData.idNum == "" -> showToast("Please Enter Visitor ID Proof Number")
            //reqFormData.idNum == "" ->showToast("Please Enter Visitor Name")

            !PrefUtil.isVisitorImgOptional() -> {
                when (reqFormData.visitorPhoto) {
                    "" -> showToast("Please Add Visitor Photo")
                    else -> {
                        when (isMeOption) {
                            true -> {
                                when {
                                    reqFormData.fromDate!!.isEmpty() -> showToast("Please Select From Date")
                                    reqFormData.toDate!!.isEmpty() -> showToast("Please Select To Date")
                                    else -> {
                                        if (CalendarUtils.isFirstDateLesserThanSecondDate(
                                                reqFormData.fromDate,
                                                reqFormData.toDate, "dd-MM-yyyy"
                                            )
                                        )
                                            isValidated = true
                                        else
                                            showToast("From Date can't be greater than To Date")
                                    }

                                }
                            }

                            false -> {
                                when {
                                    reqFormData.fromTime!!.isEmpty() -> showToast("Please Select From Time")
                                    reqFormData.toTime!!.isEmpty() -> showToast("Please Select To Time")
                                    else -> {
                                        if (CalendarUtils.isFirstDateLesserThanSecondDate(
                                                reqFormData.fromTime,
                                                reqFormData.toTime, "HH:mm"
                                            )
                                        )
                                            isValidated = true
                                        else
                                            showToast("From Time can't be greater than To Time")
                                    }

                                }
                            }
                        }
                    }
                }

            }

            reqFormData.visitorCategory == "" -> showToast("Please Select Visitor Category")
            reqFormData.visitorPurpose == "" -> showToast("Please Add Visitor Purpose")

            isMeOption == true -> {
                when {
                    reqFormData.fromDate!!.isEmpty() -> showToast("Please Select From Date")
                    reqFormData.toDate!!.isEmpty() -> showToast("Please Select To Date")
                    else -> {
                        if (CalendarUtils.isFirstDateLesserThanSecondDate(
                                reqFormData.fromDate,
                                reqFormData.toDate, "dd-MM-yyyy"
                            )
                        )
                            isValidated = true
                        else
                            showToast("From Date can't be greater than To Date")
                    }

                }
            }

            isMeOption == false -> {
                when {
                    reqFormData.fromTime!!.isEmpty() -> showToast("Please Select From Time")
                    reqFormData.toTime!!.isEmpty() -> showToast("Please Select To Time")
                    else -> {
                        if (CalendarUtils.isFirstDateLesserThanSecondDate(
                                reqFormData.fromTime,
                                reqFormData.toTime, "HH:mm"
                            )
                        )
                            isValidated = true
                        else
                            showToast("From Time can't be greater than To Time")
                    }

                }
            }

            else -> isValidated = true
        }
        return isValidated
    }

    private fun reqNewVisit(visitData: VisitorFormSubmitData) {
        onFormSubmit()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )

        val gson = Gson()
        val requestBody: RequestBody = RequestBody.create(
            MediaType.parse("application/json")
            , gson.toJson(visitData)
        )

        Log.e("REQ TRIP VALIDATE", "POST: " + gson.toJson(requestBody))
        val prefUtil = PrefUtil(this@VisitReqFormActivity)
        var url = "${prefUtil.appBaseUrl}VisitorEntryPass"
        if (isForSelfApproval!!)
            url = "${prefUtil.appBaseUrl}SelfApproval"
        apiCallable.submitFormApi(
            url,
            requestBody
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
        var idNumTxtIP: TextInputLayout? = null
        var visitorIdTxtIP: TextInputLayout? = null
        var assetsTxtIP: TextInputLayout? = null

        nameTxtIP = view.findViewById(R.id.name_txt_ip_form_vms_pop)
        mobTxtIP = view.findViewById(R.id.mob_txt_ip_form_vms_pop)
        idNumTxtIP = view.findViewById(R.id.personal_id_txt_ip_form_vms_pop)
        visitorIdTxtIP = view.findViewById(R.id.id_num_txt_ip_form_vms_pop)
        assetsTxtIP = view.findViewById(R.id.assets_txt_ip_form_vms_pop)
        idSpinnerInPopUp = view.findViewById(R.id.id_spinner_vms_form_pop)

        addIVPopUp = view.findViewById(R.id.add_photo_iv_pop_up)
        assocIVPopUp = view.findViewById(R.id.ass_photo_iv_form_vms)

        view.findViewById<MaterialCardView>(R.id.camera_visitor_img_cv).setOnClickListener {
            imageFromPopUp = true
            setUpCamera()
        }

        idCardForDD.clear()

        for (element in idCardList) {
            val name = element.name!!.toLowerCase().capitalize()

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
                var associate = AssociatesItem()

                associate.name = nameTxtIP.editText!!.text.toString()
                associate.mob = mobTxtIP.editText!!.text.toString()
                associate.idNum = idNumTxtIP.editText!!.text.toString()
                associate.visitorIdNum = visitorIdTxtIP.editText!!.text.toString()
                associate.assets = assetsTxtIP.editText!!.text.toString()
                associate.associatePhoto = associateImage
                associate.idProof = idSelInPopUp

                when {
                    associate.name!!.isEmpty() -> {
                        showToast("Please Provide Associate Name")
                    }
                    associate.mob!!.isEmpty() -> {
                        showToast("Please Provide Associate Mobile Number")
                    }
                    idSelInPopUp!!.isEmpty() -> {
                        showToast("Please Select Personal ID")
                    }
                    associate.idNum!!.isEmpty() -> {
                        showToast("Please Provide Personal ID Number")
                    }
                    associate.visitorIdNum!!.isEmpty() -> {
                        showToast("Please Provide Visitor ID Num")
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
    private fun addAssociate(associate: AssociatesItem) {
        associatesAddedList.add(associate)
        assocaiteCountTV!!.text = "${associatesAddedList.size} Associates Added"
        showToast("New Associate Added Successfully!")
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

            assocaiteCountTV!!.text = "${associatesAddedList.size} Associates Added"
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
}
