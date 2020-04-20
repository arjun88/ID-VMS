package com.idbsoftek.vms.util

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CalendarUtils {
    companion object {
        @SuppressLint("SimpleDateFormat")
        fun isFirstDateLesserThanSecondDate(
            first: String?,
            second: String?,
            format: String?
        ): Boolean {
            var status: Boolean? = false

            val dateFormat = SimpleDateFormat(format!!)
              /*  SimpleDateFormat.getDateTimeInstance()
            dateFormat.format(format)*/

            val convertedDate: Date?
            val convertedDate2: Date?
            try {
                convertedDate = dateFormat.parse(first!!)
                convertedDate2 = dateFormat.parse(second!!)

                status = convertedDate2!!.after(convertedDate)
                if (convertedDate == convertedDate2)
                    status = true
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            return status!!
        }

        fun getTodayDate(): String {
            val curDate: String

            val sdf = SimpleDateFormat("MM-dd-yyyy")

            curDate = sdf.format(Date())
            return curDate
        }

        fun getDateInRequestedFormat(inputFormat: String, apiFormat: String, date: String): String {
            var apiDate: String?

            val sdf = SimpleDateFormat(apiFormat)
            val dateToGet = SimpleDateFormat(inputFormat).parse(date)

            apiDate = sdf.format(dateToGet!!) //

            return apiDate
        }

        fun getCurrentTime(): String {

            val curTime: String

            val sdf = SimpleDateFormat("HH:mm:ss")

            curTime = sdf.format(Date())
            return curTime
        }

        //Get num of days in month and year

        fun getNumOfDaysInTheMonth(year: Int?, month: Int?): Int {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year!!)
            calendar.set(Calendar.MONTH, month!!)

            return calendar.getActualMaximum(Calendar.DATE)
        }

        fun getDayName(calendar: Calendar): Int {
            return calendar.get(Calendar.DAY_OF_WEEK)
        }
    }


}
