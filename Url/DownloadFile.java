import java.net.*;
import java.io.*;

public class DownloadFile {
    public static void main(String[] args) {
        InputStream in = null;
        FileOutputStream fout = null;

        try{
            URL remoteFile = new URL("http://dl.vafamusic.com/Full%20Album/bts/320/BTS-Boy-WithLuv%20%28VafaMusic%29.mp3");
            URLConnection fileStream = remoteFile.openConnection();
            fout = new FileOutputStream("Lagu.mp3");
            in = fileStream.getInputStream();

            int data;
            while ((data = in.read()) != -1) {
                fout.write(data);
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            System.out.println("File Berhasil Didownload");
            try {
                in.close();
                fout.flush();
                fout.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    } 
}
