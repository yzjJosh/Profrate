package com.josh.profrate;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.josh.profrate.dataStructures.Article;
import com.josh.profrate.dataStructures.Comment;
import com.josh.profrate.dataStructures.CommentReply;
import com.josh.profrate.dataStructures.Professor;
import com.josh.profrate.dataStructures.Rating;
import com.josh.profrate.dataStructures.User;
import com.josh.profrate.elements.BitmapFetcher;
import com.josh.profrate.viewContents.CommentsAndArticlesView;
import com.josh.profrate.viewContents.ViewContent;
import com.josh.profrate.viewContents.ViewOneProsessor;
import com.josh.profrate.viewContents.ViewProfessors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SecondaryActivity extends FragmentActivity {

    public static final int SEARCH = 0;
    public static final int PROFESSOR_DETAIL = 1;
    public static final int COMMENTS_AND_ARTICLES = 2;
    public static final int SINGLE_COMMENT = 3;
    public static final int SINGLE_ARTICLE = 4;

    private RelativeLayout content_layout;
    private ViewContent content;
    private ProgressBar progressBar;
    private LinearLayout error_sign;
    private LinearLayout warning_sign;
    private TextView activityTitile;
    private boolean isLoading;
    private boolean isActive;
    private Object value;
    private int cur_view;

    private SearchHandler handler = new SearchHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        cur_view = getIntent().getIntExtra("view", -1);
        switch(cur_view){
            case SEARCH:
                value = getIntent().getStringExtra("key");
                break;
            case PROFESSOR_DETAIL:
            case COMMENTS_AND_ARTICLES:
                value = getIntent().getLongExtra("prof_id", 0L);
                break;
            case SINGLE_COMMENT:
                value = getIntent().getLongExtra("comment_id", 0L);
                break;
            case SINGLE_ARTICLE:
                value = getIntent().getLongExtra("article_id", 0L);
            default:
                break;
        }
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
                return "Search for \""+value+"\"";
            case PROFESSOR_DETAIL:
                return "Professor Details";
            case COMMENTS_AND_ARTICLES:
                return "Comments and Articles";
            case SINGLE_COMMENT:
                return "Comment Details";
            case SINGLE_ARTICLE:
                return "Article Details";
            default:
                break;
        }
        return null;
    }

    private void switchContent(int view_id){
        if(view_id >COMMENTS_AND_ARTICLES || view_id < SEARCH) return;
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
        @SuppressWarnings("unchecked")
        public void run(){
            isLoading = true;
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("type", type);
            try{
                switch (type){
                    case SEARCH:
                        break;
                    case PROFESSOR_DETAIL:
                        data.put("professor", Professor.getProfessor((long)value));
                        data.put("photo", BitmapFetcher.fetchBitmap(((Professor) data.get("professor")).image_url));
                        data.put("myRating", ((Professor) data.get("professor")).getRating());
                        break;
                    case COMMENTS_AND_ARTICLES:
                        Professor professor = Professor.getProfessor((long) value);
                        data.put("professor", professor);
                        data.put("comments", professor.getComments());
                        data.put("articles", professor.getArticles());
                        Map<String, User> users = new HashMap<String, User>();
                        Map<Long, List<CommentReply>> commentReplies = new HashMap<Long, List<CommentReply>>();
                        for(Comment comment: (List<Comment>) data.get("comments")) {
                            commentReplies.put(comment.id, comment.getReplies());
                            if(!users.containsKey(comment.author_email))
                                users.put(comment.author_email, User.getUser(comment.author_email));
                            for(CommentReply reply: commentReplies.get(comment.id))
                                if(!users.containsKey(reply.author_email))
                                    users.put(reply.author_email, User.getUser(reply.author_email));
                        }
                        data.put("commentReplies", commentReplies);
                        Map<Long, List<Comment>> articleComments = new HashMap<Long, List<Comment>>();
                        for(Article article: (List<Article>) data.get("articles")) {
                            articleComments.put(article.id, article.getComments());
                            if(!users.containsKey(article.author_email))
                                users.put(article.author_email, User.getUser(article.author_email));
                            for(Comment comment: articleComments.get(article.id))
                                if(!users.containsKey(comment.author_email))
                                    users.put(comment.author_email, User.getUser(comment.author_email));
                        }
                        data.put("articleComments", articleComments);
                        data.put("users", users);
                        data.put("photo", BitmapFetcher.fetchBitmap(((Professor) data.get("professor")).image_url));
                        break;
                    default:
                        break;
                }
                data.put("success", true);
            } catch (IOException e){
                e.printStackTrace();
                data.put("success", false);
            }
            Message msg = new Message();
            msg.obj = data;
            handler.sendMessage(msg);
        }
    }

    private static class SearchHandler extends Handler {

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
                        activity.content = new ViewOneProsessor(activity, activity.content_layout, (Professor)data.get("professor"),
                                (Bitmap)data.get("photo"), (Rating)data.get("myRating"));
                        break;
                    case COMMENTS_AND_ARTICLES:
                        activity.content = new CommentsAndArticlesView(activity, activity.content_layout, (Professor) data.get("professor"),
                                (List<Comment>) data.get("comments"), (List<Article>) data.get("articles"), (Map<Long, List<CommentReply>>)data.get("commentReplies"),
                                (Map<Long, List<Comment>>)data.get("articleComments"), (Map<String, User>)data.get("users"), (Bitmap)data.get("photo"));
                        break;
                    case SINGLE_COMMENT:
                        break;
                    case SINGLE_ARTICLE:
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
