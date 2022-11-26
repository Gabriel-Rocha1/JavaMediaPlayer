package app.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;

public abstract class SongList {
	protected ObservableList<Song> songs;
	
	public SongList() {
		this.songs = FXCollections.observableArrayList();
	}

	public ObservableList list() {
		return this.songs;
	}
	
	public void add(Song song) {
		this.songs.add(song);
	}

	public void remove(Song song) {
		this.songs.remove(song);
	}
	
	public void randomize() {
		Collections.shuffle(this.songs);
	}
}
