package de.uni.koeln.odrajavascraper.scraper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import de.uni.koeln.odrajavascraper.entities.Article;

@Service
public class NytimesisraelScraper extends Scraper {

	@Override
	public List<String> getNewsUrlList() throws IOException {
		String baseUrl = "https://www.timesofisrael.com/";
        try {
            Document doc = openURL(baseUrl);
            List<String> links = new ArrayList<>();
            for (Element e : doc.body().getElementsByClass("type-post")) {
                links.add(e.getElementsByTag("a").attr("href"));
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
        Elements headings = doc.select("h1");
        String headline = headings.size() > 0 ? headings.get(0).getElementsByClass("headline").text() : "";
        
        //TEXTBODY
        Elements articleOuterContainers = doc.body().getElementsByClass("article-content");
        Elements articleContainers = articleOuterContainers.size() > 0 ? articleOuterContainers.get(0).getElementsByClass("the-content") : null;
        String textBody = articleContainers != null ? articleContainers.get(0).text() : "";
        
        //AUTHOR
        Elements articleAuthors = doc.body().getElementsByClass("byline");
        String author = articleAuthors.size() > 0 ? articleAuthors.get(0).getElementsByTag("a").text() : "";
        
        //TOPIC
        Elements breadcrumbs = doc.body().getElementsByClass("article-topics");
        Elements breadcrumbs_list = breadcrumbs.size() > 0 ? breadcrumbs.get(0).children() : null;
        String topic = "";
        if (breadcrumbs_list != null) {
        	for (Element bread: breadcrumbs_list) {
            	topic = topic.concat(bread.getElementsByTag("a").text());
            }
        }
        
        //CREATION DATE
        Elements times = doc.body().getElementsByClass("date");
        String creationDate = times.size() > 0 ? times.get(0).text() : "";
        if (creationDate.contains("Today")) {
        	LocalDate today = LocalDate.now();
        	creationDate = creationDate.replace("Today", today.toString());
        }
        
        Article article = new Article();
        article.setSource("https://www.timesofisrael.com/");
        article.setSourceName("ny times israel");
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
