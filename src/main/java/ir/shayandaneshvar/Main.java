package ir.shayandaneshvar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane root = FXMLLoader.load(getClass()
                .getResource("/main.fxml")
                .toURI()
                .toURL());
        Scene scene = new Scene(root);
        stage.setTitle("Polygon Triangulation");
        stage.getIcons().add(new Image(getClass().getResource(
                "/images/triangle.png").toURI().toURL().toString()));
        stage.setScene(scene);
        stage.show();
    }
}
