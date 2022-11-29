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

public class JavaMediaPlayer extends Application {
	public static Scene scene;
	public static User user;
	public static ArrayList<SongDirectory> directories;
	public static ArrayList<Playlist> playlists;

	public static final String DIRECTORY_FILE_PATH = "data/directory.dat";
	public static final String ACCOUNTS_FILE_PATH = "data/accounts.dat";


	@Override
	public void start(Stage stage) throws IOException {
		JavaMediaPlayer.directories = new ArrayList<>();
		JavaMediaPlayer.playlists = new ArrayList<>();

		scene = new Scene(loadFXML("view/Login"));
		scene.getStylesheets().add(JavaMediaPlayer.class.getResource("/css/application.css").toExternalForm());
		stage.setTitle("Java Media Player");
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
	}

	public static void setRoot(String fxml) throws IOException {
		scene.setRoot(loadFXML(fxml));
	}

	public static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(JavaMediaPlayer.class.getResource( fxml + ".fxml"));
		return fxmlLoader.load();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
