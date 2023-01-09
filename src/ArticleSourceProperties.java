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

    private String sourceName = null;

    public String getSourceName() {
        return sourceName;
    }

    public ArticleSourceProperties(String webpageURL, String matchersToDisassambly) {
        this.pageURL = webpageURL;
        
        String dissasembledMatchers[] = matchersToDisassambly.split(";");
        this.sourceName = dissasembledMatchers[0];
        this.containerMatcher = dissasembledMatchers[1];
        this.urlMatcher = dissasembledMatchers[2];
        this.titleMatcher = dissasembledMatchers[3]; // these three have to exist
        // these below don't need to
        if (dissasembledMatchers.length > 4) {
            if (!dissasembledMatchers[4].isBlank()) {
                this.subTitleMatcher = dissasembledMatchers[4];
            }
        }
        if (dissasembledMatchers.length > 5) {
            if (!dissasembledMatchers[5].isBlank()) {
                this.authorMatcher = dissasembledMatchers[5];
            }
        }
        if (dissasembledMatchers.length > 6) {
            if (!dissasembledMatchers[6].isBlank()) {
                this.dateMatcher = dissasembledMatchers[6];
            }
        }
        if (dissasembledMatchers.length > 7) {
            if (!dissasembledMatchers[7].isBlank()) {
                this.articleMatcher = dissasembledMatchers[7];
            }
        }
    }

}
