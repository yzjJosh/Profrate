package com.josh.profrate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class WriteArticleActivity extends Activity {

    private EditText titleEditText;
    private EditText contentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_article);
        titleEditText = (EditText)findViewById(R.id.title);
        contentEditText = (EditText)findViewById(R.id.content);
        titleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        Intent intent = getIntent();
        titleEditText.setText(intent.getStringExtra("title"));
        contentEditText.setText(intent.getStringExtra("content"));
    }

    @Override
    public void onBackPressed(){
        onBackClick(null);
    }

    public void onBackClick(View view){
        AlertDialog alertDialog = new AlertDialog.Builder(WriteArticleActivity.this).create();
        alertDialog.setTitle("Warning");
        alertDialog.setIcon(R.drawable.attention);
        alertDialog.setMessage("Are you sure you want to cancel posting this article? All the data will lost.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_CANCELED, returnIntent);
                        finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void onConfirmClick(View view){
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();
        if(title.length() == 0) {
            Toast.makeText(this, "Please enter the title!", Toast.LENGTH_LONG).show();
            return;
        }
        if(content.length() == 0) {
            Toast.makeText(this, "Please enter the content!", Toast.LENGTH_LONG).show();
            return;
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("title", title);
        returnIntent.putExtra("content", content);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
