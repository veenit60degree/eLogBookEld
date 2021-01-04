package com.drag;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.messaging.logistic.R;

public class EditLogViewHolder extends RecyclerView.ViewHolder {

    public TextView editLogSerialNoTV, startTimeTV, endTimeTV, editLogDurationTV;
    public RelativeLayout startTimeLayout, endTimeLayout, endTimeBtn, startTimeBtn;
    public LinearLayout editLogItemLay;
    public Spinner editLogStatusSpinner;


    public EditLogViewHolder(View itemView) {
        super(itemView);

        editLogSerialNoTV    = itemView.findViewById(R.id.editLogSerialNoTV);
        startTimeTV          = itemView.findViewById(R.id.startTimeTV);
        endTimeTV            = itemView.findViewById(R.id.endTimeTV);
        editLogDurationTV    = itemView.findViewById(R.id.editLogDurationTV);

        startTimeBtn         = itemView.findViewById(R.id.startTimeIV);
        endTimeBtn           = itemView.findViewById(R.id.endTimeIV);
        startTimeLayout      = itemView.findViewById(R.id.startTimeLayout);
        endTimeLayout        = itemView.findViewById(R.id.endTimeLayout);

        editLogItemLay       = itemView.findViewById(R.id.editLogItemLay);

        editLogStatusSpinner = itemView.findViewById(R.id.editLogStatusSpinner);

    }
}