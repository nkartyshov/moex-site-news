package com.moex.site.news.view;

import com.moex.site.news.SiteNewsUI;
import com.moex.site.news.model.News;
import com.moex.site.news.service.NewsParser;
import com.moex.site.news.service.NewsService;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.apache.http.HttpException;

public class NewsView extends VerticalLayout implements View {
    private Label content = new Label();
    private Panel panel = new Panel();
    private Button back = new Button("Come back");

    public NewsView() {
        this.setSizeFull();

        configureComponent();
        buildLayout();
    }

    private void configureComponent() {
        content.setWidth(100, Unit.PERCENTAGE);
        content.setHeightUndefined();
        content.setContentMode(ContentMode.HTML);

        back.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                UI.getCurrent().getNavigator().navigateTo(SiteNewsUI.MAIN);
            }
        });
    }

    private void buildLayout() {
        panel.setWidth(80, Unit.PERCENTAGE);
        panel.setHeight(100, Unit.PERCENTAGE);
        panel.setCaptionAsHtml(true);

        panel.setContent(content);

        this.addComponent(panel);
        this.setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
        this.setExpandRatio(panel, 1.0f);

        this.addComponent(back);
        this.setComponentAlignment(back, Alignment.MIDDLE_CENTER);
        this.setExpandRatio(back, 0.1f);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        String id = event.getParameters();

        try {
            News news = NewsService.getInstance().getNews(Integer.valueOf(id));

            panel.setCaption(news.getTitle());
            content.setValue(news.getBody());
        } catch (HttpException | NewsParser.ParseException e) {
            Notification.show("Oops ... something went wrong.", "Please do not worry, come back later.", Notification.Type.ERROR_MESSAGE);
        }
    }
}
