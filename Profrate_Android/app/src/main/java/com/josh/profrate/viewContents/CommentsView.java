package com.josh.profrate.viewContents;

import android.content.Context;
import android.view.ViewGroup;

import com.josh.profrate.elements.Comment;

import java.util.List;

public class CommentsView extends ViewContent {

    private final List<Comment> commentList;
    private boolean isActive;

    public CommentsView(Context context, ViewGroup parentLayout, List<Comment> comments) {
        super(context, parentLayout);
        this.commentList = comments;
        this.isActive = false;
    }

    @Override
    public void show() {
        isActive = true;
    }

    @Override
    public void clear() {
        isActive = false;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }
}
