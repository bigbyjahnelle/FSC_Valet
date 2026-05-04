package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class ClientMain extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Font.loadFont(getClass().getResourceAsStream("/fonts/Montserrat-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Montserrat-Bold.ttf"), 14);

        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/ram.png")));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("FSCValet - Login");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
