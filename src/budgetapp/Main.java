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
    /** The production environment flag. */
    public static boolean IS_PRODUCTION = false;
    /** The test environment flag. */
    public static boolean IS_TEST = false;
    /** The use Derby DB flag. */
    public static boolean USE_DERBY = true;
    
    /**
     * The default start method.
     * 
     * @param primaryStage - the primary stage.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Budget App V1.0");
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
     * This is the default main method.  It checks the arguments to determine
     * the environment we are in (i.e. which database to use).
     * 
     * @param args - arguments
     */
    public static void main(String[] args) {
        if(args.length > 0) {
            IS_PRODUCTION = "PROD".equals(args[0]);
            IS_TEST = "TEST".equals(args[0]);
        }
        USE_DERBY = IS_PRODUCTION || IS_TEST;
        launch(args);
    }
    
}
