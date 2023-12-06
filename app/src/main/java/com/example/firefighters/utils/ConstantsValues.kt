package com.example.firefighters.utils

object ConstantsValues {
    //Emergency states
    const val WORKING = "Status working"
    const val NOT_WORKING = "Status not working"
    const val FINISHED = "Status finished"

    //Filter
    const val FILTER_NAME = "id"
    const val FILTER_DEGREE = "gravity"
    const val FILTER_STATUS = "status"

    //Permissions
    const val CALL_PERMISSION_CODE = 2131
    const val LOCATION_PERMISSION_CODE = 22323
    const val SMS_PERMISSION_CODE = 11311233
    const val BLUETOOTH_PERMISSION_CODE = 231
    const val CAMERA_PERMISSION_CODE = 62321
    const val AUDIO_RECORD_PERMISSION_CODE = 700923

    //Gravities
    const val GRAVITY_NORMAL = "Normal"
    const val GRAVITY_HIGH = "High"
    const val GRAVITY_LOW = "Low"

    //Queries
    const val EMERGENCIES_COLLECTION = "emergencies"
    const val UNIT_COLLECTION = "units"
    const val WATER_POINTS_COLLECTION = "waterpoints"
    const val USERS_COLLECTION = "users"
    const val MESSAGE_COLLECTION = "messages"
    const val ADMIN_EMAIL = "admin@gmail.com"

    @JvmStatic
    var isIsFirefighter = false
    @JvmStatic
    var isIsChief = false
    @JvmStatic
    var unit: String? = ""
}
