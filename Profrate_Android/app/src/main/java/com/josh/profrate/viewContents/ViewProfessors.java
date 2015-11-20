package com.josh.profrate.viewContents;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;

import com.josh.profrate.R;
import com.josh.profrate.SecondaryActivity;
import com.josh.profrate.elements.Professor;

import java.util.List;

public class ViewProfessors extends ViewContent {

    private final List<Professor> professors;
    private boolean isActive;

    public ViewProfessors(Context context, ViewGroup parentLayout, List<Professor> professors) {
        super(context, parentLayout);
        this.professors = professors;
        this.isActive = false;
        professors.add(null);
        professors.add(null);
        professors.add(null);
        professors.add(null);
    }

    @Override
    public void show() {
        ListView listView = new ListView(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        listView.setLayoutParams(params);
        listView.setAdapter(new ProfListAdapter());
        listView.setOnItemClickListener(professorClickListener);
        listView.setDividerHeight(50);
        listView.setVerticalScrollBarEnabled(false);
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

    private AdapterView.OnItemClickListener professorClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Intent intent = new Intent(context, SecondaryActivity.class);
            intent.putExtra("view", SecondaryActivity.PROFESSOR_DETAIL);
            context.startActivity(intent);
        }
    };

    private class CommentBtnListener implements View.OnClickListener{

        private final Professor professor;

        public CommentBtnListener(Professor professor){
            this.professor = professor;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SecondaryActivity.class);
            intent.putExtra("view", SecondaryActivity.COMMENTS);
            context.startActivity(intent);
        }
    }

    private class LikeBtnListener implements View.OnClickListener{

        private final Professor professor;

        public LikeBtnListener(Professor professor){
            this.professor = professor;
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class DislikeBtnListener implements View.OnClickListener{

        private final Professor professor;

        public DislikeBtnListener(Professor professor){
            this.professor = professor;
        }

        @Override
        public void onClick(View v) {

        }
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
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.professor_list_item, parent, false);
                convertView.findViewById(R.id.comment_btn).setOnClickListener(new CommentBtnListener(professors.get(position)));
                convertView.findViewById(R.id.like_btn).setOnClickListener(new LikeBtnListener(professors.get(position)));
                convertView.findViewById(R.id.dislike_btn).setOnClickListener(new DislikeBtnListener(professors.get(position)));
            }
            return convertView;
        }
    }
}
