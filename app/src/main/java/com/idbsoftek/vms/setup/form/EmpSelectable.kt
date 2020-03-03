package com.idbsoftek.vms.setup.form

import com.idbsoftek.vms.setup.api.EmpListItem

interface EmpSelectable {
    fun onEmpSelection(emp: EmpListItem)
}