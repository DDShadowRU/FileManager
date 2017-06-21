package ddcompany.fm.fxml;

import ddcompany.fm.AlertUtils;
import ddcompany.fm.Main;
import ddcompany.fm.Util;
import ddcompany.fm.config.ConfigManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Controller {

    @FXML private TableView tableViewLeft;
    @FXML private BorderPane borderPaneLeft;
    @FXML private BorderPane borderPaneRight;
    @FXML private TextField fieldPathLeft;
    @FXML private TextField fieldPathRight;
    @FXML private TableView tableViewRight;
    @FXML private TableColumn<File,String> columnNameLeft;
    @FXML private TableColumn<File,String> columnTypeLeft;
    @FXML private TableColumn<File,String> columnSizeLeft;
    @FXML private TableColumn<File,String> columnNameRight;
    @FXML private TableColumn<File,String> columnTypeRight;
    @FXML private TableColumn<File,String> columnSizeRight;

    /*  MENU ITEMS  */
    @FXML private MenuItem menuItemCreateFile;
    @FXML private MenuItem menuItemCreateDir;
    @FXML private MenuItem menuItemQuit;
    @FXML private MenuItem menuItemAbout;
    @FXML private Menu menuFavorites;
    @FXML private Menu menuDisks;
    @FXML private Menu menuTo;
    @FXML private MenuItem menuItemSearch;

    /*
     *Текущий путь в правой и левой панели
     */
    private String tableViewLeftPath = "";
    private String tableViewRightPath = "";
    /*
     *Файлы в правой и левой панели
     */
    private ObservableList<File> listFilesLeft = FXCollections.observableArrayList();
    private ObservableList<File> listFilesRight = FXCollections.observableArrayList();
    /**
     * На какой панели сейчас фокус
     */
    private String focus = "left";

    @FXML public void initialize(){

        Callback callbackColumnName = (Callback<TableColumn.CellDataFeatures<File, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().getPath());
        Callback callbackColumnType = (Callback<TableColumn.CellDataFeatures<File, String>, ObservableValue<String>>) param -> new SimpleStringProperty( param.getValue().isDirectory() ? "<DIR>" : Util.getExt(param.getValue()) );
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

        KeyCombination keyCombinationCopy = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
        KeyCombination keyCombinationPaste = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY);

        EventHandler<MouseEvent> eventHandlerDblClick = event -> {
            if(event.getButton()== MouseButton.PRIMARY && event.getClickCount() >= 2 ) {
                if (getFocusedTableView().getSelectionModel().getSelectedIndex() != -1) {
                    File item = getFocusedTableViewListFiles().get(getFocusedTableView().getSelectionModel().getSelectedIndex());
                    openFile(item);
                }
            }
        };
        EventHandler eventHandlerKeyBord = (EventHandler<KeyEvent>) event -> {
            if( event.getCode() == KeyCode.ENTER ){

                List<File> selectedFiles = getSelectedFiles();
                if ( selectedFiles.size() == 1 ){
                    openFile( selectedFiles.get( 0 ) );
                }

            }else if ( event.getCode() == KeyCode.TAB ||
                    event.getCode() == KeyCode.LEFT ||
                    event.getCode() == KeyCode.RIGHT){

                if ( focus.equals("left") ) tableViewRight.requestFocus();
                else tableViewLeft.requestFocus();
                event.consume();

            }else if ( event.getCode() == KeyCode.BACK_SPACE ){

                openFile( new File( "..." ) );

            }else if ( event.getCode() == KeyCode.DELETE ){

                removeFiles();

            }else if ( keyCombinationCopy.match( event ) ){

                copyToClipboard();

            }else if ( keyCombinationPaste.match( event ) ){

                pasteIntoClipboard();

            }
        };

        tableViewLeft.setOnContextMenuRequested(event ->  requestContextMenu(event));
        tableViewRight.setOnContextMenuRequested(event -> requestContextMenu(event));

        tableViewLeft.setPrefWidth(Main.getStage().getWidth()/2);
        tableViewRight.setPrefWidth(Main.getStage().getWidth()/2);

        tableViewLeft.setOnMouseClicked( eventHandlerDblClick );
        tableViewRight.setOnMouseClicked( eventHandlerDblClick );

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

        tableViewLeft.setOnKeyPressed( eventHandlerKeyBord );
        tableViewRight.setOnKeyPressed( eventHandlerKeyBord );

        tableViewLeft.setItems(listFilesLeft);
        tableViewLeft.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableViewRight.setItems(listFilesRight);
        tableViewRight.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        columnNameLeft.setCellValueFactory(callbackColumnName);
        columnNameLeft.setCellFactory(callbackCellFactoryName);
        columnNameRight.setCellValueFactory(callbackColumnName);
        columnNameRight.setCellFactory(callbackCellFactoryName);

        columnTypeLeft.setCellValueFactory(callbackColumnType);
        columnTypeRight.setCellValueFactory(callbackColumnType);

        columnSizeLeft.setCellValueFactory(callbackColumnSize);
        columnSizeRight.setCellValueFactory(callbackColumnSize);

        columnTypeLeft.setPrefWidth(100);
        columnTypeRight.setPrefWidth(100);

        columnSizeLeft.setPrefWidth(100);
        columnSizeRight.setPrefWidth(100);

        columnNameLeft.setPrefWidth(borderPaneLeft.getPrefWidth()-215);
        columnNameRight.setPrefWidth(borderPaneRight.getPrefWidth()-215);

        initMenuBar();

        setLocation("left", new File(ConfigManager.getValue( ConfigManager.CONFIG_START_PATH_LEFT )) );
        setLocation("right",new File(ConfigManager.getValue( ConfigManager.CONFIG_START_PATH_RIGHT )));

    }
    /**
     * Копирование файлов в буфер обмена
     */
    private void copyToClipboard() {

        List<File> selectedFiles = getSelectedFiles();
        if ( selectedFiles.size() > 0 ) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new TransferableFile( selectedFiles ),null);
        }

    }
    /**
     * Вставка файлов из буфера обмена
     */
    private void pasteIntoClipboard() {

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        if ( clipboard.isDataFlavorAvailable( DataFlavor.javaFileListFlavor ) ){

            try {
                List<File> data = (List<File>) clipboard.getData( DataFlavor.javaFileListFlavor );
                Util.copy( data, getPath() );
                updateTableViews();
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }
    /*
    * Показываем контекстное меню для списка файлов
    */
    private void requestContextMenu( ContextMenuEvent e ) {
        ContextMenu contextMenu = new ContextMenu();
        List<File> selectedFiles = getSelectedFiles();

        if( selectedFiles.size() == 1 && selectedFiles.get(0).getName().equals("...") ){
            return;
        }

        if( selectedFiles.size() == 1 ){
            File file = selectedFiles.get(0);
            if ( file.isDirectory() ){

                Menu itemOpen = new Menu("Открыть в");

                MenuItem itemOpenHere = new MenuItem("Этой панели");
                itemOpenHere.setOnAction(event -> setLocation( focus, file ));
                itemOpen.getItems().add(itemOpenHere);

                MenuItem itemOpenOther = new MenuItem("Другой панели");
                itemOpenOther.setOnAction(event -> setLocation( focus.equals("left") ? "right" : "left", file ));
                itemOpen.getItems().add(itemOpenOther);

                MenuItem itemOpenInExplorer = new MenuItem("Проводнике");
                itemOpenInExplorer.setOnAction(event -> {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                });
                itemOpen.getItems().add(itemOpenInExplorer);

                contextMenu.getItems().add(itemOpen);

            }else {
                MenuItem itemOpenFile = new MenuItem("Открыть");
                itemOpenFile.setOnAction(event -> openFile(selectedFiles.get(0)));
                contextMenu.getItems().add(itemOpenFile);
            }

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
        itemCreateFile.setOnAction(event -> openCreateFileStage());

        MenuItem itemCreateDir = new MenuItem("Папку");
        itemCreateDir.setOnAction(event -> openCreateDirStage());

        menuCreate.getItems().addAll(itemCreateFile,itemCreateDir);
        contextMenu.getItems().add(menuCreate);

        if( selectedFiles.size() > 0 ) {

            Menu menuCopy = new Menu( "Копировать в..." );

            MenuItem menuItemCopyToClipBoard = new MenuItem( "Буфер обмена" );
            menuItemCopyToClipBoard.setOnAction(event -> copyToClipboard());
            menuCopy.getItems().add( menuItemCopyToClipBoard );

            MenuItem menuItemCopyToOtherPanel = new MenuItem( "Другую панель" );
            menuItemCopyToOtherPanel.setOnAction(event -> {
                Util.copy( selectedFiles, getPathInverse() );
                updateTableViews();
            });
            menuCopy.getItems().add( menuItemCopyToOtherPanel );

            contextMenu.getItems().add( menuCopy );

        }

        MenuItem itemPaste = new MenuItem("Вставить");
        itemPaste.setOnAction(event -> pasteIntoClipboard());
        contextMenu.getItems().add(itemPaste);

        if( !Toolkit.getDefaultToolkit().getSystemClipboard().isDataFlavorAvailable( DataFlavor.javaFileListFlavor ) ){
            itemPaste.setDisable( true );
        }

        MenuItem itemUpdate = new MenuItem("Обновить");
        itemUpdate.setOnAction(event -> updateTableView(focus));
        contextMenu.getItems().add(itemUpdate);

        if( selectedFiles.size() > 0 ){
            MenuItem itemRemove = new MenuItem("Удалить");
            itemRemove.setOnAction(event -> removeFiles());
            contextMenu.getItems().add(itemRemove);
        }
        contextMenu.show(Main.getStage(),e.getScreenX(),e.getScreenY());
    }
    /*
    * Переходим в папку или открываем файл
    */
    private void openFile(File file) {
        if( file.getName().equals("...") ){
            File parentFile = new File(getPath()).getParentFile();
            if( parentFile != null ){
                setLocation( focus, parentFile );
            }
            return;
        }
        if( !file.exists() ) return;
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
    /*
    * Удаление файла/папки
    */
    private void removeFiles() {
        List<File> list = getSelectedFiles();
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
    /*
    * Открываем окно создания файла
    */
    private void openCreateFileStage() {
        String p = getPath();
        Main.getStageCreateFileController().getFieldPath().setText(p);
        Main.getStageCreateFile().show();
    }
    /*
    * Открываем окно создания папки
    */
    private void openCreateDirStage(){
        Main.getStageCreateDir().show();
    }
    /*
    * Инициализация элементов меню
    */
    private void initMenuBar() {
        menuItemCreateFile.setOnAction(event -> {
            openCreateFileStage();
        });
        menuItemSearch.setOnAction(event -> {
            Main.getStageSearchController().getFieldPath().setText(getPath());
            Main.getStageSearch().show();
        });
        menuItemCreateDir.setOnAction(event -> openCreateDirStage());
        menuItemQuit.setOnAction(event -> System.exit(0));
        menuItemAbout.setOnAction(event -> Main.getStageAbout().show());

        MenuItem itemUser = new MenuItem(System.getProperty("user.name"));
        itemUser.setOnAction(event12 -> setLocation(focus, new File("C:/Users/" + System.getProperty("user.name"))));
        menuTo.getItems().add(itemUser);

        MenuItem itemDesktop = new MenuItem("Рабочий стол");
        itemDesktop.setOnAction(event12 -> setLocation(focus, new File("C:/Users/" + System.getProperty("user.name") + "/Desktop/")));
        menuTo.getItems().add(itemDesktop);

        MenuItem itemDocuments = new MenuItem("Мои документы");
        itemDocuments.setOnAction(event12 -> setLocation(focus, new File("C:/Users/" + System.getProperty("user.name") + "/Documents/")));
        menuTo.getItems().add(itemDocuments);

        MenuItem itemImages = new MenuItem("Изображения");
        itemImages.setOnAction(event12 -> setLocation(focus, new File("C:/Users/" + System.getProperty("user.name") + "/Pictures/")));
        menuTo.getItems().add(itemImages);

        MenuItem itemMusic = new MenuItem("Моя музыка");
        itemMusic.setOnAction(event12 -> setLocation(focus, new File("C:/Users/" + System.getProperty("user.name") + "/Music/")));
        menuTo.getItems().add(itemMusic);

        MenuItem itemVideo = new MenuItem("Мои видеозаписи");
        itemVideo.setOnAction(event12 -> setLocation(focus, new File("C:/Users/" + System.getProperty("user.name") + "/Videos/")));
        menuTo.getItems().add(itemVideo);

        MenuItem itemDownloads = new MenuItem("Загрузки");
        itemDownloads.setOnAction(event12 -> setLocation(focus, new File("C:/Users/" + System.getProperty("user.name") + "/Downloads/")));
        menuTo.getItems().add(itemDownloads);

        SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();
        menuTo.getItems().add(separatorMenuItem1);

    }
    /*
    * Переходим в папку
    */
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

    /*  GETTERS */

    public String getPath(){
        return focus.equals("left") ? tableViewLeftPath : tableViewRightPath;
    }
    private String getPathInverse() {
        return focus.equals("left") ? tableViewRightPath : tableViewLeftPath;
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
    public List<File> getSelectedFiles(){
        return focus.equals("left") ? tableViewLeft.getSelectionModel().getSelectedItems() : tableViewRight.getSelectionModel().getSelectedItems();
    }
    public TableView getFocusedTableView(){
        return focus.equals("left") ? tableViewLeft : tableViewRight;
    }
    public List<File> getFocusedTableViewListFiles(){
        return focus.equals("left") ? listFilesLeft : listFilesRight;
    }

    /*  EVENTS  */
    public void onChangeWindowSize(Number newValue){
        borderPaneLeft.setPrefWidth(newValue.intValue()/2);
        borderPaneRight.setPrefWidth(newValue.intValue()/2);

        columnNameLeft.setPrefWidth(borderPaneLeft.getPrefWidth()-215);
        columnNameRight.setPrefWidth(borderPaneRight.getPrefWidth()-215);
    }
    @FXML private void onTextFieldLeftEnter(){
        File file = new File(fieldPathLeft.getText());
        if ( file.exists() ) setLocation("left",file);
        else fieldPathLeft.setText(tableViewLeftPath);
    }
    @FXML private void onTextFieldRightEnter(){
        File file = new File(fieldPathRight.getText());
        if ( file.exists() ) setLocation("right",file);
        else fieldPathRight.setText(tableViewRightPath);
    }
    @FXML private void onMenuFavoritesShow(){
        String path = getPath();
        menuFavorites.getItems().clear();

        if( ConfigManager.containsFavorites( new File( path ) ) ){
            MenuItem itemRemove = new MenuItem( "Удалить из закладок" );
            itemRemove.setOnAction(event -> {
                ConfigManager.removeFavorites( new File( path ) );
                menuFavorites.hide();
            });
            menuFavorites.getItems().add(itemRemove);
        }else{
            MenuItem itemAdd = new MenuItem("Добавить в закладки");
            itemAdd.setOnAction(event -> {
                ConfigManager.addFavorites( new File( path ) );
                menuFavorites.hide();
            });
            menuFavorites.getItems().add(itemAdd);
        }

        for (File file : ConfigManager.getFavorites()) {
            MenuItem item = new MenuItem(file.getPath());
            item.setOnAction(event1 -> setLocation(focus, file));
            menuFavorites.getItems().add(item);
        }
    }
    @FXML private void onMenuDisksShow(){
        File[] roots = File.listRoots();
        menuDisks.getItems().clear();

        for (File root : roots) {
            MenuItem item = new MenuItem(root.getPath());
            item.setOnAction(event1 -> setLocation(focus, root));
            menuDisks.getItems().add(item);
        }
    }
    @FXML private void onMenuItemSettings(){
        Main.getStageSettingsController().getFieldStartPathLeft().setText( ConfigManager.getValue( ConfigManager.CONFIG_START_PATH_LEFT ) );
        Main.getStageSettingsController().getFieldStartPathRight().setText( ConfigManager.getValue( ConfigManager.CONFIG_START_PATH_RIGHT ) );
        Main.getStageSettingsController().getCheckBoxFNamesInBrackets().setSelected( Boolean.valueOf(ConfigManager.getValue( ConfigManager.CONFIG_FOLDER_NAMES_IN_BRACKETS )) );
        Main.getStageSettingsController().getCheckBoxFloorSize().setSelected( Boolean.valueOf(ConfigManager.getValue( ConfigManager.CONFIG_FLOOR_SIZE )) );
        Main.getStageSettings().show();
    }

}
