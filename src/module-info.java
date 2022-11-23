module JavaMediaPlayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires jaudiotagger;

    opens app to javafx.fxml;
    opens app.control to javafx.fxml;

    exports app.control;
    exports app;
}
