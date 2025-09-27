import java.io.*;
import java.net.*;

public class DownloadFile {
    public static void main(String args[]) {
        InputStream in = null;
        FileOutputStream fOut = null;

        try {
            URL remoteFile = new URL(
                "https://filesamples.com/samples/image/jpg/sample_5184%C3%973456.jpg"
            );
            URLConnection fileStream = remoteFile.openConnection();

            in = fileStream.getInputStream();
            fOut = new FileOutputStream("Musik.jpg");

            int data;
            while ((data = in.read()) != -1) {
                fOut.write(data);
            }

            System.out.println("File telah berhasil didownload");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (fOut != null) {
                    fOut.flush();
                    fOut.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
