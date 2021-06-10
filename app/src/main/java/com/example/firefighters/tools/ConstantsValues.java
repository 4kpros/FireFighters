package com.example.firefighters.tools;

public abstract class ConstantsValues {

    //Emergency states
    public static final String WORKING = "Status working";
    public static final String NOT_WORKING = "Status not working";
    public static final String FINISHED = "Status finished";

    //Filter
    public static final String FILTER_NAME = "streetName";
    public static final String FILTER_DEGREE = "gravity";
    public static final String FILTER_STATUS = "status";

    //Permissions
    public static final int CALL_PERMISSION_CODE = 1;
    public static final int LOCATION_PERMISSION_CODE = 2;
    public static final int SMS_PERMISSION_CODE = 3;
    public static final int BLUETOOTH_PERMISSION_CODE = 3;
    public static final int STORAGE_READ_PERMISSION_CODE = 4;
    public static final int STORAGE_WRITE_PERMISSION_CODE = 5;

    //Gravities
    public static final String GRAVITY_NORMAL = "Normal";
    public static final String GRAVITY_HIGH = "High";
    public static final String GRAVITY_LOW = "Low";

    //Queries
    public static final String EMERGENCIES_COLLECTION = "emergencies";
    public static final String WATER_POINTS_COLLECTION = "waterpoints";
    public static final String USERS_COLLECTION = "users";
    public static final String FIRE_FIGHTERS_COLLECTION = "firefighters";
    public static final String ADMINS_COLLECTION = "admins";
    public static final String MESSAGE_COLLECTION = "messages";
    public static final String FIRE_TRUCKS_COLLECTION = "firetrucks";
    public static final String FIRE_STATIONS_COLLECTION = "firestations";

    //User Type
    public static final String NORMAL_USER = "Normal user";
    public static final String FIRE_FIGHTER_USER = "Fire fighter user";
    public static final String ADMIN_USER = "Admin user";

    //Bottom sheet fragments
    public static final String MAP_VIEW_TAG = "Map view fragment";
    public static final String STREET_VIEW_TAG = "Street view fragment";

    public static final String EMERGENCY_DETAIL_TAG = "Emergency detail fragment";
    public static final String WATER_SOURCE_DETAIL_TAG = "Water source detail fragment";
    public static final String FIRE_STATION_DETAIL_TAG = "Fire station detail fragment";
    public static final String FIRE_TRUCK_DETAIL_TAG = "Fire truck detail fragment";
    public static final String FIRE_FIGHTER_DETAIL_TAG = "Fire fighter detail fragment";

}
