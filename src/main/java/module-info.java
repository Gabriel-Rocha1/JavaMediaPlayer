module JavaMediaPlayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires jaudiotagger;

    opens app.view to javafx.fxml;
    exports app.view;
}
