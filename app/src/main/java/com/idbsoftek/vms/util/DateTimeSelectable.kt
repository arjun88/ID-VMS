package com.idbsoftek.vms.util

interface DateTimeSelectable {
    fun onFromDateSelected(date: String)
    fun onToDateSelected(date: String)
    fun onDateSelected(date: String)
    fun onFromTimeSelected(time: String)
    fun onToTimeSelected(time: String)
}