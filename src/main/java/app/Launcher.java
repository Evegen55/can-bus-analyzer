package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Launcher extends Application {

    private Stage window;

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        window = primaryStage;
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("../mainScene.fxml");
        loader.setLocation(xmlUrl);
        Parent root = loader.load();

        window.setScene(new Scene(root));
        window.setX(0);
        window.setY(0);

        final MainController loaderController = loader.getController();
        loaderController.setMainStage(window);

        window.show();
    }
}
