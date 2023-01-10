public class ArticleSourceProperties {

    private String pageURL = "";

    public String getPageURL() {
        return pageURL;
    }

    private String containerMatcher = "";

    public String getContainerMatcher() {
        return containerMatcher;
    }

    private String urlMatcher = "";

    public String getUrlMatcher() {
        return urlMatcher;
    }

    private String titleMatcher = "";

    public String getTitleMatcher() {
        return titleMatcher;
    }

    private String subTitleMatcher = "";

    public String getSubTitleMatcher() {
        return subTitleMatcher;
    }

    private String articleMatcher = "";

    public String getArticleMatcher() {
        return articleMatcher;
    }

    private String sourceName = "";

    public String getSourceName() {
        return sourceName;
    }

    public ArticleSourceProperties(String webpageURL, String matchersToDisassambly) throws Exception {
        this.pageURL = webpageURL;
        
        // these three have to exist
        String dissasembledMatchers[] = matchersToDisassambly.split(";");
        if (dissasembledMatchers.length<4){
            throw new Exception();
        }
        this.sourceName = dissasembledMatchers[0];
        this.containerMatcher = dissasembledMatchers[1];
        this.urlMatcher = dissasembledMatchers[2];
        this.titleMatcher = dissasembledMatchers[3]; 
        // these below don't need to
        if (dissasembledMatchers.length > 4) {
            if (!dissasembledMatchers[4].isBlank()) {
                this.subTitleMatcher = dissasembledMatchers[4];
            }
        }
        if (dissasembledMatchers.length > 5) {
            if (!dissasembledMatchers[5].isBlank()) {
                this.articleMatcher = dissasembledMatchers[5];
            }
        }
    }

}
