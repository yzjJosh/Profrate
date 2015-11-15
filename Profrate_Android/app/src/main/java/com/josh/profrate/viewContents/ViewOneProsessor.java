package com.josh.profrate.viewContents;

import android.content.Context;
import android.view.ViewGroup;

import com.josh.profrate.elements.Professor;

public class ViewOneProsessor extends ViewContent {

    private final Professor professor;
    private boolean isActive;

    public ViewOneProsessor(Context context, ViewGroup parentLayout, Professor professor) {
        super(context, parentLayout);
        this.professor = professor;
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
