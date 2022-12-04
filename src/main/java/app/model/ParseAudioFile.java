package app.model;

import javafx.beans.property.SimpleStringProperty;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;

public interface ParseAudioFile {
    /**
     * Analisa um arquivo MP3 e retorna a Etiqueta que representa o título da música.
     * Caso a Etiqueta esteja vazia, retorna o nome do arquivo.
     * @param file arquivo da música
     * @return título da música ou nome do arquivo
     * @throws CannotReadException
     * @throws TagException
     * @throws InvalidAudioFrameException
     * @throws ReadOnlyFileException
     * @throws IOException
     */
    default SimpleStringProperty getTitle(File file) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        Tag tag = AudioFileIO.read(file).getTag();

        String title = tag.getFirst(FieldKey.TITLE);
        if (title.equals(""))
            title = file.getName();

        return new SimpleStringProperty(title);
    }

    /**
     * Analisa um arquivo MP3 e retorna a Etiqueta que representa o autor da música.
     * Caso a Etiqueta esteja vazia, retorna 'Artista Desconhecido'.
     * @param file arquivo da música
     * @return autor da música ou 'Artista Desconhecido'
     * @throws CannotReadException
     * @throws TagException
     * @throws InvalidAudioFrameException
     * @throws ReadOnlyFileException
     * @throws IOException
     */
    default SimpleStringProperty getArtist(File file) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        Tag tag = AudioFileIO.read(file).getTag();

        String artist = tag.getFirst(FieldKey.ARTIST);
        if (artist.equals(""))
            artist = "Artista Desconhecido";

        return new SimpleStringProperty(artist);
    }

    /**
     * Analisa um arquivo MP3 e retorna a duração da faixa em segundos.
     * @param file arquivo da música
     * @return comprimento da música, em segundos
     * @throws CannotReadException
     * @throws TagException
     * @throws InvalidAudioFrameException
     * @throws ReadOnlyFileException
     * @throws IOException
     */
    default int getLength(File file) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        AudioFile audioFile = AudioFileIO.read(file);
        AudioHeader audioHeader = audioFile.getAudioHeader();
        return audioHeader.getTrackLength();
    }

    /**
     * Verifica se um arquivo possui extensão .MP3 ou não.
     * @param file arquivo a ser checado
     * @return true caso o arquivo possua extensão .MP3, falso caso contrário.
     */
    default boolean isMP3(File file) {
        int index = file.getName().lastIndexOf(".");
        if (index > 0) {
            return file.getName().substring(index + 1).equals("mp3");
        } else
            return false;
    }
}