package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.adapter.logistic.NotificationPagerAdapter;
import com.messaging.logistic.R;
import com.models.NotificationNewsModel;
import com.models.VehicleModel;

import java.util.List;

public class NotificationNewsDialog extends Dialog {

    List<NotificationNewsModel> newsList;
    ViewPager viewPager;
    LinearLayout layout_dot;
    TextView[] dot;


    public NotificationNewsDialog(Context context, List<NotificationNewsModel> list) {
        super(context);
        this.newsList = list;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.popup_notification);

        viewPager = (ViewPager)findViewById(R.id.viewpager);

        layout_dot = (LinearLayout) findViewById(R.id.layout_dot);

       /* newsList.add(new NotificationNewsModel("News", "Adverse driving conditions means snow, ice, sleet, fog, or other adverse weather conditions or unusual road or traffic conditions that were not known, or could not reasonably be known, to: a driver immediately prior to beginning the duty day or immediately before beginning driving after a qualifying rest break or sleeper berth period, or a motor carrier immediately prior to dispatching the driver."));
        newsList.add(new NotificationNewsModel("Sports", "Adverse driving conditions means snow, ice, sleet, fog, or other adverse weather conditions or unusual road or traffic conditions that were not known, or could not reasonably be known, to: a driver immediately prior to beginning the duty day or immediately before beginning driving after a qualifying rest break or sleeper berth period, or a motor carrier immediately prior to dispatching the driver."));
        newsList.add(new NotificationNewsModel("Movies", "By selecting one of the above options, logs will be sent to FMCSA."));
        newsList.add(new NotificationNewsModel("Songs", "Connection unavailable! Your edited log will be posted to server automatically when your device will be connected with working internet connection"));
*/
        NotificationPagerAdapter pagerAdapter = new NotificationPagerAdapter(getContext(), newsList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageMargin(20);
        addDot(0);

        // whenever the page changes
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }
            @Override
            public void onPageSelected(int i) {
                addDot(i);
            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        if(newsList.size() == 0){
            dismiss();
        }
        Button notificationOkBtn = (Button)findViewById(R.id.notificationOkBtn);
        notificationOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }


    public void addDot(int page_position) {
        dot = new TextView[newsList.size()];
        layout_dot.removeAllViews();

        for (int i = 0; i < dot.length; i++) {;
            dot[i] = new TextView(getContext());
            dot[i].setText(Html.fromHtml("&#9673;"));
           // dot[i].setTextSize(35);
            //dot[i].setTextColor(getResources().getColor(R.color.darker_gray));
            layout_dot.addView(dot[i]);
        }
        //active dot
        dot[page_position].setTextColor(getContext().getResources().getColor(R.color.color_eld_theme));
    }


}
