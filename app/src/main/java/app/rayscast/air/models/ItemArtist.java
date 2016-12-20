package app.rayscast.air.models;

public class ItemArtist {
	
	int artistID;
	String artistName;
	int noOFAlbums;
	String artistArt;
	public ItemArtist(int artistID, String artistName, int noOFAlbums, String artistArt) {
		super();
		this.artistID = artistID;
		this.artistName = artistName;
		this.noOFAlbums = noOFAlbums;
		this.artistArt=artistArt;
	}
	public String getArtistArt() {
		return artistArt;
	}
	public void setArtistArt(String artistArt) {
		this.artistArt = artistArt;
	}
	public ItemArtist()
	{
		
	}
	public int getArtistID() {
		return artistID;
	}
	public void setArtistID(int artistID) {
		this.artistID = artistID;
	}
	public String getArtistName() {
		return artistName;
	}
	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}
	public int getNoOFAlbums() {
		return noOFAlbums;
	}
	public void setNoOFAlbums(int noOFAlbums) {
		this.noOFAlbums = noOFAlbums;
	}

}
