package com.adapter.logistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.messaging.logistic.R;

import java.util.List;

public class SlideMenuAdapter extends BaseAdapter {


    Context context;
    LayoutInflater mInflater;
    List<String> menuList;

    public SlideMenuAdapter(Context context, List<String> list) {
        this.context = context;
        this.menuList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public Object getItem(int position) {
        return menuList.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_slide_menu, null);

            holder.menuTitleTxtView = (TextView) convertView.findViewById(R.id.menuTitleTxtView);
            holder.menuBadgeTxtView = (TextView) convertView.findViewById(R.id.menuBadgeTxtView);
            holder.menuImgView      = (ImageView) convertView.findViewById(R.id.menuImgView);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }


        holder.menuTitleTxtView.setText(menuList.get(position));

        return convertView;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    public class ViewHolder {
        TextView menuTitleTxtView, menuBadgeTxtView;
        ImageView menuImgView;
    }
}