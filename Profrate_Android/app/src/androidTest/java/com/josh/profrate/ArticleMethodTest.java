package com.josh.profrate;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.josh.profrate.dataStructures.Article;
import com.josh.profrate.dataStructures.Comment;
import com.josh.profrate.elements.Credential;

public class ArticleMethodTest extends ApplicationTestCase<Application> {

    private final String user_account = "yangzijiangjosh@gmail.com";
    private Article article;

    public ArticleMethodTest(){
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        Credential.login(user_account, getContext());
        article = Article.getArticle(5676830073815040L);
    }

    @Override
    public void tearDown() throws Exception {
        Credential.logout();
        article = null;
    }

    public void testEdit() throws Exception{
        assertTrue(article.edit("I fuck you!", "Haha"));
        article = Article.getArticle(article.id);
        assertNotNull(article);
        assertEquals(article.title, "I fuck you!");
        assertEquals(article.content, "Haha");
    }

    public void testComment() throws Exception{
        assertEquals(article.getComments().size(), 0);
        assertTrue(article.comment("1"));
        assertEquals(article.getComments().size(), 1);
        assertTrue(article.comment("2"));
        assertEquals(article.getComments().size(), 2);
        assertTrue(article.comment("3"));
        assertEquals(article.getComments().size(), 3);
        assertTrue(article.comment("4"));
        assertEquals(article.getComments().size(), 4);
        assertTrue(article.comment("5"));
        assertEquals(article.getComments().size(), 5);
    }

    public void testLike() throws Exception{
        assertTrue(article.toggle_like());
        article = Article.getArticle(article.id);
        assertNotNull(article);
        assertTrue(article.liked_by.contains(user_account));
        assertFalse(article.disliked_by.contains(user_account));
        assertTrue(article.toggle_dislike());
        article = Article.getArticle(article.id);
        assertNotNull(article);
        assertTrue(article.disliked_by.contains(user_account));
        assertFalse(article.liked_by.contains(user_account));
    }

    public void testDelete() throws Exception{
        article = Article.getArticle(5757334940811264L);
        assertNotNull(article);
        assertTrue(article.delete());
        assertNull(Article.getArticle(5757334940811264L));
        assertFalse(article.delete());
        assertFalse(article.edit("sdfdg", "sdf"));
    }

}
