package com.josh.profrate;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.josh.profrate.elements.BackKeyInterceptableEditText;

public class CommentDialog extends Dialog {

    private BackKeyInterceptableEditText comment_input;
    private View.OnClickListener confirm_btn_listener;
    private TextView confirm_btn;

    public CommentDialog(Context context) {
        super(context, R.style.theme_comment_dialog);
        setContentView(R.layout.comment_dialog);
        Window window = getWindow();
        WindowManager.LayoutParams wmlp = window.getAttributes();
        wmlp.gravity = Gravity.BOTTOM;
        wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wmlp);
        comment_input = (BackKeyInterceptableEditText)findViewById(R.id.comment_input);
        comment_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        comment_input.setBackKeyClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        confirm_btn = (TextView)findViewById(R.id.confirm_btn);
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirm_btn_listener != null)
                    confirm_btn_listener.onClick(v);
                dismiss();
            }
        });
    }

    public CommentDialog setHint(String hint){
        comment_input.setHint(hint);
        return this;
    }

    public CommentDialog setOnConfirmButtonClickListener(View.OnClickListener listener){
        this.confirm_btn_listener = listener;
        return this;
    }

    public CommentDialog setConfirmButtonText(String text){
        confirm_btn.setText(text);
        return this;
    }

    public String getInputText(){
        return comment_input.getText().toString();
    }

    public CommentDialog setInputText(String text){
        comment_input.setText(text);
        return this;
    }

}
