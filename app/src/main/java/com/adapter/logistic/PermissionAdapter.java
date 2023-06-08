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
import androidx.core.content.ContextCompat;

import com.als.logistic.AppPermissionActivity;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.als.logistic.UILApplication;
import com.constants.Constants;
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
    Constants constants;
    Globally globally;

    public PermissionAdapter(Context context, List<PermissionModel> permissionList) {
        this.context = context;
        this.permissionList = permissionList;
        mInflater = LayoutInflater.from(context);
        globally = new Globally();
        constants = new Constants();
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
            holder.permissionGrantedImgView = (ImageView) convertView.findViewById(R.id.permissionGrantedImgView);
            holder.bleLocStatusImgView = (ImageView) convertView.findViewById(R.id.bleLocStatusImgView);

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
                holder.bleLocStatusImgView.setVisibility(View.VISIBLE);
                holder.bleLocStatusImgView.setImageResource(R.drawable.gps_status);

                enableDisableView(holder.bleLocStatusImgView, false);

            }else if (permissionType.equals(LocationPreciseP)) {
                holder.permissionImgView.setImageResource(R.drawable.loc_percise);
                holder.bleLocStatusImgView.setVisibility(View.VISIBLE);
                holder.bleLocStatusImgView.setImageResource(R.drawable.gps_status);

                enableDisableView(holder.bleLocStatusImgView, false);

            }else if(permissionType.equals(StorageP)){
                holder.bleLocStatusImgView.setVisibility(View.GONE);
                holder.permissionImgView.setImageResource(R.drawable.storage_icon);
            }else if(permissionType.equals(BluetoothP)){
                holder.permissionImgView.setImageResource(R.drawable.ble_ic);

                holder.bleLocStatusImgView.setVisibility(View.VISIBLE);
                holder.bleLocStatusImgView.setImageResource(R.drawable.ble_status);

                enableDisableView(holder.bleLocStatusImgView, true);

            }else if(permissionType.equals(NotificationP)){
                holder.permissionImgView.setImageResource(R.drawable.notifications);
                holder.bleLocStatusImgView.setVisibility(View.GONE);
            }

            int color = R.color.colorPrimary;
        if(UILApplication.getInstance().isNightModeEnabled()){
            color = R.color.white;
        }
        holder.permissionImgView.setColorFilter(ContextCompat.getColor(context, color),
                android.graphics.PorterDuff.Mode.SRC_IN);

        if (isPermissionGranted) {
            //holder.permissionStatusBtn.setText("Granted");
            holder.permissionStatusBtn.setBackgroundResource(R.drawable.green_default);
            holder.permissionGrantedImgView.setVisibility(View.VISIBLE);
            holder.permissionStatusBtn.setVisibility(View.INVISIBLE);


        } else {
            holder.permissionStatusBtn.setText("Grant");
            holder.permissionStatusBtn.setBackgroundResource(R.drawable.red_default);
            holder.permissionGrantedImgView.setVisibility(View.GONE);
            holder.permissionStatusBtn.setVisibility(View.VISIBLE);


        }


        return convertView;
    }


    void enableDisableView(ImageView view, boolean isBle){
        //holder.bleLocStatusImgView

        if(isBle){
            if(globally.isBleEnabled(context)) {
                view.setColorFilter(UILApplication.getInstance().getBleLocThemeColor());
            }else{
                view.setColorFilter(context.getResources().getColor(R.color.spinner_blue));
            }
        }else{
            if(constants.CheckGpsStatus(context)) {
                view.setColorFilter(UILApplication.getInstance().getBleLocThemeColor());
            }else{
                view.setColorFilter(context.getResources().getColor(R.color.spinner_blue));
            }
        }





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
        ImageView permissionImgView, permissionGrantedImgView, bleLocStatusImgView;

    }

}