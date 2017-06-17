package ddcompany.fm.fxml;

import ddcompany.fm.AlertUtils;
import ddcompany.fm.Main;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.File;

public class CreateDirController {

    @FXML
    private TextField field;

    @FXML
    public void initialize(){

    }

    public TextField getField() {
        return field;
    }

    /*  EVENTS  */
    @FXML
    public void onBtnCreateDirClick(){
        if ( !field.getText().isEmpty() ) {
            String path = Main.getStageController().getPath();
            path = path.endsWith("/") ? path : path + "/";
            File file = new File(path + field.getText());
            if (!file.exists()){
                if(!file.mkdirs()){
                    AlertUtils.showError("Ошибка создания папки!");
                    field.setText("");
                    return;
                }
                Main.getStageController().updateTableViews();
                Main.getStageCreateDir().close();
                field.setText("");
            }else{
                AlertUtils.showError("Папка уже существует!");
            }
        }else{
            AlertUtils.showError("Неверное имя папки!");
        }
    }

}
