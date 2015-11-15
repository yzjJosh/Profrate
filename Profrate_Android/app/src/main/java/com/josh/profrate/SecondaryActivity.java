package com.josh.profrate;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.josh.profrate.elements.Comment;
import com.josh.profrate.elements.Professor;
import com.josh.profrate.viewContents.CommentsView;
import com.josh.profrate.viewContents.ViewContent;
import com.josh.profrate.viewContents.ViewOneProsessor;
import com.josh.profrate.viewContents.ViewProfessors;

import java.util.ArrayList;
import java.util.HashMap;

public class SecondaryActivity extends Activity {

    public static final int SEARCH = 0;
    public static final int PROFESSOR_DETAIL = 1;
    public static final int COMMENTS = 2;

    private RelativeLayout content_layout;
    private ViewContent content;
    private ProgressBar progressBar;
    private LinearLayout error_sign;
    private LinearLayout warning_sign;
    private TextView activityTitile;
    private boolean isLoading;
    private boolean isActive;
    private String search_key;
    private int cur_view;

    private SearchHandler handler = new SearchHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        cur_view = getIntent().getIntExtra("view", -1);
        if(cur_view == SEARCH)
            search_key = getIntent().getStringExtra("key");
        activityTitile = ((TextView)findViewById(R.id.search_result_title));
        content_layout = (RelativeLayout) findViewById(R.id.search_result_content);
        progressBar = (ProgressBar) findViewById(R.id.search_result_progress_bar);
        error_sign = (LinearLayout) findViewById(R.id.search_result_error);
        warning_sign = (LinearLayout) findViewById(R.id.search_result_warning);
        switchContent(cur_view);
    }

    @Override
    public void onStart(){
        super.onStart();
        isActive = true;
        if(content != null && !content.isActive())
            content.show();
    }

    @Override
    public void onStop(){
        super.onStop();
        isActive = false;
    }

    private String viewName(int view_id){
        switch (view_id){
            case SEARCH:
                return "Search for \""+search_key+"\"";
            case PROFESSOR_DETAIL:
                return "Professor Details";
            case COMMENTS:
                return "Comments";
            default:
                break;
        }
        return null;
    }

    private void switchContent(int view_id){
        if(view_id >COMMENTS || view_id < SEARCH) return;
        activityTitile.setText(viewName(view_id));
        if(content != null) {
            content.clear();
            content = null;
        }
        progressBar.setVisibility(View.VISIBLE);
        error_sign.setVisibility(View.GONE);
        warning_sign.setVisibility(View.GONE);
        new LoadingThread(view_id).start();
    }

    public void onRefreshClick(View v){
        if(!isLoading)
            switchContent(cur_view);
    }

    public void onBackClick(View v){
        finish();
    }

    private class LoadingThread extends Thread{

        private final int type;

        public LoadingThread(int type){
            this.type = type;
        }

        @Override
        public void run(){
            isLoading = true;
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("type", type);
            try{
                //data.put("streams", BackEndAPI.searchStreams(search_key));
                data.put("success", true);
            } catch (Exception e) {
                e.printStackTrace();
                data.put("success", false);
            }
            Message msg = new Message();
            msg.obj = data;
            handler.sendMessage(msg);
        }
    }

    static class SearchHandler extends Handler {

        private SecondaryActivity activity;

        public SearchHandler(SecondaryActivity activity){
            this.activity = activity;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg){
            HashMap<String, Object> data = (HashMap<String, Object>)msg.obj;
            activity.progressBar.setVisibility(View.GONE);
            activity.isLoading = false;
            if((Boolean) data.get("success")) {
                switch((Integer)data.get("type")) {
                    case SEARCH:
                        activity.content = new ViewProfessors(activity, activity.content_layout, new ArrayList<Professor>());
                        break;
                    case PROFESSOR_DETAIL:
                        activity.content = new ViewOneProsessor(activity, activity.content_layout, new Professor());
                        break;
                    case COMMENTS:
                        activity.content = new CommentsView(activity, activity.content_layout, new ArrayList<Comment>());
                        break;
                    default:
                        break;
                }
                if(activity.isActive)
                    activity.content.show();
            }else
                activity.error_sign.setVisibility(View.VISIBLE);
        }
    }


}
