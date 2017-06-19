package ddcompany.fm.fxml;


import ddcompany.fm.AlertUtils;
import ddcompany.fm.Main;
import ddcompany.fm.Util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchController {

    @FXML
    private TextField fieldSearch;
    @FXML
    private TextField fieldPath;
    @FXML
    private CheckBox checkBoxIsRegEx;
    @FXML
    private CheckBox checkBoxContains;
    @FXML
    private CheckBox checkBoxSearchFiles;
    @FXML
    private CheckBox checkBoxSearchFolders;
    @FXML
    private CheckBox checkBoxSubDirs;
    @FXML
    private Button buttonOpen;
    @FXML
    private Button buttonTo;
    @FXML
    private ListView listView;

    private ObservableList<File> sortedFiles = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        listView.setItems(sortedFiles);
    }

    public TextField getFieldPath() {
        return fieldPath;
    }

    private void selectFile( File selectedFile, String f ){
        for ( int i=0;i<Main.getStageController().getListFiles(f).size();i++ ){
            if ( Main.getStageController().getListFiles(f).get(i).equals(selectedFile) ){
                Main.getStageController().getTableView(f).getSelectionModel().select(i);
                Main.getStageSearch().close();
            }
        }
    }

    /*  EVENTS  */
    @FXML
    private void onBtnPasteLeftPathClick(){
        fieldPath.setText(Main.getStageController().getTableViewLeftPath());
    }
    @FXML
    private void onBtnPasteRightPathClick(){
        fieldPath.setText(Main.getStageController().getTableViewRightPath());
    }
    @FXML
    private void onBtnToClick(){
        if ( listView.getSelectionModel().getSelectedIndex() != -1 ) {
            File selectedFile = sortedFiles.get(listView.getSelectionModel().getSelectedIndex());
            String f = "";
            if (Main.getStageController().getTableViewLeftPath().equals(selectedFile.getParent())) {
                f = "left";
            } else if (Main.getStageController().getTableViewRightPath().equals(selectedFile.getParent())) {
                f = "right";
            }
            if ( !f.isEmpty() ) {
                selectFile(selectedFile,f);
            }else{
                File path = selectedFile.isFile() ? selectedFile.getParentFile() : selectedFile;
                ContextMenu contextMenu = new ContextMenu();

                MenuItem itemLeft = new MenuItem("Левая панель");
                itemLeft.setOnAction(event -> {
                    Main.getStageController().setLocation( "left",path );
                    selectFile(selectedFile,"left");
                });
                contextMenu.getItems().add(itemLeft);

                MenuItem itemRight = new MenuItem("Правая панель");
                itemRight.setOnAction(event -> {
                    Main.getStageController().setLocation( "right",path );
                    selectFile(selectedFile,"right");
                });
                contextMenu.getItems().add(itemRight);

                contextMenu.show(Main.getStageSearch(),Main.getStageSearch().getX()+buttonTo.getLayoutX(),Main.getStageSearch().getY()+buttonTo.getLayoutY());
            }
        }
    }
    @FXML
    private void onBtnOpenClick(){

        if ( listView.getSelectionModel().getSelectedIndex() != -1 ){

            File selectedFile = sortedFiles.get(listView.getSelectionModel().getSelectedIndex());
            if ( selectedFile.isDirectory() ){

                ContextMenu contextMenu = new ContextMenu();

                MenuItem itemLeft = new MenuItem("Левая панель");
                itemLeft.setOnAction(event -> {
                    Main.getStageController().setLocation( "left",selectedFile );
                    Main.getStageSearch().close();
                });
                contextMenu.getItems().add(itemLeft);

                MenuItem itemRight = new MenuItem("Правая панель");
                itemRight.setOnAction(event -> {
                    Main.getStageController().setLocation( "right",selectedFile );
                    Main.getStageSearch().close();
                });
                contextMenu.getItems().add(itemRight);

                contextMenu.show(Main.getStageSearch(),Main.getStageSearch().getX()+buttonOpen.getLayoutX(),Main.getStageSearch().getY()+buttonOpen.getLayoutY());

            }else{
                try {
                    Desktop.getDesktop().open(selectedFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
    @FXML
    private void onBtnSearchClick(){
        File filePath = new File(fieldPath.getText());
        if ( filePath.isDirectory() && filePath.exists() ) {
            listView.setDisable(true);

            List<File> files = Util.toFileList(filePath.listFiles());
            String regex = "";
            if( checkBoxIsRegEx.isSelected() ){
                regex = fieldSearch.getText();
            }else{
                if ( checkBoxContains.isSelected() ){
                    regex = ".*\\Q"+fieldSearch.getText()+"\\E.*";
                }else{
                    regex = "^\\Q"+fieldSearch.getText()+"\\E.*";
                }
            }
            List<File> list = Util.getFilesMatchedRegex(new ArrayList<File>(), files, regex, checkBoxSubDirs.isSelected(), checkBoxSearchFolders.isSelected(), checkBoxSearchFiles.isSelected());
            sortedFiles.addAll(list);
            listView.setDisable(false);

        }else{
            AlertUtils.showError("Неверный путь!");
        }
    }
}
