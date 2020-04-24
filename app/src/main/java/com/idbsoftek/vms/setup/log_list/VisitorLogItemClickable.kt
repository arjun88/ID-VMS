package com.idbsoftek.vms.setup.log_list

interface VisitorLogItemClickable {
    fun onVisitorLogItemClick(id: String, date: String  )

    fun onVisitorLogAction(id: String, action: String)

    fun onVisitorImgClick(image: String)
}