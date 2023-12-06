package com.example.firefighters.utils

import com.example.firefighters.repositories.EmergencyRepository
import com.example.firefighters.repositories.MessageRepository
import com.example.firefighters.repositories.UnitRepository
import com.example.firefighters.repositories.UserRepository
import com.example.firefighters.repositories.WaterPointRepository
import com.example.firefighters.viewmodels.activities.BluetoothActivityViewModel
import com.example.firefighters.viewmodels.models.EmergencyViewModel
import com.example.firefighters.viewmodels.models.MessageViewModel
import com.example.firefighters.viewmodels.models.UnitViewModel
import com.example.firefighters.viewmodels.models.UserViewModel
import com.example.firefighters.viewmodels.models.WaterPointViewModel

object InjectorUtils {

    //For models
    fun provideWaterPointViewModel(): WaterPointViewModel.Factory {
        return WaterPointViewModel.Factory(WaterPointRepository())
    }
    fun provideUserViewModel(): UserViewModel.Factory {
        return UserViewModel.Factory(UserRepository())
    }
    fun provideUnitViewModel(): UnitViewModel.Factory {
        return UnitViewModel.Factory(UnitRepository())
    }
    fun provideMessageViewModel(): MessageViewModel.Factory {
        return MessageViewModel.Factory(MessageRepository())
    }
    fun provideEmergencyViewModel(): EmergencyViewModel.Factory {
        return EmergencyViewModel.Factory(EmergencyRepository())
    }

    //For activities
    fun provideBluetoothViewModel(): BluetoothActivityViewModel.Factory {
        return BluetoothActivityViewModel.Factory()
    }
}