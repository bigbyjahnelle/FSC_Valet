package shared.util;

import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.util.Duration;

    /**
     Utility class for applying reusable button effects across the application.
     Call applyAll() to attach all standard effects at once, or use the individual
     methods to apply only what you need (note: applyHover and applyMouseEnterLeave
     both write to the same event handlers, so use applyAll if you want both).
     */

public class ButtonEffects {

    private static final double HOVER_SCALE = 1.05;
    private static final double PRESS_SCALE = 0.95;
    private static final double ANIM_MS     = 120;

    /**
     Applies all standard effects to a button:
     -Scales up on hover, scales down on press
     -Opacity dims when mouse is away, full opacity on hover
     Uses combined handlers so nothing overwrites anything else.
     */
    public static void applyAll(Button button) {
        applyPress(button);

        ScaleTransition scaleIn  = new ScaleTransition(Duration.millis(ANIM_MS), button);
        scaleIn.setToX(HOVER_SCALE);
        scaleIn.setToY(HOVER_SCALE);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(ANIM_MS), button);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        button.setOpacity(0.9);

        // Single onMouseEntered: scale up + full opacity
        button.setOnMouseEntered(e -> {
            scaleIn.playFromStart();
            button.setOpacity(1.0);
        });

        // Single onMouseExited: scale back + dim
        button.setOnMouseExited(e -> {
            scaleOut.playFromStart();
            button.setOpacity(0.9);
        });
    }

    /**
     Only hover scale effect (grow on enter, shrink on exit).
     Do NOT combine with applyMouseEnterLeave — use applyAll instead.
     */

    public static void applyHover(Button button) {
        ScaleTransition scaleIn  = new ScaleTransition(Duration.millis(ANIM_MS), button);
        scaleIn.setToX(HOVER_SCALE);
        scaleIn.setToY(HOVER_SCALE);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(ANIM_MS), button);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        button.setOnMouseEntered(e -> scaleIn.playFromStart());
        button.setOnMouseExited(e -> scaleOut.playFromStart());
    }

    /**
     Only press effect (shrinks on press, pops back on release).
     Safe to call alongside applyHover since it uses different event handlers.
     */

    public static void applyPress(Button button) {
        ScaleTransition press   = new ScaleTransition(Duration.millis(ANIM_MS), button);
        press.setToX(PRESS_SCALE);
        press.setToY(PRESS_SCALE);

        ScaleTransition release = new ScaleTransition(Duration.millis(ANIM_MS), button);
        release.setToX(1.0);
        release.setToY(1.0);

        button.setOnMousePressed(e  -> press.playFromStart());
        button.setOnMouseReleased(e -> release.playFromStart());
    }

    /**
     Only opacity + cursor effect on enter/leave.
     Do NOT combine with applyHover — use applyAll instead.
     */

    public static void applyMouseEnterLeave(Button button) {
        button.setOpacity(0.9);
        button.setOnMouseEntered(e -> button.setOpacity(1.0));
        button.setOnMouseExited(e  -> button.setOpacity(0.9));
    }
}
