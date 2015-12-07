package com.josh.profrate.viewContents;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.josh.profrate.R;
import com.josh.profrate.SecondaryActivity;
import com.josh.profrate.dataStructures.Professor;
import com.josh.profrate.elements.BitmapFetcher;
import com.josh.profrate.elements.Credential;
import com.josh.profrate.elements.RatingStar;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ViewProfessors extends ViewContent {

    private static final int TASK_LOAD_PHOTO = 0;
    private static final int TASK_TOGGLE_LIKE = 1;
    private static final int TASK_TOGGLE_DISLIKE = 2;

    private final List<Professor> professors;
    private final boolean[] isLoading;
    private final boolean[] isTogglingLike;
    private final boolean[] isTogglingDislike;
    private boolean isActive;
    private TaskHandler handler;

    public ViewProfessors(Context context, ViewGroup parentLayout, List<Professor> professors) {
        super(context, parentLayout);
        this.professors = professors;
        this.isLoading = new boolean[professors.size()];
        this.isTogglingLike = new boolean[professors.size()];
        this.isTogglingDislike = new boolean[professors.size()];
        this.isActive = false;
        this.handler = new TaskHandler(this);
    }

    @Override
    public void show() {
        ListView listView = new ListView(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        listView.setLayoutParams(params);
        listView.setAdapter(new ProfListAdapter());
        listView.setDividerHeight(50);
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

    private class PhotoClickListener implements View.OnClickListener{

        private final Professor professor;

        public PhotoClickListener(Professor professor){
            this.professor = professor;
        }


        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SecondaryActivity.class);
            intent.putExtra("view", SecondaryActivity.PROFESSOR_DETAIL);
            context.startActivity(intent);
        }
    }

    private class CommentBtnListener implements View.OnClickListener{

        private final Professor professor;

        public CommentBtnListener(Professor professor){
            this.professor = professor;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SecondaryActivity.class);
            intent.putExtra("view", SecondaryActivity.COMMENTS_AND_ARTICLES);
            intent.putExtra("prof_id", professor.id);
            context.startActivity(intent);
        }
    }

    private class LikeBtnListener implements View.OnClickListener{

        private final Professor professor;
        private final ViewHolder holder;

        public LikeBtnListener(Professor professor, ViewHolder holder){
            this.professor = professor;
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            new LikeThread(holder, professor).start();
        }
    }

    private class DislikeBtnListener implements View.OnClickListener{

        private final Professor professor;
        private final ViewHolder holder;

        public DislikeBtnListener(Professor professor, ViewHolder holder){
            this.professor = professor;
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            new DislikeThread(holder, professor).start();
        }
    }

    private class ViewHolder{
        public View comment_btn;
        public View like_btn;
        public View dislike_btn;
        public View progress_bar;
        public View error_message;
        public ImageView photo;
        public TextView name;
        public TextView title;
        public RatingStar rating;
        public TextView comment_num;
        public TextView like_num;
        public TextView dislike_num;
        public ImageView like_icon;
        public ImageView dislike_icon;
        public View photo_area;
        public int position;
    }

    private class ProfListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return professors.size();
        }

        @Override
        public Object getItem(int position) {
            return professors.get(position);
        }

        @Override
        public long getItemId(int position) {
            return professors.get(position).id;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.professor_list_item, parent, false);
                holder = new ViewHolder();
                holder.comment_btn = convertView.findViewById(R.id.comment_btn);
                holder.like_btn = convertView.findViewById(R.id.like_btn);
                holder.dislike_btn = convertView.findViewById(R.id.dislike_btn);
                holder.progress_bar = convertView.findViewById(R.id.professor_photo_progress_bar);
                holder.error_message = convertView.findViewById(R.id.professor_photo_error);
                holder.name = (TextView)convertView.findViewById(R.id.name);
                holder.title = (TextView)convertView.findViewById(R.id.title);
                holder.rating = (RatingStar)convertView.findViewById(R.id.rating);
                holder.photo = (ImageView) convertView.findViewById(R.id.photo);
                holder.comment_num = (TextView) convertView.findViewById(R.id.comment_num);
                holder.like_num = (TextView) convertView.findViewById(R.id.like_num);
                holder.dislike_num = (TextView) convertView.findViewById(R.id.dislike_num);
                holder.like_icon = (ImageView) convertView.findViewById(R.id.like_icon);
                holder.dislike_icon = (ImageView) convertView.findViewById(R.id.dislike_icon);
                holder.photo_area = convertView.findViewById(R.id.photo_area);
                convertView.setTag(holder);
            }else
                holder = (ViewHolder) convertView.getTag();
            holder.position = position;
            Professor professor = professors.get(position);
            holder.comment_btn.setOnClickListener(new CommentBtnListener(professor));
            holder.like_btn.setOnClickListener(new LikeBtnListener(professor, holder));
            holder.dislike_btn.setOnClickListener(new DislikeBtnListener(professor, holder));
            holder.photo_area.setOnClickListener(new PhotoClickListener(professor));
            holder.progress_bar.setVisibility(View.VISIBLE);
            holder.error_message.setVisibility(View.GONE);
            holder.photo.setImageBitmap(null);
            holder.photo.setVisibility(View.GONE);
            holder.name.setText(professor.name);
            holder.title.setText(professor.title);
            holder.rating.setRating(professor.overall_rating.overallRating());
            holder.like_num.setText(professor.liked_by.size()+"");
            holder.dislike_num.setText(professor.disliked_by.size() + "");
            holder.comment_num.setText(professor.comment_num + professor.article_num+ "");
            if(professor.liked_by.contains(Credential.getCredential().getSelectedAccountName()))
                holder.like_icon.setImageResource(R.drawable.like_colored);
            else
                holder.like_icon.setImageResource(R.drawable.like_bw);
            if(professor.disliked_by.contains(Credential.getCredential().getSelectedAccountName()))
                holder.dislike_icon.setImageResource(R.drawable.dislike_colored);
            else
                holder.dislike_icon.setImageResource(R.drawable.dislike_bw);
            new LoadingThread(holder, professor.image_url).start();
            return convertView;
        }
    }

    private class LoadingThread extends Thread{

        private final ViewHolder holder;
        private final String url;
        private final int position;

        public LoadingThread(ViewHolder holder, String url){
            this.holder = holder;
            this.url = url;
            this.position = holder.position;
        }

        @Override
        public void run() {
            if(isLoading[position]) return;
            isLoading[position] = true;
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_LOAD_PHOTO);
            data.put("bitmap", BitmapFetcher.fetchBitmap(url));
            data.put("holder", holder);
            data.put("position", position);
            message.obj = data;
            handler.sendMessage(message);
        }

    }

    private class LikeThread extends Thread{

        private final ViewHolder holder;
        private final Professor professor;
        private final int position;

        public LikeThread(ViewHolder holder, Professor professor){
            this.holder = holder;
            this.professor = professor;
            this.position = holder.position;
        }

        @Override
        public void run(){
            if(isTogglingLike[position]) return;
            isTogglingLike[position] = true;
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_TOGGLE_LIKE);
            data.put("holder", holder);
            data.put("position", position);
            data.put("professor", professor);
            try {
                boolean success = false;
                data.put("success", success = professor.toggle_like());
                if(success){
                    if(professor.liked_by.contains(Credential.getCredential().getSelectedAccountName()))
                        professor.liked_by.remove(Credential.getCredential().getSelectedAccountName());
                    else
                        professor.liked_by.add(Credential.getCredential().getSelectedAccountName());
                    professor.disliked_by.remove(Credential.getCredential().getSelectedAccountName());
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
        private final Professor professor;
        private final int position;

        public DislikeThread(ViewHolder holder, Professor professor){
            this.holder = holder;
            this.professor = professor;
            this.position = holder.position;
        }

        @Override
        public void run(){
            if(isTogglingDislike[position]) return;
            isTogglingDislike[position] = true;
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_TOGGLE_DISLIKE);
            data.put("holder", holder);
            data.put("position", position);
            data.put("professor", professor);
            try {
                boolean success = false;
                data.put("success", success = professor.toggle_dislike());
                if(success){
                    if(professor.disliked_by.contains(Credential.getCredential().getSelectedAccountName()))
                        professor.disliked_by.remove(Credential.getCredential().getSelectedAccountName());
                    else
                        professor.disliked_by.add(Credential.getCredential().getSelectedAccountName());
                    professor.liked_by.remove(Credential.getCredential().getSelectedAccountName());
                }
            } catch (IOException e) {
                data.put("success", false);
            }
            message.obj = data;
            handler.sendMessage(message);
        }

    }

    private static class TaskHandler extends Handler {

        private final ViewProfessors content;

        public TaskHandler(ViewProfessors content){
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
                    content.isLoading[position] = false;
                    Bitmap bitmap = (Bitmap) data.get("bitmap");
                    if(position != holder.position) return;
                    holder.progress_bar.setVisibility(View.GONE);
                    if(bitmap != null){
                        holder.photo.setVisibility(View.VISIBLE);
                        holder.photo.setImageBitmap(bitmap);
                    }else{
                        holder.error_message.setVisibility(View.VISIBLE);
                    }
                    break;
                case TASK_TOGGLE_LIKE:
                    content.isTogglingLike[position] = false;
                    Professor professor = (Professor) data.get("professor");
                    if((Boolean)data.get("success")) {
                        if(professor.liked_by.contains(Credential.getCredential().getSelectedAccountName())) {
                            Toast.makeText(content.context, "Successfully like "+professor.name+"!", Toast.LENGTH_LONG).show();
                            if(position == holder.position)
                                holder.like_icon.setImageResource(R.drawable.like_colored);
                        }else {
                            Toast.makeText(content.context, "Successfully cancel liking "+professor.name+"!", Toast.LENGTH_LONG).show();
                            if(position == holder.position)
                                holder.like_icon.setImageResource(R.drawable.like_bw);
                        }
                        if(position == holder.position) {
                            holder.dislike_icon.setImageResource(R.drawable.dislike_bw);
                            holder.like_num.setText(professor.liked_by.size() + "");
                            holder.dislike_num.setText(professor.disliked_by.size() + "");
                        }
                    }else
                        Toast.makeText(content.context, "Unable to toggle liking " + professor.name + "!", Toast.LENGTH_LONG).show();
                    break;
                case TASK_TOGGLE_DISLIKE:
                    content.isTogglingDislike[position] = false;
                    professor = (Professor) data.get("professor");
                    if((Boolean)data.get("success")) {
                        if(professor.disliked_by.contains(Credential.getCredential().getSelectedAccountName())) {
                            Toast.makeText(content.context, "Successfully dislike "+professor.name+"!", Toast.LENGTH_LONG).show();
                            if(position == holder.position)
                                holder.dislike_icon.setImageResource(R.drawable.dislike_colored);
                        }else {
                            Toast.makeText(content.context, "Successfully cancel disliking "+professor.name+"!", Toast.LENGTH_LONG).show();
                            if(position == holder.position)
                                holder.dislike_icon.setImageResource(R.drawable.dislike_bw);
                        }
                        if(position == holder.position) {
                            holder.like_icon.setImageResource(R.drawable.like_bw);
                            holder.like_num.setText(professor.liked_by.size() + "");
                            holder.dislike_num.setText(professor.disliked_by.size() + "");
                        }
                    }else
                        Toast.makeText(content.context, "Unable to toggle disliking " + professor.name + "!", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

    }
}
