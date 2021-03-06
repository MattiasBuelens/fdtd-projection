package fdtd;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("ControlPanel.fxml"));
        primaryStage.setTitle("From Dusk Till Dawn - Controlepaneel");
        primaryStage.setScene(new Scene(root));
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
