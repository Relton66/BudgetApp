package budgetapp;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class for the budget application.
 * 
 */
public class Main extends Application {
    
    /** The primary stage. */
    private Stage primaryStage;
    /** The root layout. */
    private BorderPane homeLayout;
    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger(Main.class); 
    
    /**
     * The default start method.
     * 
     * @param primaryStage - the primary stage.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Budget App");       
        initHomeLayout();
    }

    /**
     * This method initializes the home layout.
     */
    public void initHomeLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/home.fxml"));
            homeLayout = (BorderPane) loader.load();
            Scene scene = new Scene(homeLayout);            
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            LOG.error("IO error in initRootLayout.", e);
        }
    }

    /**
     * This default main method.
     * 
     * @param args - arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
