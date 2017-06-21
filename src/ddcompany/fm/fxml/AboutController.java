package ddcompany.fm.fxml;

import ddcompany.fm.Main;
import ddcompany.fm.config.ConfigManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AboutController {

    @FXML private Label labelVersion;

    @FXML
    public void initialize(){
        labelVersion.setText( ConfigManager.VERSION );
    }

}
