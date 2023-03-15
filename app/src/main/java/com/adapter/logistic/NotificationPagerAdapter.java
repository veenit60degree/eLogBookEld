package com.adapter.logistic;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.als.logistic.R;
import com.models.NotificationNewsModel;

import java.util.List;

public class NotificationPagerAdapter extends PagerAdapter {

    Context context;
    List<NotificationNewsModel> pagerList;

    public NotificationPagerAdapter(Context context, List<NotificationNewsModel> pager) {
        this.context = context;
        this.pagerList = pager;
    }

    @Override
    public int getCount() {
        return pagerList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public  Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, container, false);
        TextView notificationTitleTV = (TextView)view.findViewById(R.id.notificationTitleTV);
        TextView notificationDescTV = (TextView)view.findViewById(R.id.notificationDescTV);

        notificationTitleTV.setText(pagerList.get(position).getNotificationTitle());
        notificationDescTV.setText(Html.fromHtml(pagerList.get(position).getNotificationDesc()));
        notificationDescTV.setMovementMethod(LinkMovementMethod.getInstance());

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}