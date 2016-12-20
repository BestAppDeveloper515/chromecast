package app.rayscast.air.models;

public class ItemAlbum {

	
	int albumID;
	String albumName;
	String albumArtist;
	String albumArt;
	int noOfSongs;
	public ItemAlbum(int albumID, String albumName, String albumArtist,
					 String albumArt, int noOfSongs) {
		super();
		this.albumID = albumID;
		this.albumName = albumName;
		this.albumArtist = albumArtist;
		this.albumArt = albumArt;
		this.noOfSongs = noOfSongs;
	}
	public int getAlbumID() {
		return albumID;
	}
	public void setAlbumID(int albumID) {
		this.albumID = albumID;
	}
	public String getAlbumName() {
		return albumName;
	}
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}
	public String getAlbumArtist() {
		return albumArtist;
	}
	public void setAlbumArtist(String albumArtist) {
		this.albumArtist = albumArtist;
	}
	public String getAlbumArt() {
		return albumArt;
	}
	public void setAlbumArt(String albumArt) {
		this.albumArt = albumArt;
	}
	public int getNoOfSongs() {
		return noOfSongs;
	}
	public void setNoOfSongs(int noOfSongs) {
		this.noOfSongs = noOfSongs;
	}
	
}
