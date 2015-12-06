package com.josh.profrate.viewContents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.josh.profrate.R;
import com.josh.profrate.dataStructures.Comment;

import java.util.List;

public class CommentsView extends ViewContent {

    private final List<Comment> commentList;
    private boolean isActive;

    public CommentsView(Context context, ViewGroup parentLayout, List<Comment> comments) {
        super(context, parentLayout);
        this.commentList = comments;
        this.isActive = false;
        commentList.add(null);
        commentList.add(null);
        commentList.add(null);
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

        private final Comment comment;

        public CommentBtnListener(Comment comment){
            this.comment = comment;
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class LikeBtnListener implements View.OnClickListener{

        private final Comment comment;

        public LikeBtnListener(Comment comment){
            this.comment = comment;
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class DislikeBtnListener implements View.OnClickListener{

        private final Comment comment;

        public DislikeBtnListener(Comment comment){
            this.comment = comment;
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class CommentListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return commentList.size();
        }

        @Override
        public Object getItem(int position) {
            return commentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.comment_item, parent, false);
                convertView.findViewById(R.id.comment_btn).setOnClickListener(new CommentBtnListener(commentList.get(position)));
                convertView.findViewById(R.id.like_btn).setOnClickListener(new LikeBtnListener(commentList.get(position)));
                convertView.findViewById(R.id.dislike_btn).setOnClickListener(new DislikeBtnListener(commentList.get(position)));
                LinearLayout replyList = (LinearLayout) convertView.findViewById(R.id.reply_list);
                for(int i=0; i<3; i++)
                    inflater.inflate(R.layout.comment_reply_item, replyList, true);
            }
            return convertView;
        }
    }

}
