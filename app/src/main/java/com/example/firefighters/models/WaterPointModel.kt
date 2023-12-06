package com.example.firefighters.models

import java.util.Calendar

class WaterPointModel {
    var id: Long = 0
    var senderMail: String? = null //Foreign key user sender
    var estimatedQte: Int = 0
    var sendUtc: Int
    var longitude: Float = 0f
    var latitude: Float = 0f
    var sourceType: String? = null
    var sendDate: String
    var sendHour: String

    init {
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
