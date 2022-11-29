package app.control;

import app.JavaMediaPlayer;
import app.model.Song;
import app.model.SongDirectory;

import app.model.SongList;
import app.model.SongQueue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
	@FXML private Label labelArtist;
	@FXML private Label labelTitle;

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

	@FXML private AnchorPane paneDefault;
	@FXML private AnchorPane paneDirectory;
	@FXML private ScrollPane paneListDirectory;
	@FXML private AnchorPane paneSongSelection;

	@FXML private TableView<Song> tableSongs;

	@FXML private TableColumn<Song, String> tablecolArtist;

	@FXML private TableColumn<Song, String> tablecolLength;

	@FXML private TableColumn<Song, String> tablecolTitle;

	@FXML private TableView<Song> songSelectionTable;

	@FXML private TableColumn<Song, String> tablecolSelectionArtist;
	@FXML private TableColumn<Song, String> tablecolSelectionLength;
	@FXML private TableColumn<Song, String> tablecolSelectionTitle;

	private MediaPlayer player;
	private SongQueue queue;

	private int currentGridX;
	private int currentGridY;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//TODO: inicializar o MainController
		this.paneListDirectory.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
		this.paneListDirectory.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);

		this.tablecolTitle.setReorderable(false);
		this.tablecolArtist.setReorderable(false);
		this.tablecolLength.setReorderable(false);

		this.tablecolSelectionTitle.setReorderable(false);
		this.tablecolSelectionArtist.setReorderable(false);
		this.tablecolSelectionLength.setReorderable(false);

		this.tablecolTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
		this.tablecolArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
		this.tablecolLength.setCellValueFactory(new PropertyValueFactory<>("length"));

		this.tablecolArtist.setStyle("-fx-alignment: center");
		this.tablecolLength.setStyle("-fx-alignment: center");

		Region icon = new Region();
		icon.getStyleClass().add("icon-clock");

		final int SIZE = 12;

		icon.setMinSize(SIZE, SIZE);
		icon.setPrefSize(SIZE, SIZE);
		icon.setMaxSize(SIZE, SIZE);

		this.tablecolLength.setGraphic(icon);

		try {
			this.loadDirectories();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		this.listDirectory.setCellFactory(new Callback<>() {
			@Override
			public ListCell<SongDirectory> call(ListView<SongDirectory> param) {
				return new ListCell<>() {
					@Override
					protected void updateItem(SongDirectory item, boolean empty) {
						super.updateItem(item, empty);
						if (item != null)
							setText(item.getName());
						else
							setText("");
					}
				};
			}
		});

		this.listDirectory.getSelectionModel().selectedItemProperty().addListener((observableValue, directory, t1) -> {
			SongDirectory dir = listDirectory.getSelectionModel().getSelectedItem();
			showSongList(dir);
		});

		this.tableSongs.setRowFactory( tv -> {
			TableRow<Song> row = new TableRow<>();
			row.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2 && (! row.isEmpty()) ) {
					if (this.queue != null)
							this.queue = null;

					int first = this.tableSongs.getSelectionModel().getSelectedIndex();
					int last = this.tableSongs.getItems().size();

					for (int i = first; i < last; i++)
						this.addToQueue((Song) this.tableSongs.getItems().get(i));

					this.play();
				}
			});
			return row;
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
	public void chooseDirectory() {
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
		icon.getStyleClass().add("icon-directory");

		final int SIZE = 64;

		icon.setMinSize(SIZE, SIZE);
		icon.setPrefSize(SIZE, SIZE);
		icon.setMaxSize(SIZE, SIZE);
		icon.setOnMouseClicked(e -> {
			if (this.listDirectory.getSelectionModel().getSelectedItem() == null)
				this.showSongList(directory);
			else if (this.listDirectory.getSelectionModel().getSelectedItem().equals(directory))
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
		this.tableSongs.setItems(songList.list());
		this.paneDirectory.toFront();
	}

	@FXML
	public void addToQueue(Song s) {
		if (this.queue == null)
			this.queue = new SongQueue();

		this.queue.add(s);
	}

	private void play() {
		if (player != null)
			player.dispose();

		Song currentSong = this.queue.next();
		if (currentSong == null) {
			currentSong = this.queue.first();
		}

		player = new MediaPlayer(new Media(currentSong.getPath()));
		player.setOnEndOfMedia(this::play);
		player.play();

		this.labelTitle.setText(currentSong.getTitle());
		this.labelArtist.setText(currentSong.getArtist());
	}

	@FXML
	public void playNext() {
		if (this.queue == null)
			return;

		if (player != null)
			player.dispose();

		Song currentSong = this.queue.next();
		if (currentSong == null) {
			currentSong = this.queue.first();
		}

		player = new MediaPlayer(new Media(currentSong.getPath()));
		player.setOnEndOfMedia(this::play);
		player.play();

		this.labelTitle.setText(currentSong.getTitle());
		this.labelArtist.setText(currentSong.getArtist());
	}

	@FXML
	public void playPrevious(MouseEvent e) {
		if (this.queue == null)
			return;

		if (e.getClickCount() == 2) {
			if (player != null)
				player.dispose();

			Song currentSong = this.queue.previous();
			if (currentSong == null)
				currentSong = this.queue.last();

			player = new MediaPlayer(new Media(currentSong.getPath()));
			player.setOnEndOfMedia(this::play);
			player.play();

			this.labelTitle.setText(currentSong.getTitle());
			this.labelArtist.setText(currentSong.getArtist());
		}
	}

	@FXML
	public void pauseOrResume() {
		if (this.player == null)
			return;

		if (this.player.getStatus() == MediaPlayer.Status.PLAYING) {
			this.player.pause();
			//TODO: mudar o ícone do botão para Play
		}

		if (this.player.getStatus() == MediaPlayer.Status.PAUSED) {
			this.player.play();
			//TODO: mudar o ícone do botão para Pause
		}
	}

	@FXML
	public void reset(ActionEvent e) {
		if (this.player == null)
			return;

		this.player.seek(this.player.getStartTime());
	}
}
