package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;

import com.constants.Constants;
import com.constants.SharedPref;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.local.db.RecapViewMethod;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.als.logistic.UILApplication;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class DatePickerDialog extends Dialog {

    public interface DatePickerListener {
        public void JobBtnReady(String SelectedDate, String dayOfTheWeek, String MonthFullName, String MonthShortName, int DayOfMonth);
    }


    private DatePickerListener readyListener;
    DatePicker datePicker;
    Button setDateJob;
    String CurrentCycleId, SelectedDate;
   /* int UsaMaxDays = 1 + 7;
    int CanMaxDays = 1 + 13;*/
    int DriverPermitMaxDays = 0;
    boolean IsDot = false;
    DriverPermissionMethod driverPermissionMethod;
    DBHelper dbHelper;
    Constants constants;
    RecapViewMethod recapViewMethod;
    HelperMethods hMethods;
    Globally global;
    boolean isGenerateRods = false;

    public DatePickerDialog(Context context, String currentCycleId, String selectedDate, DatePickerListener readyListener,boolean isRods) {
        super(context);
        CurrentCycleId = currentCycleId;
        SelectedDate = selectedDate;
        this.readyListener = readyListener;

        hMethods = new HelperMethods();
        global = new Globally();
        isGenerateRods = isRods;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if(UILApplication.getInstance().isNightModeEnabled()){
            getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getResources().getColor(R.color.transparent)));
        }*/

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_datepicker);
       // setCancelable(false);

        driverPermissionMethod  = new DriverPermissionMethod();
        dbHelper                = new DBHelper(getContext());
        constants               = new Constants();
        recapViewMethod         = new RecapViewMethod();

        datePicker              =(DatePicker)findViewById(R.id.datePicker);
        Calendar calendar       = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());

        setDateJob = (Button)findViewById(R.id.setDate);
        setDateJob.setText("Show Details");


        String CoDriverId = "", firstLoginTime = "";
        int DRIVER_ID           = Integer.valueOf(SharedPref.getDriverId(getContext()));
        if (!SharedPref.getDriverType(getContext()).equals(DriverConst.SingleDriver)) {
            if(SharedPref.getDriverId(getContext()).equals(DriverConst.GetDriverDetails(DriverConst.DriverID, getContext()))){
                CoDriverId   = DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getContext());
                firstLoginTime = SharedPref.getCoDriverFirstLoginTime(getContext());
            }else{
                CoDriverId   = DriverConst.GetDriverDetails(DriverConst.DriverID, getContext());
                firstLoginTime = SharedPref.getDriverFirstLoginTime(getContext());
            }
        }else{
            firstLoginTime = SharedPref.getDriverFirstLoginTime(getContext());
        }

        JSONObject logPermissionObj    = driverPermissionMethod.getDriverPermissionObj(DRIVER_ID, CoDriverId, dbHelper);
        IsDot                   = SharedPref.IsDOT(getContext());

        if(SelectedDate.length() > 6) {
            int selectedYear = Integer.parseInt(SelectedDate.substring(6, SelectedDate.length()));
            int selectedMonth = Integer.parseInt(SelectedDate.substring(0, 2));
            int selectedDay = Integer.parseInt(SelectedDate.substring(3, 5));

            String currentDate = Globally.GetCurrentDeviceDate(null, global, getContext()) + " 23:59:59";
            Date Maxdate = Globally.ParseDate(currentDate);
            long maxDate = Maxdate.getTime();
            datePicker.setMaxDate(maxDate);             // Till current date

            String[] cycleArray = CurrentCycleId.split(",");
            if (cycleArray.length > 0) {
                CurrentCycleId = cycleArray[0];

                if (CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2)) {
                    DriverPermitMaxDays =  isGenerateRods == true ?  15: 14;
                } else {
                    DriverPermitMaxDays = isGenerateRods == true ? 8 :7;
                }

                if (cycleArray.length > 1) {
                    if (cycleArray[1].equals("sendLog")) {
                        setDateJob.setText("Select Date");
                    }
                }
            } else {
                DriverPermitMaxDays = constants.GetDriverPermitDaysCount(logPermissionObj, CurrentCycleId, IsDot);

                JSONArray recap18DaysArray = recapViewMethod.getSavedRecapView18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);
                int recapArrayLength = recap18DaysArray.length();
                if (DriverPermitMaxDays > recapArrayLength) {
                    DriverPermitMaxDays = recapArrayLength-1;
                }
            }

            if (DriverPermitMaxDays < 0) {
                DriverPermitMaxDays = 0;
            }


          //  DriverPermitMaxDays = getDays(firstLoginTime, DriverPermitMaxDays);

            calendar.add(Calendar.DAY_OF_MONTH, -DriverPermitMaxDays);
            Date mindate = calendar.getTime();
            datePicker.setMinDate(mindate.getTime());

            // Calendar.MONTH start from 0, so that we are subtracting month value with -1
            datePicker.updateDate(selectedYear, selectedMonth - 1, selectedDay);    // yy mm dd
        }


        setDateJob.setOnClickListener(new DateJobListener());

    }


    private int getDays(String firstLoginTime, int DriverPermitMaxDays){
        if(firstLoginTime.length() > 10) {
           int dayDiff =  constants.getDayDiff(firstLoginTime, Globally.GetCurrentUTCTimeFormat());
           if(dayDiff >= 0 && dayDiff < DriverPermitMaxDays){
               DriverPermitMaxDays = dayDiff;
           }
        }

        return DriverPermitMaxDays;
    }

    private class DateJobListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            int day=datePicker.getDayOfMonth();
            int year=datePicker.getYear();
            int mnth=datePicker.getMonth()+1;
            String month = "", dayyy = "";

            if(String.valueOf(mnth).length() == 1)
                month = "0"+mnth;
            else
                month = String.valueOf(mnth);

            if(String.valueOf(day).length() == 1)
                dayyy = "0"+day;
            else
                dayyy = String.valueOf(day);

            if(month.length() == 1)
                month = "0"+month;

            datePicker.setVisibility(View.GONE);
           String SelectedDate = month +"/"+ dayyy  +"/"+  year;

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            sdf.setTimeZone(TimeZone.getDefault());

            SimpleDateFormat inFormat = new SimpleDateFormat("MM/dd/yyyy");
            inFormat.setTimeZone(TimeZone.getDefault());
            Date date = null;
            try {
                date = inFormat.parse(SelectedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
            outFormat.setTimeZone(TimeZone.getDefault());

            String dayOfTheWeek     = outFormat.format(date);
            String MonthFullName    =   Globally.MONTHS_FULL[mnth - 1];
            String MonthShortName   =   Globally.MONTHS[mnth - 1];

            readyListener.JobBtnReady(SelectedDate, dayOfTheWeek, MonthFullName, MonthShortName, day);
        }
    }




}
