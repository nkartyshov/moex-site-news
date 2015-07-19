package com.moex.site.news;

import com.moex.site.news.service.NewsService;
import com.moex.site.news.view.ListNewsView;
import com.moex.site.news.view.NewsView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

@Theme(ValoTheme.THEME_NAME)
@Title("News")
public class SiteNewsUI extends UI {

    public static final String MAIN = "";
    public static final String NEWS = "news";

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Navigator navigator = new Navigator(this, this);

        navigator.addView(MAIN, new ListNewsView());
        navigator.addView(NEWS, NewsView.class);
        setNavigator(navigator);

        navigator.navigateTo(MAIN);
    }
}
