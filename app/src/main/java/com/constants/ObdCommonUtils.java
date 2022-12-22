package com.constants;

import android.content.Context;

import com.local.db.ConstantsKeys;
import com.als.logistic.Globally;

import org.joda.time.DateTime;

public class ObdCommonUtils {

    public ObdCommonUtils() {
        super();
    }

    int TimeIntervalInSeconds = 10;

    public String IsEngineSyncMalfunction(Context context, String CurrentIgnitionStatus, String CurrentEngineRPM,
                                                 String CurrentOdometer, int CurrentSpeed, String CurrentEngineHours, String CurrentDate)
    {
        int maxSpeed = 10;
        int timeInSec = 0;
        double calculatedSpeed = 0;


        String lastCalledTime = SharedPref.getEngSyncLastCallTime(context);
        if (lastCalledTime.length() == 0 ) {
            SharedPref.setEngSyncMalCallTime(CurrentDate, context);
            lastCalledTime = CurrentDate;
        }

        try{
            String MalCalledLastEngIgntn = SharedPref.getEngSyncLastCallDetails(context, ConstantsKeys.MalCalledLastEngIgntn);
            String previousOdometer      = SharedPref.getEngSyncLastCallDetails(context, ConstantsKeys.MalCalledLastOdo);
            String PreviousEngineSec     = SharedPref.getEngSyncLastCallDetails(context, ConstantsKeys.MalCalledLastEngHr);
            int PrevSpeed                = Integer.valueOf(SharedPref.getEngSyncLastCallDetails(context, ConstantsKeys.MalCalledLastSpeed));
            // double PreviousRPM           = Double.parseDouble(SharedPref.getEngSyncLastCallDetails(context, ConstantsKeys.MalCalledLastRpm));

            DateTime savedDateTime = Globally.getDateTimeObj(lastCalledTime, false);
            DateTime currentDateTime = Globally.getDateTimeObj(CurrentDate, false);

            if(savedDateTime.isAfter(currentDateTime)){
                SharedPref.setMalfCallTime(CurrentDate, context);
            }
            timeInSec = (int) Constants.getDateTimeDuration(savedDateTime, currentDateTime).getStandardSeconds();
            //Seconds.secondsBetween(savedDateTime, currentDateTime).getSeconds();

            if(timeInSec >= TimeIntervalInSeconds) {
                double odometerDistance = Double.parseDouble(CurrentOdometer) - Double.parseDouble(previousOdometer);
                calculatedSpeed = (odometerDistance / 1000.0f) / (timeInSec / 3600.0f);

                boolean EngineOn = false;
                boolean isodometer = IsOdometer(CurrentOdometer);
                boolean isRpm = false;
                boolean IsOdometerChanged = false;
                boolean IsEngineSecondsChanged = false;

                if (Integer.valueOf(CurrentEngineRPM) > 0) {
                    isRpm = true;
                }

                if (isRpm) {
                    IsOdometerChanged = IsOdometerChanged(CurrentOdometer, previousOdometer);
                    IsEngineSecondsChanged = IsEngineHourChanged(CurrentEngineHours, PreviousEngineSec);
                }

                int AverageSpeed = 0;   // =================================================================
                if (EngineOn && !isodometer) {
                    return "Engine Power Status";
                } else if (isRpm && IsOdometerChanged && AverageSpeed == 0) {
                    return "Vehicle motion status";
                } else if (isRpm && !IsOdometerChanged && AverageSpeed > maxSpeed) {
                    return "Miles Driven status";
                } else if (isRpm && !IsEngineSecondsChanged) {
                    return "Engine Hours not Changed";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return "";
    }



    private boolean IsOdometer(String CurrentOdometer){

        if(Float.parseFloat(CurrentOdometer) != 0){
            return true;
        }else{
            return false;
        }
    }


    private boolean IsOdometerChanged(String currentHighPrecisionOdometer, String PreviousOdometer){

        float CurrOdometer = Float.parseFloat(currentHighPrecisionOdometer);
        float PrevOdometer = Float.parseFloat(PreviousOdometer);

        if(CurrOdometer > PrevOdometer){
            return true;
        }else{
            return false;
        }
    }


    private boolean IsEngineHourChanged(String CurrentEngineSeconds, String PreviousEngineSeconds){
        float CurrentEngineSec = Float.parseFloat(CurrentEngineSeconds);
        float PrevEngineSec = Float.parseFloat(PreviousEngineSeconds);

        if(CurrentEngineSec > PrevEngineSec){
            return true;
        }else{
            return false;
        }
    }



}
