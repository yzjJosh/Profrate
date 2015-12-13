package com.josh.profrate.viewContents;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.josh.profrate.CommentDialog;
import com.josh.profrate.R;
import com.josh.profrate.SecondaryActivity;
import com.josh.profrate.dataStructures.Article;
import com.josh.profrate.dataStructures.Comment;
import com.josh.profrate.dataStructures.CommentReply;
import com.josh.profrate.dataStructures.User;
import com.josh.profrate.elements.BitmapFetcher;
import com.josh.profrate.elements.Credential;
import com.josh.profrate.elements.TimeConverter;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArticleList extends ViewContent {

    private final static int MAX_COMMENT_NUM = 3;

    private static final int TASK_LOAD_PHOTO = 0;
    private static final int TASK_TOGGLE_LIKE = 1;
    private static final int TASK_TOGGLE_DISLIKE = 2;
    private static final int TASK_EDIT_ARTICLE = 3;
    private static final int TASK_DELETE_ARTICLE = 4;
    private static final int TASK_EDIT_COMMENT = 5;
    private static final int TASK_DELETE_COMMENT = 6;
    private static final int TASK_COMMENT_ARTICLE = 7;

    private final List<Article> articles;
    private final Map<String, User> users;
    private final Map<Long, List<Comment>> articleComments;
    private final Handler handler = new TaskHandler(this);
    private final Set<Article> isTogglingLikeness;
    private Dialog processingDialog;
    private boolean isActive;

    public ArticleList(Context context, ViewGroup parentLayout, List<Article> articles,
                        Map<Long, List<Comment>> articleComments, Map<String, User> users) {
        super(context, parentLayout);
        this.articles = articles;
        this.users = users;
        this.articleComments = articleComments;
        this.isTogglingLikeness = new HashSet<Article>();
        this.isActive = false;
        users.put(Credential.getCurrentUser().email, Credential.getCurrentUser());
    }

    @Override
    public void show() {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(final Article article: articles){
            final View articleView = inflater.inflate(R.layout.article_item, parentLayout, false);
            User author = users.get(article.author_email);
            ((TextView)articleView.findViewById(R.id.user_name)).setText(author.name);
            ((TextView)articleView.findViewById(R.id.title)).setText(article.title);
            ((TextView)articleView.findViewById(R.id.time)).setText(TimeConverter.convertTime(article.time));
            ((TextView)articleView.findViewById(R.id.content)).setText(article.content);
            ((TextView)articleView.findViewById(R.id.like_num)).setText(article.liked_by.size() + "");
            ((TextView)articleView.findViewById(R.id.dislike_num)).setText(article.disliked_by.size() + "");
            ((TextView)articleView.findViewById(R.id.comment_num)).setText(article.comment_num + "");
            if (article.liked_by.contains(Credential.getCredential().getSelectedAccountName()))
                ((ImageView)articleView.findViewById(R.id.like_icon)).setImageResource(R.drawable.like_colored);
            else
                ((ImageView)articleView.findViewById(R.id.like_icon)).setImageResource(R.drawable.like_bw);
            if (article.disliked_by.contains(Credential.getCredential().getSelectedAccountName()))
                ((ImageView)articleView.findViewById(R.id.dislike_icon)).setImageResource(R.drawable.dislike_colored);
            else
                ((ImageView)articleView.findViewById(R.id.dislike_icon)).setImageResource(R.drawable.dislike_bw);
            if(author.email.equals(Credential.getCredential().getSelectedAccountName())){
                articleView.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processingDialog = new Dialog(context, R.style.theme_dialog);
                        processingDialog.setContentView(R.layout.processing_dialog);
                        processingDialog.setCancelable(false);
                        processingDialog.show();
                        new EditArticleThread(article, "test editing", "test editing",
                                (TextView)articleView.findViewById(R.id.title),
                                (TextView)articleView.findViewById(R.id.content)).start();
                    }
                });
                articleView.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processingDialog = new Dialog(context, R.style.theme_dialog);
                        processingDialog.setContentView(R.layout.processing_dialog);
                        processingDialog.setCancelable(false);
                        processingDialog.show();
                        new DeleteArticleThread(article, articleView, parentLayout).start();
                    }
                });
            }else
                articleView.findViewById(R.id.edit_btn_area).setVisibility(View.GONE);
            final LinearLayout reply_list = (LinearLayout)articleView.findViewById(R.id.reply_list);
            articleView.findViewById(R.id.article_click_area).setOnClickListener(new ArticleClickListener(article));
            articleView.findViewById(R.id.comment_btn).setOnClickListener(new View.OnClickListener() {
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
                                    new CommentArticleThread(article, dialog.getInputText(), reply_list,
                                            (TextView) articleView.findViewById(R.id.comment_num)).start();
                                }
                            }).show();
                }
            });
            articleView.findViewById(R.id.like_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new LikeThread(article, articleView.findViewById(R.id.comment_tool_bar)).start();
                }
            });
            articleView.findViewById(R.id.dislike_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DislikeThread(article, articleView.findViewById(R.id.comment_tool_bar)).start();
                }
            });
            new LoadPhotoThread(author.photo_url, (ImageView)articleView.findViewById(R.id.user_photo)).start();
            List<Comment> comments = articleComments.get(article.id);
            int count = 0;
            for (final Comment comment : comments) {
                if (count == MAX_COMMENT_NUM) break;
                View reply_item = generateCommentView(comment, reply_list,
                        (TextView) articleView.findViewById(R.id.comment_num), author.name);
                reply_list.addView(reply_item);
                new LoadPhotoThread(users.get(comment.author_email).photo_url,
                        (ImageView)reply_item.findViewById(R.id.user_photo)).start();
                count++;
            }
            parentLayout.addView(articleView);
        }
        isActive = true;
    }

    private View generateCommentView(final Comment comment, final ViewGroup parent, final TextView commentNumText, String articleAuthorName){
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View reply_item = inflater.inflate(R.layout.comment_reply_item, parent, false);
        User user = users.get(comment.author_email);
        ((TextView) reply_item.findViewById(R.id.user_name)).setText(user.name);
        ((TextView) reply_item.findViewById(R.id.replyToName)).setText(articleAuthorName);
        ((TextView) reply_item.findViewById(R.id.comment)).setText(comment.content);
        ((TextView) reply_item.findViewById(R.id.time)).setText(TimeConverter.convertTime(comment.time));
        if(comment.author_email.equals(Credential.getCredential().getSelectedAccountName())){
            reply_item.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CommentDialog dialog = new CommentDialog(context);
                    dialog.setHint("Edit comment").setConfirmButtonText("Edit").
                            setInputText(((TextView) reply_item.findViewById(R.id.comment)).getText().toString()).
                            setOnConfirmButtonClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    processingDialog = new Dialog(context, R.style.theme_dialog);
                                    processingDialog.setContentView(R.layout.processing_dialog);
                                    processingDialog.setCancelable(false);
                                    processingDialog.show();
                                    new EditCommentThread(comment, dialog.getInputText(),
                                            (TextView) reply_item.findViewById(R.id.comment)).start();
                                }
                            }).show();
                }
            });
            reply_item.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processingDialog = new Dialog(context, R.style.theme_dialog);
                    processingDialog.setContentView(R.layout.processing_dialog);
                    processingDialog.setCancelable(false);
                    processingDialog.show();
                    new DeleteCommentThread(comment, reply_item, parent, commentNumText).start();
                }
            });
        }else
            reply_item.findViewById(R.id.edit_btn_area).setVisibility(View.GONE);
        return  reply_item;
    }

    @Override
    public void clear() {
        parentLayout.removeAllViews();
        isActive = false;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    private class ArticleClickListener implements View.OnClickListener{

        private final Article article;

        public ArticleClickListener(Article article){
            this.article = article;
        }

        @Override
        public void onClick(View v) {
            context.startActivity(new Intent(context, SecondaryActivity.class).
                    putExtra("view", SecondaryActivity.SINGLE_ARTICLE).
                    putExtra("article_id", article.id));
        }
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

        private final Article article;
        private final View commentToolBar;

        public LikeThread(Article article, View commentToolBar){
            this.article = article;
            this.commentToolBar = commentToolBar;
        }

        @Override
        public void run(){
            if(isTogglingLikeness.contains(article)) return;
            isTogglingLikeness.add(article);
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_TOGGLE_LIKE);
            data.put("article", article);
            data.put("commentToolBar", commentToolBar);
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

        private final Article article;
        private final View commentToolBar;

        public DislikeThread(Article article, View commentToolBar){
            this.article = article;
            this.commentToolBar = commentToolBar;
        }

        @Override
        public void run(){
            if(isTogglingLikeness.contains(article)) return;
            isTogglingLikeness.add(article);
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_TOGGLE_DISLIKE);
            data.put("article", article);
            data.put("commentToolBar", commentToolBar);
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

        private final Article article;
        private final String newTitle;
        private final String newContent;
        private final TextView titleText;
        private final TextView contentText;

        public EditArticleThread(Article article, String newTitle, String newContent,
                TextView titleText, TextView contentText){
            this.article = article;
            this.newTitle = newTitle;
            this.newContent = newContent;
            this.titleText = titleText;
            this.contentText = contentText;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_EDIT_ARTICLE);
            data.put("newTitle", newTitle);
            data.put("newContent", newContent);
            data.put("titleText", titleText);
            data.put("contentText", contentText);
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

        private final Article article;
        private final View article_item;
        private final ViewGroup parent;

        public DeleteArticleThread(Article article, View article_item, ViewGroup parent){
            this.article = article;
            this.article_item = article_item;
            this.parent = parent;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_DELETE_ARTICLE);
            data.put("article_item", article_item);
            data.put("parent", parent);
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

    private class EditCommentThread extends Thread{

        private final Comment comment;
        private final String newComment;
        private final TextView commentText;

        public EditCommentThread(Comment comment, String newComment, TextView commentText){
            this.comment = comment;
            this.newComment = newComment;
            this.commentText = commentText;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_EDIT_COMMENT);
            data.put("newComment", newComment);
            data.put("commentText", commentText);
            try {
                data.put("success", comment.edit(newComment));
            } catch (IOException e) {
                data.put("success", false);
                e.printStackTrace();
            }
            message.obj = data;
            handler.sendMessage(message);
        }
    }

    private class DeleteCommentThread extends Thread{

        private final Comment comment;
        private final View comment_item;
        private final ViewGroup parent;
        private final TextView commentNum;

        public DeleteCommentThread(Comment comment, View comment_item, ViewGroup parent, TextView commentNum){
            this.comment = comment;
            this.comment_item = comment_item;
            this.parent = parent;
            this.commentNum = commentNum;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_DELETE_COMMENT);
            data.put("comment_item", comment_item);
            data.put("parent", parent);
            data.put("commentNum", commentNum);
            try{
                data.put("success", comment.delete());
            }catch (IOException e){
                e.printStackTrace();
                data.put("success", false);
            }
            message.obj = data;
            handler.sendMessage(message);
        }
    }

    private class CommentArticleThread extends Thread{

        private final Article article;
        private final String commentContent;
        private final ViewGroup parent;
        private final TextView commentNumText;

        public CommentArticleThread(Article article, String commentContent, ViewGroup parent,
                                    TextView commentNumText){
            this.article = article;
            this.commentContent = commentContent;
            this.parent = parent;
            this.commentNumText = commentNumText;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_COMMENT_ARTICLE);
            data.put("commentNumText", commentNumText);
            data.put("articleAuthorName", users.get(article.author_email).name);
            data.put("parent", parent);
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

        private ArticleList content;

        public TaskHandler(ArticleList content){
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
                    content.isTogglingLikeness.remove(article);
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
                    content.isTogglingLikeness.remove(article);
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
                        ((TextView) data.get("titleText")).setText((String) data.get("newTitle"));
                        ((TextView) data.get("contentText")).setText((String) data.get("newContent"));
                    }else
                        Toast.makeText(content.context, "Unable to edit the article!", Toast.LENGTH_LONG).show();
                    break;
                case TASK_DELETE_ARTICLE:
                    content.processingDialog.dismiss();
                    if((boolean)data.get("success")){
                        Toast.makeText(content.context, "Successfully delete the article!", Toast.LENGTH_LONG).show();
                        ((ViewGroup) data.get("parent")).removeView((View) data.get("article_item"));
                    }else
                        Toast.makeText(content.context, "Unable to delete the article!", Toast.LENGTH_LONG).show();
                    break;
                case TASK_EDIT_COMMENT:
                    content.processingDialog.dismiss();
                    if((boolean)data.get("success"))
                        ((TextView)data.get("commentText")).setText((String)data.get("newComment"));
                    else
                        Toast.makeText(content.context, "Unable to edit the comment!", Toast.LENGTH_LONG).show();
                    break;
                case TASK_DELETE_COMMENT:
                    content.processingDialog.dismiss();
                    if((boolean)data.get("success")){
                        Toast.makeText(content.context, "Successfully delete the comment!", Toast.LENGTH_LONG).show();
                        ViewGroup parent = (ViewGroup) data.get("parent");
                        parent.removeView((View) data.get("comment_item"));
                        TextView commentNum = (TextView)data.get("commentNum");
                        commentNum.setText(Integer.parseInt(commentNum.getText().toString())-1+"");
                    }else
                        Toast.makeText(content.context, "Unable to delete the comment!", Toast.LENGTH_LONG).show();
                    break;
                case TASK_COMMENT_ARTICLE:
                    content.processingDialog.dismiss();
                    if((boolean)data.get("success")){
                        ViewGroup parent = (ViewGroup) data.get("parent");
                        Comment comment = (Comment) data.get("comment");
                        TextView commentNumText = (TextView) data.get("commentNumText");
                        String articleAuthorName = (String) data.get("articleAuthorName");
                        View reply_item = content.generateCommentView(comment, parent, commentNumText, articleAuthorName);
                        if(parent.getChildCount() < ArticleList.MAX_COMMENT_NUM)
                            parent.addView(reply_item, 0);
                        else {
                            parent.removeViewAt(ArticleList.MAX_COMMENT_NUM-1);
                            parent.addView(reply_item, 0);
                        }
                        commentNumText.setText(Integer.parseInt(commentNumText.getText().toString())+1+"");
                        Bitmap photo = Credential.getCurrentUserPhoto();
                        if(photo != null)
                            ((ImageView)reply_item.findViewById(R.id.user_photo)).setImageBitmap(photo);
                    }else
                        Toast.makeText(content.context, "Unable to reply the comment!", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

    }

}
