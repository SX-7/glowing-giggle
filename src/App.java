import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Map.Entry;

public class App {
    public static void main(String[] args) throws Exception {

        // Start the logger
        final Logger logger = LogManager.getLogger(App.class.getName());
        logger.debug("Logger initialized, app started");
        
        // Get the pages to download
        String configFilePath = new String("config.properties");
        Properties props = new Properties();
        File propsFile = new File(configFilePath);
        try (InputStream is = new FileInputStream(propsFile)) {
            props.loadFromXML(is);
            logger.debug("Configs loaded from 'config.properties'");
        } catch (Exception e) {
            logger.fatal("Error while loading the 'config.properties' file, aborting program.",e);
            System.exit(1);
        }
        logger.info("Done interfacing with the properties file, proceeding to using the data");

        // save properties into some property-holder class?
        HashSet<ArticleSourceProperties> ArticleMatchers = new HashSet<ArticleSourceProperties>();
        for (Entry<Object, Object> property : props.entrySet()) {
            ArticleMatchers.add(new ArticleSourceProperties(property.getKey().toString(), property.getValue().toString()));
            logger.trace("Processed properties of "+property.getKey().toString());
        }
        logger.debug("Configs converted to ArticleSourceProperties objects");

        // iterate over properties to scrape proper things
        HashSet<Article> articles = new HashSet<Article>();
        for (ArticleSourceProperties articleProps : ArticleMatchers) {
            Document document = Jsoup.connect(articleProps.getPageURL()).get(); // TODO: Error handling
            logger.trace("Downloaded article data from "+articleProps.getPageURL());
            Elements articlesScraped = document.select(articleProps.getContainerMatcher());
            for (Element articleScraped : articlesScraped) {
                Article ar = new Article(articleScraped.select(articleProps.getUrlMatcher()).first().attr("abs:href"),articleScraped.select(articleProps.getTitleMatcher()).first().text());
                if (!articleProps.getAuthorMatcher().isBlank()) {
                    ar.articleAuthor = articleScraped.select(articleProps.getAuthorMatcher()).first().text();
                }
                if (!articleProps.getSubTitleMatcher().isBlank()) {
                    ar.articleSubTitle = articleScraped.select(articleProps.getSubTitleMatcher()).first().text();
                }
                if (!articleProps.getDateMatcher().isBlank()) {
                    ar.articleDate = articleScraped.select(articleProps.getDateMatcher()).first().text();
                }
                if (!articleProps.getArticleMatcher().isBlank()) {
                    Document article = Jsoup.connect(ar.articleURL).get(); //TODO: Error handling
                    ar.articleContent = article.select(articleProps.getArticleMatcher()).text();
                    logger.trace("Downloaded article itself from "+ar.articleURL);
                }
                articles.add(ar);
            }
        }
        logger.info("Articles data downloaded and stored");

    }
}
