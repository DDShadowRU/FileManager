package ddcompany.fm.fxml;

import ddcompany.fm.AlertUtils;
import ddcompany.fm.Main;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;

public class CreateFileController{

    @FXML
    private TextField field;
    @FXML
    private TextField fieldPath;

    public TextField getFieldPath() {
        return fieldPath;
    }

    @FXML
    public void initialize(){



    }

    /*  EVENTS  */
    @FXML
    public void onBtnCreateClick() {
        String lw = Main.getStageController().getFocusedListView();
            if( !field.getText().isEmpty() ){
                String t = fieldPath.getText().endsWith("/") ? fieldPath.getText() : fieldPath.getText() + "/";
                if(new File(t).exists()) {
                    File file = new File(t + field.getText());
                    if (!file.exists()) {
                        try {
                            field.setText("");
                            file.createNewFile();
                            Main.getStageController().updateTableViews();
                            Main.getStageCreateFile().close();
                        } catch (IOException e) {
                            AlertUtils.showError("Невозможно создать файл!");
                        }
                    } else {
                        AlertUtils.showError("Файл с таким именем уже существует!");
                    }
                }else{
                    AlertUtils.showError("Неверный путь!");
                }
            }else{
                AlertUtils.showError("Введите название файла!");
            }

    }
}
