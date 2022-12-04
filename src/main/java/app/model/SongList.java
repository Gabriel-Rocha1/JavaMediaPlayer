package app.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class SongList {
	/**
	 * Lista Observável contendo todas as músicas da SongList
	 */
	protected ObservableList<Song> songs;

	/**
	 * Construtor padrão da SongList.
	 */
	public SongList() {
		this.songs = FXCollections.observableArrayList();
	}

	/**
	 *
	 * @return lista de músicas
	 */
	public ObservableList<Song> list() {
		return this.songs;
	}

	/**
	 * Adiciona um objeto Song à lista.
	 * @param song objeto adicionado
	 */
	public void add(Song song) {
		this.songs.add(song);
	}

	/**
	 * Retorna o i-ésimo Song da lista.
	 * @param i índice do objeto
	 * @return objeto Song na posição i da lista
	 */
	public Song at(int i) {
		return this.songs.get(i);
	}

	/**
	 *
	 * @return número total de Songs na lista
	 */
	public int size() {
		return this.songs.size();
	}
}
