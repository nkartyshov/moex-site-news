package com.moex.site.news.service;

import com.moex.site.news.model.News;
import org.apache.http.HttpException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class NewsService {
    private static final String URL_ID_NEWS = "http://moex.com/iss/sitenews/%d.xml";
    private static final String URL_ALL_NEWS = "http://moex.com/iss/sitenews.xml";

    public static final String XPATH = "//document/data[@id='%s']/rows/row";

    private static final NewsService newsService = new NewsService();

    private NewsService() {
    }

    public static NewsService getInstance() {
       return newsService;
    }

    public List<News> getAll() throws HttpException, NewsParser.ParseException {
        return getNews(URL_ALL_NEWS, String.format(XPATH, "sitenews"));
    }

    public News getNews(Integer id) throws HttpException, NewsParser.ParseException {
        List<News> content = getNews(String.format(URL_ID_NEWS, id), String.format(XPATH, "content"));

        if (content.isEmpty()) {
            return null;
        }

        return content.get(0);
    }

    private List<News> getNews(String url, String xpath) throws HttpException, NewsParser.ParseException {
        try (InputStream content = getContent(url)) {
            return NewsParser
                    .newParser()
                    .setXPath(xpath)
                    .parse(content);
        } catch (IOException e) {
            throw new NewsParser.ParseException("Error open content", e);
        }
    }

    private InputStream getContent(String url) throws HttpException {
        try {
            return new URL(url).openStream();
        } catch (IOException e) {
            throw new HttpException("Error handling by URL " + URL_ALL_NEWS, e);
        }
    }

}
