package com.josh.profrate.dataStructures;

import com.appspot.profrate_1148.profrateAPI.model.ProfrateProfessorMessage;
import com.josh.profrate.elements.Credential;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Professor {

    public final long id;
    public final String name;
    public final String title;
    public final String special_title;
    public final String image_url;
    public final String introduction;
    public final List<String> research_areas;
    public final List<String> research_interests;
    public final List<String> reserach_groups;
    public final String office;
    public final String phone;
    public final String email;
    public final String personal_website;
    public final Rating overall_rating;
    public final List<String> liked_by;
    public final List<String> disliked_by;

    Professor(ProfrateProfessorMessage prof){
        this.id = prof.getId();
        this.name = prof.getName();
        this.title = prof.getTitle();
        this.special_title = prof.getSpecialTitle();
        this.image_url = prof.getImage();
        this.introduction = prof.getIntroduction();
        this.research_areas = prof.getResearchAreas()==null? new ArrayList<String>(): prof.getResearchAreas();
        this.research_interests = prof.getResearchInterests()==null? new ArrayList<String>(): prof.getResearchInterests();
        this.reserach_groups = prof.getResearchGroups()==null? new ArrayList<String>(): prof.getResearchGroups();
        this.office = prof.getOffice();
        this.phone = prof.getPhone();
        this.email = prof.getEmail();
        this.personal_website = prof.getPersonalWebsite();
        this.overall_rating = new Rating(prof.getOverallRating());
        this.liked_by = prof.getLikedBy()==null? new ArrayList<String>(): prof.getLikedBy();
        this.disliked_by = prof.getDislikedBy()==null? new ArrayList<String>(): prof.getDislikedBy();
    }

    public static Professor getProfessor(long prof_id) throws IOException{
        return BackendAPI.professor_get(prof_id);
    }

    public static List<Professor> getAllProfessors() throws IOException{
        return BackendAPI.professor_get_all();
    }


    public boolean comment(String content) throws IOException{
        return BackendAPI.professor_comment(id, content, Credential.getCredential());
    }

    public boolean rate(Rating rating) throws IOException{
        return BackendAPI.professor_rate(id, rating.personality, rating.teaching_skill,
                rating.research_skill, rating.knowledge_level, Credential.getCredential());
    }

    public boolean writeArticle(String title, String content) throws IOException{
        return BackendAPI.professor_write_article(id, title, content, Credential.getCredential());
    }

    public boolean like() throws IOException{
        return BackendAPI.professor_like(id, Credential.getCredential());
    }

    public boolean dislike() throws IOException{
        return BackendAPI.professor_dislike(id, Credential.getCredential());
    }

    public List<Comment> getComments() throws IOException{
        return BackendAPI.professor_get_comments(id);
    }

    public List<Article> getArticles() throws IOException{
        return BackendAPI.professor_get_articles(id);
    }

    public Rating getRating() throws IOException{
        return BackendAPI.professor_get_rating(id, Credential.getCredential());
    }

}
