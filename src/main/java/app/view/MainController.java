package app.view;

import app.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class MainController {
	@FXML
	private Button bttnBackward;

	@FXML
	private Button bttnFoward;

	@FXML
	private Button bttnPlay;

	@FXML
	private AnchorPane explorerScreen;

	@FXML
	private ImageView imgAlbumCover;

	@FXML
	private ListView<?> listDirectory;

	@FXML
	private ListView<?> listPlaylist;

	@FXML
	private AnchorPane mainScreen;

	@FXML
	private AnchorPane miniScreen;

	@FXML
	private AnchorPane playerScreen;
	public static User user;


}
