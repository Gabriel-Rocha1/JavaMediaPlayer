package app.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;

public class SongDirectory extends SongList {

	private String directoryPath;

	private String name;
	private static final String DIRECTORY_FILE_PATH = "data/directory.dat";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public SongDirectory(String directoryPath, String name) {
		super();
		this.directoryPath = directoryPath;
		this.name = name;
		try {
			this.loadDirectory();
			this.writeDirectory();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void loadDirectory() throws IOException, CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException {
		//TODO: implementar o carregamento de um diretório no sistema.
		File directory = new File(directoryPath);
		File[] files = directory.listFiles();

		String title, artist, fileName, filePath;
		int length;

		AudioFile audioFile;
		AudioHeader audioHeader;
		Tag tag;


		for (File file : files) {
			fileName = file.toString();
			if (!isMP3(fileName))
				continue;

			audioFile = AudioFileIO.read(file);
			audioHeader = audioFile.getAudioHeader();
			tag = audioFile.getTag();

			//TODO: lidar com tags inexistentes
			title = tag.getFirst(FieldKey.TITLE);
			artist = tag.getFirst(FieldKey.ARTIST);
			length = audioHeader.getTrackLength();
			filePath = file.getPath();

			//TODO: implementar a identificação de músicas
			Song s = new Song(0, title, length, artist, filePath);
			this.add(s);
		}
	}
	
	private void writeDirectory() throws IOException {
		FileOutputStream fos = new FileOutputStream(DIRECTORY_FILE_PATH, true);
		fos.write((this.directoryPath + "\n").getBytes());
		fos.close();
	}

	private boolean isMP3(String fileName) {
		int index = fileName.lastIndexOf(".");
		if (index > 0) {
			String extension = fileName.substring(index + 1);
			return extension.equals("mp3");
		}
		return false;
	}
}
