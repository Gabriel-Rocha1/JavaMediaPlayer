package app.model;

import javafx.beans.property.SimpleStringProperty;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;

public class Song implements ParseAudioFile {
	/**
	 * título da música
	 */
	private final SimpleStringProperty title;

	/**
	 * autor(es) da música
	 */
	private final SimpleStringProperty artist;

	/**
	 * duração da música em segundos
	 */
	private final int length;

	/**
	 * caminho absoluto do arquivo da música
	 */
	private final String path;

	/**
	 * Construtor padrão da classe Song.
	 * @param title título da música
	 * @param artist autor(es) da música
	 * @param length comprimento da música, em segundos
	 * @param path caminho absoluto do arquivo da música
	 */
	public Song(SimpleStringProperty title, SimpleStringProperty artist, int length,  String path) {
		this.title = title;
		this.artist = artist;
		this.length = length;
		this.path = path;
	}

	/**
	 * Construtor alternativo da classe Song a partir de um caminho absoluto
	 * utilizado no carregamento de playlists já existentes, escritas no sistema
	 * como um conjunto de caminhos absolutos de arquivos de música
	 * @param path caminho absoluto do arquivo de música
	 * @throws CannotReadException
	 * @throws TagException
	 * @throws InvalidAudioFrameException
	 * @throws ReadOnlyFileException
	 * @throws IOException
	 */
	public Song(String path) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
		File f = new File(path);

		this.title = getTitle(f);
		this.artist = getArtist(f);
		this.length = getLength(f);
		this.path = path;
	}

	/**
	 *
	 * @return título da música
	 */
	public String getTitle() {
		return title.get();
	}

	/**
	 *
	 * @return autor(es) da música
	 */
	public String getArtist() {
		return artist.get();
	}

	/**
	 *
	 * @return duração da música, em minutos e segundos
	 */
	public String getLength() {
		int minutes = this.length / 60;
		int seconds = this.length % 60;
		return minutes + ":" + String.format("%02d", seconds);
	}

	/**
	 *
	 * @return URI da música, utilizado no MediaPlayer
	 */
	public String getURI() {
		return new File(this.path).toURI().toString();
	}

	/**
	 *
	 * @return caminho absoluto da música, utilizado na escrita de playlists no sistema
	 */
	public String getPath() {
		return this.path;
	}
}
