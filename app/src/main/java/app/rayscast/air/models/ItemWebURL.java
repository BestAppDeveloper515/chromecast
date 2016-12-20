package app.rayscast.air.models;

/**
 * Created by Qing on 8/22/2016.
 */
public class ItemWebURL {
    private String contentURL, contentName;

    public ItemWebURL() {
        contentName = "";
        contentURL = "";
    }

    public String getContentURL() {
        return contentURL;
    }

    public void setContentURL(String contentURL) {
        this.contentURL = contentURL;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }
}
