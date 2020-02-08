package de.uni.koeln.odrajavascraper.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import de.uni.koeln.odrajavascraper.entities.Article;

@Service
public class UnsereZeitScraper extends Scraper {

	@Override
	public List<String> getNewsUrlList() throws IOException {
		String baseUrl = "https://www.unsere-zeit.de";
        try {
            Document doc = openURL(baseUrl);
            List<String> topicLinks = new ArrayList<>();
            List<String> links = new ArrayList<>();
            for (Element e : doc.body().getElementsByClass("navmainscreen")) {
                Elements e2 = e.getElementsByTag("a");
                Element link = e2.size() > 0 ? e2.get(0) : null;
                if (link != null) {
                	String topic = link.attr("title");
	                if (!topic.contains("Anzeige") && !topic.contains("Position")
	                		&& !topic.contains("Parteitag")) {
	                	topicLinks.add(baseUrl.concat(link.getElementsByTag("a").attr("href")));
	                }
                }
            }
            for (String link : topicLinks) {
            	Document topicDoc = openURL(link);
            	for (Element e : topicDoc.body().getElementsByClass("art-more")) {
                    links.add(baseUrl.concat(e.getElementsByTag("a").attr("href")));
                }
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

      //AUTHOR
        Elements articleAuthors = doc.select("meta[name=author]");
        String author = articleAuthors.size() > 0 ? articleAuthors.get(0).attr("content") : "";
        
        //TEXTBODY
        Elements articleContainers = doc.body().getElementsByClass("art-text");
        String textBody = articleContainers.size() > 0 ? articleContainers.get(0).text() : "";
           
        //TOPIC
        Elements breadcrumbs = doc.body().getElementsByClass("art-section");
        String topic = breadcrumbs.size() > 0 ? breadcrumbs.get(0).getElementsByTag("a").text() : "";
        
        //CREATION DATE
        Elements times = doc.body().getElementsByClass("art-issue");
        String creationDate = times.size() > 0 ? times.get(0).text() : "";
        Matcher matcher = Pattern.compile("\\d+").matcher(creationDate);
        creationDate = matcher.find() == true ? creationDate.substring(matcher.start()) : creationDate;
        
        
        Article article = new Article();
        article.setSource("https://www.unsere-zeit.de");
        article.setSourceName("unsere zeit");
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
