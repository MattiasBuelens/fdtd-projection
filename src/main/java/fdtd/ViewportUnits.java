package fdtd;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Scene;

public class ViewportUnits {

    private final ObjectProperty<Scene> scene;

    private final DoubleProperty sceneWidth;
    private final DoubleProperty sceneHeight;

    public final NumberBinding vw;
    public final NumberBinding vh;
    public final NumberBinding vmin;
    public final NumberBinding vmax;

    public ViewportUnits() {
        scene = new SimpleObjectProperty<>(null);
        sceneWidth = new SimpleDoubleProperty(0d);
        sceneHeight = new SimpleDoubleProperty(0d);

        vw = sceneWidth.divide(100d);
        vh = sceneHeight.divide(100d);
        vmin = Bindings.min(vw, vh);
        vmax = Bindings.max(vw, vh);

        // Bind to scene width and height
        scene.addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                sceneWidth.bind(newScene.widthProperty());
                sceneHeight.bind(newScene.heightProperty());
            } else {
                sceneWidth.unbind();
                sceneHeight.unbind();
            }
        });
    }

    public void initialize(Node element) {
        scene.bind(element.sceneProperty());
    }

}
