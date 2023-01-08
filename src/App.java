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
        //Start the logger
        final Logger logger = LogManager.getLogger(App.class.getName()); //TODO: logging points
        //logger.trace("Logger initialized, app started");
        //Get the pages to download

        String configFilePath = new String("config.properties");
        Properties props = new Properties();
        File propsFile = new File(configFilePath);
        try (InputStream is = new FileInputStream(propsFile)) {
            props.loadFromXML(is);
        } catch (Exception e) {
            //logger.fatal("Error while loading the 'config.properties' file, aborting program.",e);
            System.exit(1);
        }
        
        //save properties into some property-holder class?
        HashSet<ArticleSourceProperties> ArticleMatchers = new HashSet<ArticleSourceProperties>();
        for (Entry<Object, Object> property : props.entrySet()) {
            ArticleMatchers.add(new ArticleSourceProperties(property.getKey().toString(), property.getValue().toString()));
        }
        
        //iterate over properties to scrape proper things
        //preferably save data in some article-holder class
        HashSet<Article> articles = new HashSet<Article>();
        for (ArticleSourceProperties articleProps : ArticleMatchers) {
            Document document = Jsoup.connect(articleProps.getPageURL()).get();
            Elements articlesScraped = document.select(articleProps.getContainerMatcher());
            for (Element articleScraped : articlesScraped) {
                    Article ar = new Article(articleScraped.select(articleProps.getUrlMatcher()).first().attr("abs:href"),articleScraped.select(articleProps.getTitleMatcher()).first().text());
                    //TODO: handling empty entries
                    ar.articleAuthor = articleScraped.select(articleProps.getAuthorMatcher()).first().text();
                    ar.articleSubTitle = articleScraped.select(articleProps.getSubTitleMatcher()).first().text();
                    ar.articleDate = articleScraped.select(articleProps.getDateMatcher()).first().text();
                    Document article = Jsoup.connect(ar.articleURL).get();
                    ar.articleContent = article.select(articleProps.getArticleMatcher()).text(); //TODO: check if this is proper at all
                    articles.add(ar);
            }
        }
        
        
        
    }
}
