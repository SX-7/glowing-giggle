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

import javax.swing.*;
import java.awt.*;
public class App {
    final static Logger logger = LogManager.getLogger(App.class.getName());

    private static HashSet<ArticleSourceProperties> getProperties() {
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
            try { 
                ArticleMatchers.add(new ArticleSourceProperties(property.getKey().toString(), property.getValue().toString()));
                logger.trace("Processed properties of "+property.getKey().toString());
            } catch (Exception e) {
                logger.error("Wrong formatting of properties for key "+property.getKey().toString());
            }
        }
        logger.debug("Configs converted to ArticleSourceProperties objects");
        return ArticleMatchers;
    }

    private static HashSet<Article> getArticles(HashSet<ArticleSourceProperties> articleMatchers)
    {
        HashSet<Article> articles = new HashSet<Article>();
        for (ArticleSourceProperties articleProps : articleMatchers) {
            Elements articlesScraped = null;
            try {
                Document document = Jsoup.connect(articleProps.getPageURL()).get(); 
                logger.trace("Downloaded article data from "+articleProps.getPageURL());
                articlesScraped = document.select(articleProps.getContainerMatcher());   
            } catch (Exception e) {
                logger.error("Failed connecting to "+articleProps.getPageURL()+", skipping.",e);
                continue;
            }
            // if we have the data, we can process it
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
                    try {
                        Document article = Jsoup.connect(ar.articleURL).get();
                        logger.trace("Downloaded article itself from "+ar.articleURL);
                        ar.articleContent = article.select(articleProps.getArticleMatcher()).text();
                    } catch (Exception e) {
                        logger.error("Failed connecting to "+ar.articleURL+", assigning null content",e);
                        ar.articleContent=null;
                    }
                }
                articles.add(ar);
            }
        }
        logger.info("Articles data downloaded and stored");
        return articles;
    }

    public static void createAndShowGUI() {
        // make a new window
        JFrame jf = new JFrame("News Scraper");
        JPanel pane = new JPanel(new GridBagLayout());
        
        JTextField textField = new JTextField(20);
        GridBagConstraints tfc = new GridBagConstraints();
        tfc.fill = GridBagConstraints.HORIZONTAL;
        tfc.weightx = 1;
        tfc.gridx=0;
        tfc.gridy=0;
        pane.add(textField,tfc);

        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");
        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx=1;
        bc.gridy=0;
        pane.add(searchButton,bc);
        bc.gridx=2;
        pane.add(refreshButton,bc);

        jf.getContentPane().add(pane);
        // set properties
        jf.setMinimumSize(new Dimension(500,200));
        jf.pack();
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public static void main(String[] args) throws Exception {
        logger.debug("App started");
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
        // Get the pages to download
        //HashSet<ArticleSourceProperties> articleMatcherGuidelines = getProperties();
        
        //HashSet<Article> articles = getArticles(articleMatcherGuidelines);
        // iterate over properties to scrape proper things
        
        

    }
}
