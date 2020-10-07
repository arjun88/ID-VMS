package com.idbsoftek.vms.util

import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


object ImageUtil {

    fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        val encImage =
            Base64.encodeToString(b, Base64.DEFAULT)
        print("---------- ENCODED IMAGE: $encImage")
        Log.i("STRING IMG", encImage)
        return encImage
    }

    fun encodeTobase64(image: Bitmap): String {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        val imageEncoded =
            Base64.encodeToString(b, Base64.NO_WRAP)
        print("---------- ENCODED IMAGE: $imageEncoded")
        Log.e("LOOK", imageEncoded)
        return imageEncoded
    }

    fun encodeFromString(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos) //bm is the bitmap object
        val b = baos.toByteArray()
        println(
            "---------- ENCODED IMAGE: " + Base64.encodeToString(
                b,
                Base64.DEFAULT
            )
        )
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun getBitmapFromString(encodedImage: String?): Bitmap? {
        return try {
            val decodedString =
                Base64.decode(encodedImage, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        } catch (e: Exception) {
            null
        }
    }

    fun imageString(bitmap: Bitmap): String? {
        var encodedImage: String? = null
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        try {
            encodedImage =
                Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (error: OutOfMemoryError) {
            error.printStackTrace()
        }
        return encodedImage
    }

    fun compressImage(context: Context, imageUri: String?): String {
        val filePath =
            getRealPathFromURI(context, imageUri)
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()
        //      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(filePath, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth
        //      max Height and width values of the compressed image is taken as 816x612
        val maxHeight = 816.0f
        val maxWidth = 612.0f
        var imgRatio = actualWidth / actualHeight.toFloat()
        val maxRatio = maxWidth / maxHeight
        //      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }
        //      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(
            options,
            actualWidth,
            actualHeight
        )
        //      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false
        //      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try { //          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )
        //      check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(filePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0
            )
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 3) {
                matrix.postRotate(180f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 8) {
                matrix.postRotate(270f)
                Log.d("EXIF", "Exif: $orientation")
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0,
                scaledBitmap.width, scaledBitmap.height, matrix,
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var out: FileOutputStream? = null
        val filename = filename
        try {
            out = FileOutputStream(filename)
            //          write the compressed bitmap at the destination specified by filename.
//   scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            val bytes = ByteArrayOutputStream()
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val f = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + "test.jpg"
            )
            if (f.createNewFile()) {
                Log.e("PHOTO", "created")
            }
            //write the bytes in file
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            // remember close de FileOutput
            fo.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return filename
    }

    val filename: String
        get() {
            val file = File(
                Environment.getExternalStorageDirectory().path,
                "MyFolder/Images"
            )
            if (!file.exists()) {
                file.mkdirs()
            }
            return file.absolutePath + "/" + System.currentTimeMillis() + ".jpg"
        }

    fun getRealPathFromURI(
        context: Context,
        contentURI: String?
    ): String? {
        val contentUri = Uri.parse(contentURI)
        val cursor =
            context.contentResolver.query(contentUri, null, null, null, null)
        return if (cursor == null) {
            contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(index)
        }
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio =
                Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio =
                Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = width * height.toFloat()
        val totalReqPixelsCap = reqWidth * reqHeight * 2.toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }

    fun getCompressedImage(context: Context, fileName: String?): Bitmap {
        var original: Bitmap? = null
        try {
            original = BitmapFactory.decodeStream(context.assets.open(fileName!!))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val out = ByteArrayOutputStream()
        assert(original != null)
        original!!.compress(Bitmap.CompressFormat.PNG, 100, out)
        return BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 0) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun loadResizedBitmap(
        filename: String?,
        width: Int,
        height: Int,
        exact: Boolean
    ): Bitmap? {
        var bitmap: Bitmap? = null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filename, options)
        if (options.outHeight > 0 && options.outWidth > 0) {
            options.inJustDecodeBounds = false
            options.inSampleSize = 2
            while (options.outWidth / options.inSampleSize > width
                && options.outHeight / options.inSampleSize > height
            ) {
                options.inSampleSize++
            }
            options.inSampleSize--
            bitmap = BitmapFactory.decodeFile(filename, options)
            if (bitmap != null && exact) {
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
            }
        }
        return bitmap
    }

    @Throws(IOException::class)
    fun createImageFile(
        context: Context,
        format: String,
        claimType: String
    ): File { // Create an image file name
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = claimType + timeStamp
        val storageDir =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".$format",  /* suffix */
            storageDir /* directory */
        )
        assert(storageDir != null)
        println("***** image saved")
        return image
    }

    fun getThumbnailImage(bitmap: Bitmap?, w: Int, h: Int): Bitmap {
        return ThumbnailUtils.extractThumbnail(
            bitmap,
            w,
            h
        )
    }

    @Throws(IOException::class)
    fun getCapturedImage(
        context: Context, selectedImage: Uri,
        typeOfClaim: String
    ): Bitmap? {
        val MAX_HEIGHT = 1024
        val MAX_WIDTH = 1024
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var imageStream =
            context.contentResolver.openInputStream(selectedImage)
        BitmapFactory.decodeStream(imageStream, null, options)
        assert(imageStream != null)
        imageStream!!.close()
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSizeImage(
            options,
            MAX_WIDTH,
            MAX_HEIGHT
        )
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        imageStream = context.contentResolver.openInputStream(selectedImage)
        var img = BitmapFactory.decodeStream(imageStream, null, options)
        img =
            rotateImageIfRequired(img!!, selectedImage)
        val bytes = ByteArrayOutputStream()
        img!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "$typeOfClaim$timeStamp.jpg"
        val f = File(
            Environment.getExternalStorageDirectory().toString() +
                    File.separator + "ClaimPics"
                    + File.separator + imageFileName
        )
        if (f.createNewFile()) {
            Log.e("PHOTO", "created")
        }
        return img
    }

    private fun calculateInSampleSizeImage(
        options: BitmapFactory.Options,
        reqWidth: Int, reqHeight: Int
    ): Int { // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) { // Calculate ratios of height and width to requested height and width
            val heightRatio =
                Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio =
                Math.round(width.toFloat() / reqWidth.toFloat())
            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
// with both dimensions larger than or equal to the requested height and width.
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
            // This offers some additional logic in case the image has a strange
// aspect ratio. For example, a panorama may have a much larger
// width than height. In these cases the total pixels might still
// end up being too large to fit comfortably in memory, so we should
// be more aggressive with sample down the image (=larger inSampleSize).
            val totalPixels = width * height.toFloat()
            // Anything more than 2x the requested pixels we'll sample down further
            val totalReqPixelsCap = reqWidth * reqHeight * 2.toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }
        }
        return inSampleSize
    }

    /*@Throws(IOException::class)
    private fun rotateImageIfRequired(
        context: Context,
        img: Bitmap?,
        selectedImage: Uri
    ): Bitmap? {
        val input =
            context.contentResolver.openInputStream(selectedImage)
        val ei: ExifInterface
        ei =
            if (Build.VERSION.SDK_INT > 23) ExifInterface(input) else ExifInterface(selectedImage.path)
        val orientation =
            ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(
                img,
                90
            )
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(
                img,
                180
            )
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(
                img,
                270
            )
            else -> img
        }*/

    @Throws(IOException::class)
     fun rotateImageIfRequired(img: Bitmap, selectedImage: Uri): Bitmap? {
        val ei = ExifInterface(selectedImage.path!!)
        val orientation =
            ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
            else -> img
        }
    }


    private fun rotateImage(img: Bitmap?, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg =
            Bitmap.createBitmap(img!!, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }
}