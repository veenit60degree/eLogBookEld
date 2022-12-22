package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.als.logistic.R;
import com.als.logistic.UILApplication;
import com.simplify.ink.InkView;

public class CtPatDialog extends Dialog {


    public interface SignListener {
        public void SignOkBtn(InkView inkView, ImageView imageView, int viewFlag, boolean IsSigned);
    }

    private SignListener readyListener;
    boolean IsSigned = false;
    Button signOkBtn, signCancelBtn;
    RelativeLayout clearSignBtn;
    LinearLayout inkLinLay;
    InkView inkView;
    ImageView imageView;
    int viewFlag;

    public CtPatDialog(Context context, ImageView imgView, int flag, SignListener readyListener) {
        super(context);
        this.readyListener = readyListener;
        this.imageView = imgView;
        this.viewFlag = flag;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.popup_signature);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        signOkBtn       = (Button) findViewById(R.id.signOkBtn);
        signCancelBtn   = (Button) findViewById(R.id.signCancelBtn);
        clearSignBtn    = (RelativeLayout) findViewById(R.id.clearSignBtn);
        inkLinLay       = (LinearLayout)findViewById(R.id.inkLinLay);
        inkView         = (InkView) findViewById(R.id.inkView);

       // Bitmap bitmap = loadBitmapFromView(imageView);
       // BitmapDrawable d = new BitmapDrawable(getContext().getResources(), bitmap);
       // inkView.setBackgroundDrawable(d);

        inkView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                IsSigned = true;
                return false;
            }
        });

        // if (UILApplication.getInstance().getInstance().PhoneLightMode() == Configuration.UI_MODE_NIGHT_YES) {
        if(UILApplication.getInstance().isNightModeEnabled()){
            inkLinLay.setBackgroundColor(getContext().getResources().getColor(R.color.gray_background));
        }

        signOkBtn.setOnClickListener(new SignOkListener());
        signCancelBtn.setOnClickListener(new CancelBtnListener());
        clearSignBtn.setOnClickListener(new ClearBtnListener());

    }




    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap( v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }



    private class SignOkListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            readyListener.SignOkBtn(inkView, imageView, viewFlag, IsSigned);
        }
    }


    private class CancelBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    }

    private class ClearBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            IsSigned = false;
            inkView.clearFlags();
            inkView.clear();

            try {
                inkView.setBackgroundDrawable(null);
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }

}