package SocketTCP;

import java.io.*;
import java.net.*;

public class SaranHarianClient {

    public void go() {
        try {
            Socket s = new Socket("127.0.0.1", 4242); // Langkah 1
            System.out.println("Terhubung ke server...");

            InputStreamReader strReader = new InputStreamReader(s.getInputStream());
            BufferedReader reader = new BufferedReader(strReader);

            String saran = reader.readLine();
            System.out.println("Saran buat Anda hari ini: " + saran);

            reader.close();
            s.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SaranHarianClient client = new SaranHarianClient();
        client.go();
    }
}

