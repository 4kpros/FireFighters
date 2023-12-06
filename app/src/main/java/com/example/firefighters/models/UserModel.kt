package com.example.firefighters.models

data class UserModel (
    var phoneNumber: Int = 0,
    var mail: String? = null,
    var userName: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var picture: String? = null,
    var latestLatitude: Float = 0f,
    var latestLongitude: Float = 0f,
    var isWorking: Boolean = false,
    var isFireFighter: Boolean = false,
    var isChief: Boolean = false,
    var unit: String? = null
)
{

}
