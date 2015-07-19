package com.moex.site.news.service;

import com.moex.site.news.model.News;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NewsParser {
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String PUBLISHED_AT = "published_at";
    public static final String BODY = "body";
    private String xpath;
    public static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    private NewsParser() {
    }

    public static NewsParser newParser() {
        return new NewsParser();
    }

    public NewsParser setXPath(String xpath) {
        this.xpath = xpath;
        return this;
    }

    public List<News> parse(InputStream inputStream) throws ParseException {
        List<News> newsList = new ArrayList<>();

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document parse = documentBuilder.parse(inputStream);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression compile = xPath.compile(xpath);

            NodeList nodeList = (NodeList) compile.evaluate(parse, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node item = nodeList.item(i);

                NamedNodeMap attributes = item.getAttributes();

                News news = new News();

                news.setId(Integer.valueOf(attributes.getNamedItem(ID).getNodeValue()));
                news.setTitle(attributes.getNamedItem(TITLE).getNodeValue());
                news.setPublishedDate(FORMATTER.parseDateTime(attributes.getNamedItem(PUBLISHED_AT).getNodeValue()));

                Node body = attributes.getNamedItem(BODY);
                if (body != null) {
                    news.setBody(body.getNodeValue());
                }

                newsList.add(news);
            }
        } catch (ParserConfigurationException e) {
            throw new NewsParser.ParseException("Error configure DOM parser", e);
        } catch (SAXException | IOException | XPathExpressionException e) {
            throw new NewsParser.ParseException("Error parsing content", e);
        }

        return newsList;
    }

    public static class ParseException extends Exception {
        public ParseException(String s, Throwable throwable) {
            super(s, throwable);
        }
    }
}
