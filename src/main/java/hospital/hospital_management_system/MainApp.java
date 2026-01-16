package hospital.hospital_management_system;

import hospital.hospital_management_system.utils.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        new DatabaseInitializer().seedIfEmpty();
        Parent root = FXMLLoader.load(
                getClass().getResource("MainView.fxml")
        );

        Scene scene = new Scene(root, 1000, 600);
        
        String css = getClass().getResource("app.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Hospital Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
