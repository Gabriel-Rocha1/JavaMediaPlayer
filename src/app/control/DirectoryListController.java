package app.control;

import app.JavaMediaPlayer;
import app.model.SongDirectory;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class DirectoryListController implements Initializable {
    @FXML private GridPane gridDirectory;
    @FXML private Label labelEmptyDirectory;
    private int currentGridX;
    private int currentGridY;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.currentGridX = 0;
        this.currentGridY = 0;
        this.updateGrid();
    }

    public void updateGrid() {
        this.labelEmptyDirectory.setVisible(false);
        for (SongDirectory directory : JavaMediaPlayer.directories) {
            Button bttnDir = new Button();
            bttnDir.setPrefSize(64, 64);
            bttnDir.setText("");
            bttnDir.getStyleClass().add("dir-button");
            bttnDir.setUserData(directory);
            bttnDir.setOnAction(e -> {
                JavaMediaPlayer.currentSongList = directory;
                //TODO: mudar o mainPane para Directory.fxml
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
