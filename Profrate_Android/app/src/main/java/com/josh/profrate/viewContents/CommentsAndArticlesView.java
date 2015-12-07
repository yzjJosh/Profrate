package com.josh.profrate.viewContents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.josh.profrate.R;
import com.josh.profrate.dataStructures.Article;
import com.josh.profrate.dataStructures.Comment;
import com.josh.profrate.dataStructures.Professor;
import com.josh.profrate.elements.Credential;
import com.josh.profrate.elements.TimeConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CommentsAndArticlesView extends ViewContent {

    private final Professor professor;
    private final List<Object> items;
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
                                   List<Comment> comments, List<Article> articles) {
        super(context, parentLayout);
        this.professor = professor;
        this.items = new ArrayList<Object>();
        for(Comment comment: comments)
            items.add(comment);
        for(Article article: articles)
            items.add(article);
        Collections.sort(items, comparator);
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

    private class CommentCommentBtnListener implements View.OnClickListener{

        private final Comment comment;

        public CommentCommentBtnListener(Comment comment){
            this.comment = comment;
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class CommentLikeBtnListener implements View.OnClickListener{

        private final Comment comment;

        public CommentLikeBtnListener(Comment comment){
            this.comment = comment;
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class CommentDislikeBtnListener implements View.OnClickListener{

        private final Comment comment;

        public CommentDislikeBtnListener(Comment comment){
            this.comment = comment;
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class ArticleCommentBtnListener implements View.OnClickListener{

        private final Article article;

        public ArticleCommentBtnListener(Article article){
            this.article = article;
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class ArticleLikeBtnListener implements View.OnClickListener{

        private final Article article;

        public ArticleLikeBtnListener(Article article){
            this.article = article;
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class ArticleDislikeBtnListener implements View.OnClickListener{

        private final Article article;

        public ArticleDislikeBtnListener(Article article){
            this.article = article;
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class ViewHolder{
        public ImageView user_photo;
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
            int type = getItemViewType(position);
            if (convertView == null) {
                holder = new ViewHolder();
                final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                convertView.setTag(holder);
            }else
                holder = (ViewHolder)convertView.getTag();
            if(type == 0){
                Comment comment = (Comment) getItem(position);
                holder.content.setText(comment.content);
                holder.time.setText(TimeConverter.convertTime(comment.time));
                holder.comment_btn.setOnClickListener(new CommentCommentBtnListener(comment));
                holder.like_btn.setOnClickListener(new CommentLikeBtnListener(comment));
                holder.dislike_btn.setOnClickListener(new CommentDislikeBtnListener(comment));
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
            }else{
                Article article = (Article) getItem(position);
                holder.title.setText(article.title);
                holder.content.setText(article.content);
                holder.time.setText(TimeConverter.convertTime(article.time));
                holder.comment_btn.setOnClickListener(new ArticleCommentBtnListener(article));
                holder.like_btn.setOnClickListener(new ArticleLikeBtnListener(article));
                holder.dislike_btn.setOnClickListener(new ArticleDislikeBtnListener(article));
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
            }
            holder.position = position;
            final ViewHolder finalHolder = holder;
            holder.ellipsis.setVisibility(View.GONE);
            holder.content.post(new Runnable(){
                @Override
                public void run(){
                    int lines = finalHolder.content.getLineCount();
                    if(lines > 0 && finalHolder.position == position)
                        if(finalHolder.content.getLayout().getEllipsisCount(lines-1) > 0)
                            finalHolder.ellipsis.setVisibility(View.VISIBLE);
                }
            });
            return convertView;
        }
    }

}
