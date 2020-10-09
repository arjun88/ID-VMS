package com.idbsoftek.vms.setup.analytics

import com.idbsoftek.vms.setup.log_list.VisitorListItem

interface AdminActionable {
    fun onAdminAction(visitor: VisitorListItem)
}