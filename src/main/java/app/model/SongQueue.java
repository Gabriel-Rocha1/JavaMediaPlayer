package app.model;

public class SongQueue extends SongList {
	/**
	 * representa a posição atual da Fila de Reprodução
	 */
	private int current;

	/**
	 * Construtor padrão da SongQueue.
	 */
	public SongQueue() {
		super();
		this.current = -1;
	}

	/**
	 * Atribui a posição atual ao início da fila.
	 * @return primeiro objeto Song da SongQueue
	 */
	public Song first() {
		this.current = 0;
		return this.songs.get(this.current);
	}

	/**
	 * Atribui a posição atual ao próximo objeto Song da fila.
	 * @return objeto Song na posição atual da SongQueue
	 */
	public Song next() {
		if (this.current < this.songs.size() - 1) {
			this.current++;
			return this.songs.get(this.current);
		} else
			return null;
	}

	/**
	 * Atribui a posição atual ao objeto Song anterior da fila.
	 * @return objeto Song na posição atual da SongQueue
	 */
	public Song previous() {
		if (this.current > 0) {
			this.current--;
			return this.songs.get(this.current);
		} else
			return null;
	}

	/**
	 * Atribui a posição atual ao último objeto Song da fila.
	 * @return último objeto Song da SongQueue
	 */
	public Song last() {
		this.current = this.songs.size() - 1;
		return this.songs.get(this.current);
	}
}