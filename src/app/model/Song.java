package app.model;

import javafx.beans.property.SimpleStringProperty;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;

public class Song {
	private SimpleStringProperty title;
	private int length;
	private SimpleStringProperty artist;
	
	private final String path;
	
	public Song(String title, int length, String artist, String path) {
		this.title = new SimpleStringProperty(title);
		this.length = length;
		this.artist = new SimpleStringProperty(artist);
		this.path = path;
	}

	public Song(String path) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
		String title, artist;
		int length;

		AudioFile audioFile = AudioFileIO.read(new File(path));
		AudioHeader audioHeader = audioFile.getAudioHeader();
		Tag tag = audioFile.getTag();

		title = tag.getFirst(FieldKey.TITLE);
		if (title.equals(""))
			title = new File(path).getName();

		artist = tag.getFirst(FieldKey.ARTIST);
		if (artist.equals(""))
			artist = "Artista Desconhecido";

		length = audioHeader.getTrackLength();

		this.title = new SimpleStringProperty(title);
		this.length = length;
		this.artist = new SimpleStringProperty(artist);
		this.path = path;
	}
	
	public String getTitle() {
		return title.get();
	}

	public void setTitle(String title) {
		this.title = new SimpleStringProperty(title);
	}

	public String getArtist() {
		return artist.get();
	}

	public void setArtist(String artist) {
		this.artist = new SimpleStringProperty(artist);
	}
	
	public String getLength() {
		int minutes = this.length / 60;
		int seconds = this.length % 60;
		return minutes + ":" + String.format("%02d", seconds);
	}
	
	public void setLength(int length) {
		this.length = length;
	}

	public String getURI() {
		return new File(this.path).toURI().toString();
	}

	public String getPath() {
		return this.path;
	}
}
