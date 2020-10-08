package com.idbsoftek.vms.setup.form

import com.idbsoftek.vms.setup.log_list.AscRecord

interface AssociatesActionable {
    fun onAscActionClick(asc: AscRecord, action: Int, isFromMultiDayCheckIn: Boolean)
}