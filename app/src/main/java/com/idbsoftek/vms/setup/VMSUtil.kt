package com.idbsoftek.vms.setup

class VMSUtil {
    companion object {
        val APPROVE_ACTION = "Approve"
        val REJECT_ACTION = "Reject"
        val COMPLETE_ACTION = "Completed"
        val ALLOW_ACTION = "CheckIn"
        val EXIT_ACTION = "Exit"

         val APPROVE_REJECT_BTN_ENABLED = "Pending"
         val COMPLETED_BTN_ENABLED = "CheckIn"
         val ADMIN_COMPLETED_BTN_ENABLED = "CheckIn"
         val ALLOW_BTN_ENABLED = "Approved"
         val REJECTED = "Rejected"
         val COMPLETED = "Completed"
         val EXIT_BTN_ENABLED = "Completed"
         val ADMIN_EXIT_BTN_ENABLED = "Completed"
         val EXPIRED = "Expired"

        fun getIdCardTypes(): List<IdCard>{
            var idCardList = ArrayList<IdCard>()

            var idCard = IdCard("DL","Driving License")
            idCardList.add(idCard)

            idCard = IdCard("AC","Aadhaar Card")
            idCardList.add(idCard)

            idCard = IdCard("VID","Voter ID")
            idCardList.add(idCard)

            idCard = IdCard("PC","Pan Card")
            idCardList.add(idCard)

            return idCardList
        }
    }

    data class IdCard(var code: String?, var name: String?)
}