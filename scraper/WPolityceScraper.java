package de.uni.koeln.odrajavascraper.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import de.uni.koeln.odrajavascraper.entities.Article;

@Service
public class WPolityceScraper extends Scraper {

	@Override
    public List<String> getNewsUrlList() {
		String baseUrl = "https://wpolityce.pl";
        try {
            Document doc = openURL(baseUrl);
            List<String> links = new ArrayList<>();
            for (Element e : doc.body().getElementsByAttribute("data-publication")) {
                links.add(baseUrl.concat(e.getElementsByTag("a").attr("href")));
            }
            return links;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

	@Override
	public Article scrape(String url) throws IOException {
		
		Document doc = openURL(url);

        //HEADLINE
        Elements headings = doc.select("meta[property=og:title]");
        String headline = headings.size() > 0 ? headings.get(0).attr("content") : "";
        
        //TEXTBODY
        Elements articleContainers = doc.body().getElementsByClass("article-body");
        String textBody = articleContainers.size() > 0 ? articleContainers.get(0).text() : "";
        
        //AUTHOR
        Elements articleAuthors = doc.body().getElementsByClass("article-author");
        String author = articleAuthors.size() > 0 ? articleAuthors.get(0).getElementsByTag("a").text() : "";
        
        //TOPIC
        Elements breadcrumbs = doc.body().getElementsByClass("section-title");
        String topic = breadcrumbs.size() > 0 ? breadcrumbs.get(0).getElementsByTag("a").text() : "";
        
        //CREATION DATE
        Elements times = doc.body().getElementsByTag("time");
        String creationDate = times.size() > 0 ? times.get(0).text() : "";
        
        
        Article article = new Article();
        article.setSource("https://wpolityce.pl");
        article.setSourceName("wpolityce");
        article.setCrawlDate(new Date());
        article.setHeadline(headline);
        article.setTextBody(textBody);
        article.setCreationDate(creationDate);
        article.setLink(url);
        article.setAuthor(author);
        article.setTopic(topic);
        return article;
	}
}
