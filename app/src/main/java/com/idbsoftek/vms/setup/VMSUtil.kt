package com.idbsoftek.vms.setup

class VMSUtil {
    companion object {
        val MAX_BODY_TEMP = 104.0
        val MIN_BODY_TEMP = 95.0

        val MAX_OXY_TEMP = 99.0
        val MIN_OXY_TEMP = 95.0

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

        // VMS 2.0
        val RejectAction = -1
        val PendingAction = 0
        val ApproveAction = 1
        val CheckInAction = 2
        val CheckOutAction = 5
        val MeetStartAction = 3
        val MeetCompleteAction = 4
        val SessionOutAction = 6
        val SessionInAction = 7

        val MultiDayCheckIn = -2

        data class StatusUtil(val code: Int?, val name: String?)

         fun getStatusList(): List<StatusUtil>{
            var statusList: ArrayList<StatusUtil> = ArrayList()
            var status = StatusUtil(0, "Pending")
            statusList.add(status)

            status = StatusUtil(1, "Approved")
            statusList.add(status)

            status = StatusUtil(-1, "Rejected")
            statusList.add(status)

            status = StatusUtil(2, "Checked In")
            statusList.add(status)

            status = StatusUtil(3, "Meet In Progress")
            statusList.add(status)

            status = StatusUtil(4, "Meet Completed")
            statusList.add(status)

            status = StatusUtil(5, "Checked Out")
            statusList.add(status)

            status = StatusUtil(6, "Session Out")
            statusList.add(status)

            status = StatusUtil(7, "Session In")
            statusList.add(status)

             status = StatusUtil(-2, "Multi Day CheckIn")
             statusList.add(status)
            return statusList
        }

        fun getStatusToShow(action: Int?): String{
            var statusToShow = ""
            when(action){
                RejectAction -> {
                    statusToShow = "Rejected"
                }
                PendingAction -> {
                    statusToShow = "Pending"
                }
                ApproveAction -> {
                    statusToShow = "Approved"
                }
                CheckInAction -> {
                    statusToShow = "Checked In"
                }
                CheckOutAction -> {
                    statusToShow = "Checked Out"
                }
                MeetStartAction -> {
                    statusToShow = "Meet In Progress"
                }
                MeetCompleteAction -> {
                    statusToShow = "Meet Completed"
                }
                SessionOutAction -> {
                    statusToShow = "Session Out"
                }
                SessionInAction -> {
                    statusToShow = "Session In"
                }
                MultiDayCheckIn -> {
                    statusToShow = "Multi Day Check In"
                }
                8 -> {
                    statusToShow = "Partial"
                }
                else -> {
                    statusToShow = "NA"
                }
            }
            return statusToShow
        }

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