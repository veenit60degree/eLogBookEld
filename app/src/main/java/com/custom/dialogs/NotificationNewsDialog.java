package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.adapter.logistic.NotificationPagerAdapter;
import com.constants.Constants;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.models.NotificationNewsModel;
import com.models.VehicleModel;

import java.util.List;

public class NotificationNewsDialog extends Dialog {

    List<NotificationNewsModel> newsList;
    ViewPager viewPager;
    LinearLayout layout_dot;
    TextView[] dot;
    Constants constants;
    boolean isTesting;

    public NotificationNewsDialog(Context context, List<NotificationNewsModel> list, boolean isTesting) {
        super(context);
        this.newsList = list;
        this.isTesting = isTesting;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.popup_notification);

        viewPager = (ViewPager)findViewById(R.id.viewpager);

        layout_dot = (LinearLayout) findViewById(R.id.layout_dot);
        constants = new Constants();

     /*   if(isTesting) {
           // newsList.add(new NotificationNewsModel("News", "Adverse driving conditions means snow, ice, sleet, fog, or other adverse weather conditions or unusual road or traffic conditions that were not known, or could not reasonably be known, to: a driver immediately prior to beginning the duty day or immediately before beginning driving after a qualifying rest break or sleeper berth period, or a motor carrier immediately prior to dispatching the driver."));
          //  newsList.add(new NotificationNewsModel("Sports", "Adverse driving conditions means snow, ice, sleet, fog, or other adverse weather conditions or unusual road or traffic conditions that were not known, or could not reasonably be known, to: a driver immediately prior to beginning the duty day or immediately before beginning driving after a qualifying rest break or sleeper berth period, or a motor carrier immediately prior to dispatching the driver."));
            newsList.add(new NotificationNewsModel("Movies", "By selecting one of the above options, logs will be sent to FMCSA."));
            newsList.add(new NotificationNewsModel("Songs", "Connection unavailable! Your edited log will be posted to server automatically when your device will be connected with working internet connection"));
        }*/

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
        }else if(newsList.size() == 1){
            layout_dot.setVisibility(View.GONE);
        }

        int maxDescLength = getMaxDescLength();
        if(maxDescLength <= 70){
            if(Globally.isTablet(getContext())){
                setViewPagerHeight(constants.intToPixel(getContext(), 120));
            }else{
                setViewPagerHeight(constants.intToPixel(getContext(), 80));
            }
        }else if(maxDescLength > 70 && maxDescLength <= 160){
            if(Globally.isTablet(getContext())){
                setViewPagerHeight(constants.intToPixel(getContext(), 170));
            }else{
                setViewPagerHeight(constants.intToPixel(getContext(), 130));
            }
        }else{
            if(Globally.isTablet(getContext())){
                setViewPagerHeight(constants.intToPixel(getContext(), 210));
            }else{
                setViewPagerHeight(constants.intToPixel(getContext(), 170));
            }
        }


        Button notificationOkBtn = (Button)findViewById(R.id.notificationOkBtn);
        notificationOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    // we are getting desc string length to set view pager height according to max desc string length
    private int getMaxDescLength(){
        int maxDescLength = 0;
        for(int i = 0 ; i < newsList.size() ; i++){
            int descLegthAtPos = newsList.get(i).getNotificationDesc().length();
            if(descLegthAtPos > maxDescLength){
                maxDescLength = descLegthAtPos;
            }
        }

        return maxDescLength;
    }

    private void setViewPagerHeight(final int viewPagerHeight){
        ViewTreeObserver viewTreeObserver = viewPager.getViewTreeObserver();
        viewTreeObserver
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);

                        int viewPagerWidth = viewPager.getWidth();

                        layoutParams.width = viewPagerWidth;
                        layoutParams.height = viewPagerHeight;

                        viewPager.setLayoutParams(layoutParams);
                        viewPager.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
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
