package ddcompany.fm;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

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

    /*public static boolean isRoot(File path){
        File[] roots =  File.listRoots();
        for( File root : roots ){
            if( root.getPath().equals(path.getPath()) ){
                return true;
            }
        }
        return false;
    }*/

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

    public static boolean isValidPathFile(String path) {
        if( path.matches("[A-z:\\/]+.+[.][A-z]+$") ){
            return true;
        }
        return false;
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

    public static List<File> getFilesMatchedRegex( List<File> cur, List<File> files, String regex, boolean subDirs, boolean searchFolders, boolean searchFiles ){
        for ( File file : files ){
            if ( file.isDirectory() ){
                if ( searchFolders && file.getName().matches(regex) ){
                    cur.add(file);
                }
                if ( subDirs ){
                    getFilesMatchedRegex(cur, toFileList(file.listFiles()), regex, subDirs, searchFolders, searchFiles);
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
        for ( File file : files ){
            list.add(file);
        }
        return list;
    }

    /*public static Image getImageForFile(File file){

        return new Image("file:/ddcompany/fm/resources/folder.png");
    }*/

}
