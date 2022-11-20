package app.model;

public class Song {
	private int id;
	
	private String title;
	private int length;
	private String artist;
	
	private String path;
	
	public Song(int id, String title, int length, String artist, String path) {
		this.id = id;
		this.title = title;
		this.length = length;
		this.artist = artist;
		this.path = path;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
