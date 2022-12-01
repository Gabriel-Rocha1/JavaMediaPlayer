package app.model;

import app.JavaMediaPlayer;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Playlist extends SongList {
	private User owner;
	private String name;

	public Playlist(User owner, String name) {
		super();
		this.owner = owner;
		this.name = name;
	}

	public Playlist(User owner, String name, ObservableList<Song> songs) {
		super();
		this.owner = owner;
		this.name = name;
		this.songs = songs;
	}
	
	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void write() throws IOException {
		File f = new File(JavaMediaPlayer.PLAYLISTS_DIRECTORY_PATH + "/" + JavaMediaPlayer.user.getUsername() + "/" + this.name + ".dat");
		FileOutputStream fos = new FileOutputStream(f);
		fos.write((this.name + "\n").getBytes());

		for (int i = 0; i < this.size(); i++)
			fos.write((this.at(i).getPath() + "\n").getBytes());

		fos.close();
	}

}
