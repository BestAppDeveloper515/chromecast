package app.rayscast.air.models;

public class ItemAudio {

    private String mArtist, mAlbum, mDuration, mPath, mMymeTipe;

    public ItemAudio() {
    }

    public ItemAudio(String path, String artist, String album, String duration, String mMymeTipe) {
        this.mPath = path;
        this.mArtist = artist;
        this.mAlbum = album;
        this.mDuration = duration;
        this.mMymeTipe = mMymeTipe;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        this.mArtist = artist;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        this.mAlbum = album;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        this.mDuration = duration;
    }

    public String getmMymeTipe() {
        return mMymeTipe;
    }

    public void setmMymeTipe(String mMymeTipe) {
        this.mMymeTipe = mMymeTipe;
    }

}
