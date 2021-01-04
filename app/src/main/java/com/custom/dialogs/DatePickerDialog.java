package com.custom.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;

import com.constants.Constants;
import com.constants.SharedPref;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.local.db.RecapViewMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.TabAct;
import com.messaging.logistic.fragment.EldFragment;

import org.json.JSONArray;
import org.json.JSONException;
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

    public DatePickerDialog(Context context, String currentCycleId, String selectedDate, DatePickerListener readyListener) {
        super(context);
        CurrentCycleId = currentCycleId;
        SelectedDate = selectedDate;
        this.readyListener = readyListener;

        hMethods = new HelperMethods();
        global = new Globally();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        int DRIVER_ID           = Integer.valueOf(SharedPref.getDriverId(getContext()));
        JSONObject logPermissionObj    = driverPermissionMethod.getDriverPermissionObj(DRIVER_ID, dbHelper);
        IsDot                   = SharedPref.IsDOT(getContext());

        int selectedYear    = Integer.parseInt(SelectedDate.substring(6, SelectedDate.length()));
        int selectedMonth   = Integer.parseInt(SelectedDate.substring(0, 2));
        int selectedDay     = Integer.parseInt(SelectedDate.substring(3, 5));

        String currentDate = Globally.GetCurrentDeviceDate() + " 23:59:59";
        Date Maxdate = Globally.ParseDate(currentDate);
        long maxDate = Maxdate.getTime();
         datePicker.setMaxDate(maxDate);             // Till current date

        DriverPermitMaxDays = constants.GetDriverPermitDaysCount(logPermissionObj, CurrentCycleId, IsDot);

        if(!IsDot) {
            JSONArray recap18DaysArray = recapViewMethod.getSavedRecapView18DaysArray(DRIVER_ID, dbHelper);
            /*if (DriverPermitMaxDays > recap18DaysArray.length()) {
                DriverPermitMaxDays = recap18DaysArray.length() - 1;
            }*/

            DriverPermitMaxDays = constants.getPermitMaxDays( recap18DaysArray, DriverPermitMaxDays, hMethods, global);
        }
        if(DriverPermitMaxDays < 0 ){
            DriverPermitMaxDays = 0;
        }else{
           /* if(DriverPermitMaxDays == 14 ){
                DriverPermitMaxDays = 13;
            }*/
        }


        calendar.add(Calendar.DAY_OF_MONTH, -DriverPermitMaxDays);
        Date mindate = calendar.getTime();
        datePicker.setMinDate(mindate.getTime());

        // Calendar.MONTH start from 0, so that we are subtracting month value with -1
        datePicker.updateDate(selectedYear, selectedMonth-1, selectedDay);    // yy mm dd

        setDateJob = (Button)findViewById(R.id.setDate);
        setDateJob.setText("Show Details");

        setDateJob.setOnClickListener(new DateJobListener());

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
