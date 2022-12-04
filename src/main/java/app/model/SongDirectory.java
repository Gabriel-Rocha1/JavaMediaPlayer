package app.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import app.JavaMediaPlayer;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

public class SongDirectory extends SongList implements ParseAudioFile {
	/**
	 * caminho absoluto do diretório no sistema
	 */
	private final String directoryPath;

	/**
	 * nome do diretório
	 */
	private final String name;

	/**
	 *
	 * @return nome do diretório
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @return caminho absoluto do diretório
	 */
	public String getDirectoryPath() {
		return directoryPath;
	}

	/**
	 * Construtor padrão do SongDirectory.
	 * @param directoryPath caminho absoluto do diretório
	 * @param name nome do diretório
	 * @param reloading indica se o diretório está sendo recarregado ou não. Caso não esteja, seu
	 *                  caminho absoluto será escrito no arquivo directory.dat
	 */
	public SongDirectory(String directoryPath, String name, boolean reloading) {
		super();
		this.directoryPath = directoryPath;
		this.name = name;
		try {
			this.loadDirectory();
			if (!reloading)
				this.writeDirectory();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Carrega as músicas presentes no diretório, cria objetos Song nomeados com base nas Etiquetas do arquivo MP3
	 * e as adiciona na Lista Observável de músicas.
	 * @throws IOException
	 * @throws CannotReadException
	 * @throws TagException
	 * @throws InvalidAudioFrameException
	 * @throws ReadOnlyFileException
	 */
	private void loadDirectory() throws IOException, CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException {
		File directory = new File(directoryPath);
		File[] files = directory.listFiles();

		assert files != null;
		for (File file : files) {
			if (!isMP3(file))
				continue;

			Song s = new Song(getTitle(file), getArtist(file), getLength(file),  file.getPath());
			this.add(s);
		}
	}

	/**
	 * Escreve o caminho absoluto do diretório no arquivo directory.dat.
	 * @throws IOException
	 */
	private void writeDirectory() throws IOException {
		FileOutputStream fos = new FileOutputStream(JavaMediaPlayer.DIRECTORY_FILE_PATH, true);
		fos.write((this.directoryPath + "\n").getBytes());
		fos.close();
	}
}