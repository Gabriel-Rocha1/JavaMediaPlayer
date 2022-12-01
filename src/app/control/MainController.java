package app.control;

import app.JavaMediaPlayer;
import app.model.*;

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
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

	@FXML private TextField txtNamePlaylist;

	@FXML private Label labelArtist;
	@FXML private Label labelTitle;

	@FXML private Label labelDirectory;
	@FXML private Button bttnBackward;
	@FXML private Button bttnForward;
	@FXML private Button bttnPlay;
	@FXML private Label bttnAddDirectory;
	@FXML private Label bttnAddPlaylist;
	@FXML private Button bttnNamePlaylist;

	@FXML private ImageView imgAlbumCover;

	@FXML private ListView<SongDirectory> listDirectory;
	@FXML private AnchorPane paneMenu;
	@FXML private AnchorPane paneExplorer;
	@FXML private AnchorPane paneMini;
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

	private MediaPlayer player;
	private SongQueue queue;

	private int currentDirGridX;
	private int currentDirGridY;

	private int currentPlayGridX;
	private int currentPlayGridY;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//TODO: inicializar o MainController
		this.paneListDirectory.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
		this.paneListDirectory.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);

		// tableSongs
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

		this.tablecolTitle.setReorderable(false);
		this.tablecolArtist.setReorderable(false);
		this.tablecolLength.setReorderable(false);



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

		//tableSongSelection

		this.tablecolSelectionTitle.setReorderable(false);
		this.tablecolSelectionArtist.setReorderable(false);
		this.tablecolSelectionLength.setReorderable(false);

		this.tablecolSelectionTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
		this.tablecolSelectionArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
		this.tablecolSelectionLength.setCellValueFactory(new PropertyValueFactory<>("length"));

		this.tablecolSelectionArtist.setStyle("-fx-alignment: center");
		this.tablecolSelectionLength.setStyle("-fx-alignment: center");

		this.tablecolSelectionLength.setGraphic(icon);

		this.tableSongSelection.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);



		//listDirectory
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

		try {
			this.loadDirectories();
			this.loadPlaylists();
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
				this.addDirectory(currentLine, songDir.getName(), true);

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
			this.addPlaylistToGrid(playlist);
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

		if (this.currentDirGridX == this.gridDirectory.getColumnCount()) {
			this.currentDirGridX = 0;
			this.currentDirGridY++;
		}

		this.gridDirectory.add(vboxDir, this.currentDirGridX, this.currentDirGridY);
		this.currentDirGridX++;

		this.listDirectory.getItems().add(directory);

		this.showDirectories();
	}

	@FXML
	public void addPlaylist(MouseEvent e) {
		if (JavaMediaPlayer.user.isVip()) {
			this.txtNamePlaylist.clear();
			this.paneNamePlaylist.toFront();
		} else {
			//TODO: Dialog avisando que o usuário não é VIP
			return;
		}

	}

	@FXML
	public void namePlaylist(ActionEvent e) {
		if (this.txtNamePlaylist.getText().trim().length() == 0) {
			//TODO: sinalizar que o campo "Nome da Playlist" está vazio
			return;
		}

		//TODO: checar se já existe uma playlist com esse nome

		for (SongDirectory d : JavaMediaPlayer.directories)
			for (int i = 0; i < d.size(); i++)
				this.tableSongSelection.getItems().add(d.at(i));

		this.paneSongSelection.toFront();
	}

	@FXML
	public void createPlaylist() throws IOException {
		Playlist playlist = new Playlist(
				JavaMediaPlayer.user,
				this.txtNamePlaylist.getText().trim(),
				this.tableSongSelection.getSelectionModel().getSelectedItems()
		);

		JavaMediaPlayer.playlists.add(playlist);

		//TODO: salvar playlist na memória
		playlist.write();


		Region icon = new Region();
		icon.getStyleClass().add("icon-playlist");

		final int SIZE = 64;

		icon.setMinSize(SIZE, SIZE);
		icon.setPrefSize(SIZE, SIZE);
		icon.setMaxSize(SIZE, SIZE);

		icon.setOnMouseClicked(e -> {this.showSongList(playlist);});

		Label title = new Label();
		title.setText(playlist.getName());

		VBox vboxDir = new VBox();
		vboxDir.setAlignment(Pos.CENTER);
		vboxDir.getChildren().add(icon);
		vboxDir.getChildren().add(title);

		if (this.currentPlayGridX == this.gridPlaylist.getColumnCount()) {
			this.currentPlayGridX = 0;
			this.currentPlayGridY++;
		}

		this.gridPlaylist.add(vboxDir, this.currentPlayGridX, this.currentPlayGridY);
		this.currentPlayGridX++;

		this.showPlaylists();
	}

	private void addPlaylistToGrid(Playlist playlist) {
		Region icon = new Region();
		icon.getStyleClass().add("icon-playlist");

		final int SIZE = 64;

		icon.setMinSize(SIZE, SIZE);
		icon.setPrefSize(SIZE, SIZE);
		icon.setMaxSize(SIZE, SIZE);

		icon.setOnMouseClicked(e -> {this.showSongList(playlist);});

		Label title = new Label();
		title.setText(playlist.getName());

		VBox vboxDir = new VBox();
		vboxDir.setAlignment(Pos.CENTER);
		vboxDir.getChildren().add(icon);
		vboxDir.getChildren().add(title);

		if (this.currentPlayGridX == this.gridPlaylist.getColumnCount()) {
			this.currentPlayGridX = 0;
			this.currentPlayGridY++;
		}

		this.gridPlaylist.add(vboxDir, this.currentPlayGridX, this.currentPlayGridY);
		this.currentPlayGridX++;
	}

	@FXML
	public void showPlaylists(MouseEvent e) {
		this.showPlaylists();
	}

	private void showPlaylists() {
		//TODO: GridView das Playlists
		this.paneListPlaylist.toFront();

	}

	@FXML
	public void showDefault(MouseEvent e) {
		this.showDefault();
	}

	private void showDefault() {
		this.paneDefault.toFront();
	}

	@FXML
	public void showDirectories(MouseEvent e) {
		this.showDirectories();
	}

	private void showDirectories() {
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

		player = new MediaPlayer(new Media(currentSong.getURI()));
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

		player = new MediaPlayer(new Media(currentSong.getURI()));
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

			player = new MediaPlayer(new Media(currentSong.getURI()));
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
