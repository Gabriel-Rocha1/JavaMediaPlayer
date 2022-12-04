package app.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import app.JavaMediaPlayer;
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
	private final String directoryPath;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDirectoryPath() {
		return directoryPath;
	}
	
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
	
	private void loadDirectory() throws IOException, CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException {
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

			title = tag.getFirst(FieldKey.TITLE);
			if (title.equals(""))
				title = file.getName();

			artist = tag.getFirst(FieldKey.ARTIST);
			if (artist.equals(""))
				artist = "Artista Desconhecido";

			length = audioHeader.getTrackLength();
			filePath = file.getPath();

			Song s = new Song(title, length, artist, filePath);
			this.add(s);
		}
	}
	
	private void writeDirectory() throws IOException {
		FileOutputStream fos = new FileOutputStream(JavaMediaPlayer.DIRECTORY_FILE_PATH, true);
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
