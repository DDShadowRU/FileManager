package ddcompany.fm.config;


public class FilePattern {

    private String patternName;
    private String fileName;
    private String text;

    public FilePattern( String patternName, String fileName, String text ){
        this.fileName=fileName;
        this.text=text;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPatternName() {
        return patternName;
    }

    public void setPatternName(String patternName) {
        this.patternName = patternName;
    }

}
