package app.rayscast.air.models;


public class ItemImage {
    private String path;
    private String mMimeType;

    public ItemImage() {
    }

    public ItemImage(String path, String mMimeType) {
        this.path = path;
        this.mMimeType = mMimeType;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getmMimeType() {
        return mMimeType;
    }

    public void setmMimeType(String mMimeType) {
        this.mMimeType = mMimeType;
    }

}
