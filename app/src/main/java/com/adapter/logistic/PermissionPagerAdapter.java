package com.adapter.logistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.messaging.logistic.R;
import com.models.PermissionInfoModel;

import java.util.List;

public class PermissionPagerAdapter extends PagerAdapter {

    Context context;
    List<PermissionInfoModel> pagerList;

    public PermissionPagerAdapter(Context context, List<PermissionInfoModel> pager) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_permission_info, container, false);

        TextView permissionTitleTV = (TextView)view.findViewById(R.id.permissionTitleTV);
        TextView permissionDescTV = (TextView)view.findViewById(R.id.permissionDescTV);
        ImageView permissionImgView = (ImageView)view.findViewById(R.id.permissionImgView);

        permissionTitleTV.setText(pagerList.get(position).getTitle());
        permissionDescTV.setText(pagerList.get(position).getDesc());

        permissionImgView.setImageResource(pagerList.get(position).getDrawable());

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

