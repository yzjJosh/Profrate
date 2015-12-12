package com.josh.profrate.viewContents;

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

import com.josh.profrate.R;
import com.josh.profrate.dataStructures.Article;
import com.josh.profrate.dataStructures.Comment;
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

    private final List<Article> articles;
    private final Map<String, User> users;
    private final Map<Long, List<Comment>> articleComments;
    private final Handler handler = new TaskHandler(this);
    private final Set<Article> isTogglingLikeness;
    private boolean isActive;

    public ArticleList(Context context, ViewGroup parentLayout, List<Article> articles,
                        Map<Long, List<Comment>> articleComments, Map<String, User> users) {
        super(context, parentLayout);
        this.articles = articles;
        this.users = users;
        this.articleComments = articleComments;
        this.isTogglingLikeness = new HashSet<Article>();
        this.isActive = false;
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
            articleView.findViewById(R.id.comment_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
            LinearLayout reply_list = (LinearLayout)articleView.findViewById(R.id.reply_list);
            List<Comment> comments = articleComments.get(article.id);
            int count = 0;
            for (Comment comment : comments) {
                if (count == MAX_COMMENT_NUM) break;
                User user = users.get(comment.author_email);
                View reply_item = inflater.inflate(R.layout.comment_reply_item, reply_list, false);
                ((TextView) reply_item.findViewById(R.id.user_name)).setText(user.name);
                ((TextView) reply_item.findViewById(R.id.replyToName)).setText(author.name);
                ((TextView) reply_item.findViewById(R.id.comment)).setText(comment.content);
                ((TextView) reply_item.findViewById(R.id.time)).setText(TimeConverter.convertTime(comment.time));
                reply_list.addView(reply_item);
                new LoadPhotoThread(user.photo_url, (ImageView)reply_item.findViewById(R.id.user_photo)).start();
                count++;
            }
            parentLayout.addView(articleView);
        }
        isActive = true;
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
                default:
                    break;
            }
        }

    }

}
