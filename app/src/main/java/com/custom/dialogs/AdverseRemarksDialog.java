package com.custom.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.constants.ConstantsEnum;
import com.google.android.material.textfield.TextInputLayout;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;

public class AdverseRemarksDialog extends Dialog {


    public interface RemarksListener {
        public void CancelReady();
        public void JobBtnReady(String Remarks, boolean isClaim, boolean isCompanyAssigned);

    }

    private RemarksListener readyListener;
    EditText remarksEditText ;
    Button btnLoadingJob, btnCancelLoadingJob;
    TextView TitleTV, descTextView, desc2TxtView;
    TextInputLayout reasonInputLayout;
    boolean isAdverse, IsClaim, IsCompanyAssign;
    Globally globally;

    public AdverseRemarksDialog(Context context, boolean isAdverse, boolean isClaim, boolean isCompanyAssign, RemarksListener readyListener) {
        super(context);
        this.isAdverse = isAdverse;
        this.IsClaim = isClaim;
        this.IsCompanyAssign = isCompanyAssign;
        this.readyListener = readyListener;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.popup_trailor_fields);
        setCancelable(false);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        globally = new Globally();
        remarksEditText   = (EditText)findViewById(R.id.TrailorNoEditText);

        btnLoadingJob       = (Button)findViewById(R.id.btnLoadingJob);
        btnCancelLoadingJob = (Button)findViewById(R.id.btnCancelLoadingJob);

        descTextView        = (TextView)findViewById(R.id.recordTitleTV);
        desc2TxtView        = (TextView)findViewById(R.id.desc2TxtView);
        TitleTV             = (TextView)findViewById(R.id.TitleTV);
        reasonInputLayout   = (TextInputLayout)findViewById(R.id.trailorNoInputType);

          remarksEditText.setHint(getContext().getResources().getString(R.string.reason));
          reasonInputLayout.setHint(getContext().getResources().getString(R.string.reason));

        if(isAdverse) {
            TitleTV.setText(getContext().getResources().getString(R.string.reason_for_adverse_excptn));

            String desc = "<font color='#555555'><b>Note: </b></font>" + getContext().getResources().getString(R.string.reason_for_adverse_excptn_desc) ;
            descTextView.setText(Html.fromHtml(desc));
            descTextView.setTextColor(getContext().getResources().getColor(R.color.gray_text));
            desc2TxtView.setText(". " + getContext().getResources().getString(R.string.excp_reset_auto));
            descTextView.setVisibility(View.VISIBLE);
            desc2TxtView.setVisibility(View.VISIBLE);


        }else{
            descTextView.setVisibility(View.GONE);

            if(IsClaim){
                btnLoadingJob.setText(getContext().getResources().getString(R.string.claim));
                TitleTV.setText(getContext().getResources().getString(R.string.reason_for_claim_));
            }else{
                btnLoadingJob.setText(getContext().getResources().getString(R.string.reject));
                if(IsCompanyAssign) {
                    TitleTV.setText(getContext().getResources().getString(R.string.reason_for_reject_company));
                }else{
                    TitleTV.setText(getContext().getResources().getString(R.string.reason_for_reject));
                }
            }
        }



        remarksEditText.setSingleLine(false);
        remarksEditText.setMinLines(2);
        remarksEditText.setMaxLines(4);

        btnLoadingJob.setOnClickListener(new TrailorFieldListener());
        btnCancelLoadingJob.setOnClickListener(new CancelBtnListener());

        HideKeyboard();
    }


    void HideKeyboard(){
        try {
            InputMethodManager inputMethodManager = (InputMethodManager)  getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {   }
    }



    private class TrailorFieldListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            String remarks = remarksEditText.getText().toString().trim();

            if (remarks.trim().length() >= 4  ) {
                HideKeyboard();

                readyListener.JobBtnReady(remarks, IsClaim, IsCompanyAssign);

            } else {
                if(isAdverse) {
                    globally.EldScreenToast(btnLoadingJob, ConstantsEnum.ADVERSE_REASON_ALERT, getContext().getResources().getColor(R.color.red_eld));
                }else{
                    if(IsClaim){
                        globally.EldScreenToast(btnLoadingJob, ConstantsEnum.CLAIM_UNIDENTIFIED_REASON, getContext().getResources().getColor(R.color.red_eld));
                    }else{
                        if(IsCompanyAssign) {
                            globally.EldScreenToast(btnLoadingJob, ConstantsEnum.COMPANY_REJECT_REASON, getContext().getResources().getColor(R.color.red_eld));
                        }else{
                            globally.EldScreenToast(btnLoadingJob, ConstantsEnum.REJECT_REASON_ALERT, getContext().getResources().getColor(R.color.red_eld));
                        }
                    }
                }

            }

        }
    }


    private class CancelBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            HideKeyboard();
            readyListener.CancelReady();
        }
    }



}
