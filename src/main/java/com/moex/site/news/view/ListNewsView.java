package com.moex.site.news.view;

import com.moex.site.news.SiteNewsUI;
import com.moex.site.news.model.News;
import com.moex.site.news.service.NewsParser;
import com.moex.site.news.service.NewsService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import org.apache.http.HttpException;
import org.joda.time.DateTime;

import java.util.List;

public class ListNewsView extends VerticalLayout implements View {

    public static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";

    private Table table = new Table();
    private PagingComponent pagingComponent = new PagingComponent(table);

    public ListNewsView() {
        buildLayout();
        configureComponent();
    }

    private void buildLayout() {
        this.setSizeFull();

        this.addComponent(table);
        this.setComponentAlignment(table, Alignment.MIDDLE_CENTER);

        this.addComponent(pagingComponent);
        this.setComponentAlignment(pagingComponent, Alignment.MIDDLE_CENTER);

        this.setExpandRatio(table, 1.0f);
        this.setExpandRatio(pagingComponent, 0.1f);
    }

    private void configureComponent() {
        table.setWidth(80, Unit.PERCENTAGE);
        table.setHeight(100, Unit.PERCENTAGE);
        table.setSelectable(true);

        table.addGeneratedColumn("publishedDate", new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table table, Object itemId, Object columnId) {
                News news = (News) itemId;

                DateTime publishedDate = news.getPublishedDate();
                if (publishedDate == null) {
                    return "";
                } else {
                    return publishedDate.toString(DATE_TIME_FORMAT);
                }
            }
        });

        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                Item item = event.getItem();

                UI.getCurrent().getNavigator().navigateTo(SiteNewsUI.NEWS + "/" + item.getItemProperty("id"));
            }
        });
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (table.isEmpty()) {
            try {
                List<News> all = NewsService.getInstance().getAll();

                pagingComponent.setNewsList(all);
            } catch (HttpException | NewsParser.ParseException e) {
                Notification.show("Oops ... something went wrong.", "Please do not worry, come back later.", Notification.Type.ERROR_MESSAGE);
            }
        }
    }

    private static class PagingComponent extends HorizontalLayout {
        private Button prev = new Button("<");
        private Button next = new Button(">");

        private Table table;

        private int currentIndex = 0;
        private int endIndex;

        private int pageSize = 20;

        private List<News> newsList;

        public PagingComponent(Table table) {
            this.table = table;

            configureComponent();
        }

        private void configureComponent() {
            prev.setEnabled(false);

            prev.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    if (currentIndex != 0) {
                        currentIndex -= pageSize;
                        loadTableData();
                    }

                    refreshButton();
                }
            });

            next.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    if (getLoadSize() < endIndex) {
                        currentIndex += pageSize;
                        loadTableData();
                    }

                    refreshButton();
                }
            });

            this.addComponent(prev);
            this.addComponent(next);
        }


        public void setNewsList(List<News> newsList) {
            this.newsList = newsList;
            this.endIndex = newsList.size();

            loadTableData();
        }

        private void loadTableData() {
            BeanItemContainer<News> itemContainer = new BeanItemContainer<News>(News.class);

            for (int i = currentIndex; i < getLoadSize() && i < newsList.size(); i++) {
                News news = newsList.get(i);

                itemContainer.addBean(news);
            }

            table.setContainerDataSource(itemContainer);

            table.setVisibleColumns("title", "publishedDate");
            table.setColumnHeaders("Title", "Published at");
        }

        private void refreshButton() {
            next.setEnabled(getLoadSize() < endIndex);
            prev.setEnabled(currentIndex != 0);
        }

        private int getLoadSize() {
            return currentIndex + pageSize;
        }
    }
}
