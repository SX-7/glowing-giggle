import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Map.Entry;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

final class ViewAbstractTable extends AbstractTableModel{
    //TODO: sourcing elements as HashTable<Article>, until Lucene is added
    private String[] columnNames = {"Source","Title","SubTitle","First Paragraph"};
    private ArrayList<Article> data = null;    


    public ViewAbstractTable(ArrayList<Article> data) {
        setData(data);
    }

    public void setData(ArrayList<Article> data) {
        this.data = data;
    }

    public String getUrl(int row){
        return this.data.get(row).articleURL;
    }

    public int getColumnCount() {
        return this.columnNames.length;
    }

    public int getRowCount() {
        return this.data.size();
    }

    public String getColumnName(int col) {
        return this.columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        Object retOb = null;
        switch (col) {
            case 0:
                retOb=this.data.get(row).articleSource;
                break;
            case 1:
                retOb=this.data.get(row).articleTitle;
                break;
            case 2:
                retOb=this.data.get(row).articleSubTitle;
                break;
            case 3:
                retOb=this.data.get(row).articleContent;
                break;
            default:
                break;
        }
        return retOb;
    }
}
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

    private static ArrayList<Article> getArticles(HashSet<ArticleSourceProperties> articleMatchers)
    {
        ArrayList<Article> articles = new ArrayList<Article>();
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
                //if this fails, it means that the article selector is wrong, or something similar. log an error, and move to the next one
                Article ar = null;
                try {
                    ar = new Article(articleScraped.select(articleProps.getUrlMatcher()).first().attr("abs:href"),articleScraped.select(articleProps.getTitleMatcher()).first().text());    
                } catch (Exception e) {
                    logger.error("Error while scraping basic data, likely following matchers not found. URL matcher is '"+articleProps.getUrlMatcher()+"', title matcher is '"+articleProps.getTitleMatcher()+"', article containter scraped is: \n"+articleScraped);
                    continue;
                }
                
                if (!articleProps.getSubTitleMatcher().isBlank()) {
                    if (articleScraped.select(articleProps.getSubTitleMatcher()).first() != null) {
                        ar.articleSubTitle = articleScraped.select(articleProps.getSubTitleMatcher()).first().text();
                    }
                }
                ar.articleSource = articleProps.getSourceName();
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

    //SO copied code https://stackoverflow.com/questions/10967451/open-a-link-in-browser-with-java-button
    public static boolean openWebpage(URI uri) throws Exception{
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        logger.trace("Desktop value is "+desktop);
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            desktop.browse(uri);
            logger.debug("Opened web browser with adress "+uri+", desktop "+desktop);
            return true;
        }
        logger.error("Failed to open web browser");
        return false;
    }
    
    public static boolean openWebpage(URL url) throws Exception {
        logger.trace("Converting "+url+" to URI");
        return openWebpage(url.toURI());
    }

    public static void createAndShowGUI() {
        // make a new window
        HashSet<ArticleSourceProperties> internalProperties = getProperties();

        JFrame jf = new JFrame("News Scraper");
        JPanel pane = new JPanel(new GridBagLayout());
        
        JTextField textField = new JTextField(20);
        textField.setToolTipText("Searches in Title and SubTitle fields");
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


        ViewAbstractTable dataTable = new ViewAbstractTable(getArticles(internalProperties));
        JTable table = new JTable(dataTable);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(400);
        table.getColumnModel().getColumn(2).setPreferredWidth(250);
        table.getColumnModel().getColumn(3).setPreferredWidth(250);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting()){
                    return;
                }
                try {
                    openWebpage(new URL(dataTable.getUrl(table.getSelectedRow())));
                } catch (Exception e) {
                    logger.error("Failed opening the web browser ",e);
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);
        GridBagConstraints sc = new GridBagConstraints();
        sc.gridy=1;
        sc.gridwidth=GridBagConstraints.REMAINDER;
        sc.weighty=1;
        sc.fill = GridBagConstraints.BOTH;
        sc.gridwidth=750;
        pane.add(scrollPane,sc);

        refreshButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                dataTable.setData(getArticles(internalProperties));
                dataTable.fireTableDataChanged();
            }
        });

        searchButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                
            }
        });

        jf.getContentPane().add(pane);
        // set properties
        jf.setMinimumSize(new Dimension(500,200));
        jf.setPreferredSize(new Dimension(1000,500));
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
