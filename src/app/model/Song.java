package app.model;

import javafx.beans.property.SimpleStringProperty;

public class Song {
	private int id;
	
	private SimpleStringProperty title;
	private int length;
	private SimpleStringProperty artist;
	
	private String path;
	
	public Song(int id, String title, int length, String artist, String path) {
		this.id = id;
		this.title = new SimpleStringProperty(title);
		this.length = length;
		this.artist = new SimpleStringProperty(artist);
		this.path = path;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title.get();
	}
	
	public void setTitle(String title) {
		this.title = new SimpleStringProperty(title);
	}
	
	public String getLength() {
		int minutes = this.length / 60;
		int seconds = this.length % 60;
		return minutes + ":" + String.format("%02d", seconds);
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public String getArtist() {
		return artist.get();
	}
	
	public void setArtist(String artist) {
		this.artist = new SimpleStringProperty(artist);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
