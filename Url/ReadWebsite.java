import java.net.*;
import java.io.*;

public class ReadWebsite {
    public static void main(String[] args) {
        String nextLine;
        URL url = null;
        URLConnection urlConn = null;
        InputStreamReader inStream = null;
        BufferedReader buff = null;

        try {
            // url = new URL("http://www.google.com");
            url = URI.create("http://www.google.com").toURL();
            urlConn = url.openConnection();
            inStream = new InputStreamReader(urlConn.getInputStream(), "UTF8");
            buff = new BufferedReader(inStream);

            while (true) {
                nextLine = buff.readLine();
                if (nextLine != null) {
                    System.out.println(nextLine);
                } else {
                    break;
                }
            }
        } catch(MalformedURLException e){
            System.out.println("URL Error " + e.toString());
        } catch(IOException e1){
            System.out.println("Tidak dapat baca " + e1.toString());
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                    buff.close();
                } catch(IOException e1){
                    System.out.println("Nggak bisa tutup " + e1.getMessage());
                }
            }
        }
    } 
}
