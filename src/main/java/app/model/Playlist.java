package app.model;

import app.JavaMediaPlayer;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Playlist extends SongList {
	/**
	 * nome da playlist
	 */
	private final String name;

	/**
	 * Construtor padrão da classe Playlist, sem músicas.
	 * @param name nome da playlist
	 */
	public Playlist(String name) {
		super();
		this.name = name;
	}

	/**
	 * Construtor alternativo da classe Playlist que adiciona todas as músicas de uma vez,
	 * utilizado no recarregamento de playlists no sistema.
	 * @param name nome da playlist
	 * @param songs lista de músicas
	 */
	public Playlist(String name, ObservableList<Song> songs) {
		super();
		this.name = name;
		this.songs = songs;
	}

	/**
	 *
	 * @return nome da playlist
	 */
	public String getName() {
		return name;
	}

	/**
	 * Escreve a playlist no sistema. Na primeira linha é escrito o nome da playlist, as demais linhas contém os
	 * caminhos absolutos de cada música da playlist, um por linha.
	 * @throws IOException
	 */
	public void write() throws IOException {
		File f = new File(JavaMediaPlayer.PLAYLISTS_DIRECTORY_PATH + "/" + JavaMediaPlayer.user.getUsername() + "/" + this.name + ".dat");
		FileOutputStream fos = new FileOutputStream(f);
		fos.write((this.name + "\n").getBytes());

		for (int i = 0; i < this.size(); i++)
			fos.write((this.at(i).getPath() + "\n").getBytes());

		fos.close();
	}
}
