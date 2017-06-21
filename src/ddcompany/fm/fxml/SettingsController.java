package ddcompany.fm.fxml;

import ddcompany.fm.Main;
import ddcompany.fm.config.ConfigManager;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class SettingsController {

    @FXML private TextField fieldStartPathLeft;
    @FXML private TextField fieldStartPathRight;
    @FXML private CheckBox checkBoxFNamesInBrackets;
    @FXML private CheckBox checkBoxShowSFilesFolders;
    @FXML private CheckBox checkBoxFloorSize;

    @FXML public void initialize(){}

    /*  GETTERS */
    public TextField getFieldStartPathLeft() {
        return fieldStartPathLeft;
    }
    public TextField getFieldStartPathRight() {
        return fieldStartPathRight;
    }
    public CheckBox getCheckBoxFNamesInBrackets() {
        return checkBoxFNamesInBrackets;
    }
    public CheckBox getCheckBoxFloorSize() {
        return checkBoxFloorSize;
    }

    /*  EVENTS  */
    @FXML private void onBtnSaveClick(){

        ConfigManager.findField( ConfigManager.CONFIG_START_PATH_LEFT ).setValue( fieldStartPathLeft.getText() );
        ConfigManager.findField( ConfigManager.CONFIG_START_PATH_RIGHT ).setValue( fieldStartPathRight.getText() );
        ConfigManager.findField( ConfigManager.CONFIG_FOLDER_NAMES_IN_BRACKETS ).setValue( String.valueOf(checkBoxFNamesInBrackets.isSelected()) );
        ConfigManager.findField( ConfigManager.CONFIG_SHOW_S_FILES_FOLDERS ).setValue( String.valueOf(checkBoxShowSFilesFolders.isSelected()) );
        ConfigManager.findField( ConfigManager.CONFIG_FLOOR_SIZE ).setValue( String.valueOf(checkBoxFloorSize.isSelected()) );
        ConfigManager.saveConfig();

        Main.getStageController().updateTableViews();

    }

}
