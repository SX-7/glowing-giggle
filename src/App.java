import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
public class App {
    public static void main(String[] args) throws Exception {
        //first download webpage
        final Logger logger = LogManager.getLogger(App.class.getName());

        String configFilePath = new String("lab5/config.properties");
        Properties props = new Properties();
        // Step 1: test if a file already exists
        // Step 2T: if it does, load the file with loadfromxml
        // Step 2F: if it doesn't, create, load and store defaults
        // Step 3: simple read loop with scoring system
        try {
            File propsFile = new File(configFilePath);
            // Try to create file
            if (propsFile.createNewFile()) {
                // It didn't exist! we should try writing default properties to it
                props.setProperty("wartość_minimum", "1");
                props.setProperty("wartość_maximum", "10");
                props.setProperty("procent", "70");
                props.setProperty("powtórzeń_minimum", "10");
                props.setProperty("powtórzeń_maximum", "25");
                try (OutputStream os = new FileOutputStream(propsFile)) {
                    props.storeToXML(os, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }

            } else {
                try (InputStream is = new FileInputStream(propsFile)) {
                    props.loadFromXML(is);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            // Panic!
        }
        
        logger.debug("Logger initialized");
        Document document = Jsoup.connect("https://www.theatlantic.com/most-popular/").get();
        logger.debug("Webpage downloaded");
        Elements articles = document.select("article");
        for (Element article : articles) {
            
        }
        
        //then parse and print elements to see if it works
    }
}
