package app.control;

import app.JavaMediaPlayer;

import app.model.SongDirectory;
import app.model.SongQueue;
import app.model.SongList;
import app.model.Playlist;
import app.model.Song;

import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;

import javafx.scene.Node;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.control.Dialog;

import javafx.scene.control.Slider;

import javafx.scene.input.MouseEvent;

import javafx.geometry.Pos;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;

import javafx.stage.DirectoryChooser;

import javafx.util.Callback;

import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.tag.TagException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.File;

import java.util.ResourceBundle;
import java.util.ArrayList;
import java.net.URL;

public class MainController implements Initializable {

	@FXML private TextField txtNamePlaylist;

	@FXML private Label labelArtist;
	@FXML private Label labelTitle;
	@FXML private Label labelName;

	@FXML private Button bttnPlay;
	@FXML private Region bttnMute;

	@FXML private ListView<SongDirectory> listDirectory;
	@FXML private ListView<Playlist> listPlaylist;

	@FXML private AnchorPane paneSongSelection;
	@FXML private AnchorPane paneNamePlaylist;
	@FXML private AnchorPane paneDirectories;
	@FXML private AnchorPane panePlaylists;
	@FXML private AnchorPane paneDirectory;
	@FXML private AnchorPane paneDefault;
	@FXML private AnchorPane panePlayer;

	@FXML private ScrollPane paneListDirectory;
	@FXML private ScrollPane paneListPlaylist;
	@FXML private GridPane gridDirectory;
	@FXML private GridPane gridPlaylist;

	@FXML private TableColumn<Song, String> tablecolArtist;
	@FXML private TableColumn<Song, String> tablecolLength;
	@FXML private TableColumn<Song, String> tablecolTitle;
	@FXML private TableView<Song> tableSongs;

	@FXML private TableColumn<Song, String> tablecolSelectionArtist;
	@FXML private TableColumn<Song, String> tablecolSelectionLength;
	@FXML private TableColumn<Song, String> tablecolSelectionTitle;
	@FXML private TableView<Song> tableSongSelection;

	@FXML private Dialog<String> dialog;
	@FXML private ButtonType type;

	@FXML private MenuItem menuItemLogout;

	@FXML private Slider sliderVolume;

	/**
	 * objeto MediaPlayer que lida com a reprodu????o de m??dia
	 */
	private MediaPlayer player;

	/**
	 * objeto SongQueue que armazena a fila de reprodu????o atual
	 */
	private SongQueue queue;

	// Par??metros utilizados na inser????o de ??cones no GridPane
	private int currentDirGridX;
	private int currentDirGridY;
	private int currentPlayGridX;
	private int currentPlayGridY;

	/**
	 * Efetua diversas inicializa????es necess??rias para o funcionamento da aplica????o.
	 *
	 * @param location
	 * The location used to resolve relative paths for the root object, or
	 * {@code null} if the location is not known.
	 *
	 * @param resources
	 * The resources used to localize the root object, or {@code null} if
	 * the root object was not localized.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Inicializa????o das vari??veis auxiliares
		currentDirGridX = 0;	currentDirGridY = 0;
		currentPlayGridX = 0;	currentPlayGridY = 0;

		// Estiliza????o dos (Panes) principais
		paneDirectories.getStyleClass().add("all-rounded");
		panePlayer.getStyleClass().add("bottom-rounded");
		panePlaylists.getStyleClass().add("all-rounded");
		panePlayer.getStyleClass().add("player-theme");

		// Inicializa????o do POP-UP de erros
		type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
		dialog = new Dialog<>();
		dialog.setTitle("Erro");
		dialog.getDialogPane().getButtonTypes().add(type);

		// Inicializa????o da barra de perfil
		labelName.setText("Ol??, " + JavaMediaPlayer.user.getName());
		labelName.setAlignment(Pos.CENTER_RIGHT);
		menuItemLogout.setOnAction(e -> {
			try {
				logout();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		});

		// Definindo pol??ticas da barra de scroll nas telas de Grid
		paneListDirectory.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
		paneListDirectory.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		paneListPlaylist.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
		paneListPlaylist.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);

		// Estilizando os bot??es de Play e Mute
		bttnPlay.getStyleClass().setAll("icon-playButton");
		bttnMute.getStyleClass().setAll("icon-volume");

		// Inicializa????o da RowFactory da TableView das m??sicas
		this.tableSongs.setRowFactory( tv -> {
			TableRow<Song> row = new TableRow<>();
			row.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2 && (! row.isEmpty()) ) {
					if (queue != null)
						queue = null;

					// Defini????o da Lista de Reprodu????o, da m??sica selecionada at?? a ??ltima m??sica da TableView
					int first = tableSongs.getSelectionModel().getSelectedIndex();
					int last = tableSongs.getItems().size();

					// Adi????o das m??sicas na SongQueue
					for (int i = first; i < last; i++)
						addToQueue(tableSongs.getItems().get(i));

					// In??cio da reprodu????o
					play();
				}
			});
			return row;
		});

		// Adi????o de um Listener que muda o volume do ???player??? sempre que o valor do Slider ?? modificado
		this.sliderVolume.valueProperty().addListener(observable -> {
			if (player != null)
				player.setVolume(sliderVolume.getValue() / 100);
			if (sliderVolume.getValue() == 0)
				bttnMute.getStyleClass().setAll("icon-mute");
			else
				bttnMute.getStyleClass().setAll("icon-volume");

			// Estiliza????o da barra de preenchimento do Slider
			String style = String.format("-fx-background-color: linear-gradient(to right, #AA96DA %d%%, #969696 %d%%);",
					(int) sliderVolume.getValue(), (int) sliderVolume.getValue());
			sliderVolume.lookup(".track").setStyle(style);

		});

		// Inicializa????o das TableCol's da TableView de reprodu????o de m??sicas
		tablecolTitle.setReorderable(false);
		tablecolArtist.setReorderable(false);
		tablecolLength.setReorderable(false);

		tablecolTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
		tablecolArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
		tablecolLength.setCellValueFactory(new PropertyValueFactory<>("length"));

		tablecolArtist.setStyle("-fx-alignment: center");
		tablecolLength.setStyle("-fx-alignment: center");


		// Inicializa????o das TableCol's da TableView de sele????o de m??sicas
		tablecolSelectionTitle.setReorderable(false);
		tablecolSelectionArtist.setReorderable(false);
		tablecolSelectionLength.setReorderable(false);

		tablecolSelectionTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
		tablecolSelectionArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
		tablecolSelectionLength.setCellValueFactory(new PropertyValueFactory<>("length"));

		tablecolSelectionArtist.setStyle("-fx-alignment: center");
		tablecolSelectionLength.setStyle("-fx-alignment: center");



		// Snippet de um EventFilter que possibilita a sele????o de m??ltiplas linhas da TableView com apenas um clique
		// Autor: fabian @ https://stackoverflow.com/a/39366485
		tableSongSelection.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tableSongSelection.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
			Node node = evt.getPickResult().getIntersectedNode();

			// go up from the target node until a row is found, or it's clear the
			// target node wasn't a node.
			while (node != null && node != tableSongSelection && !(node instanceof TableRow)) {
				node = node.getParent();
			}

			// if is part of a row or the row,
			// handle event instead of using standard handling
			if (node instanceof TableRow) {
				// prevent further handling
				evt.consume();

				TableRow<?> row = (TableRow<?>) node;
				TableView<?> tv = row.getTableView();

				// focus the tableview
				tv.requestFocus();

				if (!row.isEmpty()) {
					// handle selection for non-empty nodes
					int index = row.getIndex();
					if (row.isSelected()) {
						tv.getSelectionModel().clearSelection(index);
					} else {
						tv.getSelectionModel().select(index);
					}
				}
			}
		});

		// Estiliza????o das colunas que indicam a dura????o da m??sica nas TableView's
		// N??o consegui desvendar a necessidade de duas vari??veis, por??m uma s?? n??o funciona
		Region iconClock1 = new Region(), iconClock2 = new Region();
		iconClock1.getStyleClass().add("icon-clock");
		iconClock2.getStyleClass().add("icon-clock");

		final int s = 12;

		iconClock1.setMinSize(s, s);	iconClock1.setPrefSize(s, s);	iconClock1.setMaxSize(s, s);
		iconClock2.setMinSize(s, s);	iconClock2.setPrefSize(s, s);	iconClock2.setMaxSize(s, s);

		tablecolLength.setGraphic(iconClock1);
		tablecolSelectionLength.setGraphic(iconClock2);

		// Removendo a possibilidade de foco das ListView's (por quest??es est??ticas)
		listDirectory.setFocusTraversable(false);
		listPlaylist.setFocusTraversable(false);

		// CellFactory da ListView de diret??rios, adiciona o nome do SongDirectory como uma ListCell
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

		// Event que limpa a sele????o da ListView de playlists sempre que h?? um clique na ListView de diret??rios
		listDirectory.setOnMouseClicked(e -> listPlaylist.getSelectionModel().clearSelection());
		// Listener do SelectionModel que abre a TableView das m??sicas do SongDirectory associado ?? ListCell
		// Tamb??m limpa o SelectionModel da ListView de playlists
		listDirectory.getSelectionModel().selectedItemProperty().addListener((observableValue, directory, t1) -> {
			if (listDirectory.getSelectionModel().getSelectedItem() != null) {
				SongDirectory dir = listDirectory.getSelectionModel().getSelectedItem();
				showSongList(dir);
			}
		});

		// CellFactory da ListView de playlists, adiciona o nome da Playlist como uma ListCell
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

		// Event que limpa a sele????o da ListView de diret??rios sempre que h?? um clique na ListView de playlists
		listPlaylist.setOnMouseClicked(e -> listDirectory.getSelectionModel().clearSelection());
		// Listener do SelectionModel que abre a TableView das m??sicas da Playlist associada ?? ListCell
		// Tamb??m limpa o SelectionModel da ListView de diret??rios
		listPlaylist.getSelectionModel().selectedItemProperty().addListener((observableValue, directory, t1) -> {
			if (listPlaylist.getSelectionModel().getSelectedItem() != null) {
				Playlist ply = listPlaylist.getSelectionModel().getSelectedItem();
				showSongList(ply);
			}
		});

		try {
			loadDirectories();
			loadPlaylists();
		} catch (IOException | CannotReadException | TagException
				 | InvalidAudioFrameException | ReadOnlyFileException e) {
			throw new RuntimeException(e);
		}

		this.showDefault();
	}

	/**
	 * Carrega no sistema os diret??rios presentes no arquivo directory.dat
	 * @throws IOException
	 */
	private void loadDirectories() throws IOException {
		File f = new File(JavaMediaPlayer.DIRECTORY_FILE_PATH);
		if (f.exists()) {
			BufferedReader reader = new BufferedReader(new FileReader(JavaMediaPlayer.DIRECTORY_FILE_PATH));
			String path = reader.readLine();
			File dir;

			while (path != null) {
				dir = new File(path);
				addDirectory(path, dir.getName(), true);
				path = reader.readLine();
			}
			reader.close();
		}
	}

	/**
	 * Carrega no sistema as playlists salvas do usu??rio atual, cada uma em um arquivo no diret??rio do usu??rio.
	 * @throws IOException
	 * @throws CannotReadException
	 * @throws TagException
	 * @throws InvalidAudioFrameException
	 * @throws ReadOnlyFileException
	 */
	private void loadPlaylists() throws IOException, CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException {
		if (!JavaMediaPlayer.user.isVip())
			return;

		File directory = new File(JavaMediaPlayer.PLAYLISTS_DIRECTORY_PATH + "/" + JavaMediaPlayer.user.getUsername());

		if (!directory.exists())
			return;

		Playlist playlist;
		File[] files = directory.listFiles();
		BufferedReader reader;
		String name, path;

		assert files != null;
		for (File file : files) {
			reader = new BufferedReader(new FileReader(file));
			name = reader.readLine();
			playlist = new Playlist(name);

			path = reader.readLine();
			while (path != null) {
				playlist.add(new Song(path));
				path = reader.readLine();
			}

			JavaMediaPlayer.playlists.add(playlist);
			listPlaylist.getItems().add(playlist);
			addPlaylist(playlist);
		}
	}

	/**
	 * Abre um Prompt do FileExplorer padr??o do SO para que o usu??rio escolha um diret??rio
	 * que ele deseja registrar no sistema.
	 */
	@FXML
	public void chooseDirectory() {
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("Escolha um diret??rio");
		File f = dc.showDialog(JavaMediaPlayer.scene.getWindow());

		if (f != null) {
			String path = f.getAbsolutePath();
			addDirectory(path, f.getName(), false);
		}
	}

	/**
	 * Registra um diret??rio no sistema.
	 * @param path caminho absoluto do diret??rio.
	 * @param name nome do diret??rio.
	 * @param reloading indica se o diret??rio est?? sendo carregado pela primeira vez ou n??o.
	 */
	private void addDirectory(String path, String name, boolean reloading) {
		for (SongDirectory dir : JavaMediaPlayer.directories)
			if (dir.getDirectoryPath().equals(path)) {
				dialog.setContentText("O diret??rio j?? est?? registrado no sistema.");
				dialog.showAndWait();
				return;
			}

		SongDirectory directory = new SongDirectory(path, name, reloading);
		JavaMediaPlayer.directories.add(directory);

		Region icon = new Region();
		icon.getStyleClass().add("icon-directory");

		final int s = 64;

		icon.setMinSize(s, s);	icon.setPrefSize(s, s);	icon.setMaxSize(s, s);

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

	/**
	 * Inicia o processo de cria????o de uma nova playlist, caso o usu??rio tenha status VIP.
	 */
	@FXML
	public void newPlaylist() {
		listDirectory.getSelectionModel().clearSelection();
		if (JavaMediaPlayer.user.isVip()) {
			txtNamePlaylist.clear();
			paneNamePlaylist.toFront();
		} else {
			dialog.setContentText("Essa funcionalidade ?? exclusiva para membros VIP.");
			dialog.showAndWait();
		}
	}

	/**
	 * L?? e valida o nome da playlist escolhido pelo usu??rio. O nome n??o pode ser vazio
	 * e n??o pode existir outra playlist do usu??rio com o mesmo nome.
	 * Povoa a TableView de sele????o de m??sicas com todas as m??sicas de todos os
	 * diret??rios registrados no sistema.
	 */
	@FXML
	public void namePlaylist() {
		String name = this.txtNamePlaylist.getText().trim();

		if (name.length() == 0) {
			dialog.setContentText("O campo Nome da Playlist est?? vazio.");
			dialog.showAndWait();
			return;
		}

		for (Playlist p : JavaMediaPlayer.playlists)
			if (name.equals(p.getName())) {
				dialog.setContentText("J?? existe uma playlist com o nome " + name + ".");
				dialog.showAndWait();
				return;
			}

		tableSongSelection.getSelectionModel().clearSelection();
		for (SongDirectory d : JavaMediaPlayer.directories)
			for (int i = 0; i < d.size(); i++)
				tableSongSelection.getItems().add(d.at(i));

		paneSongSelection.toFront();
	}

	/**
	 * L?? as m??sicas selecionadas na TableView de sele????o de m??sicas e
	 * cria um objeto Playlist contendo-as. Adiciona esse objeto na ArrayList
	 * e na ListView, al??m de escrev??-la no sistema.
	 * @throws IOException
	 */
	@FXML
	public void createPlaylist() throws IOException {
		Playlist playlist = new Playlist(txtNamePlaylist.getText().trim());

		for (Song s : tableSongSelection.getSelectionModel().getSelectedItems()) {
			playlist.add(s);
		}

		playlist.write();

		JavaMediaPlayer.playlists.add(playlist);
		listPlaylist.getItems().add(playlist);
		addPlaylist(playlist);

		showPlaylists();
	}

	/**
	 * Adiciona uma playlist ?? Grid de playlists.
	 * @param playlist playlist a ser adicionada.
	 */
	private void addPlaylist(Playlist playlist) {
		Region icon = new Region();
		icon.getStyleClass().add("icon-playlist");

		final int s = 64;

		icon.setMinSize(s, s);	icon.setPrefSize(s, s);	icon.setMaxSize(s, s);

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

	/**
	 * Mostra a tela inicial da aplica????o.
	 */
	@FXML
	public void showDefault() {
		listDirectory.getSelectionModel().clearSelection();
		listPlaylist.getSelectionModel().clearSelection();
		paneDefault.toFront();
	}

	/**
	 * Mostra os diret??rios registrados no sistema.
	 */
	@FXML
	public void showDirectories() {
		listPlaylist.getSelectionModel().clearSelection();
		paneListDirectory.toFront();
	}

	/**
	 * Mostra as playlists registradas no sistema.
	 */
	@FXML
	public void showPlaylists() {
		listDirectory.getSelectionModel().clearSelection();
		if (!JavaMediaPlayer.user.isVip()) {
			dialog.setContentText("Essa funcionalidade ?? exclusiva para membros VIP.");
			dialog.showAndWait();
		} else {
			this.paneListPlaylist.toFront();
		}
	}

	/**
	 * Povoa a TableView de m??sicas com os objetos de uma SongList.
	 * @param songList m??sicas adicionadas ao TableView.
	 */
	private void showSongList(SongList songList) {
		tableSongs.setItems(songList.list());
		paneDirectory.toFront();
	}

	/**
	 * Adiciona uma m??sica ?? fila de reprodu????o
	 * @param s m??sica adicionada.
	 */
	@FXML
	public void addToQueue(Song s) {
		if (queue == null)
			queue = new SongQueue();

		queue.add(s);
	}

	/**
	 * Inicia o player com a primeira m??sica da fila de reprodu????o, ou com a pr??xima.
	 * Caso a pr??xima seja a ??ltima, retorna ao in??cio da fila.
	 */
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

	/**
	 * For??a a reprodu????o da pr??xima m??sica da fila de reprodu????o.
	 */
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

	/**
	 * Caso receba dois cliques, for??a a reprodu????o da m??sica anterior na fila de reprodu????o.
	 * Caso a atual seja a primeira, reproduz a ??ltima m??sica da fila.
	 * @param e evento associado ?? a????o
	 */
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

	/**
	 * Pausa o player caso esteja tocando, recome??a caso esteja pausado.
	 */
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

	/**
	 * Reinicia a m??sica atual.
	 */
	@FXML
	public void reset() {
		if (player == null)
			return;

		bttnPlay.getStyleClass().setAll("icon-pauseButton");
		player.seek(player.getStartTime());
	}

	/**
	 * Silencia o som do player.
	 */
	@FXML
	public void mute() {
		sliderVolume.setValue(0);
	}

	/**
	 * Nulifica os diret??rios, as playlists e o usu??rio atual do sistema.
	 * Retorna ?? tela de autentica????o.
	 * @throws IOException
	 */
	@FXML
	public void logout() throws IOException {
		if (player != null)
			player.dispose();

		JavaMediaPlayer.directories = new ArrayList<>();
		JavaMediaPlayer.playlists = new ArrayList<>();
		JavaMediaPlayer.user = null;

		JavaMediaPlayer.setRoot("view/Login");
	}
}
