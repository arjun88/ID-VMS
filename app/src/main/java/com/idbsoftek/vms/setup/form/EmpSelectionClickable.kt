package com.idbsoftek.vms.setup.form

import com.idbsoftek.vms.setup.api.EmpListItem

interface EmpSelectionClickable {
    fun onEmpSelectionClick(emp: EmpListItem)
}