package com.josh.profrate.viewContents;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
    private static final int TASK_EDIT_COMMENT = 3;
    private static final int TASK_DELETE_COMMENT = 4;
    private static final int TASK_EDIT_REPLY = 5;
    private static final int TASK_DELETE_REPLY = 6;
    private static final int TASK_REPLY = 8;

    private final List<Comment> comments;
    private final Map<String, User> users;
    private final Map<Long, List<CommentReply>> commentReplies;
    private final Handler handler = new TaskHandler(this);
    private final Set<Comment> isTogglingLikeness;
    private Dialog processingDialog;
    private boolean isActive;

    public CommentsList(Context context, ViewGroup parentLayout, List<Comment> comments,
                        Map<Long, List<CommentReply>> commentReplies, Map<String, User> users) {
        super(context, parentLayout);
        this.comments = comments;
        this.users = users;
        this.commentReplies = commentReplies;
        this.isTogglingLikeness = new HashSet<Comment>();
        this.isActive = false;
        users.put(Credential.getCurrentUser().email, Credential.getCurrentUser());
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
            ((TextView)commentView.findViewById(R.id.comment_num)).setText(commentReplies.get(comment.id).size() + "");
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
                                        new EditCommentThread(comment, dialog.getInputText()).start();
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
                                        new DeleteCommentThread(comment, commentView, parentLayout).start();
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
            final LinearLayout reply_list = (LinearLayout)commentView.findViewById(R.id.reply_list);
            commentView.findViewById(R.id.comment_click_area).setOnClickListener(new CommentClickListener(comment));
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
                                new ReplyThread(comment, dialog.getInputText(), reply_list,
                                        (TextView) commentView.findViewById(R.id.comment_num), comment.id).start();
                            }
                        }).show();
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
            List<CommentReply> replies = commentReplies.get(comment.id);
            int count = 0;
            for (final CommentReply reply : replies) {
                if (count == MAX_REPLY_NUM) break;
                View reply_item = generateReplyView(reply, reply_list,
                        (TextView)commentView.findViewById(R.id.comment_num), author.name, comment.id);
                reply_list.addView(reply_item);
                new LoadPhotoThread(users.get(reply.author_email).photo_url, (ImageView)reply_item.findViewById(R.id.user_photo)).start();
                count++;
            }
            parentLayout.addView(commentView);
        }
        isActive = true;
    }

    private View generateReplyView(final CommentReply reply, final ViewGroup parent, final TextView commentNumText, String commentAuthorName, final long commentId){
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View reply_item = inflater.inflate(R.layout.comment_reply_item, parent, false);
        User user = users.get(reply.author_email);
        ((TextView) reply_item.findViewById(R.id.user_name)).setText(user.name);
        ((TextView) reply_item.findViewById(R.id.replyToName)).setText(commentAuthorName);
        ((TextView) reply_item.findViewById(R.id.comment)).setText(reply.content);
        ((TextView) reply_item.findViewById(R.id.time)).setText(TimeConverter.convertTime(reply.time));
        if(reply.author_email.equals(Credential.getCredential().getSelectedAccountName())){
            reply_item.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CommentDialog dialog = new CommentDialog(context);
                    dialog.setHint("Edit reply").setConfirmButtonText("Edit").
                            setInputText(((TextView) reply_item.findViewById(R.id.comment)).getText().toString()).
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
                                    new EditReplyThread(reply, parent, dialog.getInputText(), commentId).start();
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
                                    new DeleteReplyThread(reply, parent, reply_item, commentNumText, commentId).start();
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

    private class EditCommentThread extends Thread{

        private final Comment comment;
        private final String newComment;

        public EditCommentThread(Comment comment, String newComment){
            this.comment = comment;
            this.newComment = newComment;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_EDIT_COMMENT);
            data.put("comment", comment);
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

        private final Comment comment;
        private final View comment_item;
        private final ViewGroup parent;

        public DeleteCommentThread(Comment comment, View comment_item, ViewGroup parent){
            this.comment = comment;
            this.comment_item = comment_item;
            this.parent = parent;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_DELETE_COMMENT);
            data.put("comment_item", comment_item);
            data.put("parent", parent);
            data.put("comment", comment);
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
        private final ViewGroup replyList;
        private final String newReply;
        private final long commentId;

        public EditReplyThread(CommentReply reply, ViewGroup replyList, String newReply, long commentId){
            this.reply = reply;
            this.replyList = replyList;
            this.newReply = newReply;
            this.commentId = commentId;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_EDIT_REPLY);
            data.put("replyList", replyList);
            data.put("reply", reply);
            data.put("commentId", commentId);
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
        private final TextView replyNum;
        private final long commentId;

        public DeleteReplyThread(CommentReply reply, ViewGroup parent, View replyItem, TextView replyNum, long commentId){
            this.parent = parent;
            this.replyItem = replyItem;
            this.reply = reply;
            this.replyNum = replyNum;
            this.commentId = commentId;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_DELETE_REPLY);
            data.put("parent", parent);
            data.put("replyItem", replyItem);
            data.put("replyNum", replyNum);
            data.put("reply", reply);
            data.put("commentId", commentId);
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
        private final ViewGroup parent;
        private final TextView commentNumText;
        private final long commentId;

        public ReplyThread(Comment comment, String replyContent, ViewGroup parent, TextView commentNumText, long commentId){
            this.comment = comment;
            this.replyContent = replyContent;
            this.parent = parent;
            this.commentNumText = commentNumText;
            this.commentId = commentId;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_REPLY);
            data.put("parent", parent);
            data.put("commentNumText", commentNumText);
            data.put("commentAuthorName", users.get(comment.author_email).name);
            data.put("commentId", commentId);
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
                case TASK_EDIT_COMMENT:
                    content.processingDialog.dismiss();
                    if((boolean)data.get("success")) {
                        int index = content.comments.indexOf((Comment)data.get("comment"));
                        content.comments.remove(index);
                        Comment newComment = (Comment)data.get("newComment");
                        content.comments.add(index, newComment);
                        View comment_item = content.parentLayout.getChildAt(index);
                        ((TextView) comment_item.findViewById(R.id.comment)).setText(newComment.content);
                        ((TextView) comment_item.findViewById(R.id.time)).setText(TimeConverter.convertTime(newComment.time));
                    }else
                        Toast.makeText(content.context, "Unable to edit the comment!", Toast.LENGTH_LONG).show();
                    break;
                case TASK_DELETE_COMMENT:
                    content.processingDialog.dismiss();
                    if((boolean)data.get("success")){
                        Toast.makeText(content.context, "Successfully delete the comment!", Toast.LENGTH_LONG).show();
                        ((ViewGroup) data.get("parent")).removeView((View) data.get("comment_item"));
                        content.comments.remove((Comment)data.get("comment"));
                    }else
                        Toast.makeText(content.context, "Unable to delete the comment!", Toast.LENGTH_LONG).show();
                    break;
                case TASK_EDIT_REPLY:
                    content.processingDialog.dismiss();
                    if((boolean)data.get("success")) {
                        long commentId = (long)data.get("commentId");
                        List<CommentReply> replies = content.commentReplies.get(commentId);
                        int index = replies.indexOf((CommentReply)data.get("reply"));
                        replies.remove(index);
                        CommentReply newReply = (CommentReply)data.get("newReply");
                        replies.add(index, newReply);
                        View reply_item = ((ViewGroup) data.get("replyList")).getChildAt(index);
                        ((TextView)reply_item.findViewById(R.id.comment)).setText(newReply.content);
                        ((TextView)reply_item.findViewById(R.id.time)).setText(TimeConverter.convertTime(newReply.time));
                    }else
                        Toast.makeText(content.context, "Unable to edit the reply!", Toast.LENGTH_LONG).show();
                    break;
                case TASK_DELETE_REPLY:
                    content.processingDialog.dismiss();
                    if((boolean)data.get("success")) {
                        Toast.makeText(content.context, "Successfully delete the reply!", Toast.LENGTH_LONG).show();
                        ViewGroup parent = (ViewGroup) data.get("parent");
                        parent.removeView((View) data.get("replyItem"));
                        TextView replyNum = (TextView)data.get("replyNum");
                        List<CommentReply> replies = content.commentReplies.get((long)data.get("commentId"));
                        replies.remove((CommentReply)data.get("reply"));
                        replyNum.setText(replies.size() + "");
                    }else
                        Toast.makeText(content.context, "Unable to delete the reply!", Toast.LENGTH_LONG).show();
                    break;
                case TASK_REPLY:
                    content.processingDialog.dismiss();
                    if((boolean)data.get("success")){
                        ViewGroup parent = (ViewGroup) data.get("parent");
                        CommentReply reply = (CommentReply) data.get("reply");
                        TextView commentNumText = (TextView) data.get("commentNumText");
                        String commenAuthorName = (String) data.get("commentAuthorName");
                        long commentId = (long)data.get("commentId");
                        View reply_item = content.generateReplyView(reply, parent, commentNumText, commenAuthorName, commentId);
                        if(parent.getChildCount() < CommentsList.MAX_REPLY_NUM)
                            parent.addView(reply_item, 0);
                        else {
                            parent.removeViewAt(CommentsList.MAX_REPLY_NUM-1);
                            parent.addView(reply_item, 0);
                        }
                        List<CommentReply> replies = content.commentReplies.get(commentId);
                        replies.add(0, reply);
                        commentNumText.setText(replies.size()+"");
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
