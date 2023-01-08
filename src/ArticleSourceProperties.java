public class ArticleSourceProperties {

    private String pageURL = null;
    public String getPageURL() {
        return pageURL;
    }

    private String containerMatcher = null;
    public String getContainerMatcher() {
        return containerMatcher;
    }

    private String urlMatcher = null;
    public String getUrlMatcher() {
        return urlMatcher;
    }

    private String titleMatcher = null;
    public String getTitleMatcher() {
        return titleMatcher;
    }

    private String subTitleMatcher = null;
    public String getSubTitleMatcher() {
        return subTitleMatcher;
    }

    private String authorMatcher = null;
    public String getAuthorMatcher() {
        return authorMatcher;
    }

    private String dateMatcher = null;
    public String getDateMatcher() {
        return dateMatcher;
    }

    private String articleMatcher = null;
    public String getArticleMatcher() {
        return articleMatcher;
    }

    public ArticleSourceProperties(String webpageURL, String matchersToDisassambly) {
        this.pageURL=webpageURL;
        //TODO: splitting of the matchers, error handling - or even exception throwing + escalation
    }
    
}
