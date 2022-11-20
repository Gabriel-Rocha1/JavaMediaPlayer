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
	private Button bttnForward;

	@FXML
	private Button bttnPlay;

	@FXML
	private ImageView imgAlbumCover;

	@FXML
	private ListView<?> listDirectory;

	@FXML
	private ListView<?> listPlaylist;

	@FXML
	private AnchorPane menuPane;

	@FXML
	private AnchorPane mainPane;

	@FXML
	private AnchorPane explorerPane;

	@FXML
	private AnchorPane miniPane;

	@FXML
	private AnchorPane playerPane;

	public static User user;

}
