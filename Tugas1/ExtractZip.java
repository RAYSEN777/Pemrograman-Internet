import java.io.*;
import java.util.zip.*;

public class ExtractZip {
    public static void main(String[] args) {
        String zipFile = "Folder.zip";   
        String outDir = "output";     
        String foldZip = "output";  
        String newZip = "Compressed.zip";

        try {
            extractZip(new File(zipFile), new File(outDir));
            
            compressFolder(new File(foldZip), new File(newZip));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void extractZip(File zipFile, File outDir) throws IOException {
        try (
            FileInputStream fInStream = new FileInputStream(zipFile);
            BufferedInputStream bInStream = new BufferedInputStream(fInStream);
            ZipInputStream zInStream = new ZipInputStream(bInStream)
        ) {
            ZipEntry e;
            while ((e = zInStream.getNextEntry()) != null) {
                File outFile = new File(outDir, e.getName());

                if (e.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    outFile.getParentFile().mkdirs();
                    try (FileOutputStream fOutStream = new FileOutputStream(outFile);
                        BufferedOutputStream bOutStream = new BufferedOutputStream(fOutStream)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zInStream.read(buffer)) > 0) {
                            bOutStream.write(buffer, 0, len);
                        }
                    }

                    if (outFile.getName().toLowerCase().endsWith(".zip")) {
                        extractZip(outFile, outFile.getParentFile());
                        outFile.delete(); 
                    }
                }
                zInStream.closeEntry();
            }
        }
    }

    private static void compressFolder(File folder, File zipFile) throws IOException {
        try (
            FileOutputStream fOut = new FileOutputStream(zipFile);
            BufferedOutputStream bOut = new BufferedOutputStream(fOut);
            ZipOutputStream zOut = new ZipOutputStream(bOut)
        ) {
            compressRecursive(folder, folder.getName() + "/", zOut);
        }
    }

    private static void compressRecursive(File file, String entryName, ZipOutputStream zOut) throws IOException {
        if (file.isDirectory()) {
            zOut.putNextEntry(new ZipEntry(entryName));
            zOut.closeEntry();

            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    compressRecursive(child, entryName + child.getName(), zOut);
                }
            }
        } else {
            try (FileInputStream fIn = new FileInputStream(file);
                 BufferedInputStream bIn = new BufferedInputStream(fIn)) {
                zOut.putNextEntry(new ZipEntry(entryName));
                byte[] buffer = new byte[1024];
                int len;
                while ((len = bIn.read(buffer)) > 0) {
                    zOut.write(buffer, 0, len);
                }
                zOut.closeEntry();
            }
        }
    }
}
