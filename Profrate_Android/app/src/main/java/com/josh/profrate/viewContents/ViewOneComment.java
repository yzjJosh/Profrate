package com.josh.profrate.viewContents;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.josh.profrate.CommentDialog;
import com.josh.profrate.R;
import com.josh.profrate.dataStructures.Comment;
import com.josh.profrate.dataStructures.CommentReply;
import com.josh.profrate.dataStructures.User;
import com.josh.profrate.elements.BitmapFetcher;
import com.josh.profrate.elements.Credential;
import com.josh.profrate.elements.TimeConverter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewOneComment extends ViewContent {

    private static final int TASK_LOAD_PHOTO = 0;
    private static final int TASK_TOGGLE_LIKE = 1;
    private static final int TASK_TOGGLE_DISLIKE = 2;
    private static final int TASK_EDIT_COMMENT = 3;
    private static final int TASK_DELETE_COMMENT = 4;
    private static final int TASK_EDIT_REPLY = 5;
    private static final int TASK_DELETE_REPLY = 6;
    private static final int TASK_REPLY = 7;

    private Comment comment;
    private List<CommentReply> replies;
    private Map<String, User> users;
    private Handler handler = new TaskHandler(this);
    private Dialog processingDialog;
    private boolean isTogglingLikeness;
    private boolean isActive;

    public ViewOneComment(Context context, ViewGroup parentLayout, Comment comment, List<CommentReply> replies,
                          Map<String, User> users) {
        super(context, parentLayout);
        this.comment = comment;
        this.replies = replies;
        this.users = users;
        this.isTogglingLikeness = false;
        this.isActive = false;
        users.put(Credential.getCurrentUser().email, Credential.getCurrentUser());
    }

    @Override
    public void show() {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ScrollView scrollView = new ScrollView(context);
        parentLayout.addView(scrollView);
        ViewGroup.LayoutParams params = scrollView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        scrollView.setLayoutParams(params);
        final View commentView = inflater.inflate(R.layout.comment_item, scrollView, false);
        scrollView.addView(commentView);
        User author = users.get(comment.author_email);
        ((TextView)commentView.findViewById(R.id.user_name)).setText(author.name);
        TextView commentContent = (TextView) commentView.findViewById(R.id.comment);
        commentContent.setText(comment.content);
        commentContent.setMaxLines(Integer.MAX_VALUE);
        commentContent.setEllipsize(null);
        commentContent.setText(comment.content);
        ((TextView)commentView.findViewById(R.id.time)).setText(TimeConverter.convertTime(comment.time));
        ((TextView)commentView.findViewById(R.id.like_num)).setText(comment.liked_by.size() + "");
        ((TextView)commentView.findViewById(R.id.dislike_num)).setText(comment.disliked_by.size() + "");
        ((TextView)commentView.findViewById(R.id.comment_num)).setText(replies.size() + "");
        if (comment.liked_by.contains(Credential.getCredential().getSelectedAccountName()))
            ((ImageView)commentView.findViewById(R.id.like_icon)).setImageResource(R.drawable.like_colored);
        else
            ((ImageView)commentView.findViewById(R.id.like_icon)).setImageResource(R.drawable.like_bw);
        if (comment.disliked_by.contains(Credential.getCredential().getSelectedAccountName()))
            ((ImageView)commentView.findViewById(R.id.dislike_icon)).setImageResource(R.drawable.dislike_colored);
        else
            ((ImageView)commentView.findViewById(R.id.dislike_icon)).setImageResource(R.drawable.dislike_bw);
        if(author.email.equals(Credential.getCredential().getSelectedAccountName())){
            commentView.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CommentDialog dialog = new CommentDialog(context);
                    dialog.setHint("Edit comment").setConfirmButtonText("Edit").
                            setInputText(((TextView)commentView.findViewById(R.id.comment)).getText().toString()).
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
                                    new EditCommentThread(dialog.getInputText()).start();
                                }
                            }).show();
                }
            });
            commentView.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Warning");
                    alertDialog.setIcon(R.drawable.attention);
                    alertDialog.setMessage("Are you sure you want to delete this comment? All the data are unrecoverable.");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    processingDialog = new Dialog(context, R.style.theme_dialog);
                                    processingDialog.setContentView(R.layout.processing_dialog);
                                    processingDialog.setCancelable(false);
                                    processingDialog.show();
                                    new DeleteCommentThread().start();
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
            });
        }else
            commentView.findViewById(R.id.edit_btn_area).setVisibility(View.GONE);
        commentView.findViewById(R.id.comment_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CommentDialog dialog = new CommentDialog(context);
                dialog.setHint("Reply "+users.get(comment.author_email).name).setConfirmButtonText("Reply").
                        setOnConfirmButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(dialog.getInputText().length() == 0){
                                    Toast.makeText(context, "Please enter your reply!", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                processingDialog = new Dialog(context, R.style.theme_dialog);
                                processingDialog.setContentView(R.layout.processing_dialog);
                                processingDialog.setCancelable(false);
                                processingDialog.show();
                                new ReplyThread(comment, dialog.getInputText()).start();
                            }
                        }).show();
            }
        });
        commentView.findViewById(R.id.like_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LikeThread().start();
            }
        });
        commentView.findViewById(R.id.dislike_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DislikeThread(comment, commentView.findViewById(R.id.comment_tool_bar)).start();
            }
        });
        new LoadPhotoThread(author.photo_url, (ImageView)commentView.findViewById(R.id.user_photo)).start();
        final LinearLayout reply_list = (LinearLayout) commentView.findViewById(R.id.reply_list);
        for(final CommentReply reply: replies){
            View reply_item = generateReplyView(reply);
            reply_list.addView(reply_item);
            new LoadPhotoThread(users.get(reply.author_email).photo_url,
                    (ImageView)reply_item.findViewById(R.id.user_photo)).start();
        }
        this.isActive = true;
    }

    private View generateReplyView(final CommentReply reply){
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewGroup reply_list = (ViewGroup)parentLayout.findViewById(R.id.reply_list);
        final View reply_item = inflater.inflate(R.layout.comment_reply_item, reply_list, false);
        User user = users.get(reply.author_email);
        ((TextView) reply_item.findViewById(R.id.user_name)).setText(user.name);
        ((TextView) reply_item.findViewById(R.id.replyToName)).setText(users.get(comment.author_email).name);
        TextView commentContent = (TextView) reply_item.findViewById(R.id.comment);
        commentContent.setText(reply.content);
        commentContent.setMaxLines(Integer.MAX_VALUE);
        commentContent.setEllipsize(null);
        ((TextView) reply_item.findViewById(R.id.time)).setText(TimeConverter.convertTime(reply.time));
        if(reply.author_email.equals(Credential.getCredential().getSelectedAccountName())){
            reply_item.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CommentDialog dialog = new CommentDialog(context);
                    dialog.setHint("Edit reply").setConfirmButtonText("Edit").
                            setInputText(((TextView)reply_item.findViewById(R.id.comment)).getText().toString()).
                            setOnConfirmButtonClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(dialog.getInputText().length() == 0){
                                        Toast.makeText(context, "Please enter your reply!", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    processingDialog = new Dialog(context, R.style.theme_dialog);
                                    processingDialog.setContentView(R.layout.processing_dialog);
                                    processingDialog.setCancelable(false);
                                    processingDialog.show();
                                    new EditReplyThread(reply, dialog.getInputText()).start();
                                }
                            }).show();
                }
            });
            reply_item.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Warning");
                    alertDialog.setIcon(R.drawable.attention);
                    alertDialog.setMessage("Are you sure you want to delete this reply? All the data are unrecoverable.");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    processingDialog = new Dialog(context, R.style.theme_dialog);
                                    processingDialog.setContentView(R.layout.processing_dialog);
                                    processingDialog.setCancelable(false);
                                    processingDialog.show();
                                    new DeleteReplyThread(reply, reply_list, reply_item).start();
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
            });
        }else
            reply_item.findViewById(R.id.edit_btn_area).setVisibility(View.GONE);
        return reply_item;
    }

    @Override
    public void clear() {
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
            isTogglingLikeness = true;
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_TOGGLE_LIKE);
            data.put("comment", comment);
            data.put("commentToolBar", parentLayout.findViewById(R.id.comment_tool_bar));
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
            if(isTogglingLikeness) return;
            isTogglingLikeness = true;
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

    private class EditCommentThread extends Thread{

        private final String newComment;

        public EditCommentThread(String newComment){
            this.newComment = newComment;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_EDIT_COMMENT);
            try {
                if(comment.edit(newComment)) {
                    data.put("newComment", Comment.getComment(comment.id));
                    data.put("success", true);
                }else
                    data.put("success", false);
            } catch (IOException e) {
                data.put("success", false);
                e.printStackTrace();
            }
            message.obj = data;
            handler.sendMessage(message);
        }
    }

    private class DeleteCommentThread extends Thread{

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_DELETE_COMMENT);
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

    private class EditReplyThread extends Thread{

        private final CommentReply reply;
        private final String newReply;

        public EditReplyThread(CommentReply reply, String newReply){
            this.reply = reply;
            this.newReply = newReply;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_EDIT_REPLY);
            data.put("reply", reply);
            try {
                if(reply.edit(newReply)) {
                    data.put("newReply", CommentReply.getCommentReply(reply.id));
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

    private class DeleteReplyThread extends Thread{

        private final CommentReply reply;
        private final ViewGroup parent;
        private final View replyItem;

        public DeleteReplyThread(CommentReply reply, ViewGroup parent, View replyItem){
            this.parent = parent;
            this.replyItem = replyItem;
            this.reply = reply;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_DELETE_REPLY);
            data.put("parent", parent);
            data.put("replyItem", replyItem);
            data.put("reply", reply);
            try {
                data.put("success", reply.delete());
            } catch (IOException e) {
                e.printStackTrace();
                data.put("success", false);
            }
            message.obj = data;
            handler.sendMessage(message);
        }

    }

    private class ReplyThread extends Thread{

        private final Comment comment;
        private final String replyContent;

        public ReplyThread(Comment comment, String replyContent){
            this.comment = comment;
            this.replyContent = replyContent;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_REPLY);
            try {
                long reply_id = comment.reply(replyContent);
                if(reply_id != -1) {
                    data.put("reply", CommentReply.getCommentReply(reply_id));
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

        private ViewOneComment content;

        public TaskHandler(ViewOneComment content){
            this.content = content;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message message){
            if(!content.isActive()) return;
            HashMap<String, Object> data = (HashMap<String, Object>) message.obj;
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
                    content.isTogglingLikeness = false;
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
                    content.isTogglingLikeness = false;
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
                case TASK_EDIT_COMMENT:
                    content.processingDialog.dismiss();
                    if((boolean)data.get("success")) {
                        Comment newComment = (Comment)data.get("newComment");
                        ((TextView) content.parentLayout.findViewById(R.id.comment)).setText(newComment.content);
                        ((TextView) content.parentLayout.findViewById(R.id.time)).setText(TimeConverter.convertTime(newComment.time));
                    }else
                        Toast.makeText(content.context, "Unable to edit the comment!", Toast.LENGTH_LONG).show();
                    break;
                case TASK_DELETE_COMMENT:
                    content.processingDialog.dismiss();
                    if((boolean)data.get("success")) {
                        Toast.makeText(content.context, "Successfully delete the comment!", Toast.LENGTH_LONG).show();
                        ((Activity)content.context).finish();
                    }else
                        Toast.makeText(content.context, "Unable to delete the comment!", Toast.LENGTH_LONG).show();
                    break;
                case TASK_EDIT_REPLY:
                    content.processingDialog.dismiss();
                    if((boolean)data.get("success")) {
                        CommentReply reply = (CommentReply)data.get("reply");
                        int index = content.replies.indexOf(reply);
                        content.replies.remove(index);
                        CommentReply newReply = (CommentReply)data.get("newReply");
                        content.replies.add(index, newReply);
                        View reply_item = ((ViewGroup)content.parentLayout.findViewById(R.id.reply_list)).getChildAt(index);
                        ((TextView) reply_item.findViewById(R.id.comment)).setText(newReply.content);
                        ((TextView) reply_item.findViewById(R.id.time)).setText(TimeConverter.convertTime(newReply.time));
                    }else
                        Toast.makeText(content.context, "Unable to edit the reply!", Toast.LENGTH_LONG).show();
                    break;
                case TASK_DELETE_REPLY:
                    content.processingDialog.dismiss();
                    if((boolean)data.get("success")) {
                        Toast.makeText(content.context, "Successfully delete the reply!", Toast.LENGTH_LONG).show();
                        ((ViewGroup) data.get("parent")).removeView((View) data.get("replyItem"));
                        content.replies.remove((CommentReply)data.get("reply"));
                        TextView replyNum = (TextView)content.parentLayout.findViewById(R.id.comment_num);
                        replyNum.setText(content.replies.size()+"");
                    }else
                        Toast.makeText(content.context, "Unable to delete the reply!", Toast.LENGTH_LONG).show();
                    break;
                case TASK_REPLY:
                    content.processingDialog.dismiss();
                    if((boolean)data.get("success")){
                        CommentReply reply = (CommentReply) data.get("reply");
                        View reply_item = content.generateReplyView(reply);
                        TextView commentNumText = (TextView) content.parentLayout.findViewById(R.id.comment_num);
                        content.replies.add(0, reply);
                        commentNumText.setText(content.replies.size()+"");
                        ((ViewGroup)content.parentLayout.findViewById(R.id.reply_list)).addView(reply_item, 0);
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
