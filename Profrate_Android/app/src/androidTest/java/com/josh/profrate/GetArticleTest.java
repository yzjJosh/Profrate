package com.josh.profrate;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.josh.profrate.dataStructures.Article;
import com.josh.profrate.dataStructures.Professor;
import com.josh.profrate.elements.Credential;

import java.util.List;

public class GetArticleTest extends ApplicationTestCase<Application> {

    public GetArticleTest(){
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        Credential.login("yangzijiangjosh@gmail.com", getContext());
    }

    @Override
    public void tearDown() throws Exception {
        Credential.logout();
    }

    public void testGet() throws Exception{
        Professor professor = Professor.getProfessor(6245567495667712L);
        assertTrue(professor.writeArticle("1", "1"));
        assertTrue(professor.writeArticle("2", "2"));
        assertTrue(professor.writeArticle("3", "3"));
        assertTrue(professor.writeArticle("4", "4"));
        List<Article> articles = professor.getArticles();
        for(Article article: articles){
            Article got = Article.getArticle(article.id);
            assertNotNull(got);
            assertEquals(article.id, got.id);
            assertEquals(article.content, got.content);
            assertEquals(article.title, got.title);
        }
    }

}
