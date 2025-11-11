import java.io.*;
import java.net.*;
import java.util.*;

public class AbsensiClient {
    private static InetAddress host;
    private static final int PORT = 1222;

    public static void main(String[] args) {
        try {
            // Sesuaikan IP host di sini — ganti jika server di komputer lain
            host = InetAddress.getByName("10.70.3.241");
        } catch (UnknownHostException uhEx) {
            System.out.println("Host tidak ditemukan.");
            System.exit(1);
        }
        jalankanClient();
    }

    private static void jalankanClient() {
        Socket socket = null;

        try {
            socket = new Socket(host, PORT);
            Scanner networkInput = new Scanner(socket.getInputStream());
            PrintWriter networkOutput = new PrintWriter(socket.getOutputStream(), true);
            Scanner userEntry = new Scanner(System.in);

            // Program berinteraksi step-by-step dengan server
            while (networkInput.hasNextLine()) {
                String serverMessage = networkInput.nextLine();
                System.out.println(serverMessage);

                // Jika server meminta input, baca dari user
                if (serverMessage.contains("Masukkan")) {
                    String userInput = userEntry.nextLine();
                    networkOutput.println(userInput);
                }
            }

        } catch (IOException ioEx) {
            System.out.println("Koneksi gagal: " + ioEx.getMessage());
        } finally {
            try {
                if (socket != null) {
                    System.out.println("\nMenutup koneksi...");
                    socket.close();
                }
            } catch (IOException ioEx) {
                System.out.println("Tidak bisa menutup koneksi!");
            }
        }
    }
}
