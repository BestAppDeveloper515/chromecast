package app.rayscast.air.models;


public class ItemSong {
	public int audioID;
	public String audioName = "";
	public String artistName = "";
	public String audioUURL = "";
	public String audioArt = "";
	public String mime_type = "";
	public String audioAlbum = "";
	public String filePath = "";

	public ItemSong(int id, String nm, String artis, String url, String img_url, String mime_type, String audioAlbum, String filePath) {
		this.audioID = id;
		this.audioName = nm;
		this.artistName = artis;
		this.audioUURL = url;
		this.audioArt = img_url;
		this.mime_type = mime_type;
		this.audioAlbum = audioAlbum;
		this.filePath = filePath;
	}

	public String getSongName() {
		return this.audioName;
	}

	public String getSongArtistName() {
		return this.artistName;
	}

	public String getMime_type() {
		return mime_type;
	}

	public String getAudioAlbum() {
		return audioAlbum;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getSongUrl() {
		return this.audioUURL;
	}

	public String getImageUrl() {
		return this.audioArt;
	}

}
