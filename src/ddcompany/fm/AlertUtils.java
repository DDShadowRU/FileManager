package ddcompany.fm;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;
import java.util.function.Consumer;

public class AlertUtils {

    public static String showInput( String text , String defaultValue ){
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setHeaderText(null);
        dialog.setContentText(text);

        Optional<String> result = dialog.showAndWait();
        if( result.isPresent() ) {
            return result.get();
        }else{
            return "";
        }
    }

    public static ButtonType showConfirm(String text ){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setContentText(text);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get();
    }

    public static void showError( String text ){
        showError(text,false);
    }

    public static void showError( String text , boolean wait ){

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(text);
        alert.setHeaderText(null);
        if( wait ){
            alert.showAndWait();
        }else{
            alert.show();
        }

    }

}
