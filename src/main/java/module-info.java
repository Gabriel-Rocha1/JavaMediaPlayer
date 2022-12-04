module JavaMediaPlayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires jaudiotagger;

    opens app to javafx.fxml;
    opens app.control to javafx.fxml;
    opens app.model to javafx.base;

    exports app.control;
    exports app.model;
    exports app;
}
