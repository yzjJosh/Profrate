package com.josh.profrate.viewContents;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.josh.profrate.CommentDialog;
import com.josh.profrate.CreateUserActivity;
import com.josh.profrate.R;
import com.josh.profrate.WriteArticleActivity;
import com.josh.profrate.dataStructures.Article;
import com.josh.profrate.dataStructures.Comment;
import com.josh.profrate.dataStructures.CommentReply;
import com.josh.profrate.dataStructures.Professor;
import com.josh.profrate.dataStructures.User;
import com.josh.profrate.elements.Credential;
import com.josh.profrate.elements.RatingStar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsAndArticlesView extends ViewContent{

    public static int CODE_WRITE_ARTICLE = 0;
    public static int CODE_EDIT_ARTICLE = 1;

    private static final int VIEW_COMMENTS = 0;
    private static final int VIEW_ARTICLES = 1;

    private static final int TASK_WRITE_COMMENT = 0;
    private static final int TASK_WRITE_ARTICLE = 1;

    private final Professor professor;
    private final Bitmap photo;
    private final List<Comment> comments;
    private final List<Article> articles;
    private final Map<Long, List<CommentReply>> commentReplies;
    private final Map<Long, List<Comment>> articleComments;
    private final Map<String, User> users;
    private ViewContent content;
    private Dialog processingDialog;
    private final Handler handler = new TaskHandler(this);
    private int viewType;
    private boolean isActive;

    public CommentsAndArticlesView(Context context, ViewGroup parentLayout, Professor professor,
                                   List<Comment> comments, List<Article> articles, Map<Long, List<CommentReply>> commentReplies,
                                   Map<Long, List<Comment>> articleComments, Map<String, User> users, Bitmap photo) {
        super(context, parentLayout);
        this.professor = professor;
        this.comments = comments;
        this.articles = articles;
        this.commentReplies = commentReplies;
        this.articleComments = articleComments;
        this.users = users;
        this.photo = photo;
        this.isActive = false;
        this.viewType = VIEW_COMMENTS;
        users.put(Credential.getCurrentUser().email, Credential.getCurrentUser());
    }

    @Override
    public void show() {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_comments_and_articles, parentLayout, true);
        if(photo != null)
            ((ImageView)parentLayout.findViewById(R.id.professor_photo)).setImageBitmap(photo);
        else
            ((ImageView)parentLayout.findViewById(R.id.professor_photo)).setImageResource(R.drawable.default_user_photo);
        ((TextView)parentLayout.findViewById(R.id.professor_name)).setText(professor.name);
        ((TextView)parentLayout.findViewById(R.id.professor_title)).setText(professor.title);
        if(professor.special_title != null)
            ((TextView)parentLayout.findViewById(R.id.professor_special_title)).setText(professor.special_title);
        else
            parentLayout.findViewById(R.id.professor_special_title).setVisibility(View.GONE);
        ((RatingStar)parentLayout.findViewById(R.id.professor_rating)).setRating(professor.overall_rating.overallRating());
        ((TextView)parentLayout.findViewById(R.id.comment_num)).setText(professor.comment_num + "");
        ((TextView)parentLayout.findViewById(R.id.article_num)).setText(professor.article_num + "");
        parentLayout.findViewById(R.id.comments_btn).setOnClickListener(commentsBtnClickListener);
        parentLayout.findViewById(R.id.articles_btn).setOnClickListener(articlesBtnClickListener);
        if(viewType == VIEW_COMMENTS) {
            content = new CommentsList(context, (ViewGroup) parentLayout.findViewById(R.id.content), comments, commentReplies, users);
            parentLayout.findViewById(R.id.comments_btn).setBackgroundResource(R.drawable.primary_color_border_btn_clicked);
            parentLayout.findViewById(R.id.articles_btn).setBackgroundResource(R.drawable.primary_color_border_btn_background);
            ((ImageView)parentLayout.findViewById(R.id.compose_icon)).setImageResource(R.drawable.write);
            ((TextView)parentLayout.findViewById(R.id.compose_text)).setText("Write Comment");
            parentLayout.findViewById(R.id.compose_btn).setOnClickListener(writeCommentClickListener);
        }
        else if(viewType == VIEW_ARTICLES) {
            content = new ArticleList(context, (ViewGroup) parentLayout.findViewById(R.id.content), articles, articleComments, users);
            parentLayout.findViewById(R.id.comments_btn).setBackgroundResource(R.drawable.primary_color_border_btn_background);
            parentLayout.findViewById(R.id.articles_btn).setBackgroundResource(R.drawable.primary_color_border_btn_clicked);
            ((ImageView)parentLayout.findViewById(R.id.compose_icon)).setImageResource(R.drawable.document);
            ((TextView)parentLayout.findViewById(R.id.compose_text)).setText("Write Article");
            parentLayout.findViewById(R.id.compose_btn).setOnClickListener(writeArticleListener);
        }
        content.show();
        isActive = true;
    }

    @Override
    public void clear() {
        if(content != null && content.isActive())
            content.clear();
        parentLayout.removeAllViews();
        isActive = false;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == CODE_WRITE_ARTICLE){
                String title = data.getStringExtra("title");
                String content = data.getStringExtra("content");
                processingDialog = new Dialog(context, R.style.theme_dialog);
                processingDialog.setContentView(R.layout.processing_dialog);
                processingDialog.setCancelable(false);
                processingDialog.show();
                new WriteArticleThread(title, content).start();
            }else if(requestCode == CODE_EDIT_ARTICLE){
                if(viewType == VIEW_ARTICLES)
                    ((ArticleList)content).onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private View.OnClickListener commentsBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(viewType == VIEW_COMMENTS) return;
            viewType = VIEW_COMMENTS;
            content.clear();
            content = new CommentsList(context, (ViewGroup)parentLayout.findViewById(R.id.content), comments, commentReplies, users);
            parentLayout.findViewById(R.id.comments_btn).setBackgroundResource(R.drawable.primary_color_border_btn_clicked);
            parentLayout.findViewById(R.id.articles_btn).setBackgroundResource(R.drawable.primary_color_border_btn_background);
            ((ImageView)parentLayout.findViewById(R.id.compose_icon)).setImageResource(R.drawable.write);
            ((TextView)parentLayout.findViewById(R.id.compose_text)).setText("Write Comment");
            parentLayout.findViewById(R.id.compose_btn).setOnClickListener(writeCommentClickListener);
            content.show();
        }
    };

    private View.OnClickListener articlesBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(viewType == VIEW_ARTICLES) return;
            viewType = VIEW_ARTICLES;
            content.clear();
            content = new ArticleList(context, (ViewGroup)parentLayout.findViewById(R.id.content), articles, articleComments, users);
            parentLayout.findViewById(R.id.comments_btn).setBackgroundResource(R.drawable.primary_color_border_btn_background);
            parentLayout.findViewById(R.id.articles_btn).setBackgroundResource(R.drawable.primary_color_border_btn_clicked);
            ((ImageView)parentLayout.findViewById(R.id.compose_icon)).setImageResource(R.drawable.document);
            ((TextView)parentLayout.findViewById(R.id.compose_text)).setText("Write Article");
            parentLayout.findViewById(R.id.compose_btn).setOnClickListener(writeArticleListener);
            content.show();
        }
    };

    private View.OnClickListener writeCommentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final CommentDialog dialog = new CommentDialog(context);
            dialog.setHint("Type comment").setConfirmButtonText("Comment").
                    setOnConfirmButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(dialog.getInputText().length() == 0){
                                Toast.makeText(context, "Please enter your comment!", Toast.LENGTH_LONG).show();
                                return;
                            }
                            processingDialog = new Dialog(context, R.style.theme_dialog);
                            processingDialog.setContentView(R.layout.processing_dialog);
                            processingDialog.setCancelable(false);
                            processingDialog.show();
                            new CommentThread(dialog.getInputText()).start();
                        }
                    }).show();
        }
    };

    private View.OnClickListener writeArticleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, WriteArticleActivity.class);
            ((Activity)context).startActivityForResult(intent, CODE_WRITE_ARTICLE);
        }
    };

    private class CommentThread extends Thread{

        private final String commentContent;

        public CommentThread(String commentContent){
            this.commentContent = commentContent;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_WRITE_COMMENT);
            try {
                long comment_id = professor.comment(commentContent);
                if(comment_id != -1){
                    data.put("comment", Comment.getComment(comment_id));
                    data.put("success", true);
                }else
                    data.put("success", false);
            } catch (IOException e) {
                e.printStackTrace();
                data.put("success", false);
            }
            message.obj = data;
            handler.sendMessage(message);
        }
    }

    private class WriteArticleThread extends Thread{

        private final String title;
        private final String content;

        public WriteArticleThread(String title, String content){
            this.title = title;
            this.content = content;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_WRITE_ARTICLE);
            data.put("title", title);
            data.put("content", content);
            try {
                long article_id = professor.writeArticle(title, content);
                if(article_id != -1){
                    data.put("article", Article.getArticle(article_id));
                    data.put("success", true);
                }else
                    data.put("success", false);
            } catch (IOException e) {
                e.printStackTrace();
                data.put("success", false);
            }
            message.obj = data;
            handler.sendMessage(message);
        }

    }

    private static class TaskHandler extends Handler{

        private CommentsAndArticlesView content;

        public TaskHandler(CommentsAndArticlesView content){
            this.content = content;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message message){
            HashMap<String, Object> data = (HashMap<String, Object>)message.obj;
            final int task = (int)data.get("task");
            switch(task){
                case TASK_WRITE_COMMENT:
                    content.processingDialog.dismiss();
                    if((boolean)data.get("success")) {
                        Comment comment = (Comment) data.get("comment");
                        content.comments.add(0, comment);
                        content.commentReplies.put(comment.id, new ArrayList<CommentReply>());
                        if(content.viewType == VIEW_COMMENTS) {
                            content.clear();
                            content.show();
                        }
                        ((TextView)content.parentLayout.findViewById(R.id.comment_num)).setText(content.comments.size()+"");
                    }else
                        Toast.makeText(content.context, "Unable to comment " + content.professor.name, Toast.LENGTH_LONG).show();
                    break;
                case TASK_WRITE_ARTICLE:
                    content.processingDialog.dismiss();
                    if((boolean)data.get("success")) {
                        Article article = (Article) data.get("article");
                        content.articles.add(0, article);
                        content.articleComments.put(article.id, new ArrayList<Comment>());
                        if(content.viewType == VIEW_ARTICLES) {
                            content.clear();
                            content.show();
                        }
                        ((TextView)content.parentLayout.findViewById(R.id.article_num)).setText(content.articles.size()+"");
                    }else {
                        Toast.makeText(content.context, "Unable to write article for " + content.professor.name, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(content.context, WriteArticleActivity.class);
                        intent.putExtra("title", (String)data.get("title"));
                        intent.putExtra("content", (String)data.get("content"));
                        ((Activity)content.context).startActivityForResult(intent, CODE_WRITE_ARTICLE);
                    }
                    break;
                default:
                    break;
            }
        }

    }

}
