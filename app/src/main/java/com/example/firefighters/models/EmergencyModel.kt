package com.example.firefighters.models

import com.example.firefighters.utils.ConstantsValues
import java.util.Calendar

class EmergencyModel {
    var id: Long = 0
    var senderMail: String? = null //Foreign key user sender
    var messageId: Int = 0 //Foreign key message
    var sendUtc: Int
    var longitude: Double = 0.0
    var latitude: Double = 0.0
    var gravity: String
    var status: String
    var sendDate: String
    var sendHour: String
    var currentUnit: String? = null

    init {
        gravity = ConstantsValues.GRAVITY_NORMAL
        status = ConstantsValues.NOT_WORKING
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH] + 1
        val utc = calendar[Calendar.ZONE_OFFSET]
        val hours = calendar[Calendar.HOUR_OF_DAY]
        val minutes = calendar[Calendar.MINUTE]
        val sec = calendar[Calendar.SECOND]
        sendDate = "$day/$month/$year"
        sendHour = "$hours:$minutes:$sec"
        sendUtc = utc
    }
}
