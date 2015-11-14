package com.josh.profrate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.josh.profrate.elements.Credential;
import com.josh.profrate.elements.Professor;
import com.josh.profrate.viewContents.ViewContent;
import com.josh.profrate.viewContents.ViewProfessors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    public static final int VIEW_PROFESSORS = 0;
    public static final int LOG_OUT = 1;
    private static final String[] view_names = new String[] {"View Professors", "Log out"};

    private DrawerLayout drawerLayout;
    private TextView title;
    private RelativeLayout content_layout;
    private MenuListAdapter menu_list_adapter;
    private ProgressBar progressBar;
    private LinearLayout error_sign;
    private LinearLayout warning_sign;
    private int cur_view;
    private ViewContent content;

    private boolean isLoading = false;
    private boolean isActive = true;

    private BackEndTaskHandler backEndTaskHandler = new BackEndTaskHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cur_view = getIntent().getIntExtra("view", VIEW_PROFESSORS);
        ListView drawerList = (ListView) findViewById(R.id.drawer_list);
        menu_list_adapter = new MenuListAdapter(getMenuData());
        drawerList.setAdapter(menu_list_adapter);
        drawerList.setOnItemClickListener(listItemListener);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        title = (TextView)findViewById(R.id.main_activity_title);
        content_layout = (RelativeLayout)findViewById(R.id.main_activity_content);
        progressBar = (ProgressBar) findViewById(R.id.main_activity_progress_bar);
        error_sign = (LinearLayout) findViewById(R.id.main_activity_error);
        warning_sign = (LinearLayout) findViewById(R.id.main_activity_warning);
        switchContent(cur_view);
    }

    @Override
    public void onStart(){
        super.onStart();
        isActive = true;
        if(content != null && !content.isActive())
            content.show();
    }

    @Override
    public void onStop(){
        super.onStop();
        isActive = false;
    }


    private List<Map<String, Object>> getMenuData(){
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> item = new HashMap<String, Object>();
        item.put("icon", R.drawable.error);
        item.put("view_id", VIEW_PROFESSORS);
        data.add(item);
        item = new HashMap<String, Object>();
        item.put("icon", R.drawable.user);
        item.put("view_id", LOG_OUT);
        data.add(item);
        return data;
    }

    private void switchContent(int view_id){
        if(view_id >VIEW_PROFESSORS || view_id < VIEW_PROFESSORS) return;
        title.setText(view_names[view_id]);
        if(content != null) {
            content.clear(); //Clear existing content firstly
            content = null;
        }
        progressBar.setVisibility(View.VISIBLE);
        error_sign.setVisibility(View.GONE);
        warning_sign.setVisibility(View.GONE);
        isLoading = true;
        new LoadingThread(view_id).start();
    }

    private AdapterView.OnItemClickListener listItemListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            if(cur_view == (int)id) return;
            if((int)id == LOG_OUT){
                Credential.logout();
                startActivity(new Intent(MainActivity.this, LogIn.class));
                finish();
                return;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            cur_view = (int)id;
            menu_list_adapter.notifyDataSetChanged();
            switchContent(cur_view);
        }
    };


    public void onMenuIconClick(View view){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void onSearchIconClick(View view){
        new SearchView(MainActivity.this).show();
    }

    public void onRefreshClick(View view){
        if(!isLoading)
            switchContent(cur_view);
    }

    class MenuListAdapter extends BaseAdapter {
        private List<Map<String, Object>> menuData;

        public MenuListAdapter(List<Map<String, Object>> data){
            this.menuData = data;
        }

        @Override
        public int getCount() {
            return menuData.size();
        }

        @Override
        public Object getItem(int pos) {
            return menuData.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return (long)((Integer)menuData.get(pos).get("view_id"));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.drawer_list_item, parent, false);
                ((TextView) convertView.findViewById(R.id.item_text)).setText(view_names[(int)getItemId(position)]);
                ((ImageView) convertView.findViewById(R.id.item_icon)).setImageResource((Integer)menuData.get(position).get("icon"));
            }
            if(getItemId(position) == cur_view)
                convertView.setBackgroundResource(R.color.menu_selected);
            else
                convertView.setBackgroundResource(0);
            return convertView;
        }
    }

    class LoadingThread extends Thread{

        private final int type;

        public LoadingThread(int type){
            this.type = type;
        }

        @Override
        public void run(){
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("type", type);
            try {
                switch (type) {
                    case VIEW_PROFESSORS:
                        //data.put("streams", BackEndAPI.getOwnedStreams(Credential.getCredential()));
                        break;
                    default:
                        break;
                }
                data.put("success", true);
            } catch (Exception e) {
                e.printStackTrace();
                data.put("success", false);
            }
            Message msg = new Message();
            msg.obj = data;
            backEndTaskHandler.sendMessage(msg);
        }
    }

    static class BackEndTaskHandler extends Handler {
        private  MainActivity activity;

        public BackEndTaskHandler(MainActivity activity){
            this.activity = activity;
        }
        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg){
            HashMap<String, Object> data = (HashMap<String, Object>)msg.obj;
            if(activity.cur_view != (Integer)data.get("type")) return;
            activity.progressBar.setVisibility(View.GONE);
            activity.isLoading = false;
            if((Boolean) data.get("success")) {
                switch((Integer)data.get("type")) {
                    case VIEW_PROFESSORS:
                        activity.content = new ViewProfessors(activity, activity.content_layout, new ArrayList<Professor>());
                        break;
                    default:
                        break;
                }
                if(activity.isActive)
                    activity.content.show();
            }else
                activity.error_sign.setVisibility(View.VISIBLE);
        }
    }


}
