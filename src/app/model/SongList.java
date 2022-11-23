package app.model;

import java.util.ArrayList;
import java.util.Collections;

public abstract class SongList {
	protected ArrayList<Song> songs;
	
	public SongList() {
		this.songs = new ArrayList<Song>();
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
