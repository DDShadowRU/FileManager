package ddcompany.fm.fxml;

import ddcompany.fm.AlertUtils;
import ddcompany.fm.Main;
import ddcompany.fm.Util;
import ddcompany.fm.config.ConfigManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Callback;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    @FXML
    private TableView tableViewLeft;
    @FXML
    private BorderPane borderPaneLeft;
    @FXML
    private BorderPane borderPaneRight;
    @FXML
    private TextField fieldPathLeft;
    @FXML
    private TextField fieldPathRight;
    @FXML
    private TableView tableViewRight;
    @FXML
    private TableColumn<File,String> columnNameLeft;
    @FXML
    private TableColumn<File,String> columnTypeLeft;
    @FXML
    private TableColumn<File,String> columnSizeLeft;
    @FXML
    private TableColumn<File,String> columnNameRight;
    @FXML
    private TableColumn<File,String> columnTypeRight;
    @FXML
    private TableColumn<File,String> columnSizeRight;

    /*  MENU ITEMS  */
    @FXML
    private MenuItem menuItemCreateFile;
    @FXML
    private MenuItem menuItemCreateDir;
    @FXML
    private MenuItem menuItemQuit;
    @FXML
    private MenuItem menuItemAbout;
    @FXML
    private Menu menuFavorites;
    @FXML
    private MenuItem menuItemSearch;

    private String tableViewLeftPath = "C://";
    private String tableViewRightPath = "C://";
    private ObservableList<File> listFilesLeft = FXCollections.observableArrayList();
    private ObservableList<File> listFilesRight = FXCollections.observableArrayList();
    private String focus = "left";
    private List<File> favorites = new ArrayList<>();

    @FXML
    public void initialize(){

        tableViewLeft.setOnContextMenuRequested(event ->  requestContextMenu(event));
        tableViewRight.setOnContextMenuRequested(event -> requestContextMenu(event));

        tableViewLeft.setPrefWidth(Main.getStage().getWidth()/2);
        tableViewRight.setPrefWidth(Main.getStage().getWidth()/2);

        tableViewLeft.setItems(listFilesLeft);
        tableViewLeft.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableViewRight.setItems(listFilesRight);
        tableViewRight.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Callback callbackColumnName = (Callback<TableColumn.CellDataFeatures<File, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().getPath());
        Callback callbackColumnType = (Callback<TableColumn.CellDataFeatures<File, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().getPath());
        Callback callbackColumnSize = (Callback<TableColumn.CellDataFeatures<File, String>, ObservableValue<String>>) param -> {

            if( !param.getValue().isDirectory() )
                return new SimpleStringProperty(Util.pbytes(param.getValue().length()));
            else
                return new SimpleStringProperty("");
        };
        Callback callbackCellFactoryName = new Callback<TableColumn<File, String>, TableCell<File, String>>() {
            @Override
            public TableCell<File, String> call(TableColumn<File, String> param) {
                return new TableCell<File,String>(){
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if( item!=null || !empty ) {
                            if (!item.equals("...")) {
                                File file = new File(item);
                                String str = Util.getFileNameNoExt(file.getName(), file.getPath());
                                HBox hBox = new HBox();
                                Label label = new Label();

                                if (file.isDirectory()) {
                                    if (Boolean.valueOf(ConfigManager.getValue(ConfigManager.CONFIG_FOLDER_NAMES_IN_BRACKETS))) {
                                        str = "[" + str + "]";
                                    }
                                }
                                label.setText(str);

                                hBox.getChildren().add(new ImageView(Util.getImageForExt(file)));
                                hBox.getChildren().add(label);

                                this.setGraphic(hBox);
                            }else{
                                HBox hBox = new HBox();

                                Label label = new Label("...");

                                hBox.getChildren().add( label );
                                setGraphic(hBox);
                            }
                        }else {
                            setText(null);
                            setGraphic(null);
                        }
                    }
                };
            }
        };
        Callback callbackCellFactoryType = new Callback<TableColumn<File, String>, TableCell<File, String>>() {
            @Override
            public TableCell<File, String> call(TableColumn<File, String> param) {
                return new TableCell<File,String>(){
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if( item == null || empty ){
                            setText( null );
                            setTextFill( null );
                        }else{
                            File file =  new File( item );
                            String str = "";
                            if( file.isDirectory() ) {
                                str = "<DIR>";
                            }else{
                                str = Util.getExt(file);
                            }
                            setText( str );
                        }
                    }
                };
            }
        };

        columnNameLeft.setCellValueFactory(callbackColumnName);
        columnNameLeft.setCellFactory(callbackCellFactoryName);
        columnNameRight.setCellValueFactory(callbackColumnName);
        columnNameRight.setCellFactory(callbackCellFactoryName);

        columnTypeLeft.setCellValueFactory(callbackColumnType);
        columnTypeLeft.setCellFactory(callbackCellFactoryType);
        columnTypeRight.setCellValueFactory(callbackColumnType);
        columnTypeRight.setCellFactory(callbackCellFactoryType);

        columnSizeLeft.setCellValueFactory(callbackColumnSize);
        columnSizeRight.setCellValueFactory(callbackColumnSize);

        columnTypeLeft.setPrefWidth(100);
        columnTypeRight.setPrefWidth(100);

        columnSizeLeft.setPrefWidth(100);
        columnSizeRight.setPrefWidth(100);

        columnNameLeft.setPrefWidth(borderPaneLeft.getPrefWidth()-215);
        columnNameRight.setPrefWidth(borderPaneRight.getPrefWidth()-215);

        tableViewLeft.setOnMouseClicked(event -> {
            if(event.getButton()== MouseButton.PRIMARY && event.getClickCount() >= 2 ) {
                if (tableViewLeft.getSelectionModel().getSelectedIndex() != -1) {
                    if (!tableViewLeft.getSelectionModel().getSelectedItem().equals(new File("..."))) {
                        File item = listFilesLeft.get(tableViewLeft.getSelectionModel().getSelectedIndex());
                        openFile(item);
                    } else {
                        if( new File(tableViewLeftPath).getParentFile() != null ) setLocation("left", new File(tableViewLeftPath).getParentFile());
                    }
                }
            }
        });
        tableViewRight.setOnMouseClicked(event -> {
            if(event.getButton()== MouseButton.PRIMARY && event.getClickCount() >= 2 ) {
                if (tableViewRight.getSelectionModel().getSelectedIndex() != -1 ) {
                    if (!tableViewRight.getSelectionModel().getSelectedItem().equals(new File("..."))) {
                        File item = listFilesRight.get(tableViewRight.getSelectionModel().getSelectedIndex());
                        openFile(item);
                    } else {
                        setLocation("right", new File(tableViewRightPath).getParentFile());
                    }
                }
            }
        });

        tableViewRight.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                focus="right";
            }
        });

        tableViewLeft.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                focus="left";
            }
        });

        menuItemSearch.setOnAction(event -> {
            Main.getStageSearchController().getFieldPath().setText(getPath());
            Main.getStageSearch().show();
        });

        initMenuBar();

        setLocation("left", new File(ConfigManager.getValue( ConfigManager.CONFIG_START_PATH_LEFT )) );
        setLocation("right",new File(ConfigManager.getValue( ConfigManager.CONFIG_START_PATH_RIGHT )));

    }

    private void requestContextMenu( ContextMenuEvent e ) {
        ContextMenu contextMenu = new ContextMenu();
        List<File> selectedFiles = getSelectedFiles();

        if( selectedFiles.size() == 1 && selectedFiles.get(0).getName().equals("...") ){
            return;
        }

        if( selectedFiles.size() == 1 ){
            File file = selectedFiles.get(0);
            MenuItem itemOpenFile = new MenuItem("Открыть");
            itemOpenFile.setOnAction(event -> openFile(selectedFiles.get(0)));
            contextMenu.getItems().add(itemOpenFile);

            MenuItem itemRename = new MenuItem("Переименовать");
            itemRename.setOnAction(event -> {
                String newName = AlertUtils.showInput("Введите новое имя файла: ",file.getName());
                if( !newName.isEmpty() ){
                    file.renameTo(new File(file.getParent() + File.separator + newName));
                    updateTableViews();
                }
            });
            contextMenu.getItems().add(itemRename);
        }

        Menu menuCreate = new Menu("Создать");

        MenuItem itemCreateFile = new MenuItem("Файл");
        itemCreateFile.setOnAction(event -> createFile());

        MenuItem itemCreateDir = new MenuItem("Папку");
        itemCreateDir.setOnAction(event -> createDir());

        menuCreate.getItems().addAll(itemCreateFile,itemCreateDir);
        contextMenu.getItems().add(menuCreate);

        if( selectedFiles.size() > 0 ) {

            MenuItem itemCopyIn = new MenuItem("Копировать в другую панель");
            itemCopyIn.setOnAction(event -> {
                String str = getPathInverse();
                for ( File file : selectedFiles ){
                    Util.copy( file.toPath() , Paths.get(str + File.separator + file.getName() ));
                }
                updateTableViews();
            });
            contextMenu.getItems().add(itemCopyIn);

            MenuItem itemCutIn = new MenuItem("Переместить в другую панель");
            itemCutIn.setOnAction(event -> {
                String str = getPathInverse();
                for ( File file : selectedFiles ){
                    Util.copy( file.toPath() , Paths.get(str + File.separator + file.getName() ));
                    if( file.isDirectory() )Util.removeDirectory(file);
                    else file.delete();
                }
                updateTableViews();
            });
            contextMenu.getItems().add(itemCutIn);

        }

        MenuItem itemUpdate = new MenuItem("Обновить");
        itemUpdate.setOnAction(event -> updateTableView(focus));
        contextMenu.getItems().add(itemUpdate);

        if( selectedFiles.size() > 0 ){
            MenuItem itemRemove = new MenuItem("Удалить");
            itemRemove.setOnAction(event -> removeItems());
            contextMenu.getItems().add(itemRemove);
        }
        contextMenu.show(Main.getStage(),e.getScreenX(),e.getScreenY());
    }

    private void openFile(File file) {
        if ( file.isDirectory() ){
            setLocation(focus,file);
        }else{
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<File> getSelectedFiles(){
        return focus.equals("left") ? tableViewLeft.getSelectionModel().getSelectedItems() : tableViewRight.getSelectionModel().getSelectedItems();
    }

    private void removeItems() {
        List<File> list = focus.equals("left") ? tableViewLeft.getSelectionModel().getSelectedItems() : tableViewRight.getSelectionModel().getSelectedItems();
        if( list.size() > 0 ){

            if (AlertUtils.showConfirm("Удалить файлы/папки("+list.size()+")?") == ButtonType.OK){
                if(!Util.remove(list)){
                    AlertUtils.showError("Ошибка удаления файла/папки!");
                }

                updateTableViews();
            }
        }

    }

    private void updateTableView(String l) {

        setLocation(l,l.equals("left") ? new File(tableViewLeftPath) : new File(tableViewRightPath));

    }

    public void updateTableViews() {

        updateTableView("left");
        updateTableView("right");

    }

    private void createFile() {
        String p = getPath();
        Main.getStageCreateFileController().getFieldPath().setText(p);
        Main.getStageCreateFile().show();
    }

    private void createDir(){
        Main.getStageCreateDir().show();
    }

    private void initMenuBar() {
        menuItemCreateFile.setOnAction(event -> {
            createFile();
        });
        menuItemCreateDir.setOnAction(event -> createDir());
        menuItemQuit.setOnAction(event -> System.exit(0));
        menuItemAbout.setOnAction(event -> Main.getStageAbout().show());
    }

    private void saveFavorites(){
        if ( !new File("data").exists() ){
            new File("data").mkdir();
        }
        if ( !new File("data/favorites.dat").exists() ){
            try {
                new File("data/favorites.dat").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                //очищаем файл
                Files.write(Paths.get("data/favorites.dat"), "".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for( File file : favorites ) {
            try {
                Files.write(Paths.get("data/favorites.dat"), (file.getPath()+"\n").getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setLocation(String u, File file) {
        if( file.exists() ) {
            if (u == "left") {
                listFilesLeft.clear();
                tableViewLeftPath = file.getPath();
                fieldPathLeft.setText(file.getPath());
            }
            if (u == "right") {
                listFilesRight.clear();
                tableViewRightPath = file.getPath();
                fieldPathRight.setText(file.getPath());
            }

            if (u.equals("left")) listFilesLeft.add(new File("..."));
            if (u.equals("right")) listFilesRight.add(new File("..."));

            List<File> files = Util.sort(1, file.listFiles());
            for (File f : files) {
                if( f.isHidden() ){
                    if ( !Boolean.valueOf(ConfigManager.getValue( ConfigManager.CONFIG_SHOW_S_FILES_FOLDERS )) ) continue;
                }
                if (u.equals("left")) {
                    listFilesLeft.add(f);
                } else if (u.equals("right")) {
                    listFilesRight.add(f);
                }
            }
        }
    }

    public void addFavorite(File file){
        favorites.add(file);
    }

    public String getPath(){
        return focus.equals("left") ? tableViewLeftPath : tableViewRightPath;
    }

    private String getPathInverse() {
        return focus.equals("left") ? tableViewRightPath : tableViewLeftPath;
    }

    public String getFocusedListView(){
        return focus;
    }

    public String getTableViewLeftPath(){
        return tableViewLeftPath;
    }

    public String getTableViewRightPath(){
        return tableViewRightPath;
    }

    public TableView getTableView(String f) {
        return f.equals("left") ? tableViewLeft : tableViewRight;
    }

    public List<File> getListFiles(String f) {
        return f.equals("left") ? listFilesLeft : listFilesRight;
    }

    /*  EVENTS  */
    public void onChangeWindowSize(Number newValue){
        borderPaneLeft.setPrefWidth(newValue.intValue()/2);
        borderPaneRight.setPrefWidth(newValue.intValue()/2);

        columnNameLeft.setPrefWidth(borderPaneLeft.getPrefWidth()-215);
        columnNameRight.setPrefWidth(borderPaneRight.getPrefWidth()-215);
    }

    @FXML
    private void onTextFieldLeftEnter(){
        File file = new File(fieldPathLeft.getText());
        if ( file.exists() ) setLocation("left",file);
        else fieldPathLeft.setText(tableViewLeftPath);
    }
    @FXML
    private void onTextFieldRightEnter(){
        File file = new File(fieldPathRight.getText());
        if ( file.exists() ) setLocation("right",file);
        else fieldPathRight.setText(tableViewRightPath);
    }

    @FXML
    private void onMenuFavoritesClick(){
        File[] roots = File.listRoots();
        String path = getPath();
        menuFavorites.getItems().clear();

        if( favorites.contains(new File(path)) ){
            MenuItem itemRemove = new MenuItem("Удалить из закладок");
            itemRemove.setOnAction(event -> {
                favorites.remove(new File(path));
                saveFavorites();
                menuFavorites.hide();
            });
            menuFavorites.getItems().add(itemRemove);
        }else{
            MenuItem itemAdd = new MenuItem("Добавить в закладки");
            itemAdd.setOnAction(event -> {
                favorites.add(new File(path));
                saveFavorites();
                menuFavorites.hide();
            });
            menuFavorites.getItems().add(itemAdd);
        }

        SeparatorMenuItem separatorMenuItem2 = new SeparatorMenuItem();
        menuFavorites.getItems().add(separatorMenuItem2);

        for (File root : roots) {
            MenuItem item = new MenuItem(root.getPath());
            item.setOnAction(event1 -> setLocation(focus, root));
            menuFavorites.getItems().add(item);
        }

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        menuFavorites.getItems().add(separatorMenuItem);

        MenuItem itemUser = new MenuItem(System.getProperty("user.name"));
        itemUser.setOnAction(event12 -> setLocation(focus, new File("C:/Users/" + System.getProperty("user.name"))));
        menuFavorites.getItems().add(itemUser);

        MenuItem itemDesktop = new MenuItem("Рабочий стол");
        itemDesktop.setOnAction(event12 -> setLocation(focus, new File("C:/Users/" + System.getProperty("user.name") + "/Desktop/")));
        menuFavorites.getItems().add(itemDesktop);

        MenuItem itemDocuments = new MenuItem("Мои документы");
        itemDocuments.setOnAction(event12 -> setLocation(focus, new File("C:/Users/" + System.getProperty("user.name") + "/Documents/")));
        menuFavorites.getItems().add(itemDocuments);

        MenuItem itemImages = new MenuItem("Изображения");
        itemImages.setOnAction(event12 -> setLocation(focus, new File("C:/Users/" + System.getProperty("user.name") + "/Pictures/")));
        menuFavorites.getItems().add(itemImages);

         MenuItem itemMusic = new MenuItem("Моя музыка");
        itemMusic.setOnAction(event12 -> setLocation(focus, new File("C:/Users/" + System.getProperty("user.name") + "/Music/")));
        menuFavorites.getItems().add(itemMusic);

         MenuItem itemVideo = new MenuItem("Мои видеозаписи");
        itemVideo.setOnAction(event12 -> setLocation(focus, new File("C:/Users/" + System.getProperty("user.name") + "/Videos/")));
        menuFavorites.getItems().add(itemVideo);

        SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();
        menuFavorites.getItems().add(separatorMenuItem1);

        for (File file : favorites) {
            MenuItem item = new MenuItem(file.getPath());
            item.setOnAction(event1 -> setLocation(focus, file));
            menuFavorites.getItems().add(item);
        }
    }

    @FXML
    private void onMenuItemSettings(){
        Main.getStageSettingsController().getFieldStartPathLeft().setText( ConfigManager.getValue( ConfigManager.CONFIG_START_PATH_LEFT ) );
        Main.getStageSettingsController().getFieldStartPathRight().setText( ConfigManager.getValue( ConfigManager.CONFIG_START_PATH_RIGHT ) );
        Main.getStageSettingsController().getCheckBoxFNamesInBrackets().setSelected( Boolean.valueOf(ConfigManager.getValue( ConfigManager.CONFIG_FOLDER_NAMES_IN_BRACKETS )) );
        Main.getStageSettings().show();
    }
}
