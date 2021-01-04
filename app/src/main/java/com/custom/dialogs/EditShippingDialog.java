package com.custom.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.driver.details.DriverConst;
import com.local.db.DBHelper;
import com.local.db.ShipmentHelperMethod;
import com.messaging.logistic.R;

public class EditShippingDialog extends Dialog {


    String ShipperNumber = "", ShipperName = "" , Commodity, FromAddress = "", ToAddress = "";
    private Button shippingDocSaveBtn, shippingDocCancelBtn;
    private EditText shipperNoEditText, shipperNameEditText, commodityEditText, FromEditText, ToEditText;
    private TextView shippingInfoTitle;
    private LinearLayout officeAddLay, terminalAddLay;
    EditShippingListener editShippingListener;


    public EditShippingDialog(Context context, String shipperNumber, String shipperName, String commodity,
                              String FromAddress, String ToAddress,
                              EditShippingListener editListener) {
        super(context);

        this.ShipperNumber = shipperNumber;
        this.ShipperName = shipperName;
        this.Commodity = commodity;
        this.FromAddress = FromAddress;
        this.ToAddress = ToAddress;
        this.editShippingListener = editListener;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.popup_shipping_doc);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setCancelable(false);

      //  shipmentHelper          = new ShipmentHelperMethod();
      //  dbHelper                = new DBHelper(getContext());

        shipperNoEditText       = (EditText)findViewById(R.id.shipperNoEditText);
        shipperNameEditText     = (EditText)findViewById(R.id.shipperNameEditText);
        commodityEditText       = (EditText)findViewById(R.id.commodityEditText);
        FromEditText            = (EditText)findViewById(R.id.shipperStateEditText);
        ToEditText              = (EditText)findViewById(R.id.shipperPostalEditText);

        shippingInfoTitle       = (TextView)findViewById(R.id.shippingInfoTitle);

        shippingDocSaveBtn      = (Button)findViewById(R.id.shippingDocSaveBtn);
        shippingDocCancelBtn    = (Button)findViewById(R.id.shippingDocCancelBtn);
        officeAddLay            = (LinearLayout)findViewById(R.id.officeAddLay);
        terminalAddLay          = (LinearLayout)findViewById(R.id.terminalAddLay);

        officeAddLay.setVisibility(View.GONE);
        terminalAddLay.setVisibility(View.GONE);

        shippingInfoTitle.setText(getContext().getResources().getString(R.string.EditShippingInfo));
        shippingDocSaveBtn.setText(getContext().getResources().getString(R.string.update));
        shipperNoEditText.setText(ShipperNumber);
        shipperNameEditText.setText(ShipperName);
        commodityEditText.setText(Commodity);
        FromEditText.setText(FromAddress);
        ToEditText.setText(ToAddress);


        shippingDocSaveBtn.setOnClickListener(new ShipperFieldListener());

        shippingDocCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }

    public interface EditShippingListener {
        public void EditShippingReady(String shipperNo, String shipperName, String commodity,
                                      String fromAdd, String toAdd);
    }


    @Override
    protected void onStop() {
        HideKeyboard();
        super.onStop();
    }

    void HideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }

    private class ShipperFieldListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            HideKeyboard();

            editShippingListener.EditShippingReady(
                    shipperNoEditText.getText().toString().trim(),
                    shipperNameEditText.getText().toString().trim(),
                    commodityEditText.getText().toString().trim(),
                    FromEditText.getText().toString().trim(),
                    ToEditText.getText().toString().trim()

            );
        }
    }
}
