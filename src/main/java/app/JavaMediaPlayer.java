package app;

import app.model.Playlist;
import app.model.SongDirectory;
import app.model.User;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class JavaMediaPlayer extends Application {
	/**
	 * objeto Scene principal da aplicação
	 */
	public static Scene scene;

	/**
	 * usuário atual
	 */
	public static User user;

	/**
	 * lista de diretórios carregados no sistema
	 */
	public static ArrayList<SongDirectory> directories;

	/**
	 * lista de playlists do usuário atual
	 */
	public static ArrayList<Playlist> playlists;

	/**
	 * caminho relativo para o arquivo de diretórios
	 */
	public static final String DIRECTORY_FILE_PATH = "data/directory.dat";

	/**
	 * caminho relativo para o diretório de playlists
	 */
	public static final String PLAYLISTS_DIRECTORY_PATH = "data/playlists";

	/**
	 * caminho relativo para o arquivo de contas do sistema
	 */
	public static final String ACCOUNTS_FILE_PATH = "data/accounts.dat";


	@Override
	public void start(Stage stage) throws IOException {
		JavaMediaPlayer.directories = new ArrayList<>();
		JavaMediaPlayer.playlists = new ArrayList<>();

		scene = new Scene(loadFXML("view/Login"));
		scene.getStylesheets().add(Objects.requireNonNull(JavaMediaPlayer.class.getResource("/css/application.css")).toExternalForm());
		stage.setTitle("Java Media Player");
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Muda o Root Node atual da Scene da aplicação.
	 * @param fxml caminho relativo para o arquivo FXML contendo a nova árvore de nós
	 * @throws IOException
	 */
	public static void setRoot(String fxml) throws IOException {
		scene.setRoot(loadFXML(fxml));
	}

	private static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(JavaMediaPlayer.class.getResource( fxml + ".fxml"));
		return fxmlLoader.load();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
