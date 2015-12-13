package com.josh.profrate.viewContents;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.josh.profrate.R;
import com.josh.profrate.SecondaryActivity;
import com.josh.profrate.dataStructures.Professor;
import com.josh.profrate.dataStructures.Rating;
import com.josh.profrate.elements.Credential;
import com.josh.profrate.elements.RatingStar;

import java.io.IOException;
import java.util.HashMap;

public class ViewOneProsessor extends ViewContent {

    private static final int TASK_RATE = 0;
    private static final int TASK_TOGGLE_LIKE = 1;
    private static final int TASK_TOGGLE_DISLIKE = 2;

    private Professor professor;
    private final Bitmap photo;
    private Rating myRating;
    private final TaskHandler handler = new TaskHandler(this);
    private Dialog processingDialog;
    private boolean isTogglingLikeness;
    private boolean isActive;

    public ViewOneProsessor(Context context, ViewGroup parentLayout, Professor professor, Bitmap photo, Rating myRating) {
        super(context, parentLayout);
        this.professor = professor;
        this.photo = photo;
        this.myRating = myRating;
        this.isTogglingLikeness = false;
        this.isActive = false;
    }

    @Override
    public void show() {
        isActive = true;
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.professor_detail, parentLayout, true);
        if(photo != null)
            ((ImageView)parentLayout.findViewById(R.id.professor_photo)).setImageBitmap(photo);
        else
            ((ImageView)parentLayout.findViewById(R.id.professor_photo)).setImageResource(R.drawable.default_user_photo);
        ((TextView)parentLayout.findViewById(R.id.professor_name)).setText(professor.name);
        ((TextView)parentLayout.findViewById(R.id.professor_title)).setText(professor.title);
        if(professor.special_title == null)
            parentLayout.findViewById(R.id.professor_special_title).setVisibility(View.GONE);
        else
            ((TextView)parentLayout.findViewById(R.id.professor_special_title)).setText(professor.special_title);
        if(professor.office == null)
            parentLayout.findViewById(R.id.professor_office_area).setVisibility(View.GONE);
        else
            ((TextView)parentLayout.findViewById(R.id.professor_office)).setText(professor.office);
        if(professor.phone == null)
            parentLayout.findViewById(R.id.professor_phone_area).setVisibility(View.GONE);
        else {
            TextView phone = (TextView) parentLayout.findViewById(R.id.professor_phone);
            phone.setText(professor.phone);
            phone.setPaintFlags(phone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            phone.setOnClickListener(phoneClickListener);
        }
        if(professor.email == null)
            parentLayout.findViewById(R.id.professor_email_area).setVisibility(View.GONE);
        else {
            TextView email = (TextView) parentLayout.findViewById(R.id.professor_email);
            email.setText(professor.email);
            email.setPaintFlags(email.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            email.setOnClickListener(emailClickListener);
        }
        if(professor.personal_website == null)
            parentLayout.findViewById(R.id.professor_personal_website_area).setVisibility(View.GONE);
        else {
            TextView personal_website = (TextView) parentLayout.findViewById(R.id.professor_personal_website);
            personal_website.setText(professor.personal_website);
            personal_website.setPaintFlags(personal_website.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            personal_website.setOnClickListener(personalWebsiteClickListener);
        }
        ((RatingStar)parentLayout.findViewById(R.id.rating_personality)).setRating(professor.overall_rating.personality);
        ((RatingStar)parentLayout.findViewById(R.id.rating_research_skill)).setRating(professor.overall_rating.research_skill);
        ((RatingStar)parentLayout.findViewById(R.id.rating_teaching_skill)).setRating(professor.overall_rating.teaching_skill);
        ((RatingStar)parentLayout.findViewById(R.id.rating_knowledge_level)).setRating(professor.overall_rating.knowledge_level);
        ((RatingStar)parentLayout.findViewById(R.id.rating_overall)).setRating(professor.overall_rating.overallRating());
        if(myRating == null) {
            parentLayout.findViewById(R.id.my_rating_not_available).setVisibility(View.VISIBLE);
            parentLayout.findViewById(R.id.my_rating).setVisibility(View.GONE);
        }else {
            parentLayout.findViewById(R.id.my_rating_not_available).setVisibility(View.GONE);
            parentLayout.findViewById(R.id.my_rating).setVisibility(View.VISIBLE);
            ((RatingStar) parentLayout.findViewById(R.id.my_rating)).setRating(myRating.overallRating());
        }
        parentLayout.findViewById(R.id.rate_btn).setOnClickListener(rateClickListener);
        if(professor.introduction == null || professor.introduction.length() == 0)
            parentLayout.findViewById(R.id.introduction_area).setVisibility(View.GONE);
        else
            ((TextView)parentLayout.findViewById(R.id.professor_introduction)).setText(professor.introduction);
        if(professor.research_areas.size()==0 && professor.research_interests.size()==0 && professor.reserach_groups.size()==0)
            parentLayout.findViewById(R.id.research_info_area).setVisibility(View.GONE);
        else {
            if (professor.research_areas.size() > 0) {
                String areas = "";
                for (String area : professor.research_areas)
                    areas += area + '\n';
                ((TextView) parentLayout.findViewById(R.id.professor_research_areas)).setText(areas);
            } else
                parentLayout.findViewById(R.id.professor_research_areas_area).setVisibility(View.GONE);
            if (professor.research_interests.size() > 0) {
                String interests = "";
                for (String interest : professor.research_interests)
                    interests += interest + '\n';
                ((TextView) parentLayout.findViewById(R.id.professor_research_interests)).setText(interests);
            } else
                parentLayout.findViewById(R.id.professor_research_interests_area).setVisibility(View.GONE);
            if (professor.reserach_groups.size() > 0) {
                String groups = "";
                for (String group : professor.reserach_groups)
                    groups += group + '\n';
                ((TextView) parentLayout.findViewById(R.id.professor_research_groups)).setText(groups);
            } else
                parentLayout.findViewById(R.id.professor_research_groups_area).setVisibility(View.GONE);
        }
        ((TextView)parentLayout.findViewById(R.id.comment_num)).setText(professor.comment_num+professor.article_num+"");
        ((TextView)parentLayout.findViewById(R.id.like_num)).setText(professor.liked_by.size()+"");
        ((TextView)parentLayout.findViewById(R.id.dislike_num)).setText(professor.disliked_by.size()+"");
        if(professor.liked_by.contains(Credential.getCredential().getSelectedAccountName()))
            ((ImageView)parentLayout.findViewById(R.id.like_icon)).setImageResource(R.drawable.like_colored);
        else
            ((ImageView)parentLayout.findViewById(R.id.like_icon)).setImageResource(R.drawable.like_bw);
        if(professor.disliked_by.contains(Credential.getCredential().getSelectedAccountName()))
            ((ImageView)parentLayout.findViewById(R.id.dislike_icon)).setImageResource(R.drawable.dislike_colored);
        else
            ((ImageView)parentLayout.findViewById(R.id.dislike_icon)).setImageResource(R.drawable.dislike_bw);
        parentLayout.findViewById(R.id.comment_btn).setOnClickListener(commentClickListener);
        parentLayout.findViewById(R.id.like_btn).setOnClickListener(likeClickListener);
        parentLayout.findViewById(R.id.dislike_btn).setOnClickListener(dislikeClickListener);
    }

    @Override
    public void clear() {
        isActive = false;
        parentLayout.removeAllViews();
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    private View.OnClickListener personalWebsiteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView link = (TextView)v;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link.getText().toString()));
            context.startActivity(browserIntent);
        }
    };

    private View.OnClickListener emailClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView email = (TextView) v;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{email.getText().toString()});
            try {
                context.startActivity(Intent.createChooser(intent, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener phoneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String phone_number = ((TextView) v).getText().toString();
            String phone = "tel:";
            for(int i=0; i<phone_number.length(); i++)
                if(Character.isDigit(phone_number.charAt(i)))
                    phone += phone_number.charAt(i);
            Uri number = Uri.parse(phone);
            Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
            context.startActivity(callIntent);
        }
    };

    private View.OnClickListener rateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Dialog ratingDialog = new Dialog(context, R.style.theme_dialog);
            ratingDialog.setContentView(R.layout.rate_professor_dialog);
            final RatingStar personalityRating = (RatingStar) ratingDialog.findViewById(R.id.rating_personality);
            final RatingStar teachingSkillRating = (RatingStar) ratingDialog.findViewById(R.id.rating_teaching_skill);
            final RatingStar researchSkillRating = (RatingStar) ratingDialog.findViewById(R.id.rating_research_skill);
            final RatingStar knowledgeLevelRating = (RatingStar) ratingDialog.findViewById(R.id.rating_knowledge_level);
            if(myRating != null) {
                personalityRating.setRating(myRating.personality);
                teachingSkillRating.setRating(myRating.teaching_skill);
                researchSkillRating.setRating(myRating.research_skill);
                knowledgeLevelRating.setRating(myRating.knowledge_level);
            }
            ratingDialog.findViewById(R.id.ok_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    double personality = personalityRating.getRating();
                    double teaching_skill = teachingSkillRating.getRating();
                    double research_skill = researchSkillRating.getRating();
                    double knowledge_level = knowledgeLevelRating.getRating();
                    new RateThread(new Rating(personality, teaching_skill, research_skill, knowledge_level)).start();
                    ratingDialog.dismiss();
                    processingDialog = new Dialog(context, R.style.theme_dialog);
                    processingDialog.setContentView(R.layout.processing_dialog);
                    processingDialog.setCancelable(false);
                    processingDialog.show();
                }
            });
            ratingDialog.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ratingDialog.dismiss();
                }
            });
            ratingDialog.show();
        }
    };

    private View.OnClickListener commentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SecondaryActivity.class);
            intent.putExtra("view", SecondaryActivity.COMMENTS_AND_ARTICLES);
            intent.putExtra("prof_id", professor.id);
            context.startActivity(intent);
        }
    };

    private View.OnClickListener likeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new LikeThread().start();
        }
    };

    private View.OnClickListener dislikeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new DislikeThread().start();
        }
    } ;

    private class RateThread extends Thread{

        private Rating rating;

        public RateThread(Rating rating){
            this.rating = rating;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_RATE);
            try {
                professor.rate(rating);
                professor = Professor.getProfessor(professor.id);
                myRating = professor.getRating();
                data.put("success", true);
            } catch (IOException e) {
                e.printStackTrace();
                data.put("success", false);
            }
            message.obj = data;
            handler.sendMessage(message);
        }

    }

    private class LikeThread extends Thread{

        @Override
        public void run(){
            if(isTogglingLikeness) return;
            isTogglingLikeness = true;
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_TOGGLE_LIKE);
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

        @Override
        public void run(){
            if(isTogglingLikeness) return;
            isTogglingLikeness = true;
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("task", TASK_TOGGLE_DISLIKE);
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

        private ViewOneProsessor content;

        public TaskHandler(ViewOneProsessor content){
            this.content = content;
        }
        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message message){
            if(!content.isActive()) return;
            HashMap<String, Object> data = (HashMap<String, Object>)message.obj;
            int task = (int)data.get("task");
            boolean success = (boolean)data.get("success");
            switch (task){
                case TASK_RATE:
                    if(success) {
                        content.processingDialog.dismiss();
                        content.clear();
                        content.show();
                        Toast.makeText(content.context, "Successfully rate " + content.professor.name, Toast.LENGTH_LONG).show();
                    }
                    else
                        Toast.makeText(content.context, "Unable to rate "+content.professor.name, Toast.LENGTH_LONG).show();
                    break;
                case TASK_TOGGLE_LIKE:
                    content.isTogglingLikeness = false;
                    if(success) {
                        if(content.professor.liked_by.contains(Credential.getCredential().getSelectedAccountName())) {
                            Toast.makeText(content.context, "Successfully like "+content.professor.name+"!", Toast.LENGTH_LONG).show();
                            ((ImageView)content.parentLayout.findViewById(R.id.like_icon)).setImageResource(R.drawable.like_colored);
                        }else {
                            Toast.makeText(content.context, "Successfully cancel liking "+content.professor.name+"!", Toast.LENGTH_LONG).show();
                            ((ImageView)content.parentLayout.findViewById(R.id.like_icon)).setImageResource(R.drawable.like_bw);
                        }
                        ((ImageView)content.parentLayout.findViewById(R.id.dislike_icon)).setImageResource(R.drawable.dislike_bw);
                        ((TextView)content.parentLayout.findViewById(R.id.like_num)).setText(content.professor.liked_by.size() + "");
                        ((TextView)content.parentLayout.findViewById(R.id.dislike_num)).setText(content.professor.disliked_by.size() + "");
                    }else
                        Toast.makeText(content.context, "Unable to toggle liking " + content.professor.name + "!", Toast.LENGTH_LONG).show();
                    break;
                case TASK_TOGGLE_DISLIKE:
                    content.isTogglingLikeness = false;
                    if(success) {
                        if(content.professor.disliked_by.contains(Credential.getCredential().getSelectedAccountName())) {
                            Toast.makeText(content.context, "Successfully dislike "+content.professor.name+"!", Toast.LENGTH_LONG).show();
                            ((ImageView)content.parentLayout.findViewById(R.id.dislike_icon)).setImageResource(R.drawable.dislike_colored);
                        }else {
                            Toast.makeText(content.context, "Successfully cancel disliking "+content.professor.name+"!", Toast.LENGTH_LONG).show();
                            ((ImageView)content.parentLayout.findViewById(R.id.dislike_icon)).setImageResource(R.drawable.dislike_bw);
                        }
                        ((ImageView)content.parentLayout.findViewById(R.id.like_icon)).setImageResource(R.drawable.like_bw);
                        ((TextView)content.parentLayout.findViewById(R.id.like_num)).setText(content.professor.liked_by.size() + "");
                        ((TextView)content.parentLayout.findViewById(R.id.dislike_num)).setText(content.professor.disliked_by.size() + "");
                    }else
                        Toast.makeText(content.context, "Unable to toggle disliking " + content.professor.name + "!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

}
