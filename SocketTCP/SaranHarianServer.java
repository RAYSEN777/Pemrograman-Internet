package SocketTCP;

import java.io.*;
import java.net.*;

public class SaranHarianServer {

    String[] daftarSaran = {
        "Makan yang sedikit",
        "Anda gak terlalu gemuk kok",
        "Satu kata: tidak cocok",
        "Untuk hari ini, Anda boleh makan sepuasnya",
        "Anda perlu mempertimbangkan model rambut itu"
    };

    public void go() {
        try {
            try (ServerSocket serverSock = new ServerSocket(4242)) {
                System.out.println("Server berjalan di port 4242...");

                while (true) {
                    Socket sock = serverSock.accept(); // Langkah 2
                    PrintStream writer = new PrintStream(sock.getOutputStream()); // Langkah 3

                    String saran = getSaran(); // Langkah 4
                    writer.println(saran);
                    writer.close(); // Langkah 5

                    System.out.println("Mengirim saran: " + saran);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getSaran() {
        int random = (int) (Math.random() * daftarSaran.length);
        return daftarSaran[random];
    }

    public static void main(String[] args) {
        SaranHarianServer server = new SaranHarianServer();
        server.go();
    }
}
