package app.control;

import app.JavaMediaPlayer;
import app.model.Song;
import app.model.SongDirectory;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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
	@FXML private Label bttnAddPlaylist;

	@FXML private ImageView imgAlbumCover;

	@FXML private ListView<?> listDirectory;
	@FXML private ListView<?> listPlaylist;

	@FXML private AnchorPane paneMenu;
	@FXML private AnchorPane paneExplorer;
	@FXML private AnchorPane paneMini;
	@FXML private AnchorPane panePlayer;

	@FXML private GridPane gridDirectory;

	@FXML private Pane paneDefault;
	@FXML private Pane paneDirectory;
	@FXML private Pane paneListDirectory;

	@FXML private TableView<?> songsTable;

	@FXML private TableColumn<Song, String> tablecolArtist;

	@FXML private TableColumn<Song, String> tablecolLength;

	@FXML private TableColumn<Song, String> tablecolTitle;

	private int currentGridX;
	private int currentGridY;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//TODO: inicializar o MainController
		this.tablecolTitle.setResizable(false);
		this.tablecolTitle.setReorderable(false);

		this.tablecolArtist.setResizable(false);
		this.tablecolArtist.setReorderable(false);

		this.tablecolLength.setResizable(false);
		this.tablecolLength.setReorderable(false);

		this.tablecolTitle.setCellValueFactory(new PropertyValueFactory<Song,String>("title"));
		this.tablecolArtist.setCellValueFactory(new PropertyValueFactory<Song,String>("artist"));
		this.tablecolLength.setCellValueFactory(new PropertyValueFactory<Song,String>("length"));

		this.tablecolArtist.setStyle("-fx-alignment: center");
		this.tablecolLength.setStyle("-fx-alignment: CENTER-RIGHT");

		this.showDefault();
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
		this.showListDirectory();
	}

	private void showDefault() {

		this.paneDefault.toFront();
	}

	private void showListDirectory() {

		this.updateGrid();
		this.paneListDirectory.toFront();
	}

	private void showDirectory(SongDirectory directory) {

		this.songsTable.setItems(directory.list());
		this.paneDirectory.toFront();
	}

	public void updateGrid() {
		for (SongDirectory directory : JavaMediaPlayer.directories) {
			Button bttnDir = new Button();
			bttnDir.setPrefSize(64, 64);
			bttnDir.setText("");
			bttnDir.getStyleClass().add("dir-button");
			bttnDir.setOnAction(e -> {
				this.showDirectory(directory);
			});

			Label lblDir = new Label();
			lblDir.setText(directory.getName());

			VBox vboxDir = new VBox();
			vboxDir.setAlignment(Pos.CENTER);
			vboxDir.getChildren().add(bttnDir);
			vboxDir.getChildren().add(lblDir);

			if (this.currentGridX == this.gridDirectory.getColumnCount()) {
				this.currentGridX = 0;
				this.currentGridY++;
			}

			this.gridDirectory.add(vboxDir, this.currentGridX, this.currentGridY);
			this.currentGridX++;
		}
	}
}
