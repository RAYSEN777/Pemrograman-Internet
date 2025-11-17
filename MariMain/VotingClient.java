package MariMain;

import java.io.*;
import java.net.*;
import java.util.*;

public class VotingClient {
    private static final String SERVER_IP = "192.168.1.23"; 
    private static final int PORT = 1234;

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(SERVER_IP, PORT);
            Scanner input = new Scanner(socket.getInputStream());
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            Scanner userInput = new Scanner(System.in)
        ) {
            
            while (input.hasNextLine()) {
                String serverMsg = input.nextLine();
                System.out.println(serverMsg);

                
                if (serverMsg.toLowerCase().contains("ketik huruf kandidat")) {
                    System.out.print("Masukkan pilihan Anda: ");
                    String pilihan = userInput.nextLine();
                    output.println(pilihan);
                }
            }
        } catch (IOException e) {
            System.out.println("Tidak dapat terhubung ke server.");
        }
    }
}
