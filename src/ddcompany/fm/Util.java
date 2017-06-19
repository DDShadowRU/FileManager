package ddcompany.fm;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Util {

    public static List<File> sort(int sortType,File[] files){
        List<File> sorted = new ArrayList<File>();
        if( files!=null ) {
            if (sortType == 1) {

                for (File f : files) {
                    if (f.isDirectory()) {
                        sorted.add(f);
                    }
                }

                for (File f : files) {
                    if (!f.isDirectory()) {
                        sorted.add(f);
                    }
                }

            }
        }
        return sorted;
    }

    public static void removeDirectory(File file){
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for( File f : files ){
                if( f.isDirectory() ) {
                    removeDirectory(f);
                } else f.delete();
            }
            file.delete();
        }
    }

    public static void copy(Path path, Path pathTo){
        if( path.toFile().isDirectory() ){
            File[] files = path.toFile().listFiles();
            if ( !pathTo.toFile().exists() ) {
                pathTo.toFile().mkdir();
            }
            for ( File file : files ){
                copy( file.toPath(), Paths.get(pathTo.toString() + File.separator + file.getName()) );
            }
        }else{
            try {
                if( new File( pathTo.toString() ).exists() ){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText(null);
                    alert.setContentText("Заменить файл " + pathTo.toString()+"?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK){
                        Files.copy(path, pathTo, StandardCopyOption.REPLACE_EXISTING);
                    }
                }else{
                    Files.copy(path, pathTo, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean remove(List<File> toRemove ){
        for( File file : toRemove ){
            if( file.getPath()!="..." ) {
                try {
                    if (!file.isDirectory()) file.delete();
                    else removeDirectory(file);
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String getExt(File file){
        if ( !file.isDirectory() ) {
            int index = file.getName().indexOf(".");
            return index == -1 ? "" : file.getName().substring(file.getName().lastIndexOf(".")+1);
        }else {
            return "";
        }
    }

    public static String pbytes(Long bytes){
        double lon = 0;
        String string = " Bytes";

        if( bytes >=1024 ){

            lon = bytes / 1024;
            string = " KB";

            if( lon >= 1024 ){

                lon = lon / 1024;
                string = " MB";

                if( lon >= 1024 ){

                    lon = lon / 1024;
                    string = " GB";

                }

            }
        }

        return lon + string;
    }

    public static String getFileNameNoExt(String name,String path) {
        if( !new File(path).isDirectory() && name.indexOf(".")==0 ){
            return name;
        }
        return !new File(path).isDirectory() ? (name.indexOf(".") == -1 ? name : name.substring(0,name.indexOf("."))) : name;
    }

    public static File[] toFileArray(List<File> searchedFiles) {
        File[] files = new File[searchedFiles.size()];
        for ( int i=0;i<searchedFiles.size();i++ ){
            files[i]=searchedFiles.get(i);
        }
        return files;
    }

    public static List<File> getFilesMatchedRegex(List<File> cur, List<File> files, String regex, boolean subDirs, boolean searchFolders, boolean searchFiles){
        for ( File file : files ){
            if ( file.isDirectory() && !file.getName().equals("...") ){
                if ( searchFolders && file.getName().matches(regex) ){
                    cur.add(file);
                }
                if ( subDirs ){
                    List<File> fileList = toFileList(file.listFiles());
                    if( fileList != null ) getFilesMatchedRegex(cur, fileList, regex, subDirs, searchFolders, searchFiles);
                }
            }else{
                if ( searchFiles && file.getName().matches(regex) ){
                    cur.add(file);
                }
            }
        }
        return cur;
    }

    public static List<File> toFileList(File[] files) {
        List<File> list = new ArrayList<>();
        if( files!=null ){
            for ( File file : files ){
                list.add(file);
            }
        }
        return list;
    }

    public static Image getImageForExt(File file){
        Image image = new Image("/ddcompany/fm/resources/unknown.png");
        if ( file.isDirectory() ){
            image = new Image("/ddcompany/fm/resources/folder.png");
        }else {
            String ext = getExt(file);
            if ( ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") ){
                image = new Image("/ddcompany/fm/resources/image.png");
            }else if( ext.equals("txt") || ext.equals("ini") ){
                image = new Image("/ddcompany/fm/resources/text.png");
            }else if( ext.equals("zip") || ext.equals("rar") ){
                image = new Image("/ddcompany/fm/resources/archive.png");
            }else if( ext.equals("mp3") ){
                image = new Image("/ddcompany/fm/resources/audio.png");
            }else if( ext.equals("jar") ){
                image = new Image("/ddcompany/fm/resources/jar.png");
            }else if( ext.equals("exe") ){
                image = new Image("/ddcompany/fm/resources/exe.png");
            }
        }
        return image;
    }

}
