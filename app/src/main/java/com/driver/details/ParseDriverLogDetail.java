package com.driver.details;

import com.local.db.ConstantsKeys;
import com.models.DriverDetailModel;

import org.json.JSONObject;


public class ParseDriverLogDetail {

    public ParseDriverLogDetail() {
        super();
    }


    /*---------------- PARSE JSON -----------------*/
    public static DriverDetailModel driverDetailModel( JSONObject resultJson) throws Exception{

        double StartLatitude 	= 0.0;
        double StartLongitude 	= 0.0;
        double EndLatitude 		= 0.0;
        double EndLongitude 	= 0.0;
        int CurrentCycleId      = 0;

        if( !resultJson.isNull("StartLatitude") && !resultJson.getString("StartLatitude").equalsIgnoreCase("") )
            StartLatitude = resultJson.getInt("StartLatitude");
        if( !resultJson.isNull("StartLongitude") && !resultJson.getString("StartLongitude").equalsIgnoreCase(""))
            StartLongitude = resultJson.getInt("StartLongitude");
        if( !resultJson.isNull("EndLatitude") && !resultJson.getString("EndLatitude").equalsIgnoreCase(""))
            EndLatitude = resultJson.getInt("EndLatitude");
        if( !resultJson.isNull("EndLongitude") && !resultJson.getString("EndLongitude").equalsIgnoreCase("") )
            EndLongitude = resultJson.getInt("EndLongitude");
        if(!resultJson.isNull("CurrentCycleId"))
            CurrentCycleId = resultJson.getInt("CurrentCycleId");

        DriverDetailModel model = new DriverDetailModel(
                resultJson.getInt("DriverLogId"),
                resultJson.getInt("DriverId"),
                resultJson.getInt("ProjectId"),
                resultJson.getInt("DriverStatusId"),
                resultJson.getString("StartDateTime"),
                resultJson.getString("EndDateTime"),
                resultJson.getString("TotalHours"),
                StartLatitude,
                StartLongitude,
                EndLatitude,
                EndLongitude,
                CurrentCycleId,
                resultJson.getBoolean("IsViolation"),
                resultJson.getString("CreatedDate"),
                resultJson.getString("UTCStartDateTime"),
                resultJson.getString("UTCEndDateTime")
        );

        return model;
    }


}
