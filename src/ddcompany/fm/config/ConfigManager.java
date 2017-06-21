package ddcompany.fm.config;

import ddcompany.fm.AlertUtils;
import ddcompany.fm.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для работы с конфигом
 */
public class ConfigManager {

    private static File dataDir = new File("data/");
    private static File fileConfig = new File("data/config.dat");
    private static List<ConfigUnit> defaultFields = new ArrayList<>();
    private static List<ConfigUnit> fields = new ArrayList<>();
    private static List<File> favorites = new ArrayList<>();

    public static final String CONFIG_START_PATH_LEFT = "startPathLeft";
    public static final String CONFIG_START_PATH_RIGHT = "startPathRight";
    public static final String CONFIG_FOLDER_NAMES_IN_BRACKETS = "folderNamesInBrackets";
    public static final String CONFIG_SHOW_S_FILES_FOLDERS = "showSFilesFolders";
    public static final String CONFIG_FLOOR_SIZE = "floorSize";

    public static final String VERSION = "0.2";

    /*
    * Инициализация конфига.Вызывается при старте программы
    */
    public static void initConfig(){
        try {
            //Начальная папка для левой панели
            defaultFields.add(new ConfigUnit(CONFIG_START_PATH_LEFT, "C:/"));
            //Начальная папка для правой панели
            defaultFields.add(new ConfigUnit(CONFIG_START_PATH_RIGHT, "C:/"));
            //Отображение имен папок в квадратных скобках
            defaultFields.add(new ConfigUnit(CONFIG_FOLDER_NAMES_IN_BRACKETS, "false"));
            //Отображение скрытых папок/файлов
            defaultFields.add(new ConfigUnit(CONFIG_SHOW_S_FILES_FOLDERS, "false"));
            //Округление размера файлов
            defaultFields.add(new ConfigUnit(CONFIG_FLOOR_SIZE, "true"));

            if ( !dataDir.exists() ) dataDir.mkdir();
            if ( !fileConfig.exists() ) fileConfig.createNewFile();

            readAndAddDefaults();
        }catch ( Exception e ){
            AlertUtils.showError("Произошла ошибка инициализации файла конфигураций!",true);
            System.exit(0);
        }
    }

    /*
    * Поиск поля в конфиге
    */
    public static ConfigUnit findField( String f ){

        for ( ConfigUnit unit : fields ){

            if( unit.getName().equals(f) ){

                return unit;

            }

        }
        return null;

    }

    /*
     *Функция читает конфиг и добавляет к нему поля если они не существуют
     */
    private static void readAndAddDefaults() throws IOException {
        //List<ConfigUnit> configUnits = new ArrayList<>();
        String[] str = new String(Files.readAllBytes(fileConfig.toPath())).split("\n");
        boolean rewriteConfig = false;
        for ( String s : str ){
            if( !s.isEmpty() ) {
                String[] strings = s.split("=");
                fields.add(new ConfigUnit(strings[0], strings[1]));
            }
        }

        for ( ConfigUnit unit : defaultFields ){

            if ( findField( unit.getName() ) == null ){

                fields.add( new ConfigUnit(unit.getName(), unit.getValue() ) );
                rewriteConfig = true;
            }

        }

        if ( rewriteConfig ){

            saveConfig();

        }

    }

    /*
    * Сохранение конфига
    */
    public static void saveConfig(){
        try {
            Files.write(fileConfig.toPath(), "".getBytes());

            for (ConfigUnit unit : fields) {

                Files.write(fileConfig.toPath(), (unit.getName() + "=" + unit.getValue() + "\n").getBytes(), StandardOpenOption.APPEND);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * Получение значения
    */
    public static String getValue( String name ){
        ConfigUnit unit = findField( name );
        return unit == null ? "" : unit.getValue();
    }

    /*
    * Сохранение закладок
    */
    public static void saveFavorites(){
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

    /**
     * Добавление в закладки
     * @param file папка
     */
    public static void addFavorites( File file ){
        if ( file.isDirectory() ) favorites.add(file);
        saveFavorites();
    }

    /**
     * Добавлена ли папка в закладки
     * @param file папка
     * @return
     */
    public static boolean containsFavorites( File file ){
        return favorites.contains( file );
    }

    /**
     * Удаление из закладок
     * @param file папка
     */
    public static void removeFavorites( File file ){
        favorites.remove( file );
        saveFavorites();
    }

    /*  GETTERS */
    public static List<File> getFavorites() {
        return favorites;
    }
}
