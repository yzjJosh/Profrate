package com.josh.profrate.viewContents;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.josh.profrate.CommentDialog;
import com.josh.profrate.R;
import com.josh.profrate.dataStructures.Article;
import com.josh.profrate.dataStructures.Comment;
import com.josh.profrate.dataStructures.CommentReply;
import com.josh.profrate.dataStructures.User;
import com.josh.profrate.elements.BitmapFetcher;
import com.josh.profrate.elements.Credential;
import com.josh.profrate.elements.TimeConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewOneArticle extends ViewContent{

    private static final int TASK_LOAD_PHOTO = 0;
    private static final int TASK_TOGGLE_LIKE = 1;
    private static final int TASK_TOGGLE_DISLIKE = 2;
    private static final int TASK_EDIT_ARTICLE = 4;
    private static final int TASK_DELETE_ARTICLE = 5;
    private static final int TASK_COMMENT_ARTICLE = 6;

    private Article article;
    private List<Comment> comments;
    Map<Long, List<CommentReply>> commentReplies;
    private Map<String, User> users;
    private Handler handler = new TaskHandler(this);
    private ViewContent commentListContent;
    private Dialog processingDialog;
    private boolean isTogglingLikeness;
    private boolean isActive;

    public ViewOneArticle(Context context, ViewGroup parentLayout, Article article, List<Comment> comments,
                          Map<Long, List<CommentReply>> commentReplies, Map<String, User> users) {
        super(context, parentLayout);
        this.article = article;
        this.comments = comments;
        this.users = users;
        this.commentReplies = commentReplies;
        this.isTogglingLikeness = false;
        this.isActive = false;
        users.put(Credential.getCurrentUser().email, Credential.getCurrentUser());
    }

    @Override
    public void show() {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_article, parentLayout, true);
        User author = users.get(article.author_email);
        ((TextView)parentLayout.findViewById(R.id.user_name)).setText(author.name);
        ((TextView)parentLayout.findViewById(R.id.title)).setText(article.title);
        ((TextView)parentLayout.findViewById(R.id.time)).setText(TimeConverter.convertTime(article.time));
        ((TextView)parentLayout.findViewById(R.id.content)).setText(article.content);
        ((TextView)parentLayout.findViewById(R.id.like_num)).setText(article.liked_by.size() + "");
        ((TextView)parentLayout.findViewById(R.id.dislike_num)).setText(article.disliked_by.size() + "");
        ((TextView)parentLayout.findViewById(R.id.comment_num)).setText(article.comment_num + "");
        if (article.liked_by.contains(Credential.getCredential().getSelectedAccountName()))
            ((ImageView)parentLayout.findViewById(R.id.like_icon)).setImageResource(R.drawable.like_colored);
        else
            ((ImageView)parentLayout.findViewById(R.id.like_icon)).setImageResource(R.drawable.like_bw);
        if (article.disliked_by.contains(Credential.getCredential().getSelectedAccountName()))
            ((ImageView)parentLayout.findViewById(R.id.dislike_icon)).setImageResource(R.drawable.dislike_colored);
        else
            ((ImageView)parentLayout.findViewById(R.id.dislike_icon)).setImageResource(R.drawable.dislike_bw);
        if(author.email.equals(Credential.getCredential().getSelectedAccountName())){
            parentLayout.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processingDialog = new Dialog(context, R.style.theme_dialog);
                    processingDialog.setContentView(R.layout.processing_dialog);
                    processingDialog.setCancelable(false);
                    processingDialog.show();
                    new EditArticleThread("test editing", "test editing").start();
                }
            });
            parentLayout.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processingDialog = new Dialog(context, R.style.theme_dialog);
                    processingDialog.setContentView(R.layout.processing_dialog);
                    processingDialog.setCancelable(false);
                    processingDialog.show();
                    new DeleteArticleThread().start();
                }
            });
        }else
            parentLayout.findViewById(R.id.edit_btn_area).setVisibility(View.GONE);
        parentLayout.findViewById(R.id.comment_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CommentDialog dialog = new CommentDialog(context);
                dialog.setHint("Type comment").setConfirmButtonText("Comment").
                        setOnConfirmButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                processingDialog = new Dialog(context, R.style.theme_dialog);
                                processingDialog.setContentView(R.layout.processing_dialog);
                                processingDialog.setCancelable(false);
                                processingDialog.show();
                                new CommentArticleThread(dialog.getInputText()).start();
                            }
                        }).show();
            }
        });
        parentLayout.findViewById(R.id.like_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LikeThread().start();
            }
        });
        parentLayout.findViewById(R.id.dislike_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DislikeThread().start();
            }
        });
        LinearLayout comment_list = (LinearLayout)parentLayout.findViewById(R.id.comment_list);
        commentListContent = new CommentsList(context, comment_list, comments, commentReplies, users);
        commentListContent.show();
        this.isActive = true;
    }

    @Override
    public void clear() {
        if(commentListContent != null)
            commentListContent.clear();
        parentLayout.removeAllViews();
        this.isActive = false;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    private class LoadPhotoThread extends Thread{

        private final String url;
        private final ImageView image;

        public LoadPhotoThread(String url, ImageView image){
            this.url = url;
            this.image = image;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_LOAD_PHOTO);
            data.put("bitmap", BitmapFetcher.fetchBitmap(url));
            data.put("image", image);
            message.obj = data;
            handler.sendMessage(message);
        }

    }

    private class LikeThread extends Thread{

        @Override
        public void run(){
            if(isTogglingLikeness) return;
            isTogglingLikeness = false;
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_TOGGLE_LIKE);
            data.put("article", article);
            data.put("commentToolBar", parentLayout.findViewById(R.id.comment_tool_bar));
            try {
                boolean success = false;
                data.put("success", success = article.toggle_like());
                if(success){
                    if(article.liked_by.contains(Credential.getCredential().getSelectedAccountName()))
                        article.liked_by.remove(Credential.getCredential().getSelectedAccountName());
                    else
                        article.liked_by.add(Credential.getCredential().getSelectedAccountName());
                    article.disliked_by.remove(Credential.getCredential().getSelectedAccountName());
                }

            } catch (IOException e) {
                data.put("success", false);
            }
            message.obj = data;
            handler.sendMessage(message);
        }

    }

    private class DislikeThread extends Thread{

        @Override
        public void run(){
            if(isTogglingLikeness) return;
            isTogglingLikeness = true;
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_TOGGLE_DISLIKE);
            data.put("article", article);
            data.put("commentToolBar", parentLayout.findViewById(R.id.comment_tool_bar));
            try {
                boolean success = false;
                data.put("success", success = article.toggle_dislike());
                if(success){
                    if(article.disliked_by.contains(Credential.getCredential().getSelectedAccountName()))
                        article.disliked_by.remove(Credential.getCredential().getSelectedAccountName());
                    else
                        article.disliked_by.add(Credential.getCredential().getSelectedAccountName());
                    article.liked_by.remove(Credential.getCredential().getSelectedAccountName());
                }
            } catch (IOException e) {
                data.put("success", false);
            }
            message.obj = data;
            handler.sendMessage(message);
        }


    }

    private class EditArticleThread extends Thread{

        private final String newTitle;
        private final String newContent;

        public EditArticleThread(String newTitle, String newContent){
            this.newTitle = newTitle;
            this.newContent = newContent;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_EDIT_ARTICLE);
            data.put("newTitle", newTitle);
            data.put("newContent", newContent);
            try {
                data.put("success", article.edit(newTitle, newContent));
            } catch (IOException e) {
                data.put("success", false);
                e.printStackTrace();
            }
            message.obj = data;
            handler.sendMessage(message);
        }
    }

    private class DeleteArticleThread extends Thread{

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_DELETE_ARTICLE);
            try{
                data.put("success", article.delete());
            }catch (IOException e){
                e.printStackTrace();
                data.put("success", false);
            }
            message.obj = data;
            handler.sendMessage(message);
        }
    }

    private class CommentArticleThread extends Thread{

        private final String commentContent;

        public CommentArticleThread(String commentContent){
            this.commentContent = commentContent;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_COMMENT_ARTICLE);
            try {
                long comment_id = article.comment(commentContent);
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

    private static class TaskHandler extends Handler {

        private ViewOneArticle content;

        public TaskHandler(ViewOneArticle content){
            this.content = content;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg){
            if(!content.isActive()) return;
            HashMap<String, Object> data = (HashMap<String, Object>) msg.obj;
            final int task = (int) data.get("task");
            switch (task){
                case TASK_LOAD_PHOTO:
                    Bitmap bitmap = (Bitmap) data.get("bitmap");
                    ImageView image = (ImageView) data.get("image");
                    if(bitmap != null)
                        image.setImageBitmap(bitmap);
                    break;
                case TASK_TOGGLE_LIKE:
                    Article article = (Article) data.get("article");
                    View commentToolBar = (View) data.get("commentToolBar");
                    content.isTogglingLikeness = false;
                    if((boolean)data.get("success")) {
                        if (article.liked_by.contains(Credential.getCredential().getSelectedAccountName())) {
                            Toast.makeText(content.context, "Successfully like the article!", Toast.LENGTH_LONG).show();
                            ((ImageView)commentToolBar.findViewById(R.id.like_icon)).setImageResource(R.drawable.like_colored);
                        } else {
                            Toast.makeText(content.context, "Successfully cancel liking the article!", Toast.LENGTH_LONG).show();
                            ((ImageView)commentToolBar.findViewById(R.id.like_icon)).setImageResource(R.drawable.like_bw);
                        }
                        ((ImageView)commentToolBar.findViewById(R.id.dislike_icon)).setImageResource(R.drawable.dislike_bw);
                        ((TextView)commentToolBar.findViewById(R.id.like_num)).setText(article.liked_by.size() + "");
                        ((TextView)commentToolBar.findViewById(R.id.dislike_num)).setText(article.disliked_by.size() + "");
                    }else
                        Toast.makeText(content.context, "Unable to toggle liking the article!", Toast.LENGTH_LONG).show();
                    break;
                case TASK_TOGGLE_DISLIKE:
                    article = (Article) data.get("article");
                    commentToolBar = (View) data.get("commentToolBar");
                    content.isTogglingLikeness = false;
                    if((boolean)data.get("success")) {
                        if (article.disliked_by.contains(Credential.getCredential().getSelectedAccountName())) {
                            Toast.makeText(content.context, "Successfully dislike the article!", Toast.LENGTH_LONG).show();
                            ((ImageView)commentToolBar.findViewById(R.id.dislike_icon)).setImageResource(R.drawable.dislike_colored);
                        } else {
                            Toast.makeText(content.context, "Successfully cancel disliking the article!", Toast.LENGTH_LONG).show();
                            ((ImageView)commentToolBar.findViewById(R.id.dislike_icon)).setImageResource(R.drawable.dislike_bw);
                        }
                        ((ImageView)commentToolBar.findViewById(R.id.like_icon)).setImageResource(R.drawable.like_bw);
                        ((TextView)commentToolBar.findViewById(R.id.like_num)).setText(article.liked_by.size() + "");
                        ((TextView)commentToolBar.findViewById(R.id.dislike_num)).setText(article.disliked_by.size() + "");
                    }else
                        Toast.makeText(content.context, "Unable to toggle disliking the article!", Toast.LENGTH_LONG).show();
                    break;
                case TASK_EDIT_ARTICLE:
                    content.processingDialog.dismiss();
                    if((boolean)data.get("success")) {
                        ((TextView) content.parentLayout.findViewById(R.id.title)).setText((String) data.get("newTitle"));
                        ((TextView) content.parentLayout.findViewById(R.id.content)).setText((String) data.get("newContent"));
                    }else
                        Toast.makeText(content.context, "Unable to edit the article!", Toast.LENGTH_LONG).show();
                    break;
                case TASK_DELETE_ARTICLE:
                    content.processingDialog.dismiss();
                    if((boolean)data.get("success")) {
                        Toast.makeText(content.context, "Successfully delete the article!", Toast.LENGTH_LONG).show();
                        ((Activity)content.context).finish();
                    }else
                        Toast.makeText(content.context, "Unable to delete the article!", Toast.LENGTH_LONG).show();
                    break;
                case TASK_COMMENT_ARTICLE:
                    content.processingDialog.dismiss();
                    if((boolean)data.get("success")){
                        Comment comment = (Comment)data.get("comment");
                        content.comments.add(0, comment);
                        content.commentReplies.put(comment.id, new ArrayList<CommentReply>());
                        content.commentListContent.clear();
                        content.commentListContent.show();
                        ((TextView)content.parentLayout.findViewById(R.id.comment_num)).setText(content.comments.size()+"");
                    }else
                        Toast.makeText(content.context, "Unable to comment this article", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

    }

}
