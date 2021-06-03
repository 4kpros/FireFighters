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
    public static  int CALL_PERMISSION_CODE = 1;
    public static  int LOCATION_PERMISSION_CODE = 2;
    public static  int SMS_PERMISSION_CODE = 3;
    public static  int STORAGE_READ_PERMISSION_CODE = 4;
    public static  int STORAGE_WRITE_PERMISSION_CODE = 5;

    //Gravities
    public static String GRAVITY_NORMAL = "Normal";
    public static String GRAVITY_HIGH = "High";
    public static String GRAVITY_LOW = "Low";

    //Bottom sheet fragments
    public static String MAP_VIEW_TAG = "Map view fragment";
    public static String STREET_VIEW_TAG = "Street view fragment";

    public static String EMERGENCY_DETAIL_TAG = "Emergency detail fragment";
    public static String WATER_SOURCE_DETAIL_TAG = "Water source detail fragment";
    public static String FIRE_STATION_DETAIL_TAG = "Fire station detail fragment";
    public static String FIRE_TRUCK_DETAIL_TAG = "Fire truck detail fragment";
    public static String FIRE_FIGHTER_DETAIL_TAG = "Fire fighter detail fragment";

}
