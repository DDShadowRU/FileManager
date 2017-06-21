package ddcompany.fm;

import ddcompany.fm.config.ConfigManager;
import ddcompany.fm.fxml.Controller;
import ddcompany.fm.fxml.CreateFileController;
import ddcompany.fm.fxml.SearchController;
import ddcompany.fm.fxml.SettingsController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main extends Application {

    private static Stage stage;
    private static Controller stageController;
    private static Stage stageCreateFile;
    private static CreateFileController stageCreateFileController;
    private static SearchController stageSearchController;
    private static SettingsController stageSettingsController;
    private static Stage stageCreateDir;
    private static Stage stageAbout;
    private static Stage stageSearch;
    private static Stage stageSettings;

    public static Stage getStage() {
        return stage;
    }
    public static Stage getStageCreateFile() {
        return stageCreateFile;
    }
    public static Stage getStageAbout() {return stageAbout;}
    public static Stage getStageCreateDir() {return stageCreateDir;}
    public static Stage getStageSearch() {
        return stageSearch;
    }
    public static Stage getStageSettings() {
        return stageSettings;
    }

    public static Controller getStageController() {
        return stageController;
    }
    public static SearchController getStageSearchController() {
        return stageSearchController;
    }
    public static CreateFileController getStageCreateFileController() {
        return stageCreateFileController;
    }
    public static SettingsController getStageSettingsController() {
        return stageSettingsController;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        ConfigManager.initConfig();

        stage=primaryStage;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("fxml/main.fxml"));
        Parent root = loader.load();
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.setTitle("File manager");
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        stageController = loader.getController();

        scene.widthProperty().addListener((observable, oldValue, newValue) -> ((Controller)loader.getController()).onChangeWindowSize(newValue));

        primaryStage.show();

        /////   Create File Stage   /////
        FXMLLoader loaderCreateFile = new FXMLLoader();
        loaderCreateFile.setLocation(getClass().getResource("fxml/create_file.fxml"));

        stageCreateFile = new Stage(StageStyle.UTILITY);
        Parent rootCreateFile = loaderCreateFile.load();
        stageCreateFile.setResizable(false);
        stageCreateFile.setTitle("Create new file");
        Scene sceneCreateFile = new Scene(rootCreateFile, 200, 200);
        stageCreateFile.setScene(sceneCreateFile);
        stageCreateFile.initModality(Modality.WINDOW_MODAL);
        stageCreateFile.initOwner(primaryStage);
        stageCreateFileController = loaderCreateFile.getController();

        /////   Create Directory Stage   /////
        FXMLLoader loaderCreateDir = new FXMLLoader();
        loaderCreateDir.setLocation(getClass().getResource("fxml/create_dir.fxml"));

        stageCreateDir = new Stage(StageStyle.UTILITY);
        Parent rootAbout = loaderCreateDir.load();
        stageCreateDir.setResizable(false);
        stageCreateDir.setTitle("About");
        Scene sceneCreateDir = new Scene(rootAbout, 200, 200);
        stageCreateDir.setScene(sceneCreateDir);
        stageCreateDir.initModality(Modality.WINDOW_MODAL);
        stageCreateDir.initOwner(primaryStage);

        /////   About Stage   /////
        FXMLLoader loaderAbout = new FXMLLoader();
        loaderAbout.setLocation(getClass().getResource("fxml/about.fxml"));

        stageAbout = new Stage(StageStyle.UTILITY);
        Parent rootAboutS = loaderAbout.load();
        stageAbout.setResizable(false);
        stageAbout.setTitle("About");
        Scene sceneAbout = new Scene(rootAboutS, 200, 200);
        stageAbout.setScene(sceneAbout);
        stageAbout.initModality(Modality.WINDOW_MODAL);
        stageAbout.initOwner(primaryStage);

        /////   Search Stage   /////
        FXMLLoader loaderSearch = new FXMLLoader();
        loaderSearch.setLocation(getClass().getResource("fxml/search.fxml"));

        stageSearch = new Stage(StageStyle.UTILITY);
        Parent rootSearch = loaderSearch.load();
        stageSearch.setResizable(false);
        stageSearch.setTitle("Найти...");
        Scene sceneSearch = new Scene(rootSearch, 600, 400);
        stageSearch.setScene(sceneSearch);
        stageSearch.initModality(Modality.WINDOW_MODAL);
        stageSearch.initOwner(primaryStage);
        stageSearchController = loaderSearch.getController();

        /////   Settings Stage   /////
        FXMLLoader loaderSettings = new FXMLLoader();
        loaderSettings.setLocation(getClass().getResource("fxml/settings.fxml"));

        stageSettings = new Stage(StageStyle.UTILITY);
        Parent rootSettings = loaderSettings.load();
        stageSettings.setResizable(false);
        stageSettings.setTitle("Настройки");
        Scene sceneSettings = new Scene(rootSettings, 600, 400);
        stageSettings.setScene(sceneSettings);
        stageSettings.initModality(Modality.WINDOW_MODAL);
        stageSettings.initOwner(primaryStage);
        stageSettingsController = loaderSettings.getController();

        readFavorites();
    }

    private void readFavorites(){
        if ( new File("data/favorites.dat").exists() ){
            try {
                String pathsString = new String(Files.readAllBytes(Paths.get("data/favorites.dat")));
                String[] paths = pathsString.split("\n");
                for ( String path : paths ){
                    if( !path.isEmpty() ){
                        ConfigManager.addFavorites( new File( path ) );
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
