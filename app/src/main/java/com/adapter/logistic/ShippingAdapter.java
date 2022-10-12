package com.adapter.logistic;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.constants.APIs;
import com.constants.Constants;
import com.constants.Logger;
import com.constants.RequestResponse;
import com.constants.SharedPref;
import com.constants.ShippingPost;
import com.custom.dialogs.EditShippingDialog;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.ShipmentHelperMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.models.ShipmentModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class ShippingAdapter extends BaseAdapter {

    private Context mContext;
    LayoutInflater mInflater;
    List<ShipmentModel> shippingList;
    EditShippingDialog editShippingDialog;
    Globally global;
    ShippingPost postRequest;
    ProgressDialog progressDialog;
    ShipmentHelperMethod shipmentHelper;
    DBHelper dbHelper;
    JSONArray shipment18DaysJsonArray, shipmentJsonArray;
    String DriverId = "",  MainDriverId = "", CoDriverId = "", IsSingleDriver, DeviceId;
    int DriverType, selectedPos = -1;
    String Msg = "Shipping information updated.";
    String MsgOffline = "Shipping information will be saved automatically with working internet connection";
    String colorEld   = "#1A3561";
    TextView timeDTv;

    String SavedDateStr;
    String ShipperNumberStr;
    String CommodityStr;
    String ShipperNameStr;
    String FromAddStr;
    String ToAddStr;

    JSONObject updatedShipmentObj;
    ShipmentModel shipmentPositionModel;



    public ShippingAdapter(Context c, String driver_id, String isSingle, String deviceId, int driverType, List<ShipmentModel> supportLst) {
        mContext        = c;
        shippingList    = supportLst;
        mInflater       = LayoutInflater.from(mContext);
        DriverId        = driver_id;
        IsSingleDriver  = isSingle;
        DeviceId        = deviceId;
        DriverType      = driverType;
        global          = new Globally();
        shipmentHelper  = new ShipmentHelperMethod();
        dbHelper        = new DBHelper(mContext);

        postRequest     = new ShippingPost(mContext, requestResponse);
        progressDialog  = new ProgressDialog(mContext);
        progressDialog.setMessage("Saving ...");

        MainDriverId    = DriverConst.GetDriverDetails(DriverConst.DriverID, mContext);
        CoDriverId      = DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, mContext);

    }

    @Override
    public int getCount() {
        return shippingList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return shippingList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder ;
        final ShipmentModel shippingModel = shippingList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_shipping, null);

            holder.billNoDTV        = (TextView)convertView.findViewById(R.id.billNoDTV);
            holder.shipperNameDTV   = (TextView)convertView.findViewById(R.id.shipperNameDTV);
            holder.commudityTV      = (TextView)convertView.findViewById(R.id.commudityTV);
            holder.fromLocDTV       = (TextView)convertView.findViewById(R.id.fromLocDTV);
            holder.toLocDTV         = (TextView)convertView.findViewById(R.id.toLocDTV);
            timeDTv          = (TextView)convertView.findViewById(R.id.timeDTv);
            holder.editInfoTxtVw    = (TextView)convertView.findViewById(R.id.editInfoTxtVw);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.billNoDTV.setText(shippingModel.getBlNoTripNo());
        holder.shipperNameDTV.setText(shippingModel.getShipperName());
        holder.commudityTV.setText(shippingModel.getCommodity());
        holder.fromLocDTV.setText(shippingModel.getFromAddress());
        holder.toLocDTV.setText(shippingModel.getToAddress());

        holder.editInfoTxtVw.setText(Html.fromHtml("<b><u> Edit </u></b>"));

        String savedDate = shippingModel.getSavedDate();
        String shippingTime = "";
        if(savedDate.length() > 11){
            shippingTime = Globally.ConvertTo12HTimeFormat(savedDate, Globally.DateFormatWithMillSec);
        }

        if(SharedPref.isTimestampEnabled(mContext)) {
            timeDTv.setText(shippingTime);
        }

        holder.editInfoTxtVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                if (editShippingDialog != null && editShippingDialog.isShowing()) {
                    editShippingDialog.dismiss();
                }

                    selectedPos = position;
                    editShippingDialog = new EditShippingDialog(mContext, shippingModel.getBlNoTripNo(), shippingModel.getShipperName(),
                            shippingModel.getCommodity(), shippingModel.getFromAddress(), shippingModel.getToAddress(),
                            new EditShippingInfoListener());
                    editShippingDialog.show();

            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            }
        });


        return convertView;
    }


    public class ViewHolder {
        TextView billNoDTV, commudityTV, shipperNameDTV, fromLocDTV, toLocDTV, editInfoTxtVw;
    }




    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }



    private class EditShippingInfoListener implements EditShippingDialog.EditShippingListener {


        @Override
        public void EditShippingReady(String shipperNo, String shipperName, String commodity,
                                        String fromAdd, String toAdd) {

            if (shipperNo.length() > 0 ) {
                try {
                    if (editShippingDialog != null && editShippingDialog.isShowing())
                        editShippingDialog.dismiss();
                } catch (final IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (final Exception e) {
                    e.printStackTrace();
                }


                try {

                    shipment18DaysJsonArray = shipmentHelper.getShipment18DaysArray(Integer.valueOf(DriverId), dbHelper);

                    if (!IsSingleDriver.equals(DriverConst.SingleDriver)) {
                        if (DriverType == Constants.MAIN_DRIVER_TYPE) {
                            MainDriverId = DriverConst.GetDriverDetails(DriverConst.DriverID, mContext);
                            CoDriverId = DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, mContext);
                        } else {
                            MainDriverId = DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, mContext);
                            CoDriverId = DriverConst.GetDriverDetails(DriverConst.DriverID, mContext);

                        }
                    }

                    try {
                        shipmentJsonArray = shipmentHelper.getSavedShipmentArray(Integer.valueOf(global.PROJECT_ID), dbHelper);
                    } catch (Exception e) {
                        e.printStackTrace();
                        shipmentJsonArray = new JSONArray();
                    }

                    shipmentPositionModel = shippingList.get(selectedPos);
                    SavedDateStr = shipmentPositionModel.getSavedDate();
                    updatedShipmentObj = shipmentHelper.createUpdatedInfoObject(MainDriverId, CoDriverId, DeviceId,
                            shipmentPositionModel.getDate(), shipperNo, shipperName, fromAdd, toAdd,
                            SavedDateStr, commodity);

                    if (shipmentJsonArray.length() > 0) {

                        boolean isExist = false;
                        for (int i = 0; i < shipmentJsonArray.length(); i++) {
                            JSONObject obj = (JSONObject) shipmentJsonArray.get(i);
                            if (obj.has(ConstantsKeys.ShippingSavedDate)) {
                                String arraySavedDate = obj.getString(ConstantsKeys.ShippingSavedDate);
                                if (SavedDateStr.equals(arraySavedDate)) {
                                    isExist = true;
                                    if (obj.has(ConstantsKeys.IsUpdateRecord)) {
                                        shipmentJsonArray.put(i, updatedShipmentObj);
                                        break;
                                    } else {
                                        updatedShipmentObj.put(ConstantsKeys.IsUpdateRecord, false);
                                        shipmentJsonArray.put(i, updatedShipmentObj);
                                        break;
                                    }

                                }
                            }
                        }

                        if (!isExist) {
                            shipmentJsonArray.put(updatedShipmentObj);
                        }
                    } else {
                        shipmentJsonArray.put(updatedShipmentObj);
                    }


                    // save sending data locally first. In case if not posted to sarver due to any issue.
                    shipmentHelper.ShipmentHelper(Integer.valueOf(global.PROJECT_ID), dbHelper, shipmentJsonArray);

                    ShipperNumberStr = shipperNo;
                    CommodityStr = commodity;
                    ShipperNameStr = shipperName;
                    FromAddStr = fromAdd;
                    ToAddStr = toAdd;

                    if (global.isConnected(mContext)) {
                        progressDialog.show();
                        //POST data to server
                        postRequest.PostListingData(shipmentJsonArray, APIs.SAVE_SHIPPING_DOC_NUMBER, 1);
                    } else {
                        global.EldToastWithDuration(timeDTv, MsgOffline, Color.parseColor(colorEld));
                        updateArryWithAdapter();
                        notifyDataSetChanged();
                    }
                    // shipmentHelper.Update18DaysShippingList(MainDriver18DaysJsonArray, MainDriverJson, MainDriverId, SelectedDate, dbHelper);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                global.EldScreenToast(timeDTv, "Enter BL/Trip Number to update shipping information", Color.parseColor(colorEld));   //or Shipper Name and Commodity

            }
        }
    }


    void updateArryWithAdapter(){
        // update local shipping table
        shipment18DaysJsonArray = shipmentHelper.updateShipping18DaysArray(DriverId, SavedDateStr, updatedShipmentObj, shipment18DaysJsonArray, dbHelper);

        ShipmentModel shippingModel = new ShipmentModel(
                shipmentPositionModel.getParcableId(),
                shipmentPositionModel.getDriverId(),
                shipmentPositionModel.getCoDriverId(),
                shipmentPositionModel.getDeviceId(),
                shipmentPositionModel.getDate(),
                ShipperNumberStr,
                CommodityStr,
                ShipperNameStr,
                FromAddStr,
                ToAddStr,
                SavedDateStr,
                false,
                false
        );
        shippingList.set(selectedPos, shippingModel);


    }

    RequestResponse requestResponse = new RequestResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, int flag) {

            if(progressDialog != null){
                progressDialog.dismiss();
            }

            JSONObject obj = null;
            String status = "";

            try {
                obj = new JSONObject(response);
                status = obj.getString("Status");
                if(status.equalsIgnoreCase("true")) {

                    String message = obj.getString("Message");
                    if(message.equals("Success")) {
                        updateArryWithAdapter();

                        global.EldScreenToast(timeDTv, Msg, Color.parseColor(colorEld));

                    }else{
                        global.EldToastWithDuration(timeDTv, message, mContext.getResources().getColor(R.color.colorVoilation) );
                    }

                    // Clear shipping inputs after ssave
                    shipmentJsonArray = new JSONArray();
                    shipmentHelper.ShipmentHelper(Integer.valueOf(global.PROJECT_ID), dbHelper, shipmentJsonArray);

                }else{
                    global.EldToastWithDuration(timeDTv, MsgOffline, Color.parseColor(colorEld));
                }

                notifyDataSetChanged();

            } catch (Exception e) {
                global.EldToastWithDuration(timeDTv, MsgOffline, Color.parseColor(colorEld));
                notifyDataSetChanged();

                e.printStackTrace();
            }

        }

        @Override
        public void onResponseError(String error, int flag) {
            Logger.LogDebug("errorrr ", ">>>error dialog: " );
            if(progressDialog != null){
                progressDialog.dismiss();
            }

            updateArryWithAdapter();

            global.EldToastWithDuration(timeDTv, MsgOffline, Color.parseColor(colorEld));
            notifyDataSetChanged();
        }
    };





}
