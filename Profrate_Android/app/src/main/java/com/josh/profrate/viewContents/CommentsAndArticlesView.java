package com.josh.profrate.viewContents;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.josh.profrate.R;
import com.josh.profrate.dataStructures.Article;
import com.josh.profrate.dataStructures.Comment;
import com.josh.profrate.dataStructures.CommentReply;
import com.josh.profrate.dataStructures.Professor;
import com.josh.profrate.dataStructures.User;
import com.josh.profrate.elements.RatingStar;

import java.util.List;
import java.util.Map;

public class CommentsAndArticlesView extends ViewContent{

    private static final int VIEW_COMMENTS = 0;
    private static final int VIEW_ARTICLES = 1;

    private final Professor professor;
    private final Bitmap photo;
    private final List<Comment> comments;
    private final List<Article> articles;
    private final Map<Long, List<CommentReply>> commentReplies;
    private final Map<Long, List<Comment>> articleComments;
    private final Map<String, User> users;
    private ViewContent content;
    private int viewType;
    private boolean isActive;

    public CommentsAndArticlesView(Context context, ViewGroup parentLayout, Professor professor,
                                   List<Comment> comments, List<Article> articles, Map<Long, List<CommentReply>> commentReplies,
                                   Map<Long, List<Comment>> articleComments, Map<String, User> users, Bitmap photo) {
        super(context, parentLayout);
        this.professor = professor;
        this.comments = comments;
        this.articles = articles;
        this.commentReplies = commentReplies;
        this.articleComments = articleComments;
        this.users = users;
        this.photo = photo;
        this.isActive = false;
        this.viewType = VIEW_COMMENTS;
    }

    @Override
    public void show() {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_comments_and_articles, parentLayout, true);
        if(photo != null)
            ((ImageView)parentLayout.findViewById(R.id.professor_photo)).setImageBitmap(photo);
        else
            ((ImageView)parentLayout.findViewById(R.id.professor_photo)).setImageResource(R.drawable.default_user_photo);
        ((TextView)parentLayout.findViewById(R.id.professor_name)).setText(professor.name);
        ((TextView)parentLayout.findViewById(R.id.professor_title)).setText(professor.title);
        if(professor.special_title != null)
            ((TextView)parentLayout.findViewById(R.id.professor_special_title)).setText(professor.special_title);
        else
            parentLayout.findViewById(R.id.professor_special_title).setVisibility(View.GONE);
        ((RatingStar)parentLayout.findViewById(R.id.professor_rating)).setRating(professor.overall_rating.overallRating());
        parentLayout.findViewById(R.id.comments_btn).setOnClickListener(commentsBtnClickListener);
        parentLayout.findViewById(R.id.articles_btn).setOnClickListener(articlesBtnClickListener);
        if(viewType == VIEW_COMMENTS) {
            content = new CommentsList(context, (ViewGroup) parentLayout.findViewById(R.id.content), comments, commentReplies, users);
            parentLayout.findViewById(R.id.comments_btn).setBackgroundResource(R.drawable.primary_color_border_btn_clicked);
            parentLayout.findViewById(R.id.articles_btn).setBackgroundResource(R.drawable.primary_color_border_btn_background);
        }
        else if(viewType == VIEW_ARTICLES) {
            content = new ArticleList(context, (ViewGroup) parentLayout.findViewById(R.id.content), articles, articleComments, users);
            parentLayout.findViewById(R.id.comments_btn).setBackgroundResource(R.drawable.primary_color_border_btn_background);
            parentLayout.findViewById(R.id.articles_btn).setBackgroundResource(R.drawable.primary_color_border_btn_clicked);
        }
        content.show();
        isActive = true;
    }

    @Override
    public void clear() {
        if(content != null && content.isActive())
            content.clear();
        parentLayout.removeAllViews();
        isActive = false;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    private View.OnClickListener commentsBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(viewType == VIEW_COMMENTS) return;
            viewType = VIEW_COMMENTS;
            content.clear();
            content = new CommentsList(context, (ViewGroup)parentLayout.findViewById(R.id.content), comments, commentReplies, users);
            parentLayout.findViewById(R.id.comments_btn).setBackgroundResource(R.drawable.primary_color_border_btn_clicked);
            parentLayout.findViewById(R.id.articles_btn).setBackgroundResource(R.drawable.primary_color_border_btn_background);
            content.show();
        }
    };

    private View.OnClickListener articlesBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(viewType == VIEW_ARTICLES) return;
            viewType = VIEW_ARTICLES;
            content.clear();
            content = new ArticleList(context, (ViewGroup)parentLayout.findViewById(R.id.content), articles, articleComments, users);
            parentLayout.findViewById(R.id.comments_btn).setBackgroundResource(R.drawable.primary_color_border_btn_background);
            parentLayout.findViewById(R.id.articles_btn).setBackgroundResource(R.drawable.primary_color_border_btn_clicked);
            content.show();
        }
    };

}
