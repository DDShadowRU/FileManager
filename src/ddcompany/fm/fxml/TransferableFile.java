package ddcompany.fm.fxml;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;


public class TransferableFile implements Transferable {

    private List<File> files;

    public TransferableFile(List<File> selectedFiles) {

         this.files = selectedFiles;

    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {

        return new DataFlavor[]{ DataFlavor.javaFileListFlavor };

    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {

        return flavor.equals( DataFlavor.javaFileListFlavor );

    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {

        if( flavor.equals( DataFlavor.javaFileListFlavor ) ){
            return files;
        }else{
            throw new UnsupportedFlavorException( flavor );
        }

    }
}
