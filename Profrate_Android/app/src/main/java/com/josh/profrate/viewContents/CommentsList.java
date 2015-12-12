package com.josh.profrate.viewContents;

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

import com.josh.profrate.R;
import com.josh.profrate.SecondaryActivity;
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

public class CommentsList extends ViewContent {

    private final static int MAX_REPLY_NUM = 3;

    private static final int TASK_LOAD_PHOTO = 0;
    private static final int TASK_TOGGLE_LIKE = 1;
    private static final int TASK_TOGGLE_DISLIKE = 2;

    private final List<Comment> comments;
    private final Map<String, User> users;
    private final Map<Long, List<CommentReply>> commentReplies;
    private final Handler handler = new TaskHandler(this);
    private final Set<Comment> isTogglingLikeness;
    private boolean isActive;

    public CommentsList(Context context, ViewGroup parentLayout, List<Comment> comments,
                        Map<Long, List<CommentReply>> commentReplies, Map<String, User> users) {
        super(context, parentLayout);
        this.comments = comments;
        this.users = users;
        this.commentReplies = commentReplies;
        this.isTogglingLikeness = new HashSet<Comment>();
        this.isActive = false;
    }

    @Override
    public void show() {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(final Comment comment: comments){
            final View commentView = inflater.inflate(R.layout.comment_item, parentLayout, false);
            User author = users.get(comment.author_email);
            ((TextView)commentView.findViewById(R.id.user_name)).setText(author.name);
            ((TextView)commentView.findViewById(R.id.comment)).setText(comment.content);
            ((TextView)commentView.findViewById(R.id.time)).setText(TimeConverter.convertTime(comment.time));
            ((TextView)commentView.findViewById(R.id.like_num)).setText(comment.liked_by.size() + "");
            ((TextView)commentView.findViewById(R.id.dislike_num)).setText(comment.disliked_by.size() + "");
            ((TextView)commentView.findViewById(R.id.comment_num)).setText(comment.reply_num + "");
            if (comment.liked_by.contains(Credential.getCredential().getSelectedAccountName()))
                ((ImageView)commentView.findViewById(R.id.like_icon)).setImageResource(R.drawable.like_colored);
            else
                ((ImageView)commentView.findViewById(R.id.like_icon)).setImageResource(R.drawable.like_bw);
            if (comment.disliked_by.contains(Credential.getCredential().getSelectedAccountName()))
                ((ImageView)commentView.findViewById(R.id.dislike_icon)).setImageResource(R.drawable.dislike_colored);
            else
                ((ImageView)commentView.findViewById(R.id.dislike_icon)).setImageResource(R.drawable.dislike_bw);
            commentView.findViewById(R.id.comment_click_area).setOnClickListener(new CommentClickListener(comment));
            commentView.findViewById(R.id.comment_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            commentView.findViewById(R.id.like_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new LikeThread(comment, commentView.findViewById(R.id.comment_tool_bar)).start();
                }
            });
            commentView.findViewById(R.id.dislike_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DislikeThread(comment, commentView.findViewById(R.id.comment_tool_bar)).start();
                }
            });
            new LoadPhotoThread(author.photo_url, (ImageView)commentView.findViewById(R.id.user_photo)).start();
            LinearLayout reply_list = (LinearLayout)commentView.findViewById(R.id.reply_list);
            List<CommentReply> replies = commentReplies.get(comment.id);
            int count = 0;
            for (CommentReply reply : replies) {
                if (count == MAX_REPLY_NUM) break;
                User user = users.get(reply.author_email);
                View reply_item = inflater.inflate(R.layout.comment_reply_item, reply_list, false);
                ((TextView) reply_item.findViewById(R.id.user_name)).setText(user.name);
                ((TextView) reply_item.findViewById(R.id.replyToName)).setText(author.name);
                ((TextView) reply_item.findViewById(R.id.comment)).setText(reply.content);
                ((TextView) reply_item.findViewById(R.id.time)).setText(TimeConverter.convertTime(reply.time));
                reply_list.addView(reply_item);
                new LoadPhotoThread(user.photo_url, (ImageView)reply_item.findViewById(R.id.user_photo)).start();
                count++;
            }
            parentLayout.addView(commentView);
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

    private class CommentClickListener implements View.OnClickListener {

        private final Comment comment;

        public CommentClickListener(Comment comment){
            this.comment = comment;
        }

        @Override
        public void onClick(View v) {
            context.startActivity(new Intent(context, SecondaryActivity.class).
                    putExtra("view", SecondaryActivity.SINGLE_COMMENT).
                    putExtra("comment_id", comment.id));
        }
    };

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

        private final Comment comment;
        private final View commentToolBar;

        public LikeThread(Comment comment, View commentToolBar){
            this.comment = comment;
            this.commentToolBar = commentToolBar;
        }

        @Override
        public void run(){
            if(isTogglingLikeness.contains(comment)) return;
            isTogglingLikeness.add(comment);
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_TOGGLE_LIKE);
            data.put("comment", comment);
            data.put("commentToolBar", commentToolBar);
            try {
                boolean success = false;
                data.put("success", success = comment.toggle_like());
                if(success){
                    if(comment.liked_by.contains(Credential.getCredential().getSelectedAccountName()))
                        comment.liked_by.remove(Credential.getCredential().getSelectedAccountName());
                    else
                        comment.liked_by.add(Credential.getCredential().getSelectedAccountName());
                    comment.disliked_by.remove(Credential.getCredential().getSelectedAccountName());
                }

            } catch (IOException e) {
                data.put("success", false);
            }
            message.obj = data;
            handler.sendMessage(message);
        }

    }

    private class DislikeThread extends Thread{

        private final Comment comment;
        private final View commentToolBar;

        public DislikeThread(Comment comment, View commentToolBar){
            this.comment = comment;
            this.commentToolBar = commentToolBar;
        }

        @Override
        public void run(){
            if(isTogglingLikeness.contains(comment)) return;
            isTogglingLikeness.add(comment);
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_TOGGLE_DISLIKE);
            data.put("comment", comment);
            data.put("commentToolBar", commentToolBar);
            try {
                boolean success = false;
                data.put("success", success = comment.toggle_dislike());
                if(success){
                    if(comment.disliked_by.contains(Credential.getCredential().getSelectedAccountName()))
                        comment.disliked_by.remove(Credential.getCredential().getSelectedAccountName());
                    else
                        comment.disliked_by.add(Credential.getCredential().getSelectedAccountName());
                    comment.liked_by.remove(Credential.getCredential().getSelectedAccountName());
                }
            } catch (IOException e) {
                data.put("success", false);
            }
            message.obj = data;
            handler.sendMessage(message);
        }


    }

    private static class TaskHandler extends Handler {

        private CommentsList content;

        public TaskHandler(CommentsList content){
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
                    Comment comment = (Comment) data.get("comment");
                    View commentToolBar = (View) data.get("commentToolBar");
                    content.isTogglingLikeness.remove(comment);
                    if((boolean)data.get("success")) {
                        if (comment.liked_by.contains(Credential.getCredential().getSelectedAccountName())) {
                            Toast.makeText(content.context, "Successfully like the comment!", Toast.LENGTH_LONG).show();
                            ((ImageView)commentToolBar.findViewById(R.id.like_icon)).setImageResource(R.drawable.like_colored);
                        } else {
                            Toast.makeText(content.context, "Successfully cancel liking the comment!", Toast.LENGTH_LONG).show();
                            ((ImageView)commentToolBar.findViewById(R.id.like_icon)).setImageResource(R.drawable.like_bw);
                        }
                        ((ImageView)commentToolBar.findViewById(R.id.dislike_icon)).setImageResource(R.drawable.dislike_bw);
                        ((TextView)commentToolBar.findViewById(R.id.like_num)).setText(comment.liked_by.size() + "");
                        ((TextView)commentToolBar.findViewById(R.id.dislike_num)).setText(comment.disliked_by.size() + "");
                    }else
                        Toast.makeText(content.context, "Unable to toggle liking the comment!", Toast.LENGTH_LONG).show();
                    break;
                case TASK_TOGGLE_DISLIKE:
                    comment = (Comment) data.get("comment");
                    commentToolBar = (View) data.get("commentToolBar");
                    content.isTogglingLikeness.remove(comment);
                    if((boolean)data.get("success")) {
                        if (comment.disliked_by.contains(Credential.getCredential().getSelectedAccountName())) {
                            Toast.makeText(content.context, "Successfully dislike the comment!", Toast.LENGTH_LONG).show();
                            ((ImageView)commentToolBar.findViewById(R.id.dislike_icon)).setImageResource(R.drawable.dislike_colored);
                        } else {
                            Toast.makeText(content.context, "Successfully cancel disliking the comment!", Toast.LENGTH_LONG).show();
                            ((ImageView)commentToolBar.findViewById(R.id.dislike_icon)).setImageResource(R.drawable.dislike_bw);
                        }
                        ((ImageView)commentToolBar.findViewById(R.id.like_icon)).setImageResource(R.drawable.like_bw);
                        ((TextView)commentToolBar.findViewById(R.id.like_num)).setText(comment.liked_by.size() + "");
                        ((TextView)commentToolBar.findViewById(R.id.dislike_num)).setText(comment.disliked_by.size() + "");
                    }else
                        Toast.makeText(content.context, "Unable to toggle disliking the comment!", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

    }

}
