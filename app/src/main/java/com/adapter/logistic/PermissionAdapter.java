package com.adapter.logistic;

import android.Manifest;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.als.logistic.AppPermissionActivity;
import com.als.logistic.R;
import com.models.HelpDocModel;
import com.models.PermissionModel;

import org.w3c.dom.Text;

import java.util.List;

public class PermissionAdapter extends BaseAdapter {

    String LocationP               = "Location";
    String LocationPreciseP        = "Location Precise";
    String StorageP                = "Storage";
    String BluetoothP              = "Bluetooth";
    String NotificationP           = "Notifications";

    Context context;
    LayoutInflater mInflater;
    List<PermissionModel> permissionList;

    public PermissionAdapter(Context context, List<PermissionModel> permissionList) {
        this.context = context;
        this.permissionList = permissionList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return permissionList.size();
    }

    @Override
    public Object getItem(int position) {
        return permissionList.get(position);
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
            convertView = mInflater.inflate(R.layout.items_permissions, null);

            holder.permissionNameTV = (TextView) convertView.findViewById(R.id.permissionNameTV);
            holder.permissionStatusDescTV = (TextView) convertView.findViewById(R.id.permissionStatusDescTV);
            holder.permissionStatusBtn = (TextView) convertView.findViewById(R.id.permissionStatusBtn);

            holder.permissionImgView = (ImageView) convertView.findViewById(R.id.permissionImgView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String permissionType = permissionList.get(position).getPermissionType();
        boolean isPermissionGranted = permissionList.get(position).IsPermissionGranted();
        holder.permissionNameTV.setText(permissionType);
        holder.permissionStatusDescTV.setText(permissionList.get(position).getPermissionDesc());


            if (permissionType.equals(LocationP)) {
                holder.permissionImgView.setImageResource(R.drawable.loc_approx);
            }else if (permissionType.equals(LocationPreciseP)) {
                holder.permissionImgView.setImageResource(R.drawable.loc_percise);
            }else if(permissionType.equals(StorageP)){
                holder.permissionImgView.setImageResource(R.drawable.storage_icon);
            }else if(permissionType.equals(BluetoothP)){
                holder.permissionImgView.setImageResource(R.drawable.ble_ic);
            }else if(permissionType.equals(NotificationP)){
                holder.permissionImgView.setImageResource(R.drawable.notifications);
            }




        if (isPermissionGranted) {
            holder.permissionStatusBtn.setText("Allowed");
            holder.permissionStatusBtn.setBackgroundResource(R.drawable.green_default);
        } else {
            holder.permissionStatusBtn.setText("Allow");
            holder.permissionStatusBtn.setBackgroundResource(R.drawable.red_default);
        }

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
        TextView permissionNameTV, permissionStatusDescTV, permissionStatusBtn;
        ImageView permissionImgView;

    }

}