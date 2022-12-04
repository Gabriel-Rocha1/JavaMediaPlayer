package app.control;

import app.JavaMediaPlayer;
import app.model.*;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

	@FXML private TextField txtNamePlaylist;

	@FXML private Label labelArtist;
	@FXML private Label labelTitle;
	@FXML private Label labelName;

	@FXML private Label labelDirectory;
	@FXML private Button bttnBackward;
	@FXML private Button bttnForward;
	@FXML private Button bttnPlay;
	@FXML private Label bttnAddDirectory;
	@FXML private Label bttnAddPlaylist;
	@FXML private Button bttnNamePlaylist;
	@FXML private Region bttnMute;

	@FXML private ListView<SongDirectory> listDirectory;
	@FXML private ListView<Playlist> listPlaylist;
	@FXML private AnchorPane paneMenu;
	@FXML private AnchorPane paneDirectories;
	@FXML private AnchorPane panePlaylists;
	@FXML private AnchorPane panePlayer;

	@FXML private GridPane gridDirectory;
	@FXML private GridPane gridPlaylist;

	@FXML private AnchorPane paneDefault;
	@FXML private AnchorPane paneDirectory;
	@FXML private ScrollPane paneListDirectory;
	@FXML private ScrollPane paneListPlaylist;
	@FXML private AnchorPane paneNamePlaylist;
	@FXML private AnchorPane paneSongSelection;

	@FXML private TableView<Song> tableSongs;

	@FXML private TableColumn<Song, String> tablecolArtist;

	@FXML private TableColumn<Song, String> tablecolLength;

	@FXML private TableColumn<Song, String> tablecolTitle;

	@FXML private TableView<Song> tableSongSelection;

	@FXML private TableColumn<Song, String> tablecolSelectionArtist;
	@FXML private TableColumn<Song, String> tablecolSelectionLength;
	@FXML private TableColumn<Song, String> tablecolSelectionTitle;

	@FXML private Dialog<String> dialog;
	@FXML private ButtonType type;

	@FXML private MenuButton menuProfile;

	@FXML private MenuItem menuItemLogout;

	@FXML private Slider sliderVolume;

	private MediaPlayer player;
	private SongQueue queue;

	private int currentDirGridX;
	private int currentDirGridY;

	private int currentPlayGridX;
	private int currentPlayGridY;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		panePlayer.getStyleClass().add("bottom-rounded");

		paneDirectories.getStyleClass().add("all-rounded");
		panePlaylists.getStyleClass().add("all-rounded");

		panePlayer.getStyleClass().add("player-theme");

		dialog = new Dialog<>();
		dialog.setTitle("Erro");
		type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(type);

		labelName.setText("Olá, " + JavaMediaPlayer.user.getName());
		labelName.setAlignment(Pos.CENTER_RIGHT);
		menuItemLogout.setOnAction(e -> {
			try {
				logout();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		});

		paneListDirectory.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
		paneListDirectory.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);

		bttnPlay.getStyleClass().setAll("icon-playButton");

		bttnMute.getStyleClass().setAll("icon-volume");

		// tableSongs
		this.tableSongs.setRowFactory( tv -> {
			TableRow<Song> row = new TableRow<>();
			row.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2 && (! row.isEmpty()) ) {
					if (queue != null)
						queue = null;

					int first = tableSongs.getSelectionModel().getSelectedIndex();
					int last = tableSongs.getItems().size();

					for (int i = first; i < last; i++)
						addToQueue(tableSongs.getItems().get(i));

					play();
				}
			});
			return row;
		});

		this.sliderVolume.valueProperty().addListener(observable -> {
			if (player != null)
				player.setVolume(sliderVolume.getValue() / 100);
			if (sliderVolume.getValue() == 0)
				bttnMute.getStyleClass().setAll("icon-mute");
			else
				bttnMute.getStyleClass().setAll("icon-volume");

			String style = String.format("-fx-background-color: linear-gradient(to right, #AA96DA %d%%, #969696 %d%%);",
					(int) sliderVolume.getValue(), (int) sliderVolume.getValue());
			sliderVolume.lookup(".track").setStyle(style);

		});

		tablecolTitle.setReorderable(false);
		tablecolArtist.setReorderable(false);
		tablecolLength.setReorderable(false);
		tablecolTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
		tablecolArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
		tablecolLength.setCellValueFactory(new PropertyValueFactory<>("length"));
		tablecolArtist.setStyle("-fx-alignment: center");
		tablecolLength.setStyle("-fx-alignment: center");


		//tableSongSelection
		tablecolSelectionTitle.setReorderable(false);
		tablecolSelectionArtist.setReorderable(false);
		tablecolSelectionLength.setReorderable(false);
		tablecolSelectionTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
		tablecolSelectionArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
		tablecolSelectionLength.setCellValueFactory(new PropertyValueFactory<>("length"));
		tablecolSelectionArtist.setStyle("-fx-alignment: center");
		tablecolSelectionLength.setStyle("-fx-alignment: center");
		tableSongSelection.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


		Region iconClock1 = new Region(), iconClock2 = new Region();
		iconClock1.getStyleClass().add("icon-clock");
		iconClock2.getStyleClass().add("icon-clock");

		final int s = 12;

		iconClock1.setMinSize(s, s);
		iconClock1.setPrefSize(s, s);
		iconClock1.setMaxSize(s, s);
		iconClock2.setMinSize(s, s);
		iconClock2.setPrefSize(s, s);
		iconClock2.setMaxSize(s, s);

		tablecolLength.setGraphic(iconClock1);
		tablecolSelectionLength.setGraphic(iconClock2);

		//listDirectory
		listDirectory.setFocusTraversable(false);
		listPlaylist.setFocusTraversable(false);

		listDirectory.setCellFactory(new Callback<>() {
			@Override
			public ListCell<SongDirectory> call(ListView<SongDirectory> param) {
				return new ListCell<>() {
					@Override
					protected void updateItem(SongDirectory item, boolean empty) {
						super.updateItem(item, empty);
						if (item != null) {
							setText(item.getName());
							setStyle("-fx-font-weight: bold");
						} else
							setText("");
					}
				};
			}
		});

		listDirectory.setOnMouseClicked(e -> {listPlaylist.getSelectionModel().clearSelection();});

		listDirectory.getSelectionModel().selectedItemProperty().addListener((observableValue, directory, t1) -> {
			if (listDirectory.getSelectionModel().getSelectedItem() != null) {
				SongDirectory dir = listDirectory.getSelectionModel().getSelectedItem();
				showSongList(dir);
			}
		});

		listPlaylist.setCellFactory(new Callback<>() {
			@Override
			public ListCell<Playlist> call(ListView<Playlist> param) {
				return new ListCell<>() {
					@Override
					protected void updateItem(Playlist item, boolean empty) {
						super.updateItem(item, empty);
						if (item != null) {
							setText(item.getName());
							setStyle("-fx-font-weight: bold");
						} else
							setText("");
					}
				};
			}
		});

		listPlaylist.setOnMouseClicked(e -> {listDirectory.getSelectionModel().clearSelection();});

		listPlaylist.getSelectionModel().selectedItemProperty().addListener((observableValue, directory, t1) -> {
			if (listPlaylist.getSelectionModel().getSelectedItem() != null) {
				Playlist ply = listPlaylist.getSelectionModel().getSelectedItem();
				showSongList(ply);
			}
		});

		try {
			loadDirectories();
			loadPlaylists();
		} catch (IOException | CannotReadException | TagException | InvalidAudioFrameException | ReadOnlyFileException e) {
			throw new RuntimeException(e);
		}

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
				addDirectory(currentLine, songDir.getName(), true);

				currentLine = reader.readLine();
			}
			reader.close();
		}
	}

	private void loadPlaylists() throws IOException, CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException {
		if (!JavaMediaPlayer.user.isVip())
			return;

		File directory = new File(JavaMediaPlayer.PLAYLISTS_DIRECTORY_PATH + "/" + JavaMediaPlayer.user.getUsername());

		if (!directory.exists())
			return;

		Playlist playlist;
		File[] files = directory.listFiles();
		BufferedReader reader;
		String line;
		for (File file : files) {
			reader = new BufferedReader(new FileReader(file));
			line = reader.readLine();
			playlist = new Playlist(JavaMediaPlayer.user, line);

			line = reader.readLine();
			while (line != null) {
				playlist.add(new Song(line));
				line = reader.readLine();
			}
			JavaMediaPlayer.playlists.add(playlist);
			listPlaylist.getItems().add(playlist);
			addPlaylistToGrid(playlist);
		}
	}

	@FXML
	public void chooseDirectory() {
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("Escolha um diretório");
		File f = dc.showDialog(JavaMediaPlayer.scene.getWindow());

		if (f != null) {
			String path = f.getAbsolutePath();
			addDirectory(path, f.getName(), false);
		}
	}

	private void addDirectory(String path, String name, boolean reloading) {
		for (SongDirectory dir : JavaMediaPlayer.directories)
			if (dir.getDirectoryPath().equals(path)) {
				dialog.setContentText("O diretório já está registrado no sistema.");
				dialog.showAndWait();
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
			if (listDirectory.getSelectionModel().getSelectedItem() == null)
				showSongList(directory);
			else if (listDirectory.getSelectionModel().getSelectedItem().equals(directory))
				showSongList(directory);
			else
				listDirectory.getSelectionModel().select(directory);
		});

		Label title = new Label();
		title.setText(directory.getName());

		VBox vboxDir = new VBox();
		vboxDir.setAlignment(Pos.CENTER);
		vboxDir.getChildren().add(icon);
		vboxDir.getChildren().add(title);

		if (currentDirGridX == gridDirectory.getColumnCount()) {
			currentDirGridX = 0;
			currentDirGridY++;
		}

		gridDirectory.add(vboxDir, currentDirGridX, currentDirGridY);
		currentDirGridX++;

		listDirectory.getItems().add(directory);

		showDirectories();
	}

	@FXML
	public void addPlaylist() {
		listDirectory.getSelectionModel().clearSelection();
		if (JavaMediaPlayer.user.isVip()) {
			txtNamePlaylist.clear();
			paneNamePlaylist.toFront();
		} else {
			dialog.setContentText("Essa funcionalidade é exclusiva para membros VIP.");
			dialog.showAndWait();
		}
	}

	@FXML
	public void namePlaylist() {
		String name = this.txtNamePlaylist.getText().trim();

		if (name.length() == 0) {
			dialog.setContentText("O campo Nome da Playlist está vazio.");
			dialog.showAndWait();
			return;
		}

		for (Playlist p : JavaMediaPlayer.playlists)
			if (name.equals(p.getName())) {
				dialog.setContentText("Já existe uma playlist com o nome " + name + ".");
				dialog.showAndWait();
				return;
			}

		for (SongDirectory d : JavaMediaPlayer.directories)
			for (int i = 0; i < d.size(); i++)
				tableSongSelection.getItems().add(d.at(i));

		paneSongSelection.toFront();
	}

	@FXML
	public void createPlaylist() throws IOException {
		Playlist playlist = new Playlist(
				JavaMediaPlayer.user,
				txtNamePlaylist.getText().trim(),
				tableSongSelection.getSelectionModel().getSelectedItems()
		);

		JavaMediaPlayer.playlists.add(playlist);

		playlist.write();

		Region icon = new Region();
		icon.getStyleClass().add("icon-playlist");

		final int SIZE = 64;

		icon.setMinSize(SIZE, SIZE);
		icon.setPrefSize(SIZE, SIZE);
		icon.setMaxSize(SIZE, SIZE);

		icon.setOnMouseClicked(e -> {
			if (listPlaylist.getSelectionModel().getSelectedItem() == null)
				showSongList(playlist);
			else if (listPlaylist.getSelectionModel().getSelectedItem().equals(playlist))
				showSongList(playlist);
			else
				listPlaylist.getSelectionModel().select(playlist);
		});

		Label title = new Label();
		title.setText(playlist.getName());

		VBox vboxDir = new VBox();
		vboxDir.setAlignment(Pos.CENTER);
		vboxDir.getChildren().add(icon);
		vboxDir.getChildren().add(title);

		if (currentPlayGridX == gridPlaylist.getColumnCount()) {
			currentPlayGridX = 0;
			currentPlayGridY++;
		}

		gridPlaylist.add(vboxDir, currentPlayGridX, currentPlayGridY);
		currentPlayGridX++;

		listPlaylist.getItems().add(playlist);
		showPlaylists();
	}

	private void addPlaylistToGrid(Playlist playlist) {
		Region icon = new Region();
		icon.getStyleClass().add("icon-playlist");

		final int SIZE = 64;

		icon.setMinSize(SIZE, SIZE);
		icon.setPrefSize(SIZE, SIZE);
		icon.setMaxSize(SIZE, SIZE);

		icon.setOnMouseClicked(e -> {
			if (listPlaylist.getSelectionModel().getSelectedItem() == null)
				showSongList(playlist);
			else if (listPlaylist.getSelectionModel().getSelectedItem().equals(playlist))
				showSongList(playlist);
			else
				listPlaylist.getSelectionModel().select(playlist);
		});

		Label title = new Label();
		title.setText(playlist.getName());

		VBox vboxDir = new VBox();
		vboxDir.setAlignment(Pos.CENTER);
		vboxDir.getChildren().add(icon);
		vboxDir.getChildren().add(title);

		if (currentPlayGridX == gridPlaylist.getColumnCount()) {
			currentPlayGridX = 0;
			currentPlayGridY++;
		}

		gridPlaylist.add(vboxDir, currentPlayGridX, currentPlayGridY);
		currentPlayGridX++;
	}

	@FXML
	public void showPlaylists(MouseEvent e) {
		listDirectory.getSelectionModel().clearSelection();
		showPlaylists();
	}

	private void showPlaylists() {
		if (!JavaMediaPlayer.user.isVip()) {
			dialog.setContentText("Essa funcionalidade é exclusiva para membros VIP.");
			dialog.showAndWait();
		} else {
			this.paneListPlaylist.toFront();
		}
	}

	@FXML
	public void showDefault(MouseEvent e) {
		showDefault();
	}

	private void showDefault() {
		listDirectory.getSelectionModel().clearSelection();
		listPlaylist.getSelectionModel().clearSelection();
		paneDefault.toFront();
	}

	@FXML
	public void showDirectories(MouseEvent e) {
		listPlaylist.getSelectionModel().clearSelection();
		showDirectories();
	}

	private void showDirectories() {
		paneListDirectory.toFront();
	}

	private void showSongList(SongList songList) {
		tableSongs.setItems(songList.list());
		paneDirectory.toFront();
	}

	@FXML
	public void addToQueue(Song s) {
		if (queue == null)
			queue = new SongQueue();

		queue.add(s);
	}

	private void play() {
		bttnPlay.getStyleClass().setAll("icon-pauseButton");

		if (player != null)
			player.dispose();

		Song currentSong = queue.next();
		if (currentSong == null) {
			currentSong = queue.first();
		}

		player = new MediaPlayer(new Media(currentSong.getURI()));
		player.setOnEndOfMedia(this::play);
		player.setVolume(sliderVolume.getValue() / 100);
		player.play();

		labelTitle.setText(currentSong.getTitle());
		labelArtist.setText(currentSong.getArtist());
	}

	@FXML
	public void playNext() {
		if (player == null)
			return;

		bttnPlay.getStyleClass().setAll("icon-pauseButton");

		player.dispose();

		Song currentSong = queue.next();
		if (currentSong == null) {
			currentSong = queue.first();
		}

		player = new MediaPlayer(new Media(currentSong.getURI()));
		player.setOnEndOfMedia(this::play);
		player.setVolume(sliderVolume.getValue() / 100);
		player.play();

		labelTitle.setText(currentSong.getTitle());
		labelArtist.setText(currentSong.getArtist());
	}

	@FXML
	public void playPrevious(MouseEvent e) {
		if (player == null)
			return;

		bttnPlay.getStyleClass().setAll("icon-pauseButton");

		if (e.getClickCount() == 2) {
			player.dispose();

			Song currentSong = queue.previous();
			if (currentSong == null)
				currentSong = queue.last();

			player = new MediaPlayer(new Media(currentSong.getURI()));
			player.setOnEndOfMedia(this::play);
			player.setVolume(sliderVolume.getValue() / 100);
			player.play();

			labelTitle.setText(currentSong.getTitle());
			labelArtist.setText(currentSong.getArtist());
		}
	}

	@FXML
	public void pauseOrResume() {
		if (player == null)
			return;

		if (player.getStatus() == MediaPlayer.Status.PLAYING) {
			bttnPlay.getStyleClass().setAll("icon-playButton");
			player.pause();
		}

		if (player.getStatus() == MediaPlayer.Status.PAUSED) {
			bttnPlay.getStyleClass().setAll("icon-pauseButton");
			player.play();
		}
	}

	@FXML
	public void reset() {
		if (player == null)
			return;

		bttnPlay.getStyleClass().setAll("icon-pauseButton");
		player.seek(player.getStartTime());
	}

	@FXML
	public void mute() {
		sliderVolume.setValue(0);
	}

	public void logout() throws IOException {
		if (player != null)
			player.dispose();

		JavaMediaPlayer.directories = new ArrayList<>();
		JavaMediaPlayer.playlists = new ArrayList<>();
		JavaMediaPlayer.user = null;

		JavaMediaPlayer.setRoot("view/Login");
	}
}
