import java.util.zip.ZipInputStream;
import java.io.FileInputStream;
import java.util.zip.*;
import java.io.*;

public class testing {
    public static void main(String[] args) {
        FileInputStream fInStream = null;

        try {
            fInStream = new FileInputStream("Folder.zip");
            ZipInputStream zInStream = new ZipInputStream(fInStream);
            File outFile = new File("Tugas1", "Folder/iniFolder.zip");


            
            ZipEntry e; 
            while ((e = zInStream.getNextEntry()) != null) {
                System.out.println("Entry: " + e.getName());
                zInStream.closeEntry();
            }

            System.out.println("parent : " + outFile.getParentFile());
            System.out.println();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (fInStream != null) {
                    fInStream.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
