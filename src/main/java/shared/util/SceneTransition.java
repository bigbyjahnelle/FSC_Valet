package shared.util;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 Handles seamless screen transitions by swapping the root node of the existing
 scene rather than creating a new Scene. This keeps the stage maximized and
 prevents the desktop from flashing between screens.
 */
public class SceneTransition {

    private static final double FADE_MS = 200;

    /**
     Fades out the current screen, swaps the root to the new FXML, then fades in.
     The stage stays maximized and at the correct size throughout.

     @param stage     the current stage
     @param fxmlPath  absolute resource path e.g. "/fxml/dashboard.fxml"
     @param title     window title for the new screen
     */
    public static void fadeSwitch(Stage stage, String fxmlPath, String title) {
        Parent currentRoot = stage.getScene().getRoot();

        FadeTransition fadeOut = new FadeTransition(Duration.millis(FADE_MS), currentRoot);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(SceneTransition.class.getResource(fxmlPath));
                Parent newRoot = loader.load();
                newRoot.setOpacity(0.0);

                stage.getScene().setRoot(newRoot);
                stage.setTitle(title);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(FADE_MS), newRoot);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        fadeOut.play();
    }
}
