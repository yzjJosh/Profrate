package com.josh.profrate.viewContents;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.josh.profrate.LogIn;
import com.josh.profrate.R;
import com.josh.profrate.elements.Credential;
import com.josh.profrate.elements.Professor;

import java.util.List;

public class ViewProfessors extends ViewContent {

    private final List<Professor> professors;

    public ViewProfessors(Context context, ViewGroup parentLayout, List<Professor> professors) {
        super(context, parentLayout);
        this.professors = professors;
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
        parentLayout.addView(listView);
    }

    @Override
    public void clear() {
        parentLayout.removeAllViews();
    }

    @Override
    public boolean isActive() {
        return false;
    }

    private AdapterView.OnItemClickListener professorClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {

        }
    };

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
            }
            return convertView;
        }
    }
}
