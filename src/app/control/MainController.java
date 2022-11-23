package app.control;

import app.JavaMediaPlayer;
import app.model.SongDirectory;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
	@FXML private Button bttnBackward;
	@FXML private Button bttnForward;
	@FXML private Button bttnPlay;
	@FXML private Label bttnAddDirectory;

	@FXML private ImageView imgAlbumCover;

	@FXML private ListView<?> listDirectory;
	@FXML private ListView<?> listPlaylist;

	@FXML private AnchorPane menuPane;
	@FXML private AnchorPane mainPane;
	@FXML private AnchorPane explorerPane;
	@FXML private AnchorPane miniPane;
	@FXML private AnchorPane playerPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//TODO: inicializar o MainController
	}

	@FXML
	public void changeMainPane(MouseEvent e) throws IOException {
		String newPane = ""; //TODO: criar Pane default
		if (e.getSource().equals(listDirectory))
			newPane = "DirectoryList";
		if (e.getSource().equals(listPlaylist))
			newPane = "PlaylistList";

		mainPane.getChildren().setAll(JavaMediaPlayer.loadFXML(newPane));
	}

	@FXML
	public void changeMainPane(String fxml) throws IOException {
		mainPane.getChildren().setAll(JavaMediaPlayer.loadFXML(fxml));
	}

	@FXML
	public void chooseDirectory() throws IOException {
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("Escolha um diretório");
		File f = dc.showDialog(JavaMediaPlayer.scene.getWindow());

		if (f != null) {
			this.addDirectory(f.getAbsolutePath(), f.getName());
		} else {
			//TODO: lidar com diretório nulo
		}
	}

	private void addDirectory(String path, String name) throws IOException {
		SongDirectory dir = new SongDirectory(path, name);
		JavaMediaPlayer.directories.add(dir);
		changeMainPane("view/DirectoryList");
	}
}
