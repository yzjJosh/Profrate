package com.josh.profrate.viewContents;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.josh.profrate.R;
import com.josh.profrate.dataStructures.Article;
import com.josh.profrate.dataStructures.Comment;
import com.josh.profrate.dataStructures.CommentReply;
import com.josh.profrate.dataStructures.Professor;
import com.josh.profrate.dataStructures.User;
import com.josh.profrate.elements.BitmapFetcher;
import com.josh.profrate.elements.Credential;
import com.josh.profrate.elements.TimeConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsAndArticlesView extends ViewContent {

    private static final int TASK_LOAD_PHOTO = 0;
    private static final int TASK_TOGGLE_LIKE = 1;
    private static final int TASK_TOGGLE_DISLIKE = 2;

    private final Professor professor;
    private final List<Object> items;
    private final Map<Long, List<CommentReply>> commentReplies;
    private final Map<Long, List<Comment>> articleComments;
    private final Map<String, User> users;
    private final boolean[] isTogglingLikeness;
    private final TaskHandler handler;
    private boolean isActive;
    private static final Comparator<Object> comparator = new Comparator<Object>() {

        private int compare(long a, long b){
            if(a == b) return 0;
            else if(a < b) return -1;
            else return 1;
        }

        @Override
        public int compare(Object lhs, Object rhs) {
            if(lhs == rhs) return 0;
            if(lhs instanceof Article && rhs instanceof Article)
                return compare(((Article)lhs).time, ((Article)rhs).time);
            if(lhs instanceof Comment && rhs instanceof Comment)
                return compare(((Comment)lhs).time, ((Comment)rhs).time);
            if(lhs instanceof Comment && rhs instanceof Article)
                return compare(((Comment)lhs).time, ((Article)rhs).time);
            if(lhs instanceof Article && rhs instanceof Comment)
                return compare(((Article)lhs).time, ((Comment)rhs).time);
            return 0;
        }
    };

    public CommentsAndArticlesView(Context context, ViewGroup parentLayout, Professor professor,
                                   List<Comment> comments, List<Article> articles, Map<Long, List<CommentReply>> commentReplies,
                                   Map<Long, List<Comment>> articleComments, Map<String, User> users) {
        super(context, parentLayout);
        this.professor = professor;
        this.items = new ArrayList<Object>();
        this.handler = new TaskHandler(this);
        this.commentReplies = commentReplies;
        this.articleComments = articleComments;
        this.users = users;
        items.addAll(comments);
        items.addAll(articles);
        Collections.sort(items, comparator);
        this.isTogglingLikeness = new boolean[items.size()];
        this.isActive = false;
    }

    @Override
    public void show() {
        ListView listView = new ListView(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        listView.setLayoutParams(params);
        listView.setAdapter(new CommentListAdapter());
        listView.setDividerHeight(30);
        listView.setVerticalScrollBarEnabled(false);
        listView.setSelector(android.R.color.transparent);
        parentLayout.addView(listView);
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

    private class CommentBtnListener implements View.OnClickListener{

        private final Object target;

        public CommentBtnListener(Object target){
            this.target = target;
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class LikeBtnListener implements View.OnClickListener{

        private final Object target;
        private final ViewHolder holder;

        public LikeBtnListener(ViewHolder holder, Object target){
            this.target = target;
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            new LikeThread(holder, target).start();
        }
    }

    private class DislikeBtnListener implements View.OnClickListener{

        private final Object target;
        private final ViewHolder holder;

        public DislikeBtnListener(ViewHolder holder, Object target){
            this.target = target;
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            new DislikeThread(holder, target).start();
        }
    }


    private class ViewHolder{
        public final static int MAX_REPLY_NUM = 3;

        public ImageView user_photo;
        public View user_photo_progress_bar;
        public View user_photo_area;
        public TextView user_name;
        public TextView title;
        public TextView content;
        public TextView time;
        public View comment_btn;
        public View like_btn;
        public View dislike_btn;
        public ImageView like_icon;
        public ImageView dislike_icon;
        public TextView like_num;
        public TextView dislike_num;
        public TextView comment_num;
        public TextView ellipsis;
        public LinearLayout reply_list;
        public List<View> reply_items;
        public int position;
    }

    private class CommentListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getItemViewType(int position){
            Object item = items.get(position);
            if(item instanceof Comment) return 0;
            else return 1;
        }

        @Override
        public long getItemId(int position) {
            if(getItemViewType(position) == 0)
                return ((Comment)getItem(position)).id;
            else
                return ((Article)getItem(position)).id;
        }

        @Override
        public int getViewTypeCount(){
            return 2;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int type = getItemViewType(position);
            if (convertView == null) {
                holder = new ViewHolder();
                if(type == 0) {
                    convertView = inflater.inflate(R.layout.comment_item, parent, false);
                    holder.content = (TextView) convertView.findViewById(R.id.comment);
                }else{
                    convertView = inflater.inflate(R.layout.article_item, parent, false);
                    holder.content = (TextView) convertView.findViewById(R.id.content);
                    holder.title = (TextView) convertView.findViewById(R.id.title);
                }
                holder.user_name = (TextView) convertView.findViewById(R.id.user_name);
                holder.user_photo = (ImageView) convertView.findViewById(R.id.user_photo);
                holder.user_photo_progress_bar = convertView.findViewById(R.id.user_photo_progress_bar);
                holder.user_photo_area = convertView.findViewById(R.id.user_photo_area);
                holder.comment_btn = convertView.findViewById(R.id.comment_btn);
                holder.like_btn = convertView.findViewById(R.id.like_btn);
                holder.dislike_btn = convertView.findViewById(R.id.dislike_btn);
                holder.like_icon = (ImageView) convertView.findViewById(R.id.like_icon);
                holder.dislike_icon = (ImageView) convertView.findViewById(R.id.dislike_icon);
                holder.like_num = (TextView) convertView.findViewById(R.id.like_num);
                holder.dislike_num = (TextView) convertView.findViewById(R.id.dislike_num);
                holder.comment_num = (TextView) convertView.findViewById(R.id.comment_num);
                holder.ellipsis = (TextView) convertView.findViewById(R.id.ellipsis);
                holder.reply_list = (LinearLayout) convertView.findViewById(R.id.reply_list);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.reply_items = new ArrayList<View>();
                for(int i=0; i<ViewHolder.MAX_REPLY_NUM; i++)
                    holder.reply_items.add(inflater.inflate(R.layout.comment_reply_item, holder.reply_list, false));
                convertView.setTag(holder);
            }else
                holder = (ViewHolder)convertView.getTag();
            holder.position = position;
            if(type == 0){
                Comment comment = (Comment) getItem(position);
                User author = users.get(comment.author_email);
                holder.user_name.setText(author.name);
                holder.content.setText(comment.content);
                holder.time.setText(TimeConverter.convertTime(comment.time));
                holder.like_num.setText(comment.liked_by.size() + "");
                holder.dislike_num.setText(comment.disliked_by.size() + "");
                holder.comment_num.setText(comment.reply_num+"");
                if(comment.liked_by.contains(Credential.getCredential().getSelectedAccountName()))
                    holder.like_icon.setImageResource(R.drawable.like_colored);
                else
                    holder.like_icon.setImageResource(R.drawable.like_bw);
                if(comment.disliked_by.contains(Credential.getCredential().getSelectedAccountName()))
                    holder.dislike_icon.setImageResource(R.drawable.dislike_colored);
                else
                    holder.dislike_icon.setImageResource(R.drawable.dislike_bw);
                holder.user_photo.setImageBitmap(null);
                holder.user_photo.setVisibility(View.GONE);
                holder.user_photo_progress_bar.setVisibility(View.VISIBLE);
                new LoadPhotoThread(author.photo_url, holder.user_photo_area, holder).start();
                holder.reply_list.removeAllViews();
                List<CommentReply> replies = commentReplies.get(comment.id);
                int count = 0;
                for(CommentReply reply: replies){
                    if(count == ViewHolder.MAX_REPLY_NUM) break;
                    User user = users.get(reply.author_email);
                    View reply_item = holder.reply_items.get(count);
                    ((TextView)reply_item.findViewById(R.id.user_name)).setText(user.name);
                    ((TextView)reply_item.findViewById(R.id.replyToName)).setText(author.name);
                    ((TextView)reply_item.findViewById(R.id.comment)).setText(reply.content);
                    ((TextView)reply_item.findViewById(R.id.time)).setText(TimeConverter.convertTime(reply.time));
                    View photoArea = reply_item.findViewById(R.id.user_photo_area);
                    ((ImageView)photoArea.findViewById(R.id.user_photo)).setImageBitmap(null);
                    photoArea.findViewById(R.id.user_photo).setVisibility(View.GONE);
                    photoArea.findViewById(R.id.user_photo_progress_bar).setVisibility(View.VISIBLE);
                    new LoadPhotoThread(user.photo_url, photoArea, holder).start();
                    holder.reply_list.addView(reply_item);
                    count ++;
                }
            }else{
                Article article = (Article) getItem(position);
                User author = users.get(article.author_email);
                holder.user_name.setText(author.name);
                holder.title.setText(article.title);
                holder.content.setText(article.content);
                holder.time.setText(TimeConverter.convertTime(article.time));
                holder.like_num.setText(article.liked_by.size() + "");
                holder.dislike_num.setText(article.disliked_by.size() + "");
                holder.comment_num.setText(article.comment_num+"");
                if(article.liked_by.contains(Credential.getCredential().getSelectedAccountName()))
                    holder.like_icon.setImageResource(R.drawable.like_colored);
                else
                    holder.like_icon.setImageResource(R.drawable.like_bw);
                if(article.disliked_by.contains(Credential.getCredential().getSelectedAccountName()))
                    holder.dislike_icon.setImageResource(R.drawable.dislike_colored);
                else
                    holder.dislike_icon.setImageResource(R.drawable.dislike_bw);
                holder.user_photo.setImageBitmap(null);
                holder.user_photo.setVisibility(View.GONE);
                holder.user_photo_progress_bar.setVisibility(View.VISIBLE);
                new LoadPhotoThread(author.photo_url, holder.user_photo_area, holder).start();
                holder.reply_list.removeAllViews();
                List<Comment> comments = articleComments.get(article.id);
                int count = 0;
                for(Comment comment: comments){
                    if(count == ViewHolder.MAX_REPLY_NUM) break;
                    User user = users.get(comment.author_email);
                    View reply_item = holder.reply_items.get(count);
                    ((TextView)reply_item.findViewById(R.id.user_name)).setText(user.name);
                    ((TextView)reply_item.findViewById(R.id.replyToName)).setText(author.name);
                    ((TextView)reply_item.findViewById(R.id.comment)).setText(comment.content);
                    ((TextView)reply_item.findViewById(R.id.time)).setText(TimeConverter.convertTime(comment.time));
                    View photoArea = reply_item.findViewById(R.id.user_photo_area);
                    ((ImageView)photoArea.findViewById(R.id.user_photo)).setImageBitmap(null);
                    photoArea.findViewById(R.id.user_photo).setVisibility(View.GONE);
                    photoArea.findViewById(R.id.user_photo_progress_bar).setVisibility(View.VISIBLE);
                    new LoadPhotoThread(user.photo_url, photoArea, holder).start();
                    holder.reply_list.addView(reply_item);
                    count ++;
                }
            }
            holder.comment_btn.setOnClickListener(new CommentBtnListener(getItem(position)));
            holder.like_btn.setOnClickListener(new LikeBtnListener(holder, getItem(position)));
            holder.dislike_btn.setOnClickListener(new DislikeBtnListener(holder, getItem(position)));
            final ViewHolder finalHolder = holder;
            holder.ellipsis.setVisibility(View.GONE);
            holder.content.post(new Runnable() {
                @Override
                public void run() {
                    int lines = finalHolder.content.getLineCount();
                    if (lines > 0 && finalHolder.position == position)
                        if (finalHolder.content.getLayout().getEllipsisCount(lines - 1) > 0)
                            finalHolder.ellipsis.setVisibility(View.VISIBLE);
                }
            });
            return convertView;
        }
    }

    private class LoadPhotoThread extends Thread{

        private final String url;
        private final View photoArea;
        private final ViewHolder holder;
        private final int position;

        public LoadPhotoThread(String url, View photoArea, ViewHolder holder){
            this.url = url;
            this.photoArea = photoArea;
            this.holder = holder;
            this.position = holder.position;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_LOAD_PHOTO);
            data.put("bitmap", BitmapFetcher.fetchBitmap(url));
            data.put("holder", holder);
            data.put("position", position);
            data.put("photoArea", photoArea);
            message.obj = data;
            handler.sendMessage(message);
        }

    }

    private class LikeThread extends Thread{

        private final ViewHolder holder;
        private final Object target;
        private final int position;

        public LikeThread(ViewHolder holder, Object target){
            this.holder = holder;
            this.target = target;
            this.position = holder.position;
        }

        @Override
        public void run(){
            if(isTogglingLikeness[position]) return;
            isTogglingLikeness[position] = true;
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_TOGGLE_LIKE);
            data.put("holder", holder);
            data.put("position", position);
            data.put("target", target);
            try {
                boolean success = false;
                if(target instanceof Comment) {
                    Comment comment = (Comment) target;
                    data.put("success", success = comment.toggle_like());
                    if(success){
                        if(comment.liked_by.contains(Credential.getCredential().getSelectedAccountName()))
                            comment.liked_by.remove(Credential.getCredential().getSelectedAccountName());
                        else
                            comment.liked_by.add(Credential.getCredential().getSelectedAccountName());
                        comment.disliked_by.remove(Credential.getCredential().getSelectedAccountName());
                    }
                }else {
                    Article article = (Article) target;
                    data.put("success", success = article.toggle_like());
                    if(success){
                        if(article.liked_by.contains(Credential.getCredential().getSelectedAccountName()))
                            article.liked_by.remove(Credential.getCredential().getSelectedAccountName());
                        else
                            article.liked_by.add(Credential.getCredential().getSelectedAccountName());
                        article.disliked_by.remove(Credential.getCredential().getSelectedAccountName());
                    }
                }

            } catch (IOException e) {
                data.put("success", false);
            }
            message.obj = data;
            handler.sendMessage(message);
        }

    }

    private class DislikeThread extends Thread{

        private final ViewHolder holder;
        private final Object target;
        private final int position;

        public DislikeThread(ViewHolder holder, Object target){
            this.holder = holder;
            this.target = target;
            this.position = holder.position;
        }

        @Override
        public void run(){
            if(isTogglingLikeness[position]) return;
            isTogglingLikeness[position] = true;
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_TOGGLE_DISLIKE);
            data.put("holder", holder);
            data.put("position", position);
            data.put("target", target);
            try {
                boolean success = false;
                if(target instanceof Comment) {
                    Comment comment = (Comment) target;
                    data.put("success", success = comment.toggle_dislike());
                    if(success){
                        if(comment.disliked_by.contains(Credential.getCredential().getSelectedAccountName()))
                            comment.disliked_by.remove(Credential.getCredential().getSelectedAccountName());
                        else
                            comment.disliked_by.add(Credential.getCredential().getSelectedAccountName());
                        comment.liked_by.remove(Credential.getCredential().getSelectedAccountName());
                    }
                }else {
                    Article article = (Article) target;
                    data.put("success", success = article.toggle_dislike());
                    if(success){
                        if(article.disliked_by.contains(Credential.getCredential().getSelectedAccountName()))
                            article.disliked_by.remove(Credential.getCredential().getSelectedAccountName());
                        else
                            article.disliked_by.add(Credential.getCredential().getSelectedAccountName());
                        article.liked_by.remove(Credential.getCredential().getSelectedAccountName());
                    }
                }

            } catch (IOException e) {
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
        public void handleMessage(Message msg){
            if(!content.isActive) return;
            HashMap<String, Object> data = (HashMap<String, Object>) msg.obj;
            final int task = (int) data.get("task");
            final ViewHolder holder = (ViewHolder) data.get("holder");
            final int position = (Integer) data.get("position");
            switch (task){
                case TASK_LOAD_PHOTO:
                    if(holder.position != position)
                        return;
                    View photoArea = (View) data.get("photoArea");
                    Bitmap bitmap = (Bitmap) data.get("bitmap");
                    photoArea.findViewById(R.id.user_photo_progress_bar).setVisibility(View.GONE);
                    ImageView image = (ImageView) photoArea.findViewById(R.id.user_photo);
                    image.setVisibility(View.VISIBLE);
                    if(bitmap == null)
                        image.setImageResource(R.drawable.error);
                    else
                        image.setImageBitmap(bitmap);
                    break;
                case TASK_TOGGLE_LIKE:
                    content.isTogglingLikeness[position] = false;
                    Object target = data.get("target");
                    if((Boolean)data.get("success")) {
                        if(target instanceof Comment) {
                            Comment comment = (Comment) target;
                            if (comment.liked_by.contains(Credential.getCredential().getSelectedAccountName())) {
                                Toast.makeText(content.context, "Successfully like the comment!", Toast.LENGTH_LONG).show();
                                if (position == holder.position)
                                    holder.like_icon.setImageResource(R.drawable.like_colored);
                            } else {
                                Toast.makeText(content.context, "Successfully cancel liking the comment!", Toast.LENGTH_LONG).show();
                                if (position == holder.position)
                                    holder.like_icon.setImageResource(R.drawable.like_bw);
                            }
                        }else {
                            Article article = (Article) target;
                            if (article.liked_by.contains(Credential.getCredential().getSelectedAccountName())) {
                                Toast.makeText(content.context, "Successfully like the article!", Toast.LENGTH_LONG).show();
                                if (position == holder.position)
                                    holder.like_icon.setImageResource(R.drawable.like_colored);
                            } else {
                                Toast.makeText(content.context, "Successfully cancel liking the article!", Toast.LENGTH_LONG).show();
                                if (position == holder.position)
                                    holder.like_icon.setImageResource(R.drawable.like_bw);
                            }
                        }
                        if(position == holder.position) {
                            holder.dislike_icon.setImageResource(R.drawable.dislike_bw);
                            if(target instanceof Comment) {
                                Comment comment = (Comment) target;
                                holder.like_num.setText(comment.liked_by.size() + "");
                                holder.dislike_num.setText(comment.disliked_by.size() + "");
                            }else{
                                Article article = (Article) target;
                                holder.like_num.setText(article.liked_by.size() + "");
                                holder.dislike_num.setText(article.disliked_by.size() + "");
                            }
                        }
                    }else {
                        if(target instanceof Comment)
                            Toast.makeText(content.context, "Unable to toggle liking the comment!", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(content.context, "Unable to toggle liking the article!", Toast.LENGTH_LONG).show();
                    }
                    break;
                case TASK_TOGGLE_DISLIKE:
                    content.isTogglingLikeness[position] = false;
                    target = data.get("target");
                    if((Boolean)data.get("success")) {
                        if(target instanceof Comment) {
                            Comment comment = (Comment) target;
                            if (comment.disliked_by.contains(Credential.getCredential().getSelectedAccountName())) {
                                Toast.makeText(content.context, "Successfully dislike the comment!", Toast.LENGTH_LONG).show();
                                if (position == holder.position)
                                    holder.dislike_icon.setImageResource(R.drawable.dislike_colored);
                            } else {
                                Toast.makeText(content.context, "Successfully cancel disliking the comment!", Toast.LENGTH_LONG).show();
                                if (position == holder.position)
                                    holder.dislike_icon.setImageResource(R.drawable.dislike_bw);
                            }
                        }else {
                            Article article = (Article) target;
                            if (article.disliked_by.contains(Credential.getCredential().getSelectedAccountName())) {
                                Toast.makeText(content.context, "Successfully dislike the article!", Toast.LENGTH_LONG).show();
                                if (position == holder.position)
                                    holder.dislike_icon.setImageResource(R.drawable.dislike_colored);
                            } else {
                                Toast.makeText(content.context, "Successfully cancel disliking the article!", Toast.LENGTH_LONG).show();
                                if (position == holder.position)
                                    holder.dislike_icon.setImageResource(R.drawable.dislike_bw);
                            }
                        }
                        if(position == holder.position) {
                            holder.like_icon.setImageResource(R.drawable.like_bw);
                            if(target instanceof Comment) {
                                Comment comment = (Comment) target;
                                holder.like_num.setText(comment.liked_by.size() + "");
                                holder.dislike_num.setText(comment.disliked_by.size() + "");
                            }else{
                                Article article = (Article) target;
                                holder.like_num.setText(article.liked_by.size() + "");
                                holder.dislike_num.setText(article.disliked_by.size() + "");
                            }
                        }
                    }else {
                        if(target instanceof Comment)
                            Toast.makeText(content.context, "Unable to toggle disliking the comment!", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(content.context, "Unable to toggle disliking the article!", Toast.LENGTH_LONG).show();
                    }
                    break;
                default:
                    break;
            }
        }

    }

}
