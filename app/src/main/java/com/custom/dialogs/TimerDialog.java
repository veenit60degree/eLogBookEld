package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.als.logistic.R;


public class TimerDialog extends Dialog {

    int  Hour, Min;
    TimePicker timePicker;


    public interface TimePickerListener {
        public void TimePickerReady(int position, int SelectedHour, int SelectedMin, TextView startView, TextView endView, String viewType, TimePicker timePicker);
    }

    String viewType = "";
    int position;
    private TimePickerListener timePickerListener;
    TextView startTimeTextView, endTimeTextView;


    public TimerDialog(Context context, int pos, int hour, int min, TextView startView, TextView endView, String view_type, TimePickerListener timeListener) {
        super(context);
        this.position = pos;
        this.Hour = hour;
        this.Min = min;
        this.startTimeTextView = startView;
        this.endTimeTextView = endView;
        this.viewType = view_type;
        this.timePickerListener = timeListener;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.item_timer_picker);
        // setCancelable(false);

        timePicker          = (TimePicker)findViewById(R.id.timePicker);
        Button cancelBtn    = (Button)findViewById(R.id.cancelBtn);
        Button onBtn        = (Button)findViewById(R.id.okBtn);

        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(Hour);
        timePicker.setCurrentMinute(Min);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        onBtn.setOnClickListener(new TimePickerClickListener()) ;



    }




    private class TimePickerClickListener implements View.OnClickListener {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
            int currentApiVersion = Build.VERSION.SDK_INT;
            if (currentApiVersion > Build.VERSION_CODES.LOLLIPOP_MR1){
                Hour = timePicker.getHour();
                Min = timePicker.getMinute();
            } else {
                Hour = timePicker.getCurrentHour();
                Min = timePicker.getCurrentMinute();
            }

            timePickerListener.TimePickerReady(position, Hour, Min, startTimeTextView, endTimeTextView, viewType, timePicker );
        }
    };


}





