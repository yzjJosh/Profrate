package com.josh.profrate.elements;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class BackKeyInterceptableEditText extends EditText {

    private OnClickListener backKeyClickedListener;

    public BackKeyInterceptableEditText(Context context) {
        super(context);
    }

    public BackKeyInterceptableEditText(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public BackKeyInterceptableEditText(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    public void setBackKeyClickedListener(OnClickListener listener){
        this.backKeyClickedListener = listener;
    }

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && backKeyClickedListener != null) {
            backKeyClickedListener.onClick(this);
            return false;
        }
        return super.dispatchKeyEvent(event);
    }


}
