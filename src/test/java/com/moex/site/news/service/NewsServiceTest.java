package com.moex.site.news.service;

import com.moex.site.news.model.News;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Nikolay on 19.07.2015.
 */
public class NewsServiceTest {

    private NewsService newsService = NewsService.getInstance();

    @Test
    public void testGetAll() throws Exception {
        List<News> all = newsService.getAll();

        assertFalse(all.isEmpty());
    }

    @Test
    public void testGetNews() throws Exception {
        News news = newsService.getNews(10146);

        assertNotNull(news);
    }
}