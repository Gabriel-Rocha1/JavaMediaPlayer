package app.control;

import app.JavaMediaPlayer;
import app.model.Song;
import app.model.SongDirectory;

import app.model.SongList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
	@FXML private Label labelDirectory;
	@FXML private Button bttnBackward;
	@FXML private Button bttnForward;
	@FXML private Button bttnPlay;
	@FXML private Label bttnAddDirectory;
	@FXML private Label bttnAddPlaylist;

	@FXML private ImageView imgAlbumCover;

	@FXML private ListView<SongDirectory> listDirectory;
	@FXML private ListView<String> listPlaylist;

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

		try {
			this.loadDirectories();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		this.listDirectory.setCellFactory(new Callback<>() {
			@Override
			public ListCell<SongDirectory> call(ListView<SongDirectory> param) {
				ListCell<SongDirectory> cell = new ListCell<SongDirectory>() {
					@Override
					protected void updateItem(SongDirectory item, boolean empty) {
						super.updateItem(item, empty);
						if (item != null)
							setText(item.getName());
						else
							setText("");
					}
				};
				return cell;
			}
		});

		this.listDirectory.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SongDirectory>() {
			@Override
			public void changed(ObservableValue<? extends SongDirectory> observableValue, SongDirectory directory, SongDirectory t1) {
				SongDirectory dir = listDirectory.getSelectionModel().getSelectedItem();
				showSongList(dir);
			}
		});



		this.showDefault();
	}

	private void loadDirectories() throws IOException {
		File f = new File(JavaMediaPlayer.DIRECTORY_FILE_PATH);
		if (f.exists()) {
			BufferedReader reader = new BufferedReader(new FileReader(JavaMediaPlayer.DIRECTORY_FILE_PATH));
			String currentLine = reader.readLine();

			File songDir;

			while (currentLine != null) {
				songDir = new File(currentLine);
				this.addDirectory(currentLine, songDir.getName(), true);

				currentLine = reader.readLine();
			}
			reader.close();
		}
	}


	@FXML
	public void chooseDirectory() throws IOException {
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("Escolha um diretório");
		File f = dc.showDialog(JavaMediaPlayer.scene.getWindow());
		String path = f.getAbsolutePath();

		this.addDirectory(path, f.getName(), false);
	}

	private void addDirectory(String path, String name, boolean reloading) {
		for (SongDirectory dir : JavaMediaPlayer.directories)
			if (dir.getDirectoryPath().equals(path)) {
				//TODO: alertar que o diretório já existe

				return;
			}

		SongDirectory directory = new SongDirectory(path, name, reloading);
		JavaMediaPlayer.directories.add(directory);

		Region icon = new Region();
		icon.getStyleClass().add("dir-button");

		final int SIZE = 64;

		icon.setMinSize(SIZE, SIZE);
		icon.setPrefSize(SIZE, SIZE);
		icon.setMaxSize(SIZE, SIZE);
		icon.setOnMouseClicked(e -> {
			if (this.listDirectory.getSelectionModel().getSelectedItem().equals(directory))
				this.showSongList(directory);
			else
				this.listDirectory.getSelectionModel().select(directory);
		});

		Label title = new Label();
		title.setText(directory.getName());

		VBox vboxDir = new VBox();
		vboxDir.setAlignment(Pos.CENTER);
		vboxDir.getChildren().add(icon);
		vboxDir.getChildren().add(title);

		if (this.currentGridX == this.gridDirectory.getColumnCount()) {
			this.currentGridX = 0;
			this.currentGridY++;
		}

		this.gridDirectory.add(vboxDir, this.currentGridX, this.currentGridY);
		this.currentGridX++;

		this.listDirectory.getItems().add(directory);

		this.showListDirectory();
	}

	@FXML
	public void showDefault(MouseEvent e) {
		this.showDefault();
	}

	private void showDefault() {

		this.paneDefault.toFront();
	}

	@FXML
	public void showListDirectory(MouseEvent e) {
		this.showListDirectory();
	}

	private void showListDirectory() {

		this.paneListDirectory.toFront();
	}

	private void showSongList(SongList songList) {

		this.songsTable.setItems(songList.list());
		this.paneDirectory.toFront();
	}
}
