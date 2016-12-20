package app.rayscast.air.models;

/**
 * Created by darshanz on 8/29/16.
 */
public class HomeMenuItem {
    public final static int VIDEO = 0;
    public final static int AUDIO = 1;
    public final static int GALLERY = 2;
    public final static int WEB = 3;
    public final static int DROPBOX = 4;
    public final static int ADD_LINK = 5;

    private int imageResource;
    private String label;
    private int type;

    public HomeMenuItem(int imageResource, String label, int type) {
        this.imageResource = imageResource;
        this.label = label;
        this.type = type;
    }

    public int getImageResource() {
        return imageResource;
    }


    public String getLabel() {
        return label;
    }


    public int getType() {
        return type;
    }


}
