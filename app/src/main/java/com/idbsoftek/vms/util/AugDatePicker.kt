package com.idbsoftek.vms.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.util.*

class AugDatePicker(context: Context, dateTimeSelectable: DateTimeSelectable) {
    private var context: Context? = null
    private var dateTimeSelectable: DateTimeSelectable? = null

    init {
        this.context = context
        this.dateTimeSelectable = dateTimeSelectable
    }

    // DATE PICKER

    private var mYear: Int = 2019
    private var mMonth: Int = 1
    private var mDay: Int = 1

    fun showDatePicker(
        isFromDate: Boolean,
        isSingleDate: Boolean,
        fromDate: String,
        toDate: String,
        futureDateCanbeSelected: Boolean
    ) {
        val c = Calendar.getInstance()

        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)
        mYear = c.get(Calendar.YEAR)

        c.set(Calendar.MONTH, Calendar.JANUARY)
        c.set(Calendar.DAY_OF_MONTH, 1)
        c.add(Calendar.YEAR, -2)

        if (isFromDate) {
            if (fromDate.isNotEmpty()) {
                val fromDateArray = fromDate.split("-")

                mMonth = fromDateArray[1].toInt() - 1
                mDay = fromDateArray[2].toInt()
                mYear = fromDateArray[0].toInt()
            }
        } else {
            if (toDate.isNotEmpty()) {
                val toDateArray = toDate.split("-")

                mMonth = toDateArray[1].toInt() - 1
                mDay = toDateArray[2].toInt()
                mYear = toDateArray[0].toInt()

            }
        }
        val datePickerDialog = DatePickerDialog(
            context!!,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val dateString: String = if (dayOfMonth < 10) {
                    "0$dayOfMonth"
                } else
                    dayOfMonth.toString()
                val monthString: String = if (monthOfYear + 1 < 10) {
                    "0" + (monthOfYear + 1)
                } else
                    (monthOfYear + 1).toString()

                if (isSingleDate) {
                    dateTimeSelectable!!.onDateSelected("$year-$monthString-$dateString")
                } else {
                    if (isFromDate)
                        dateTimeSelectable!!.onFromDateSelected("$year-$monthString-$dateString")
                    else
                        dateTimeSelectable!!.onToDateSelected("$year-$monthString-$dateString")
                }


            }, mYear, mMonth, mDay
        )

        if (futureDateCanbeSelected) {
            datePickerDialog.datePicker.minDate = Date().time
        } else {
            datePickerDialog.datePicker.minDate = c.timeInMillis
            datePickerDialog.datePicker.maxDate = Date().time
        }
        datePickerDialog.show()
    }

    // TIME PICKER
    private var mHour: Int = 0
    private var mMinute: Int = 0

    fun showTimePicker(
        isFromTime: Boolean,
        inTime: String,
        outTime: String
    ) {
        if (isFromTime) {
            if (inTime.isNotEmpty()) {
                val fromTimeArray = inTime.split(":")

                mHour = fromTimeArray[0].toInt()
                mMinute = fromTimeArray[1].toInt()
            } else {
                mHour = 0
                mMinute = 0
            }
        } else {
            if (outTime.isNotEmpty()) {
                val toTimeArray = outTime.split(":")

                mHour = toTimeArray[0].toInt()
                mMinute = toTimeArray[1].toInt()
            } else {
                mHour = 0
                mMinute = 0
            }
        }

        val timePickerDialog = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val hourString: String = if (hourOfDay < 10) {
                    "0$hourOfDay"
                } else
                    hourOfDay.toString()
                val minString: String = if (minute < 10) {
                    "0$minute"
                } else
                    minute.toString()

                if (isFromTime)
                    dateTimeSelectable!!.onFromTimeSelected("$hourString:$minString")
                else
                    dateTimeSelectable!!.onToTimeSelected("$hourString:$minString")

            }, mHour, mMinute, false
        )

        timePickerDialog.show()
    }
}