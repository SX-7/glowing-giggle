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
        this.pageURL = webpageURL;
        String dissasembledMatchers[] = matchersToDisassambly.split(";");
        this.containerMatcher = dissasembledMatchers[0];
        this.urlMatcher = dissasembledMatchers[1];
        this.titleMatcher = dissasembledMatchers[2]; // these three have to exist
        // these below don't need to
        if (dissasembledMatchers.length > 3) {
            if (!dissasembledMatchers[3].isBlank()) {
                this.subTitleMatcher = dissasembledMatchers[3];
            }
        }
        if (dissasembledMatchers.length > 4) {
            if (!dissasembledMatchers[4].isBlank()) {
                this.authorMatcher = dissasembledMatchers[4];
            }
        }
        if (dissasembledMatchers.length > 5) {
            if (!dissasembledMatchers[5].isBlank()) {
                this.dateMatcher = dissasembledMatchers[5];
            }
        }
        if (dissasembledMatchers.length > 6) {
            if (!dissasembledMatchers[6].isBlank()) {
                this.articleMatcher = dissasembledMatchers[6];
            }
        }
    }

}
